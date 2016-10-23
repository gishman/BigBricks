package com.recipegrace.bigbricks.activiti

import net.liftweb.db.DB
import net.liftweb.util.Props
import org.activiti.engine.ProcessEngineConfiguration

/**
  * Created by Ferosh Jacob on 10/16/16.
  */
object ExecuteProcessTest extends App{




   val processEngine = ProcessEngineConfiguration
        .createProcessEngineConfigurationFromResource("/activiti.cfg.xml")
        .buildProcessEngine()
      val runtimeService = processEngine.getRuntimeService()
      val repositoryService = processEngine.getRepositoryService()
      repositoryService.createDeployment()
        .addClasspathResource("FinancialReportProcess.bpmn20.xml")
        .deploy()
     val process = runtimeService.startProcessInstanceByKey("financialReport")

}
