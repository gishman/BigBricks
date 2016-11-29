package com.homedepot.bigbricks.ui

import net.liftweb.http.LiftScreen

import scala.xml.NodeSeq

/**
  * Created by Ferosh Jacob on 11/28/16.
  */
abstract class BSLiftScreen extends LiftScreen with BigBricksLogging {

  def submitButtonName:String

  override def cancelButton = <button class="btn btn-default btn-primary">Cancel</button>

  override def finishButton = <button class="btn btn-default btn-primary">{submitButtonName}</button>

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
