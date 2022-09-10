/*
 *
 *  Copyright (c) 2021. Mark Grechanik and Lone Star Consulting, Inc. All rights reserved.
 *
 *   Unless required by applicable law or agreed to in writing, software distributed under
 *   the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *   either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 *
 */
package Generation

import HelperUtils.{CreateLogger, Parameters}

import java.util.Timer
import RSGStateMachine.{RSGFunction, map}

import scala.concurrent.duration.Duration
import util.Random.*
import scala.util.{Success, Try}

//this is a factory for producing an instance of the simulator
object LogMsgSimulator:
  import collection.immutable.ListMap
  val logger = CreateLogger(classOf[LogMsgSimulator])

  def apply(initstate: Tuple2[RandomStringGenerator, String], maxCount: Long = 0) = (new LogMsgSimulator(initstate)).ProduceLogMessage(initstate, maxCount)

class LogMsgSimulator(val initstate:Tuple2[RandomStringGenerator,String]):
  import LogMsgSimulator.*

  val randVals = scala.util.Random(Parameters.randomSeed)

  import scala.annotation.tailrec
  @tailrec private def ProduceLogMessage(inputState: Tuple2[RandomStringGenerator,String], counter: Long, useCounter:Boolean = true): Tuple2[RandomStringGenerator,String] =
    if Parameters.maxCount > 0 && counter <= 0 then inputState
    else
      if Parameters.timePeriod._1 < Parameters.timePeriod._2 then Thread.sleep(scala.util.Random.between(Parameters.timePeriod._1, Parameters.timePeriod._2))
      else Thread.sleep(Parameters.timePeriod._2)
      val nextState = map(rsg => inputState._1.next)(x => {
        val rv = randVals.nextFloat()
        Parameters.logRanges.filterNot((k, v) => (k._1 > rv || k._2 < rv)).toList.headOption match {
          case Some(entry) => entry._2(x)
          case None => logger.info(x)
        }
        x
      })(inputState._1)
      ProduceLogMessage(nextState, counter - 1, useCounter)
