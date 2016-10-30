package com.recipegrace.bigbricks.workflow

import org.activiti.engine.ProcessEngineConfiguration
import org.activiti.engine.repository.ProcessDefinition
import org.activiti.engine.runtime.ProcessInstance
import scala.collection.JavaConversions._
/**
  * Created by Ferosh Jacob on 10/21/16.
  */



object WorkflowWrapper  extends ActivitiToBigBricksConverters{


  val processEngine = ProcessEngineConfiguration
    .createProcessEngineConfigurationFromResource("/activiti.cfg.xml")
    .buildProcessEngine()
  val runtimeService = processEngine.getRuntimeService()
  val repositoryService = processEngine.getRepositoryService()
  val taskService = processEngine.getTaskService


  def countDefintions = {
    repositoryService.createProcessDefinitionQuery().active().list().size
  }

  def countActiveProcesses()= {
    runtimeService.createProcessInstanceQuery().active().count()
  }
  def listActiveProcesses(first:Int, max:Int)= {


    runtimeService.createProcessInstanceQuery().active().listPage(first,max).map(f=> InstanceToBBProcess(f))
  }
  def listActiveProcesses()= {


    runtimeService.createProcessInstanceQuery().active().list().map(f=> InstanceToBBProcess(f))
  }

  def InstanceToBBProcess(f: ProcessInstance): BBProcess = {

    val currrentTask = taskService.createTaskQuery().processInstanceId(f.getId).singleResult().getName
    BBProcess(f.getId, f.getName, f.getProcessDefinitionId, f.getProcessDefinitionName, currrentTask)

  }

  def deployProcess(fileName:String, content:String)= {
    val deployment =repositoryService.createDeployment().addString(fileName,content).deploy()
     deployment.getId
  }
  def countActiveProcessDefintions()= {
    repositoryService.createProcessDefinitionQuery().active().count()
  }
  def listActiveProcessDefinitions(first:Int, max:Int)= {
    repositoryService.createProcessDefinitionQuery().active().listPage(first,max).map(f=> definitionToBBProcessDefinition(f))
  }
  def listActiveProcessDefinitions()= {
    repositoryService.createProcessDefinitionQuery().active().list().map(f=> definitionToBBProcessDefinition(f))
  }

  def listTasks() = {
    taskService.createTaskQuery().active().list().map(f=>taskToBBTask(f))
  }

}
