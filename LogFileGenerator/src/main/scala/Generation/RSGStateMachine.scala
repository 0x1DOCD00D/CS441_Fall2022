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


import HelperUtils.Parameters
import HelperUtils.Parameters.logger
import com.mifmif.common.regex.Generex

import scala.util.Random

//this is a workhorse of the random string generator - it generates instances of the
//regex pattern and inserts them at random intervals into the generated strings.
case class RandomStringGenerator(val lengthRange: Tuple2[Int, Int], val seed: Long) {
  private val rg = new Random(seed)
  val generex: Generex = new Generex(Parameters.generatingPattern)

  private def constructString(inp:String, len: Int): String =
    if len <= 0 then inp else constructString(inp+rg.nextPrintableChar, len - 1)

  def  next: Tuple2[RandomStringGenerator, String] =
    val length =  rg.nextInt(lengthRange._2) + lengthRange._1
    val patternString = constructString(rg.nextPrintableChar().toString, length/2).concat(if rg.nextFloat() < Parameters.patternFrequency then generex.random() else "")
    val resultString = patternString + constructString(rg.nextPrintableChar().toString, length/2)
    (RandomStringGenerator(lengthRange, if (resultString.hashCode > seed) resultString.hashCode - seed else resultString.hashCode + seed), resultString)
}

//A monadic implementation of the state machine - will use CATS later - it takes a function
//that produces the updated generator and a random string with the implementation of unit
//and map functions.
object RSGStateMachine {
  type RSGFunction = RandomStringGenerator => Tuple2[RandomStringGenerator, String]

  def unit(str: String): RSGFunction = rsg => rsg.next

  def map(state: RSGFunction)(logThisString: String => String): RSGFunction = {
    rsg => {
      val (rsgNext, strValue) = state(rsg)
      (rsgNext, logThisString(strValue))
    }
  }
}