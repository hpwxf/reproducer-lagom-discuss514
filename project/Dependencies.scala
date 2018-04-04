import sbt.Keys.scalaVersion
import sbt._

object Dependencies {

  object Version {
    val scala = "2.12.4"
    val macwire = "2.3.0"
  }

  val resolvers =
    Seq(
      "Atlassian Releases" at "https://maven.atlassian.com/public/",
      "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
      Resolver.jcenterRepo
    )

  object Library {
    object MacWire {
      val corePack : Seq[ModuleID] = Seq(
        "com.softwaremill.macwire" %% "proxy" % Version.macwire,
        // "com.softwaremill.macwire" %% "util" % Version.macwire,
        "com.softwaremill.macwire" %% "macros" % Version.macwire % "provided"
      )
    }
  }
}
