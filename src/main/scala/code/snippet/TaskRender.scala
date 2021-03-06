package code.snippet

import com.homedepot.bigbricks.ui.{BSLiftScreen, BigBricksLogging, HTMLCodeGenerator}

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


    def createOperations(x: BBTask) = {
      <td>
        {SHtml.link("completetask", () => {
        selectedTaskId.set(Full(x.id))
      }, <span class="glyphicon glyphicon-hand-right"></span>)}
      </td>
    }

    val page = WorkflowWrapper.listTasks()
    createTable[BBTask](page.toList,
      "ID" -> ((x: BBTask) => x.id),
      "Task Name" -> ((x: BBTask) => x.name),
      "Actions" -> createOperations _
    )
  }


}

class CompleteTask extends BSLiftScreen {

  val taskId = selectedTaskId.get match {
    case Full(s) => s
    case _ => {
      net.liftweb.http.S.redirectTo("list")
    }

  }
  val fields = WorkflowWrapper.listTaskVariables(taskId).map(f => {
    field(f._1, f._2.toString, "class" -> "form-control")
  }
  )


  def finish() {
    val variables = fields.map(f => f.displayName -> f.get).toMap
    WorkflowWrapper.completeTask(taskId, variables)
    logAndDisplayMessage(LoggingInfo, s"${taskId} completed! ")
  }

  override def submitButtonName: String = "Complete Task"
}
