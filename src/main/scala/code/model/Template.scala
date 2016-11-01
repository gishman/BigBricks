package code.model

import com.recipegrace.bigbricks.ui.HTMLCodeGenerator
import net.liftweb.mapper._

/**
 * The singleton that has methods for accessing the database
 */
object Template extends Template with LongKeyedMetaMapper[Template] {

  override def fieldOrder = List(templateName,template)
  formatFormElement = bsformFormElement
}

class Template extends LongKeyedMapper[Template]  with IdPK  with HTMLCodeGenerator{
  def getSingleton = Template // what's the "meta" server
  object templateName extends MappedString(this, 50){

    override def displayName = "Template name"
    override def toForm = addClassAttribute(super.toForm)

  }
  // define an additional field for a personal essay
  object template extends MappedTextarea(this, 2048) {
    override def textareaRows  = 20
    override def textareaCols = 100
    override def displayName = "Template"
    override def toForm = addClassAttribute(super.toForm)

  }


}



