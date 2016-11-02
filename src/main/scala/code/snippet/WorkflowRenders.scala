package code.snippet

import code.model.Process
import com.homedepot.bigbricks.ui.BigBricksLogging
import com.homedepot.bigbricks.validation.ProcessVariableValidation
import com.homedepot.bigbricks.workflow.WorkflowWrapper
import net.liftweb.common.{Box, Empty, Full}
import net.liftweb.http.SHtml._
import net.liftweb.http._
import net.liftweb.http.js.{JsCmd, JsCmds}
import net.liftweb.util.Helpers._

/**
  * Created by Ferosh Jacob on 10/21/16.
  */


class TaskRender {


  def list = {
    val page = WorkflowWrapper.listTasks()
        page.flatMap(u => <tr>
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
      </tr>)
  }
}


