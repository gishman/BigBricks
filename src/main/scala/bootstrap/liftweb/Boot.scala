package bootstrap.liftweb

import com.recipegrace.bigbricks.workflow.WorkflowWrapper
import net.liftweb._
import net.liftweb.http.js.jquery.JQueryArtifacts
import util._
import Helpers._

import common._
import http._
import sitemap._
import Loc._
import mapper._

import code.model._
import net.liftmodules.{JQueryModule, FoBo, FoBoBs}

import scala.language.postfixOps

/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot extends Logger {
  def boot {

    if (!DB.jndiJdbcConnAvailable_?) {
      sys.props.put("h2.implicitRelativePath", "true")
      val vendor = new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
			     Props.get("db.url") openOr 
			     "jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE",
			     Props.get("db.user"), Props.get("db.password"))
      LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)
      DB.defineConnectionManager(util.DefaultConnectionIdentifier, vendor)
    }

    // Use Lift's Mapper ORM to populate the database
    // you don't need to use Mapper to use Lift... use
    // any ORM you want
    Schemifier.schemify(true, Schemifier.infoF _, User)
    Schemifier.schemify(true, Schemifier.infoF _, Project)
    Schemifier.schemify(true, Schemifier.infoF _, code.model.Template)
    Schemifier.schemify(true, Schemifier.infoF _, Job)
    Schemifier.schemify(true, Schemifier.infoF _, Cluster)

    // where to search snippet
    LiftRules.addToPackages("code")

    def sitemapMutators = User.sitemapMutator
    //The SiteMap is built in the Site object bellow 
    LiftRules.setSiteMapFunc(() => sitemapMutators(Site.sitemap))

    //Init the FoBo - Front-End Toolkit module, 
    //see http://liftweb.net/lift_modules for more info
    FoBo.Toolkit.Init=FoBo.Toolkit.JQuery224
    FoBo.Toolkit.Init=FoBo.Toolkit.Bootstrap337 
    FoBo.Toolkit.Init=FoBo.Toolkit.FontAwesome463    
    
    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)
    
    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    // What is the function to test if a user is logged in?
    LiftRules.loggedInTest = Full(() => User.loggedIn_?)

    LiftRules.jsArtifacts = JQueryArtifacts
    LiftRules.ajaxPostTimeout = 900000
    JQueryModule.InitParam.JQuery = JQueryModule.JQuery191
    JQueryModule.init()

    // Use HTML5 for rendering
    LiftRules.htmlProperties.default.set((r: Req) =>
      new Html5Properties(r.userAgent))    
      
    LiftRules.noticesAutoFadeOut.default.set( (notices: NoticeType.Value) => {
        notices match {
          case NoticeType.Notice => Full((8 seconds, 4 seconds))
          case _ => Empty
        }
     }
    ) 
    
    //Lift CSP settings see http://content-security-policy.com/ and 
    //Lift API for more information.  
    LiftRules.securityRules = () => {
      SecurityRules(content = Some(ContentSecurityPolicy(           
        scriptSources = List(
            ContentSourceRestriction.Self),   
        styleSources = List(
            ContentSourceRestriction.Self)
            )))
    }    
    // Make a transaction span the whole HTTP request
   info(  s"Workflow intiated with active workflows:${WorkflowWrapper.countDefintions}")
    S.addAround(DB.buildLoanWrapper)
  }

 object Site {

   val loggedIn = If(() => User.loggedIn_?,
     () => RedirectResponse("/user_mgt/login"))
   val divider1   = Menu("divider1") / "divider1"
   val ddLabel1   = Menu.i("UserDDLabel") / "ddlabel1"
   val ddLabel2   = Menu.i("Components") / "ddlabel2"
   val home       = Menu.i("Home") / "index"
   val processLabel = Menu.i("Process") / "ddlabel3"
   val taskLabel = Menu.i("Task") / "ddlabel4"

   val userMenu   = User.AddUserMenusHere

   val dataMenu    = Menu.i("Data") / "data" / "index"


   val jobMenu    = (Menu.i("Jobs") / "components"/"job" / "index")
     //.rule(loggedIn)
   val editJobMenu =  Menu.i("Edit Job") / "components"/"job" /"edit" >> Hidden
   val addJobMenu =  Menu.i("Add Job") / "components"/"job" /"add" >> Hidden
   val deleteJobMenu =  Menu.i("Delete Job") / "components"/"job" /"delete" >> Hidden


   val projectMenu    = (Menu.i("Projects") / "components"/"project" / "index")
     //.rule(loggedIn)
   val editProjectMenu =  Menu.i("Edit Project") / "components"/"project" /"edit" >> Hidden
   val addProjectMenu =  Menu.i("Add Project") / "components"/"project" /"add" >> Hidden
   val deleteProjectMenu =  Menu.i("Delete Project") / "components"/"project" /"delete" >> Hidden

   val templateMenu    = (Menu.i("Templates") / "components"/"template" / "index")
     //.rule(loggedIn)
   val editTemplateMenu =  Menu.i("Edit Template") / "components"/"template" /"edit" >> Hidden
   val addTemplateMenu =  Menu.i("Add Template") / "components"/"template" /"add" >> Hidden
   val deleteTemplateMenu =  Menu.i("Delete Template") / "components"/"template" /"delete" >> Hidden

   val clusterMenu    = (Menu.i("Clusters") / "components"/"cluster" / "index")
     //.rule(loggedIn)
   val editClusterMenu =  Menu.i("Edit Cluster") / "components"/"cluster" /"edit" >> Hidden
   val addClusterMenu =  Menu.i("Add Cluster") / "components"/"cluster" /"add" >> Hidden
   val deleteClusterMenu =  Menu.i("Delete Cluster") / "components"/"cluster" /"delete" >> Hidden




   val processDefnMenu    = Menu.i("Process defintion") / "workflow"/ "processdefinition"
   val taskListMenu    = Menu.i("List tasks") / "workflow"/ "listtasks"

   val activeProcessMenu =  (Menu.i("Active processes") / "workflow"/"activeprocess")
   //.rule(loggedIn)

   val deployProcessMenu = Menu.i("Deploy process") / "workflow"/"dynamic"


   def sitemap = SiteMap(


     home          >> LocGroup("lg1")
     ,editJobMenu,addJobMenu,deleteJobMenu
     ,editProjectMenu,addProjectMenu,deleteProjectMenu
     ,editTemplateMenu,addTemplateMenu,deleteTemplateMenu
     ,editClusterMenu,addClusterMenu,deleteClusterMenu,

     processLabel >> LocGroup("topRight") >> PlaceHolder submenus (activeProcessMenu, processDefnMenu,deployProcessMenu),
     taskLabel >> LocGroup("topRight") >> PlaceHolder submenus (taskListMenu),


     ddLabel1      >> LocGroup("topRight") >> PlaceHolder submenus (

       divider1  >> FoBoBs.BSLocInfo.Divider >> userMenu
       ),
     ddLabel2      >> LocGroup("topRight") >> PlaceHolder submenus (
       projectMenu,
       templateMenu,
       clusterMenu,
       jobMenu,
       dataMenu
       )
   )
 }


}
