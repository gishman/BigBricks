package code.snippet


import code.lib.BigBricksLogging
import com.recipegrace.bigbricks.workflow.WorkflowWrapper
import net.liftweb.common.{Empty, Box, Loggable, Full}
import net.liftweb.http.SHtml._
import net.liftweb.http.js.{JsCmds, JsCmd}
import net.liftweb.http._


import net.liftweb.common.{Box, Empty, Full, Loggable}
import net.liftweb.http.SHtml._
import net.liftweb.http.js.{JsCmds, JsCmd}
import net.liftweb.util.Helpers._

import scala.xml.NodeSeq

/**
  * Created by Ferosh Jacob on 10/21/16.
  */

object DeployProcess extends BigBricksLogging {


  def importContent(fileName:String,content: String, processVariables:String) = {
    val version= WorkflowWrapper.deployProcess(fileName,content)

  }

  def render = {
    var processVariables:Box[String]=Empty
    var upload: Box[FileParamHolder] = Empty

    def process() : JsCmd = {
      (upload,processVariables) match {
        case (Full(FileParamHolder(_, mimeType, fileName, file, )), Full(ps)) => {

          val definitionContent= new String(file, "iso-8859-1")
          if(goodPS) {
            importContent(fileName, definitionContent, ps)
            val message = s"${fileName} deployed"
            logAndDisplayMessage(LoggingInfo, message)
          }else {
            val message = s" invalid process variables: $ps"
            logAndDisplayMessage(LoggingWarn,message)
          }

        }


        case _ => logAndDisplayMessage(LoggingError,"file not found in the upload")
      }
      JsCmds.Noop
    }


    "#processvariables" #> text("",processVariables[_] ) &
    "#file" #> fileUpload(f => upload = Full(f)) &
      "type=submit" #> onSubmitUnit(process)

  }
}

class TaskRender {


  def list = {
    val page = WorkflowWrapper.listTasks()
    <table class="table table-condensed">
      <thread>
        <tr>

          <th>ID</th>
          <th>Task name</th>
          <th>Edit</th>
        </tr>
      </thread>
      <tbody>
        {page.flatMap(u => <tr>
        <td>
          {u.id}
        </td>
        <td>
          {u.name}
        </td>
        <td>
          {/*SHtml.link("product-type-details", () => {
            logAndDisplayMessage(ListType, Info, u.Id._1.toString, Mid, "Group: " + u.Id._1.toString + " selected")
            selectedProductTypeGroup.set(Full(u))
          }, <span class="glyphicon glyphicon-edit"></span>)
          }*/}
        </td>
      </tr>)}
      </tbody>
    </table>
  }
}
class ProcessRender  {


  def list = {
    val page = WorkflowWrapper.listActiveProcesses()
    <table class="table table-condensed">
      <thread>
        <tr>

          <th>ID</th>
          <th>Definition name</th>
          <th>Current state</th>
          <th>Edit</th>
        </tr>
      </thread>
      <tbody>
        {page.flatMap(u => <tr>
        <td>
          {u.id}
        </td>
        <td>
          {u.definitionName}
        </td>
        <td>
          {u.state}
        </td>
        <td>
          {/*SHtml.link("product-type-details", () => {
            logAndDisplayMessage(ListType, Info, u.Id._1.toString, Mid, "Group: " + u.Id._1.toString + " selected")
            selectedProductTypeGroup.set(Full(u))
          }, <span class="glyphicon glyphicon-edit"></span>)
          }*/}
        </td>
      </tr>)}
      </tbody>
    </table>
  }
}
object selectedProcessDefinition extends RequestVar[Box[String]](Empty)
class ProcessDefinitionRender  {

  def list = {
   val page =WorkflowWrapper.listActiveProcessDefinitions()
    <table class="table table-condensed">
      <thread>
        <tr>

          <th>ID</th>
          <th>Name</th>
          <th>Version</th>
          <th>Edit</th>
        </tr>
      </thread>
      <tbody>
        {page.flatMap(u => <tr>
        <td>
          {u.id}
        </td>
        <td>
          {u.name}
        </td>
        <td>
          {u.version}
        </td>
        <td>

          {/*SHtml.link("startprocess", () => {
          selectedProcessDefinition.set(Full(u.id))
          }, <span class="glyphicon glyphicon-edit"></span>)
          }*/}
        </td>
      </tr>)}
      </tbody>
    </table>
  }
}
