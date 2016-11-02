package com.homedepot.bigbricks.validation

import net.liftweb.common.{Full, Box}

/**
  * Created by Ferosh Jacob on 10/29/16.
  */
trait ProcessVariableValidation {

  val regex = "^[a-zA-Z_$][a-zA-Z_$0-9]*$"

  def goodPS(x:String):String= x match {
    case "" =>""
    case _ => {
      val parts =  x.split(",",-1).map(f=> f.trim)
      val inValidVariables =parts
        .filterNot(f=> {
          f.matches(regex)
        })
      if(inValidVariables.isEmpty){

        val distinct = parts.groupBy(f=>f).filter(f=> f._2.length >1)
        distinct.isEmpty match {
          case true => ""
          case _ => s"process variables repeated:'${distinct.keys.mkString(",")}'"
        }

      }else s"invalid process variables:'${inValidVariables.mkString(",")}'"

    }
  }

}
