package com.homedepot.bigbricks.ui


import com.homedepot.bbd.gcloud.DataProcOperations
import net.liftweb.actor.LiftActor
import net.liftweb.util.Schedule
import net.liftweb.util.Helpers._
import scala.collection.JavaConversions._
/**
  * Created by Ferosh Jacob on 11/28/16.
  */
object ClusterRunningCheck extends BigBricksLogging {
  object ClusterActor extends LiftActor  {

    case class Check()

    val dataProcOperations = new DataProcOperations

    def longRunningClusters(projectName:String) = {

     dataProcOperations.listClusterNamesAndDuration(projectName,"global")
        .toList.filter(f=> f._2 > 100)
        .map(f=> s" * ${f._1} (duration ${"%.2f".format(f._2)} mins) [$projectName]").mkString("\n")



    }

    def checkClustersRunning() = {

      logger.info("running cluster checking...")
      longRunningClusters("hd-www-search")+longRunningClusters("hd-www-dev") match {
        case "" => ""
        case x:String  => {


          val finalMessage=
            "The following cluster(s) are running more than 100 minutes, please make sure this is not by accident!\n" +x
          logger.info(finalMessage)
          SlackNotification.notifyInOrangeGraph(finalMessage)
        }

      }

    }

    override protected def messageHandler = {

      case Check => {
        checkClustersRunning()
        Schedule.schedule(this, Check, 1 hour)
      }
    }
  }
  def checkAnyClustersRunning = {

    ClusterActor ! ClusterActor.Check
  }
}
