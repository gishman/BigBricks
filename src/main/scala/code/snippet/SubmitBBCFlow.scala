package code.snippet

import com.homedepot.bbc.Main
import com.homedepot.bigbricks.ui.{DeployWorkflow, BSLiftScreen, BigBricksLogging}
import net.liftweb.util.FieldError

/**
  * Created by Ferosh Jacob on 11/28/16.
  */
class SubmitBBCFlow extends BSLiftScreen with DeployWorkflow{
  override def submitButtonName: String = "Submit BBC Flow"

  val bbc = textarea("BBC","", validBBC _, "class" -> "form-control", "rows" -> "25", "style"->"font-family:monospace;")
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
