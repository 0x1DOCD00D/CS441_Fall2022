package HelperUtils

import scala.collection.immutable.ListMap
import scala.util.{Failure, Success, Try}
import scala.collection.JavaConverters.*

/*
 *
 *  Copyright (c) 2021. Mark Grechanik and Lone Star Consulting, Inc. All rights reserved.
 *   
 *   Unless required by applicable law or agreed to in writing, software distributed under
 *   the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *   either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 *  
 */

/*
* This module obtains configuration parameter values from application.conf and converts them
* into appropriate scala types.
* */
object Parameters:
  private val logger = CreateLogger(classOf[Parameters.type])
  val config = ObtainConfigReference("randomLogGenerator") match {
    case Some(value) => value
    case None => throw new RuntimeException("Cannot obtain a reference to the config data.")
  }

  //Type match is used to dependently type configuration parameter values
  //based on the default input values of the specific config parameter.
  type ConfigType2Process[T] = T match
    case Int => Int
    case Long => Long
    case String => String
    case Double => Double
    case Tuple2[Double, Double] => Tuple2[Double, Double]

  //comparing double values should be done within certain precision
  private val COMPARETHREASHOLD = 0.00001d
  implicit private val comp: Ordering[Double] = new Ordering[Double] {
    def compare(x: Double, y: Double) =
      if math.abs(x - y) <= COMPARETHREASHOLD then 0 else if x - y > COMPARETHREASHOLD then -1 else 1
  }

  private def timeoutRange: Tuple2[Long, Long] =
    val lst = Try(config.getLongList(s"randomLogGenerator.TimePeriod").asScala.toList) match {
      case Success(value) => value.sorted
      case Failure(exception) => logger.error(s"No config parameter Timeout is provided")
        throw new IllegalArgumentException(s"No config data for Timeout")
    }
    if lst.length != 2 then throw new IllegalArgumentException(s"Incorrect range of values is specified for Timeout")
    (lst(0), lst(1))
  end timeoutRange


  //for config parameter likelihood ranges, e.g., error = [0.3, 0.1], they are obtained from the conf file
  //and then sorted in the ascending order
  private def logMsgRange(logTypeName: String): Tuple2[Double, Double] =
    val lst = Try(config.getDoubleList(s"randomLogGenerator.logMessageType.$logTypeName").asScala.toList) match {
      case Success(value) => value.sorted
      case Failure(exception) => logger.error(s"No config parameter is provided: $logTypeName")
        throw new IllegalArgumentException(s"No config data for $logTypeName")
    }
    if lst.length != 2 then throw new IllegalArgumentException(s"Incorrect range of values is specified for log $logTypeName")
    (lst(0), lst(1))
  end logMsgRange

  //It returns a function that takes the name of config entry and obtains the value of this entry if it exists
  //or it logs a warning message if it is absent and returns a default value
  private def func4Parameter[T](defaultVal: T, f: String => T): String => T =
    (pName: String) => Try(f(s"randomLogGenerator.$pName")) match {
      case Success(value) => value
      case Failure(exception) => logger.warn(s"No config parameter $pName is provided. Defaulting to $defaultVal")
        defaultVal
    }
  end func4Parameter

  //in this dependently typed function a typesafe config API method is invoked
  //whose name and return value corresponds to the type of the type parameter, T
  private def getParam[T](pName: String, defaultVal: T): ConfigType2Process[T] =
    defaultVal match {
      case v: Int => func4Parameter(v, config.getInt)(pName)
      case v: Long => func4Parameter(v, config.getLong)(pName)
      case v: String => func4Parameter(v, config.getString)(pName)
      case v: Double => func4Parameter(v, config.getDouble)(pName)
      case v: Tuple2[Double, Double] => logMsgRange(pName)
    }
  end getParam

  import scala.concurrent.duration.*

  private val MINSTRINGLENGTH = 10
  private val minStrLen = getParam("MinString", MINSTRINGLENGTH)

  //these vals are the public interface of this object, so that its
  //clients can obtain typed config parameter values
  val minStringLength: Int = if minStrLen < MINSTRINGLENGTH then
    logger.warn(s"Min string length is set to $MINSTRINGLENGTH")
    MINSTRINGLENGTH
  else minStrLen
  val maxStringLength = getParam("MaxString", 50)
  val minSymbol = getParam("MinSymbol", 32)
  val maxSymbol = getParam("MaxSymbol", 126)
  val generatingPattern = getParam("Pattern", "([a-c][e-g][0-3]|[A-Z][5-9][f-w]){5,15}")
  val patternFrequency = getParam("Frequency", 0.05d)
  val randomSeed = getParam("Seed", System.currentTimeMillis())
  val timePeriod = timeoutRange
  val maxCount = getParam("MaxCount", 0)
  val runDurationInMinutes: Duration = if Parameters.maxCount > 0 then Duration.Inf else getParam("DurationMinutes", 0).minutes

  if Parameters.maxCount > 0 then logger.warn(s"Max count ${Parameters.maxCount} is used to create records instead of timeouts")
  if timePeriod._1 < 0 || timePeriod._2 < 0 then throw new IllegalArgumentException("Timer period cannot be less than zero")

  //it is important to determine if likelihood ranges are not nested, otherwise
  //it would be difficult to make sense of the types of the generated log messages
  //if two types of messages have the same likehood of being generated, then this
  //likelihood conflict should be resolved. It can be, but then clients would have
  //hard time understanding why certain types of messages may appear more than other
  //types of messages, if it comes to that. It is better to inform the client about the overlap.
  private def checkForNestedRanges(input: ListMap[Tuple2[Double, Double], _]): Boolean =
    val overlaps = for {
      range1 <- input.keySet
      range2 <- input.keySet
      if (range1._1 >= range2._1 && range1._2 <= range2._2 && range1 != range2)
    } yield (range1, range2)
    if overlaps.toList.length > 0 then
      logger.error(s"Ranges of likelihoods overlap: $overlaps")
      true
    else
      false
  end checkForNestedRanges

  private val compFunc: (Tuple2[Double, Double], Tuple2[Double, Double]) => Boolean = (input1, input2) => (input1._1 <= input2._1) && (input1._2 < input2._2)
  val logRanges = ListMap(Map[Tuple2[Double, Double], String => Unit](
    getParam("info", (0.3d, 1d)) -> logger.info,
    getParam("error", (0d, 0.05d)) -> logger.error,
    getParam("warn", (0.05d, 0.15d)) -> logger.warn,
    getParam("debug", (0.15d, 0.3d)) -> logger.debug
  ).toSeq.sortWith((i1, i2) => compFunc(i1._1, i2._1)): _*)
  if checkForNestedRanges(logRanges) then throw new Exception("Overrlapping likelihood ranges will lead to the loss of precision.")
