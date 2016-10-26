package code.model

import net.liftweb.mapper._
import code.lib.BootstrapCodeGenerator._
/**
  * The singleton that has methods for accessing the database
  */
object ProcessVariables extends ProcessVariables with LongKeyedMetaMapper[ProcessVariables] {


  override def fieldOrder = List(processVariablesName,processName,version)
  override def dbTableName = "processVariables"

  formatFormElement = bsformFormElement






}

class ProcessVariables extends LongKeyedMapper[ProcessVariables]  with IdPK {
  def getSingleton = ProcessVariables // what's the "meta" server
  object processVariablesName extends MappedString(this, 100) {

    override def displayName = "Process variables name"
    override def toForm = addClassAttribute(super.toForm)


  }


  object processName extends MappedString(this, 25) {
    override def toForm = addClassAttribute(super.toForm)
    override def displayName = "process name"
  }
  object version extends MappedInt(this) {
    override def toForm = addClassAttribute(super.toForm)

  }



}




