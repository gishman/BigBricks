package com.recipegrace.bigbricks.db

import com.recipegrace.biglibrary.core.BaseTest
import net.liftweb.db.StandardDBVendor
import net.liftweb.mapper.DB
import net.liftweb.util
import net.liftweb.util.Props
import com.recipegrace.bigbricks.data.BigBricksExport._
import com.recipegrace.bigbricks.data.BigBricksImport._
/**
 * Created by fjacob on 8/21/15.
 */
class DBExportTest extends BaseTest {


   test("db test"){

      val vendor =
        new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
          Props.get("db.url") openOr
            "jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE",
          Props.get("db.user"), Props.get("db.password"))
      vendor.closeAllConnections_!()
      DB.defineConnectionManager(util.DefaultConnectionIdentifier, vendor)

      val content = exportBigBricks()
      importBigBricks(content)
      content.startsWith("{\"clusters\":[],") shouldEqual true

    }



    }
