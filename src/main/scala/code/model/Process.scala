package code.model

import com.homedepot.bigbricks.ui.HTMLCodeGenerator
import net.liftweb.mapper._
/**
  * The singleton that has methods for accessing the database
  */
object Process extends Process with LongKeyedMetaMapper[Process] {


  override def fieldOrder = List(processVariablesName,processName,deployementId)

}

class Process extends LongKeyedMapper[Process]  with IdPK  with HTMLCodeGenerator{
  def getSingleton = Process

  def getProcessVariables = {
    processVariablesName.get.split(",")
  }
  // what's the "meta" server
  object processVariablesName extends MappedString(this, 250) {
    override def displayName = "Process variables name"

    override def toForm = addClassAttribute(super.toForm)
  }

  object processName extends MappedString(this, 100) {
    override def displayName = "Process name"

    override def toForm = addClassAttribute(super.toForm)

  }

  object deployementId extends MappedString(this, 25) {
    override def displayName = "Deployement Id"
    override def toForm = addClassAttribute(super.toForm)
  }

}




