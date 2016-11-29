package com.homedepot.bigbricks.db

import bootstrap.liftweb.DBInit
import code.model.Process
import com.homedepot.bigbricks.data.BBCData
import com.recipegrace.biglibrary.core.BaseTest
import net.liftweb.json.Extraction._
import net.liftweb.json._

import scala.io.Source

/**
  * Created by fjacob on 8/21/15.
  */
class DBExportTest extends BaseTest with BBCData {


  test("db test export") {
    DBInit.init

    val data = exportData

    data shouldBe ""

  }

  test("db test import") {
    DBInit.init

    val data = importData(Source.fromFile("files/bbc.json").mkString)

    data shouldBe ""



  }


}
