package com.homedepot.bigbricks.ui

import net.liftweb.common.Loggable
import net.liftweb.http.S

/**
  * Created by Ferosh Jacob on 10/21/16.
  */
trait BigBricksLogging extends Loggable {

  def logAndDisplayMessage(logType: LoggingType, message: String) = {
    logType match {
      case LoggingInfo => {
        S.notice(message)
        logger.info(message)
      }
      case LoggingWarn => {
        S.warning(message)
        logger.warn(message)
      }
      case LoggingError => {
        S.error(message)
        logger.error(message)
      }
    }
  }

  sealed trait LoggingType

  case object LoggingInfo extends LoggingType

  case object LoggingWarn extends LoggingType

  case object LoggingError extends LoggingType
}
