package code.snippet


import com.recipegrace.bigbricks.ui.{BigBricksLogging, HTMLCodeGenerator}

import com.recipegrace.bigbricks.workflow.{WorkflowWrapper}
import com.recipegrace.bigbricks.workflow.WorkflowWrapper.BBProcess
import net.liftweb.common.{Box, Empty, Full}
import net.liftweb.http.js.JsCmds.RedirectTo
import net.liftweb.http.{SHtml, SessionVar, RequestVar, S}
import net.liftweb.http.js.{JsCmds, JsCmd}
import net.liftweb.util.Helpers._
import net.liftweb.http.SHtml._


/**
  * Created by Ferosh Jacob on 10/31/16.
  */
object selectedProcessId extends RequestVar[Box[String]](Empty)
object currentProcessesType extends SessionVar[Box[String]](Empty)
class ProcessInstanceRender extends HTMLCodeGenerator with  BigBricksLogging   {
  val statuses = Seq("Current", "Finished").map(f=> (f,f))
  val currentStatus =  currentProcessesType.get

  private def replace(status: String): JsCmd = {
    currentProcessesType.set(Full(status))
    logAndDisplayMessage(LoggingInfo, "Selected status to" + status)
    RedirectTo("processinstances", () => {
      S.notice(" Project changed to: " + status)
    })

  }


  def selectProcessStatus = {

    "#processStatus" #> ajaxSelect(statuses, currentStatus, { (s:String) =>replace(s)}, "class" -> "form-control")
  }
    def list = {


      def createOperations(x:BBProcess) = {
        <td>
          {SHtml.link("processdetails", () => {
          selectedProcessId.set(Full(x.id))
        }, <span class="glyphicon glyphicon-eye-open"></span>)}
        </td>
      }

      val page = WorkflowWrapper.listProcesses(currentStatus.getOrElse(""))
      createTable[BBProcess](page.toList,
        "ID" -> ((x:BBProcess)=> x.definitionId),
      "Process Name" -> ((x:BBProcess)=> x.definitionName),
        "Process ID" -> ((x:BBProcess)=> x.id),
        "State" -> ((x:BBProcess)=> x.state),
        "Actions" -> createOperations _
      )
    }
}
