import com.typesafe.sbt.SbtPgp.autoImportImpl._
import sbt.Keys._
import sbt._
import com.earldouglas.xwp.TomcatPlugin
import org.scoverage.coveralls.Imports.CoverallsKeys._

object Build extends Build {

  lazy val bigBricks = Project("BigBricks", file("."))

    .enablePlugins(TomcatPlugin)
    .settings(
      pgpPassphrase := Some(passphrase.toCharArray),
      pgpSecretRing := file("local.secring.gpg"),
      pgpPublicRing := file("local.pubring.gpg"),
      scalaVersion := currentScalaVersion,
      organization := organizationName,
      libraryDependencies ++= Seq(
        "com.recipegrace" %% "core" % electricVersion % "test",
        "net.liftweb" %% "lift-webkit" % liftVersion % "compile",
        "net.liftweb" %% "lift-mapper" % liftVersion % "compile",
        "net.liftmodules" %% "fobo_3.0" % "1.7" % "compile",
        "org.activiti" % "activiti-engine" % activitiVersion,
        "ch.qos.logback" % "logback-classic" % "1.1.3",
        "org.eclipse.jetty" % "jetty-webapp" % "8.1.17.v20150415" % "container,test",
        "org.eclipse.jetty" % "jetty-plus" % "8.1.17.v20150415" % "container,test", // For Jetty Config
        "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "container,test" artifacts Artifact("javax.servlet", "jar", "jar"),
        "com.recipegrace" %% "bigbricks-delegates" % "0.1.1-SNAPSHOT",
        "com.recipegrace" %% "bigbricks-core" % "0.1.1-SNAPSHOT"
      ),
      publishTo := {
        val nexus = "https://oss.sonatype.org/"
        if (isSnapshot.value) Some(ossSnapshots)
        else Some(ossStaging)
      },
      credentials += Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", username, password),
      pomIncludeRepository := { _ => false },
      pomExtra := (
        <url>http://recipegrace.com/recipegrace</url>
          <licenses>
            <license>
              <name>BSD-style</name>
              <url>http://www.opensource.org/licenses/bsd-license.php</url>
              <distribution>repo</distribution>
            </license>
          </licenses>
          <scm>
            <url>git@github.com:recipegrace/BigLibrary.git</url>
            <connection>scm:git:git@github.com:recipegrace/BigLibrary.git</connection>
          </scm>
          <developers>
            <developer>
              <id>feroshjacob</id>
              <name>Ferosh Jacob</name>
              <url>http://www.feroshjacob.com</url>
            </developer>
          </developers>),
      coverallsToken := Some("my-token"),
      resolvers ++= Seq(Resolver.sonatypeRepo("releases"), Resolver.sonatypeRepo("snapshots")))
  val currentScalaVersion = "2.11.6"
  val organizationName = "com.recipegrace"
  val electricVersion = "0.0.5-SNAPSHOT"
  val activitiVersion = "5.17.0"
  val liftVersion = "3.0-RC4"
  val username = System.getenv().get("SONATYPE_USERNAME")
  val password = System.getenv().get("SONATYPE_PASSWORD")
  val coverallToken = System.getenv().get("COVERALL_TOKEN")
  val passphrase = System.getenv().get("PGP_PASSPHRASE") match {
    case x: String => x
    case null => ""
  }
  val ossSnapshots = "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  val ossStaging = "Sonatype OSS Snapshots" at "https://oss.sonatype.org/service/local/staging/deploy/maven2"
}
