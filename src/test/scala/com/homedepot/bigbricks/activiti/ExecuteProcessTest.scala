package com.homedepot.bigbricks.activiti

import java.util

import com.homedepot.bigbricks.workflow.WorkflowWrapper
import net.liftweb.db.DB
import net.liftweb.util.Props
import org.activiti.engine.ProcessEngineConfiguration
import scala.collection.JavaConversions._
import scala.io.Source

/**
  * Created by Ferosh Jacob on 10/16/16.
  */
object ExecuteProcessTest extends App {


  val processEngine = ProcessEngineConfiguration
    .createProcessEngineConfigurationFromResource("/activiti.cfg.xml")
    .buildProcessEngine()
  val runtimeService = processEngine.getRuntimeService()
  val repositoryService = processEngine.getRepositoryService()
  //val content = Source.fromFile("files/Submitdataprocexample.bpmn20.xml").getLines().mkString
  //val processFile ="ExampleProcess.bpmn20.xml"
  val processFile = "TwoDelete.bpmn20.xml"
  val content = Source.fromFile("files/" + processFile).getLines().mkString
  val deployment = repositoryService.createDeployment()
    .addString(processFile, content)
    .deploy()

  processEngine.getRepositoryService.createProcessDefinitionQuery().list().map(f => f.getId).foreach(println)
  runtimeService.startProcessInstanceByKey("process7")

  System.exit(0)
}

