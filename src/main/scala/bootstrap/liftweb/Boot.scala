package bootstrap.liftweb

import net.liftweb._
import util._
import Helpers._

import common._
import http._
import sitemap._
import Loc._
import mapper._

import code.model.{Job, User, Project, Template}
import net.liftmodules.FoBo

import scala.language.postfixOps

/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot {
    if (!DB.jndiJdbcConnAvailable_?) {
      val vendor = 
	new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
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
    Schemifier.schemify(true, Schemifier.infoF _, Template)
    Schemifier.schemify(true, Schemifier.infoF _, Job)

    // where to search snippet
    LiftRules.addToPackages("code")


    def sitemapMutators = User.sitemapMutator
    //The SiteMap is built in the Site object bellow 
    LiftRules.setSiteMapFunc(() => sitemapMutators(Site.sitemap))

    //Init the FoBo - Front-End Toolkit module, 
    //see http://liftweb.net/lift_modules for more info
    FoBo.InitParam.JQuery=FoBo.JQuery1102  
    FoBo.InitParam.ToolKit=FoBo.Bootstrap320 
    FoBo.init() 
    
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
    
    // Make a transaction span the whole HTTP request
    S.addAround(DB.buildLoanWrapper)
  }
  
  object Site {

    val loggedIn = If(() => User.loggedIn_?,
      () => RedirectResponse("/user_mgt/login"))

    import scala.xml._
    val divider1   = Menu("divider1") / "divider1"
    val ddLabel1   = Menu.i("UserDDLabel") / "ddlabel1"
    val home       = Menu.i("Home") / "index"
    val userMenu   = User.AddUserMenusHere
    val jobMenu    = (Menu.i("Jobs") / "job" / "index").rule(loggedIn)
    val editJobMenu =  Menu.i("Edit Job") / "job" /"edit" >> Hidden
    val addJobMenu =  Menu.i("Add Job") / "job" /"add" >> Hidden
    val deleteJobMenu =  Menu.i("Delete Job") / "job" /"delete" >> Hidden

    val projectMenu    = (Menu.i("Projects") / "project" / "index").rule(loggedIn)
    val editProjectMenu =  Menu.i("Edit Project") / "project" /"edit" >> Hidden
    val addProjectMenu =  Menu.i("Add Project") / "project" /"add" >> Hidden
    val deleteProjectMenu =  Menu.i("Delete Project") / "project" /"delete" >> Hidden

    val templateMenu    = (Menu.i("Templates") / "template" / "index").rule(loggedIn)
    val dataMenu    = (Menu.i("Data") / "data" / "index").rule(loggedIn)
    val editTemplateMenu =  Menu.i("Edit Template") / "template" /"edit" >> Hidden
    val addTemplateMenu =  Menu.i("Add Template") / "template" /"add" >> Hidden
    val deleteTemplateMenu =  Menu.i("Delete Template") / "template" /"delete" >> Hidden

    /*val twbs  = Menu(Loc("twbs",
         ExtLink("http://getbootstrap.com/"),
         S.loc("Bootstrap3", Text("Bootstrap3")),
         LocGroup("lg2"),
         FoBo.TBLocInfo.LinkTargetBlank ))     */


    def sitemap = SiteMap(


      home          >> LocGroup("lg1")
      ,editJobMenu,addJobMenu,deleteJobMenu
      ,editProjectMenu,addProjectMenu,deleteProjectMenu
      ,editTemplateMenu,addTemplateMenu,deleteTemplateMenu,
      jobMenu >>LocGroup("lg1"),
      projectMenu >>LocGroup("lg1"),
     templateMenu >>LocGroup("lg1"),
    dataMenu >>LocGroup("lg1"),
        ddLabel1      >> LocGroup("topRight") >> PlaceHolder submenus (
            divider1  >> FoBo.TBLocInfo.Divider >> userMenu
            )
    )
  }
  
}