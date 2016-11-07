package com.homedepot.bigbricks.workflow

import org.activiti.engine.ProcessEngineConfiguration
import org.activiti.engine.runtime.ProcessInstance
import scala.collection.JavaConversions._
import scala.collection.immutable.HashMap

/**
  * Created by Ferosh Jacob on 10/21/16.
  */


object WorkflowWrapper extends ActivitiToBigBricksConverters {


  val processEngine = ProcessEngineConfiguration
    .createProcessEngineConfigurationFromResource("/activiti.cfg.xml")
    .buildProcessEngine()
  val runtimeService = processEngine.getRuntimeService()
  val historyService = processEngine.getHistoryService
  val repositoryService = processEngine.getRepositoryService()
  val taskService = processEngine.getTaskService


  def countDefintions = {
    repositoryService.createProcessDefinitionQuery().active().list().size
  }

  def countActiveProcesses() = {
    runtimeService.createProcessInstanceQuery().active().count()
  }

  def listActiveProcesses(first: Int, max: Int) = {


    runtimeService.createProcessInstanceQuery().active().listPage(first, max).map(f => InstanceToBBProcess(f))
  }

  def InstanceToBBProcess(f: ProcessInstance): BBProcess = {

    val currrentTask = taskService.createTaskQuery().processInstanceId(f.getId).singleResult()
    val taskName = if (currrentTask == null) "" else currrentTask.getName
    BBProcess(f.getId, f.getName, f.getProcessDefinitionId, f.getProcessDefinitionName, taskName)

  }

  def startProcess(deploymentId: String, variables: Map[String, String] = new HashMap[String, String]()) = {
    val definition = repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId).singleResult()
    runtimeService.startProcessInstanceById(definition.getId, variables)
  }

  def deployProcess(fileName: String, content: String) = {
    val deployment = repositoryService.createDeployment().addString(fileName, content).deploy()
    deployment.getId
  }

  def deleteDeployment(deploymentId: String) = {
    repositoryService.deleteDeployment(deploymentId)
  }

  def countActiveProcessDefintions() = {
    repositoryService.createProcessDefinitionQuery().active().count()
  }

  def listActiveProcessDefinitions(first: Int, max: Int) = {
    repositoryService.createProcessDefinitionQuery().active().listPage(first, max).map(f => definitionToBBProcessDefinition(f))
  }

  def listActiveProcessDefinitions() = {
    repositoryService.createProcessDefinitionQuery().active().list().map(f => definitionToBBProcessDefinition(f))
  }

  def listProcesses(status: String): List[BBProcess] = {
    status match {
      case "Finished" => listFinishedProcesses()
      case _ => listActiveProcesses()
    }
  }

  def listFinishedProcesses(): List[BBProcess] = {
    historyService.createHistoricProcessInstanceQuery()
      .list().map(processToBBProcess).toList
  }

  def listActiveProcesses() = {


    runtimeService.createProcessInstanceQuery().active().list().map(f => InstanceToBBProcess(f)).toList
  }

  def listTasks() = {
    taskService.createTaskQuery().active().list().map(f => taskToBBTask(f))
  }

  def listTaskVariables(taskID: String) = {
    taskService.getVariables(taskID).toList
  }

  def completeTask(taskID: String, variables: Map[String, AnyRef]) = {
    taskService.complete(taskID, variables)
  }

  def listProcessVariables(processInstanceId: String, status: String): List[(String, AnyRef)] = {
    status match {
      case "Finished" => historyService.createHistoricProcessInstanceQuery()
        .includeProcessVariables().processInstanceId(processInstanceId).singleResult().getProcessVariables.toList
      case _ => runtimeService.getVariables(processInstanceId).toList.toList
    }

  }

}
