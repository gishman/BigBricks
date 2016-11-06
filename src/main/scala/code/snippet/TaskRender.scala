package code.snippet

import com.homedepot.bigbricks.ui.{BigBricksLogging, HTMLCodeGenerator}

import com.homedepot.bigbricks.workflow.WorkflowWrapper
import net.liftweb.common.{Empty, Box, Full}
import net.liftweb.http.{LiftScreen, RequestVar, SHtml, S}
import WorkflowWrapper.BBTask
import scala.xml.NodeSeq

/**
  * Created by Ferosh Jacob on 11/3/16.
  */
object selectedTaskId extends RequestVar[Box[String]](Empty)
class TaskRender extends HTMLCodeGenerator with BigBricksLogging {

  def list = {


    def createOperations(x:BBTask) = {
      <td>
        {SHtml.link("completetask", () => {
        selectedTaskId.set(Full(x.id))
      }, <span class="glyphicon glyphicon-hand-right"></span>)}
      </td>
    }

    val page = WorkflowWrapper.listTasks()
    createTable[BBTask](page.toList,
      "ID" -> ((x:BBTask)=> x.id),
      "Task Name" -> ((x:BBTask)=> x.name),
      "Actions" -> createOperations _
    )
  }



}
class CompleteTask extends LiftScreen  with BigBricksLogging{

  val taskId = selectedTaskId.get match {
    case Full(s) => s
    case _ => {
      net.liftweb.http.S.redirectTo("list")
    }

  }
  val fields = WorkflowWrapper.listTaskVariables(taskId).map(f=> {
    field(f._1, f._2.toString, "class"->"form-control")
  }
  )


  def finish() {
    val variables =fields.map(f=> f.displayName -> f.get).toMap
    WorkflowWrapper.completeTask(taskId,variables)
    logAndDisplayMessage(LoggingInfo,  s"${taskId} completed! ")
  }

  override def finishButton = <button class="btn btn-default btn-primary" >Complete task</button>
  override def cancelButton = <button class="btn btn-default btn-primary" >Cancel</button>

  override def formName: String = "sample"


  override def defaultFieldNodeSeq: NodeSeq =
    <div class="form-group">
      <label class="label field"></label>
      <span class="value fieldValue"></span>
      <span class="help"></span>
      <div class="errors">
        <div class="error"></div>
      </div>
    </div>
}
