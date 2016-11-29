package com.homedepot.bigbricks.ui

import code.model.Process
import com.homedepot.bbc.Main
import com.homedepot.bigbricks.workflow.WorkflowWrapper
import net.liftweb.common.{Full, Box}

/**
  * Created by Ferosh Jacob on 11/25/16.
  */
trait DeployWorkflow extends BigBricksLogging{


  def deployProcessContent(processVars: String, definitionContent: String, fileName: String, isBBC:Box[Boolean]): Unit = {
    val message = s"$fileName deployed"
   isBBC match {
      case Full(false)=> {
        logger.info("Activiti file deploying..")
        val deployId=WorkflowWrapper.deployProcess(fileName, definitionContent)
        Process.create.processName(fileName).processVariablesName(processVars).deployementId(deployId).save()
        logAndDisplayMessage(LoggingInfo, message)
      }
      case _ => {
        deployBBC(processVars, definitionContent)

      }
    }


  }

  def deployBBC(processVars: String, definitionContent: String): Unit = {
    logger.info("BBC file deploying..")
    Main.generateProcess(definitionContent) match {
      case Some(x) => {
        val name:String = (x  \\ "process" \"@name").text
        val deployId = WorkflowWrapper.deployProcess(toBPMN20File(name), x.toString())
        val message = s"$name deployed"
        Process.create.processName(toBPMN20File(name)).processVariablesName(processVars).deployementId(deployId).save()
        logAndDisplayMessage(LoggingInfo, message)
      }
      case None => {
        logAndDisplayMessage(LoggingError, Main.errorMessage)
        None
      }
    }
  }

  def toBPMN20File(fileName:String) = {
    fileName+".bpmn20.xml"
  }
}
