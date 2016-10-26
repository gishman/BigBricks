package code.snippet

import net.liftweb.http.LiftScreen
import net.liftweb.http.S

import scala.xml.NodeSeq

/**
  * Create by Ferosh Jacob on 10/25/16
  */

object Sample extends LiftScreen {

  val fields = (0 to 10).map(f=> {
    field("Name"+f, "", "class"->"form-control")
  }
  )


  def finish() {
    S.notice("I like  too!")
  }

  override def finishButton = <button class="btn btn-default btn-primary" >Review</button>
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
