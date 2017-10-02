import sbt.Resolver

addCommandAlias("build", "^all test makeIvyXml packagedArtifacts")

name := "sbt-jspc-plugin"

organization := "com.evolutiongaming"

sbtPlugin := true

name := "sbt-jspc-plugin"

crossSbtVersions := Seq("0.13.16", "1.0.2")

publishMavenStyle := false

description := "JSP compiler"

startYear := Some(2015)

organizationHomepage := Some(url("http://evolutiongaming.com"))

bintrayOrganization := Some("evolutiongaming")

libraryDependencies ++= Seq(
  "org.eclipse.jetty" % "jetty-util" % "8.1.14.v20131031",
  "org.eclipse.jetty" % "jetty-jsp" % "8.1.14.v20131031",
  "org.codehaus.plexus" % "plexus-utils" % "3.0.17"
)

val scalacOpts = Seq(
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked",
  "-deprecation",
  "-Xfatal-warnings",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Xfuture")

scalacOptions in(Compile, doc) ++= Seq("-no-link-warnings")

publishArtifact in (Compile, packageDoc) := false

resolvers += Resolver.bintrayRepo("evolutiongaming", "sbt-plugins")

licenses := Seq(("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0")))

val Scala210 = "2.10.6"
val Scala212 = "2.12.3"

scalaVersion := (CrossVersion partialVersion (sbtVersion in pluginCrossBuild).value match {
  case Some((0, 13)) => Scala210
  case Some((1, _)) => Scala212
  case _ => sys error s"Unhandled sbt version ${(sbtVersion in pluginCrossBuild).value}"
})

// Scala 2.10 (used by SBT 0.13.x) supports only Java 1.6
// we will remove this as soon as we will be able to switch to SBT 1.0 and Scala 2.12 for good
scalacOptions in Compile := (CrossVersion partialVersion (sbtVersion in pluginCrossBuild).value match {
  case Some((0, 13)) => scalacOpts.filterNot(_.startsWith("-target")) :+ "-target:jvm-1.6"
  case Some((1, _)) => scalacOpts
  case _ => sys error s"Unhandled sbt version ${(sbtVersion in pluginCrossBuild).value}"
})

import ReleaseTransformations._
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  releaseStepCommandAndRemaining("^ test"),
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("^ publish"),
  releaseStepTask(bintrayRelease),
  setNextVersion,
  commitNextVersion,
  pushChanges
)