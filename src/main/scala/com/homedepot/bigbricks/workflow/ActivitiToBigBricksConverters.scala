package com.homedepot.bigbricks.workflow

import org.activiti.engine.history.HistoricProcessInstance
import org.activiti.engine.task.Task
import org.activiti.engine.repository.ProcessDefinition

/**
  * Created by Ferosh Jacob on 10/22/16.
  */
trait ActivitiToBigBricksConverters extends BigBricksWorkflowComponents {

  def definitionToBBProcessDefinition(f: ProcessDefinition): BBProcessDefintiion = {
    BBProcessDefintiion(f.getId, f.getName, f.getVersion)
  }

  def taskToBBTask(f: Task): BBTask = {
    BBTask(f.getId, f.getName)
  }

  def processToBBProcess(f: HistoricProcessInstance,definitionName:String) = {
    if (f.getEndTime != null) {
      BBProcess(f.getId, f.getName, f.getProcessDefinitionId, definitionName, "Finished", f.getStartTime.toString, f.getEndTime.toString)
    }
    else

      BBProcess(f.getId, f.getName, f.getProcessDefinitionId, "", "")
  }
}
