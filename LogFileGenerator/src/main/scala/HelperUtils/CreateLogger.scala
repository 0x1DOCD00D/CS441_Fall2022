/*
 *
 *  Copyright (c) 2021. Mark Grechanik and Lone Star Consulting, Inc. All rights reserved.
 *
 *   Unless required by applicable law or agreed to in writing, software distributed under
 *   the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *   either express or implied.  See the License for the specific language governing permissions and limitations under the License.
 *
 */
package HelperUtils

import org.slf4j.{Logger, LoggerFactory}

import scala.util.{Failure, Success, Try}

object CreateLogger:
  def apply[T](class4Logger: Class[T]):Logger =
    val LOGBACKXML = "logback.xml"
    val logger = LoggerFactory.getLogger(class4Logger)
    Try(getClass.getClassLoader.getResourceAsStream(LOGBACKXML)) match {
      case Failure(exception) => logger.error(s"Failed to locate $LOGBACKXML for reason $exception")
      case Success(inStream) => inStream.close()
    }
    logger
