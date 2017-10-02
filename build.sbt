import sbt.Resolver

name := "sbt-jspc-plugin"

organization := "com.evolutiongaming"

sbtPlugin := true

name := "sbt-jspc-plugin"

crossSbtVersions := Seq("0.13.16", "1.0.0")

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

scalacOptions ++= Seq(
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

scalacOptions in (Compile,doc) ++= Seq("-no-link-warnings")

resolvers += Resolver.bintrayRepo("evolutiongaming", "maven")

licenses := Seq(("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0")))