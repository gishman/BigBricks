package com.homedepot.bigbricks.data

import code.model.Process
import com.homedepot.bigbricks.ui.DeployWorkflow
import net.liftweb.json.Extraction._
import net.liftweb.json._

/**
  * Created by fjacob on 8/21/15.
  */


trait BBCData extends DeployWorkflow {


  def exportData = {
    implicit val formats = DefaultFormats
    val processes= Process.findAll().map(f=> f.bbc.get)
    decompose(processes)
  }


  def importData (content:String) = {
    implicit val formats = DefaultFormats

   val bccs=  parse(content).extract[List[String]]




    bccs.map(deployBBCBatch)

    logAndDisplayMessage(LoggingInfo, s"${bccs.size} workflows deployed")}


}
