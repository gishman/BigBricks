package code.snippet


import com.homedepot.bigbricks.ui.{BigBricksLogging, HTMLCodeGenerator}
import com.homedepot.bigbricks.workflow.WorkflowWrapper
import WorkflowWrapper.BBProcess
import net.liftweb.common.{Box, Empty, Full}
import net.liftweb.http.js.JsCmds.RedirectTo
import net.liftweb.http.{SHtml, SessionVar, RequestVar, S}
import net.liftweb.http.js.{JsCmds, JsCmd}
import net.liftweb.util.Helpers._
import net.liftweb.http.SHtml._

import scala.xml.NodeSeq


/**
  * Created by Ferosh Jacob on 10/31/16.
  */
object selectedProcessId extends RequestVar[Box[String]](Empty)

object currentProcessesType extends SessionVar[Box[String]](Empty)

class ProcessInstanceRender extends HTMLCodeGenerator with BigBricksLogging {
  val statuses = Seq("Current", "Finished").map(f => (f, f))
  val currentStatus = currentProcessesType.get
  val homepage = "processinstances"

  def selectProcessStatus = {

    "#processStatus" #> ajaxSelect(statuses, currentStatus, { (s: String) => replace(s) }, "class" -> "form-control")
  }

  private def replace(status: String): JsCmd = {
    currentProcessesType.set(Full(status))
    logAndDisplayMessage(LoggingInfo, "Selected status to" + status)
    RedirectTo(homepage, () => {
      S.notice(" Project changed to: " + status)
    })

  }

  def list = {


    def createOperations(x: BBProcess) = {
      <td>
        {SHtml.link("processdetails", () => {
        selectedProcessId.set(Full(x.id))
      }, <span class="glyphicon glyphicon-eye-open"></span>)}
      </td>
    }

    val page = WorkflowWrapper.listProcesses(currentStatus.getOrElse(""))
    createTable[BBProcess](page,
      "ID" -> ((x: BBProcess) => x.definitionId),
//      "Process Name" -> ((x: BBProcess) => x.name), Looks it is empty
      "Definition Name" -> ((x: BBProcess) => x.definitionName),
      "Process ID" -> ((x: BBProcess) => x.id),
      "State" -> ((x: BBProcess) => x.state),
      "Create time" -> ((x: BBProcess) => x.startTime),
      "End time" -> ((x: BBProcess) => x.endTime),
      "Actions" -> createOperations _
    )
  }

  def details: NodeSeq =
    selectedProcessId.get match {
      case Full(id) => {
        val variables = WorkflowWrapper.listProcessVariables(id, currentStatus.getOrElse(""))
        variables match {

          case List() => "No variables found!"
          case list: List[(String, AnyRef)] => createTable[(String, AnyRef)](list,
            "Name" -> ((x: (String, AnyRef)) => x._1),
            "Value" -> ((x: (String, AnyRef)) => x._2.toString)
          )

        }
      }
      case _ => <b>"Ooops no process found"</b>
    }


}
