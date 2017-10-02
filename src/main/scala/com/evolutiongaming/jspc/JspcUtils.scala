package com.evolutiongaming.jspc

import java.io._
import java.net.URL

import org.apache.jasper.JspC
import org.codehaus.plexus.util.FileUtils
import org.eclipse.jetty.util.IO
import org.eclipse.jetty.util.resource.Resource
import sbt._

import scala.collection.JavaConverters._

case class JspcUtils(
  generatedClasses: String /* from jsp.out.dir */ ,
  classesDirectory: File /* project.build.outputDirectory */ ,
  webAppSourceDirectory: String, //default-value="${basedir}/src/main/webapp"
  artifacts: Seq[File],
  webXmlFragment: String /* ${basedir}/target/webfrag.xml */ ,
  baseDir: File,
  includes: String = """**/*.jsp""",
  excludes: String = """**/.git/**""") {

  val END_OF_WEBAPP: String = "</web-app>"
  val insertionMarker: String = ""

  lazy val webXml: File = baseDir / "src" / "main" / "webapp" / "WEB-INF" / "web.xml"

  def execute(): Unit = {
    insureOutputDirectoryExists()
    compile()
    if (webXml.exists()) updateWebXml()
  }

  private def updateWebXml(): Unit = {
    val webXml: File = getWebXmlFile()

    if (!webXml.exists()) sys.error(webXml.getPath + " does not exist, cannot merge with generated fragment")

    val fragmentWebXml: File = new File(webXmlFragment)
    if (!fragmentWebXml.exists) sys.error("No fragment web.xml file generated")

    val mergedWebXml: File = fragmentWebXml.getParentFile / "web.xml"

    val webXmlReader: BufferedReader = new BufferedReader(new FileReader(webXml))
    val mergedWebXmlWriter: PrintWriter = new PrintWriter(new FileWriter(mergedWebXml))
    try {
      var atInsertPoint: Boolean = false
      var atEOF: Boolean = false
      val marker: String = if (insertionMarker == "") END_OF_WEBAPP else insertionMarker

      while (!atInsertPoint && !atEOF) {
        val line: String = webXmlReader.readLine
        if (line == null) atEOF = true
        else if (line.indexOf(marker) >= 0) {
          atInsertPoint = true
        } else {
          mergedWebXmlWriter.println(line)
        }
      }
      
      val fragmentWebXmlReader: BufferedReader = new BufferedReader(new FileReader(fragmentWebXml))
      try {
        IO.copy(fragmentWebXmlReader, mergedWebXmlWriter)
        if (marker == END_OF_WEBAPP) mergedWebXmlWriter.println(END_OF_WEBAPP)
        IO.copy(webXmlReader, mergedWebXmlWriter)
      } finally {
        if (fragmentWebXmlReader != null) fragmentWebXmlReader.close()
      }

    } finally {
      if (webXmlReader != null) webXmlReader.close()
      if (mergedWebXmlWriter != null) mergedWebXmlWriter.close()
    }
  }

  private def getWebXmlFile(): File = {
    val baseDirCan: File = baseDir.getCanonicalFile
    val defaultWebAppSrcDir: File = (baseDirCan / "src" / "main" / "webapp").getCanonicalFile
    val webAppSrcDir: File = file(webAppSourceDirectory).getCanonicalFile
    val defaultWebXml: File = (defaultWebAppSrcDir / "web.xml").getCanonicalFile
    val webXmlFile: File = webXml.getCanonicalFile

    if (webXmlFile.compareTo(defaultWebXml) != 0) webXml else webAppSrcDir / "web.xml"
  }

  private def insureOutputDirectoryExists(): Unit = {
    // For some reason JspC doesn't like it if the dir doesn't
    // already exist and refuses to create the web.xml fragment
    val generatedSourceDirectoryFile: File = file(generatedClasses)

    if (generatedSourceDirectoryFile.exists()) {
      FileUtils.deleteDirectory(generatedSourceDirectoryFile)
    }

    generatedSourceDirectoryFile.mkdirs()
  }

  private def setUpWebAppClassPath(): List[URL] =
    List(Resource.toURL(file(getClassesDir(classesDirectory.getCanonicalPath)))) ++ (artifacts map Resource.toURL).toList

  private def getClassesDir(classesDir: String): String = classesDir + (if (classesDir.endsWith(File.pathSeparator)) "" else File.separator)

  private def compile() = {

    val webAppUrls: List[URL] = setUpWebAppClassPath()

    val webAppClassPath: String =
      webAppUrls map (f => new File(f.toURI).getCanonicalPath) mkString File.pathSeparator

    val jspc = new JspC

    jspc.setWebXmlFragment(webXmlFragment)
    jspc.setUriroot(webAppSourceDirectory)
    jspc.setOutputDir(generatedClasses)
    jspc.setClassPath(webAppClassPath)
    jspc.setSystemClassPath(webAppClassPath)
    jspc.setCompile(true)
    jspc.setCompilerSourceVM("1.6")
    jspc.setCompilerTargetVM("1.6")
    jspc.setPackage("jsp")
    jspc.setSmapSuppressed(true)
    jspc.setSmapDumped(false)

    val jspFiles: String = getJspFiles(webAppSourceDirectory)

    jspc.setJspFiles(jspFiles)

    jspc.execute()
  }

  private def getJspFiles(webAppSourceDirectory: String): String =
    FileUtils.getFileNames(file(webAppSourceDirectory), includes, excludes, false).asScala mkString ","
}