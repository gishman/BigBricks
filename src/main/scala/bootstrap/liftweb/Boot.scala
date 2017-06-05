package bootstrap.liftweb

import com.homedepot.bigbricks.ui.ClusterRunningCheck
import com.homedepot.bigbricks.workflow.WorkflowWrapper
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


     DBInit.init
    // Use Lift's Mapper ORM to populate the database
    // you don't need to use Mapper to use Lift... use
    // any ORM you want
    Schemifier.schemify(true, Schemifier.infoF _, User)
    Schemifier.schemify(true, Schemifier.infoF _, Process)

    // where to search snippet
    LiftRules.addToPackages("code")

    def sitemapMutators = User.sitemapMutator
    //The SiteMap is built in the Site object bellow 
    LiftRules.setSiteMapFunc(() => sitemapMutators(Site.sitemap))

    //Init the FoBo - Front-End Toolkit module, 
    //see http://liftweb.net/lift_modules for more info
    FoBo.Toolkit.Init = FoBo.Toolkit.JQuery224
    FoBo.Toolkit.Init = FoBo.Toolkit.Bootstrap337
    FoBo.Toolkit.Init = FoBo.Toolkit.FontAwesome463

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

    LiftRules.noticesAutoFadeOut.default.set((notices: NoticeType.Value) => {
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
    info(s"Workflow intiated with active workflows:${WorkflowWrapper.countDefintions}")

    ClusterRunningCheck.checkAnyClustersRunning
    S.addAround(DB.buildLoanWrapper)

  }

  object Site {

    val loggedIn = If(() => User.loggedIn_?,
      () => RedirectResponse("/user_mgt/login"))
    val divider1 = Menu("divider1") / "divider1"
    val ddLabel1 = Menu.i("UserDDLabel") / "ddlabel1"
    val ddLabel2 = Menu.i("Components") / "ddlabel2"
    val processLabel = Menu.i("Process") / "ddlabel3"
    val taskLabel = Menu.i("Task") / "ddlabel4"

    val userMenu = User.AddUserMenusHere

    val dataMenu = Menu.i("Data") / "data"
    val simpleJob = Menu.i("Submit BBC Flow") / "activiti"
    val concourseJob = Menu.i("Concourse Flow") / "index"
    val yamlFIle = Menu.i("YAML file") / "yamlfile"

    val processDefnMenu = Menu.i("Process defintion") / "workflow" / "process" / "list"
    val processDefnEditMenu = Menu.i("Edit Process defintion") / "workflow" / "process" / "edit" >> Hidden
    val taskListMenu = Menu.i("List tasks") / "workflow" / "task" / "list"
    val completeTaskMenu = Menu.i("Complete task") / "workflow" / "task" / "completetask" >> Hidden

    val activeProcessMenu = Menu.i("Home") / "workflow" / "process" / "processinstances"
    val processDetailsMenu = Menu.i("Process details") / "workflow" / "process" / "processdetails" >> Hidden

    val deployProcessMenu = Menu.i("Deploy process") / "workflow" / "process" / "deploy"
    val startProcessMenu = Menu.i("Edit process") / "workflow" / "process" / "start" >> Hidden
    val deleteProcessMenu = Menu.i("Delete process") / "workflow" / "process" / "delete" >> Hidden


    def sitemap = SiteMap(


      activeProcessMenu >> LocGroup("lg1"),
      concourseJob >> LocGroup("lg2"),
      simpleJob >>LocGroup("lg2"),
      processLabel >> LocGroup("topRight") >> PlaceHolder submenus(
         processDefnMenu, deployProcessMenu, startProcessMenu, deleteProcessMenu, processDetailsMenu,processDefnEditMenu),
      taskLabel >> LocGroup("topRight") >> PlaceHolder submenus(taskListMenu, completeTaskMenu ,yamlFIle),
      ddLabel1 >> LocGroup("topRight") >> PlaceHolder submenus (
          divider1 >> FoBoBs.BSLocInfo.Divider >> userMenu
        ),
      ddLabel2 >> LocGroup("topRight") >> PlaceHolder submenus dataMenu

    )
  }


}
