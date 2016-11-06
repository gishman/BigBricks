package com.homedepot.bigbricks.workflow

/**
  * Created by Ferosh Jacob on 10/22/16.
  */
trait BigBricksWorkflowComponents {

  case class BBProcess(id:String, name:String, definitionId:String,definitionName:String,  state:String, startTime:String="",endTime:String="")
  case class BBProcessDefintiion(id:String,name:String, version:Int)
  case class BBTask(id:String, name:String)
}
