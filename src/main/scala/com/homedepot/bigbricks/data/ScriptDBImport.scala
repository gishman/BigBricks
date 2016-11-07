package com.homedepot.bigbricks.data

import net.liftweb.json._

/**
  * Created by fjacob on 8/2/15.
  */
object ScriptDBImport {

  implicit val formats = DefaultFormats

  def get(json: JValue, nodes: String*) = {
    nodes.map(f => (json \ f).values)

  }

  def extractJobs(jobsText: String): List[ScriptDBJob] = {
    for (job <- parse(jobsText).asInstanceOf[JArray].children)
      yield getJob(job)
  }

  def getJob(json: JValue, nodes: String*): ScriptDBJob = {
    json.extract[ScriptDBJob]

  }

  def extractTemplates(templateText: String): List[ScriptDBTemplate] = {
    for (template <- parse(templateText).asInstanceOf[JArray].children)
      yield getTemplate(template)
  }

  def getTemplate(json: JValue, nodes: String*): ScriptDBTemplate = {
    json.extract[ScriptDBTemplate]

  }

  def extractProjects(projectsText: String): List[ScriptDBProject] = {

    for (project <- parse(projectsText).asInstanceOf[JArray].children)
      yield getProject(project)


  }

  def getProject(json: JValue, nodes: String*): ScriptDBProject = {
    json.extract[ScriptDBProject]

  }

  case class ScriptDBTemplate(templateName: String, template: String)

  case class ScriptDBProject(projectLocation: String, projectName: String, jarName: String)

  case class ScriptDBJob(projectName: String, templateName: String, mainClassName: String,
                         programArguments: Map[String, String], vmArguments: Map[String, String])
}
