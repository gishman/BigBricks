package code.snippet

import java.io.{FileWriter, File}


import com.homedepot.bigbricks.data.{UploadToCloud, BBCData}
import com.homedepot.bigbricks.ui.BigBricksLogging
import net.liftweb.common.{Box, Empty, Full, Loggable}
import net.liftweb.http.JsonResponse
import net.liftweb.http.SHtml._
import net.liftweb.util.Schedule
import org.apache.commons.io.FileUtils
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.liftweb.http._
import net.liftweb.util.Helpers._
import scala.xml.Text
import net.liftweb.http.js.{JsCmds, JsCmd}
/**
  * A snippet that binds behavior, functions,
  * to HTML elements
  */
object ExportBBC extends BBCData {


  def exportBBC() = {

    val json= exportData

    logAndDisplayMessage(LoggingInfo,"downloaded json")
    val jsonContent=JsonResponse(json, "Content-Type"-> "text/plain;charset=utf-8" ::
      "Content-Disposition"->("attachement;filename=\"bbc.json\"")::Nil, Nil, 200 )
    SHtml.link("/index", ()=> throw new ResponseShortcutException(jsonContent), Text("As JSON"), "download"->("bbc.json"))
  }


}
object ImportBBC extends BBCData {


  def render = {
    var upload: Box[FileParamHolder] = Empty

    def process() : JsCmd = {
      upload match {
        case Full(FileParamHolder(_, mimeType, fileName, file)) => {

          val jsonContent= new String(file, "iso-8859-1")
          importData(jsonContent)
          val message = s"inserted  ${fileName}"
          logAndDisplayMessage(LoggingInfo,message)
        }
        case _ => logAndDisplayMessage(LoggingInfo,"file not found in the upload")
      }
      JsCmds.Noop
    }



    "#file" #> fileUpload(f => upload = Full(f)) &
      "type=submit" #> onSubmitUnit(process)

  }
}

object BackupBBC extends  BBCData with UploadToCloud {
  def backupBBC() = {


    Schedule(() => {
      try {
        import net.liftweb.json._
        val bigBricksBackupPath = "/tmp/BigBricksBackup"
        val tmpDir = new File(bigBricksBackupPath)
        if (tmpDir.exists()) FileUtils.deleteDirectory(tmpDir)
        tmpDir.mkdirs()


        val writer = new FileWriter(tmpDir.getAbsolutePath + File.separator + "bbc")
        writer.append(compactRender(exportData))
        writer.close()



        val zipFile = "/tmp/BigBricksBackup" + System.currentTimeMillis() + ".zip"

        val zippedFile = new ZipFile(zipFile)
        val parameters = new ZipParameters()
        zippedFile.addFolder(bigBricksBackupPath, parameters)

        System.getProperty("GCS_BUCKET") match {
          case null => logger.error(" GCS bucket not initalized")
          case "" => logger.error(" GCS bucket not initalized")
          case x: String => {
            uploadZipFile(new File(zipFile), x)
            logger.info(s"bbc data downloaded and upload" +
              s" to cloud  location x finished")
          }
        }
      }catch{
        case ex:Exception => {
          ex.printStackTrace()
          logger.info(s"Mosambi backup and upload to cloud  location  failed")
        }
      }
    })
    val message = s"scheduled the bbc backup generation and upload"
    logAndDisplayMessage(LoggingInfo,message)
  }

  def render =  {
    "type=submit" #> onSubmitUnit(backupBBC)
  }
}
