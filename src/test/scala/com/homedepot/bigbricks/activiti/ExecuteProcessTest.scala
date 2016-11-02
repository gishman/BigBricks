package com.homedepot.bigbricks.activiti

import java.util

import com.homedepot.bigbricks.workflow.WorkflowWrapper
import net.liftweb.db.DB
import net.liftweb.util.Props
import org.activiti.engine.ProcessEngineConfiguration
import scala.collection.JavaConversions._
/**
  * Created by Ferosh Jacob on 10/16/16.
  */
object ExecuteProcessTest extends App{




   val processEngine = ProcessEngineConfiguration
        .createProcessEngineConfigurationFromResource("/activiti.cfg.xml")
        .buildProcessEngine()
      val runtimeService = processEngine.getRuntimeService()
    /*  val repositoryService = processEngine.getRepositoryService()
      repositoryService.createDeployment()
        .addClasspathResource("FinancialReportProcess.bpmn20.xml")
        .deploy()
  */
 processEngine.getRepositoryService.createProcessDefinitionQuery().list().map(f=> f.getId).foreach(println)


      val map = new util.HashMap[String,AnyRef]()
      map.put("a","1")
     val process = runtimeService.startProcessInstanceById("financialReport:2:15003",map)
     runtimeService.setVariable(process.getId,"hola","sdd")

     val vars= WorkflowWrapper.listProcessVariables(process.getId)

     println(vars.size)

}
