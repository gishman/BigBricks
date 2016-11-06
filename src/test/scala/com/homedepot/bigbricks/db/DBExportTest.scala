package com.homedepot.bigbricks.db

import code.model.Process
import com.homedepot.bigbricks.data.{BigBricksImport, BigBricksExport}
import com.recipegrace.biglibrary.core.BaseTest
import net.liftweb.db.StandardDBVendor
import net.liftweb.http.LiftRules
import net.liftweb.mapper.DB
import net.liftweb.util
import net.liftweb.util.Props
/**
 * Created by fjacob on 8/21/15.
 */
class DBExportTest extends BaseTest {


   test("db test"){

     if (!DB.jndiJdbcConnAvailable_?) {
       sys.props.put("h2.implicitRelativePath", "true")
       val vendor = new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
         Props.get("db.url") openOr
           "jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE",
         Props.get("db.user"), Props.get("db.password"))
       LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)
       DB.defineConnectionManager(util.DefaultConnectionIdentifier, vendor)
     }

/*      val content = BigBricksExport. exportBigBricks()
      BigBricksImport. importBigBricks(content)
      content.startsWith("{\"clusters\":[],") shouldEqual true
*/
     Process.findAll().head.getProcessVariables should have size 0

    }



    }
