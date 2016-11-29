package code.snippet

import code.model.Process
import com.homedepot.bbc.Main
import com.homedepot.bigbricks.ui.{BSLiftScreen, DeployWorkflow, BigBricksLogging, HTMLCodeGenerator}
import com.homedepot.bigbricks.validation.ProcessVariableValidation
import com.homedepot.bigbricks.workflow.WorkflowWrapper
import net.liftweb.common.{Full, Empty, Box}
import net.liftweb.http.S._
import net.liftweb.http.SHtml._
import net.liftweb.http.js.{JsCmds, JsCmd}
import net.liftweb.http._
import net.liftweb.mapper.{MaxRows, StartAt}
import net.liftweb.util.Helpers._
import net.liftweb.util.{FieldError, Schedule}

import scala.xml.NodeSeq


/**
  * Created by Ferosh Jacob on 10/30/16.
  */


object selectedBigBricksProcessDefinition extends RequestVar[Box[Process]](Empty)


class ProcessDefinitionRender extends   ProcessVariableValidation with HTMLCodeGenerator with DeployWorkflow{

  val homePage = "list.html"

  def deployProcess = {


    var processVariables: Box[String] = Empty
    var upload: Box[FileParamHolder] = Empty
    var isBBC:Box[Boolean]=Empty

    def process(): JsCmd = {

      for {
        fileUpload <- upload
        processVars <- processVariables
      } yield {
        val definitionContent = new String(fileUpload.file, "iso-8859-1")
        goodPS(processVars) match {
          case "" => {
            val fileName =fileUpload.fileName
            deployProcessContent(processVars, definitionContent, fileName,isBBC)
            return net.liftweb.http.S.redirectTo(homePage)
          }
          case x: String => {
            logAndDisplayMessage(LoggingError, x)

          }
        }
      }
      JsCmds.Noop

    }


    "#processvariables" #> text("", f => processVariables = Full(f)) &
      "#file" #> fileUpload(f => upload = Full(f)) &
      "#isBBC" #> checkbox(true,f=> isBBC=Full(f)) &
      "type=submit" #> onSubmitUnit(process)

  }



  def confirmDelete = {

    (for (process <- selectedBigBricksProcessDefinition.is) // find the process
      yield {
        def deleteProcess() {
          val mesage = s"${process.processName.get} deleted"
          WorkflowWrapper.deleteDeployment(process.deployementId.get)
          process.delete_!
          net.liftweb.http.S.redirectTo(homePage)
          logAndDisplayMessage(LoggingInfo, mesage)
        }
        ".process" #> process.processName.get &
          ".delete" #> submit("Delete", deleteProcess, "class" -> "btn btn-primary")

      }) openOr {
      logAndDisplayMessage(LoggingError, "Process not found")
      net.liftweb.http.S.redirectTo(homePage)
    }
  }

  def list = {
    val page = Process.findAll(StartAt(0), MaxRows(20))

    def createOperations(x: Process) = {
      <td>
        {SHtml.link("edit", () => { selectedBigBricksProcessDefinition.set(Full(x)) }, <span class="glyphicon glyphicon-edit"></span>)}
        {SHtml.link("delete", () => { selectedBigBricksProcessDefinition.set(Full(x)) }, <span class="glyphicon glyphicon-remove"></span>)}
        {SHtml.link("start", () => { selectedBigBricksProcessDefinition.set(Full(x)) }, <span class="glyphicon glyphicon-play-circle"></span>)}
      </td>
    }

    createTable[Process](page,
      "ID" -> ((x: Process) => x.id.toString()),
      Process.processName.displayName -> ((x: Process) => x.processName.get),
      Process.deployementId.displayName -> ((x: Process) => x.deployementId.get),
      Process.processVariablesName.displayName -> ((x: Process) => x.processVariablesName.get),
      "Actions" -> createOperations _
    )

  }
}
class SubmitBBCFlow extends BSLiftScreen with DeployWorkflow{
  override def submitButtonName: String = "Submit BBC Flow"

  val content = selectedBigBricksProcessDefinition.get match {
    case Full(x) => x.bbc.get
    case _ =>""

  }
  val bbc = textarea("BBC",content, validBBC _, "class" -> "form-control", "rows" -> "25", "style"->"font-family:monospace;")
  def validBBC(bbc:String):List[FieldError] = {

    Main.generateProcess(bbc) match {
      case None => Main.errorMessage
      case _ => Nil
    }
  }
  override protected def finish(): Unit = {
    deployBBC("", bbc.get)
  }
}
class StartProcess extends BSLiftScreen{

  val process = selectedBigBricksProcessDefinition.get match {
    case Full(s) => s
    case _ => {
      net.liftweb.http.S.redirectTo("list")
    }

  }
  val fields = process.getProcessVariables.map(f => {
    field(f, "", "class" -> "form-control")
  }
  )


  def finish() {
    val variables = fields.map(f => f.displayName -> f.get).toMap
    Schedule(() => {
      WorkflowWrapper.startProcess(process.deployementId.get, variables)
      logAndDisplayMessage(LoggingInfo, s"${process.processName.get} finished! ")
    })
    logAndDisplayMessage(LoggingInfo, s"${process.processName.get} started! ")
  }

  override def submitButtonName: String = "Start Process"
}