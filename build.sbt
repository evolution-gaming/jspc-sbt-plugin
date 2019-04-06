import sbt.Resolver

addCommandAlias("build", "^all test makeIvyXml packagedArtifacts")

name := "sbt-jspc-plugin"

organization := "com.evolutiongaming"

sbtPlugin := true

name := "sbt-jspc-plugin"

publishMavenStyle := false

description := "JSP compiler plugin for sbt"

startYear := Some(2015)

organizationHomepage := Some(url("http://evolutiongaming.com"))

bintrayOrganization := Some("evolutiongaming")

libraryDependencies ++= Seq(
  "org.eclipse.jetty" % "jetty-util" % "8.2.0.v20160908",
  "org.eclipse.jetty" % "jetty-jsp" % "8.2.0.v20160908",
  "org.codehaus.plexus" % "plexus-utils" % "3.1.0")

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

import ReleaseTransformations._
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  releaseStepCommandAndRemaining("^ test"),
  setReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("^ publish"),
  releaseStepTask(bintrayRelease))