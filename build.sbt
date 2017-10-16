import sbt.Keys._
import sbt._
import com.typesafe.sbt.SbtPgp.autoImportImpl._


val currentScalaVersion = "2.12.2"
  val organizationName = "com.homedepot"
  val activitiVersion = "5.17.0"
  val liftVersion = "3.1.0"
  val bBCVersion = "0.0.5-SNAPSHOT"
  val username = System.getenv().get("SONATYPE_USERNAME")
  val password = System.getenv().get("SONATYPE_PASSWORD")
  val passphrase = System.getenv().get("PGP_PASSPHRASE") match {
    case x: String => x
    case null => ""
  }
  val ossSnapshots = "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  val ossStaging = "Sonatype OSS Snapshots" at "https://oss.sonatype.org/service/local/staging/deploy/maven2"
  lazy val bigBricks = Project("BigBricks", file("."))
    .enablePlugins(TomcatPlugin)
    .settings(
      pgpPassphrase := Some(passphrase.toCharArray),
      pgpSecretRing := file("local.secring.gpg"),
      pgpPublicRing := file("local.pubring.gpg"),
      scalaVersion := currentScalaVersion,
      organization := organizationName,
      libraryDependencies ++= Seq(
        "net.liftweb" %% "lift-webkit" % liftVersion % "compile",
        "net.liftweb" %% "lift-mapper" % liftVersion % "compile",
        "net.liftmodules" %% "fobo_3.0" % "1.7" % "compile",
        "org.activiti" % "activiti-engine" % activitiVersion,
        "ch.qos.logback" % "logback-classic" % "1.1.3",
        "org.eclipse.jetty" % "jetty-webapp" % "8.1.17.v20150415" % "container,test",
        "org.eclipse.jetty" % "jetty-plus" % "8.1.17.v20150415" % "container,test", // For Jetty Config
        "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "container,test" artifacts Artifact("javax.servlet", "jar", "jar"),
        "com.homedepot" %% "bigbricks-delegates" % "0.0.5-SNAPSHOT",
        "com.homedepot" %% "bigbricks-core" % bBCVersion,
        "net.lingala.zip4j"%"zip4j"%"1.3.2"

    ),
    publishTo := {
      if (isSnapshot.value) Some(ossSnapshots)
      else Some(ossStaging)
    },
    credentials += Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", username, password),
    pomIncludeRepository := { _ => false },
    pomExtra := (
      <url>http://homedepot.com</url>
        <licenses>
          <license>
            <name>BSD-style</name>
            <url>http://www.opensource.org/licenses/bsd-license.php</url>
            <distribution>repo</distribution>
          </license>
        </licenses>
        <scm>
          <url>git@github.com:homedepot/BigLibrary.git</url>
          <connection>scm:git:git@github.com:homedepot/BigLibrary.git</connection>
        </scm>
        <developers>
          <developer>
            <id>feroshjacob</id>
            <name>Ferosh Jacob</name>
            <url>http://www.feroshjacob.com</url>
          </developer>
        </developers>),
    resolvers ++= Seq(Resolver.sonatypeRepo("releases"), Resolver.sonatypeRepo("snapshots"), ossSnapshots))