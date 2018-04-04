import play.routes.compiler.InjectedRoutesGenerator
import play.sbt.PlayImport.PlayKeys
import play.sbt.routes.RoutesKeys.routesGenerator
import play.sbt.{Play, PlayScala}
import play.twirl.sbt.Import.TwirlKeys
import sbt.Keys._
import sbt._

/**
  * Basic settings
  */
object BasicSettings extends AutoPlugin {
  override def trigger: PluginTrigger = allRequirements

  override def projectSettings: Seq[Setting[_]] = Seq(
    organization := "com.absttraction",
    version := "0.0.2",
    resolvers ++= Dependencies.resolvers,
    scalaVersion := crossScalaVersions.value.head,
    crossScalaVersions := Seq(Dependencies.Version.scala),
    scalacOptions ++= Seq(
      "-feature", // Emit warning and location for usages of features that should be imported explicitly.
      "-deprecation", // Emit warning and location for usages of deprecated APIs.
      "-unchecked", // Enable additional warnings where generated code depends on assumptions.
      "-language:postfixOps", // such as "expiry: FiniteDuration = 24 hours"

      // WARNING: These warnings may generated errors
      "-Xfatal-warnings", // Fail the compilation if there are any warnings. // FIXME When warnings will be OK
      "-Xlint:-unused,_", // Enable recommended additional warnings.
      "-Ywarn-unused:-imports,", // disable warnings about unsed import (cf scalac -Ywarn-unused:help or -Xlint:help)
      //"-Ywarn-unused:-imports,_,", // disable warnings about unsed import (cf scalac -Ywarn-unused:help or -Xlint:help)
      "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver.
      "-Ywarn-dead-code", // Warn when dead code is identified.
      "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
      "-Ywarn-nullary-override", // Warn when non-nullary overrides nullary, e.g. def foo() over def foo.
      "-Ywarn-numeric-widen" // Warn when numerics are widened.

    // May be not used
    // "-language:reflectiveCalls",
    // "-language:implicitConversions"
    ),
    scalacOptions in Test ~= { (options: Seq[String]) =>
      options filterNot (_ == "-Ywarn-dead-code") // Allow dead code in tests (to support using mockito).
    },

    maxErrors := 10000

  //
  //    parallelExecution in Test := false,
  //    fork in Test := true,
  //    fork in run := true
  )
}

/**
  * Scalariform settings
  * cf https://github.com/scala-ide/scalariform
  *    https://github.com/sbt/sbt-scalariform
  *    https://github.com/thesamet/scalariform-intellij-plugin
  */
object ScalariformSettings extends AutoPlugin {

  import com.typesafe.sbt.SbtScalariform._

  import scalariform.formatter.preferences._

  lazy val prefs = Seq(
    ScalariformKeys.preferences := ScalariformKeys.preferences.value
      .setPreference(IndentSpaces, 2)
      .setPreference(AlignParameters, true)
      .setPreference(AlignSingleLineCaseStatements, true)
      .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 40)
      .setPreference(CompactControlReadability, false)
      .setPreference(CompactStringConcatenation, false)
      .setPreference(DoubleIndentConstructorArguments, true)
      .setPreference(FormatXml, false)
      .setPreference(IndentLocalDefs, true)
      .setPreference(IndentWithTabs, false)
      .setPreference(DanglingCloseParenthesis, Force)
      .setPreference(MultilineScaladocCommentsStartOnFirstLine, false)
      .setPreference(PlaceScaladocAsterisksBeneathSecondAsterisk, true)
      .setPreference(PreserveSpaceBeforeArguments, true)
      .setPreference(RewriteArrowSymbols, false)
      .setPreference(SpaceBeforeColon, false)
      .setPreference(SpaceInsideBrackets, false)
      .setPreference(SpaceInsideParentheses, false)
      .setPreference(SpacesWithinPatternBinders, true)
  )

  override def trigger: PluginTrigger = allRequirements

  override def projectSettings: Seq[Setting[_]] = prefs
}

/**
  * Doc settings
  */
object DocSettings extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements

  // Disable documentation creation
  override def projectSettings: Seq[Setting[_]] = Seq(
    publishArtifact in (Compile, packageDoc) := false,
    publishArtifact in packageDoc := false,
    sources in (Compile, doc) := Seq.empty
  )
}

/**
  * Play settings
  */
object PlaySettings extends AutoPlugin {
  override def requires: Plugins = Play

  override def projectSettings: Seq[Setting[_]] =
    Seq(
      // Monitor Twirl templates with SBT layout
      PlayKeys.playMonitoredFiles ++= (sourceDirectories in (Compile, TwirlKeys.compileTemplates)).value,

      // Router settings
      routesGenerator := InjectedRoutesGenerator,

      // Disable documentation
      sources in (Compile, doc) := Seq.empty,
      publishArtifact in (Compile, packageDoc) := false
    )
}
