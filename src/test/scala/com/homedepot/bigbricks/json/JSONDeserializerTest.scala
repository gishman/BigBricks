package com.homedepot.bigbricks.json

import code.model.Project
import com.recipegrace.biglibrary.core.BaseTest
import net.liftweb.json.Extraction._
import net.liftweb.json.{DefaultFormats, _}
/**
 * Created by fjacob on 8/16/15.
 */
object JSONDeserializerTest extends BaseTest{
  implicit val formats = DefaultFormats

test(" json test"){
      val project:Project = Project.create.projectName("projectName")
       render(decompose(project)) shouldEqual project.projectName.get

}

}
