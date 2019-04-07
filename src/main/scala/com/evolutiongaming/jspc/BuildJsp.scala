package com.evolutiongaming.jspc

import java.io._

import org.apache.jasper.JspC
import org.codehaus.plexus.util.FileUtils
import org.eclipse.jetty.util.IO
import org.eclipse.jetty.util.resource.Resource
import sbt._

import scala.collection.JavaConverters._

object BuildJsp {

  def apply(
    generatedClasses: String,
    classesDirectory: File,
    webAppSourceDirectory: String,
    artifacts: Seq[File],
    webXmlFragment: String,
    baseDir: File,
    includes: String,
    excludes: String,
    sourceJVM: String,
    targetJVM: String
  ): Unit = {

    val endOfWebapp = "</web-app>"
    val insertionMarker = ""

    val webXml = baseDir / "src" / "main" / "webapp" / "WEB-INF" / "web.xml"

    def updateWebXml(): Unit = {
      val webXml = getWebXmlFile()

      if (!webXml.exists()) sys.error(s"${ webXml.getPath } does not exist, cannot merge with generated fragment")

      val fragmentWebXml = new File(webXmlFragment)
      if (!fragmentWebXml.exists) sys.error("No fragment web.xml file generated")

      val mergedWebXml = fragmentWebXml.getParentFile / "web.xml"

      val webXmlReader = new BufferedReader(new FileReader(webXml))
      val mergedWebXmlWriter = new PrintWriter(new FileWriter(mergedWebXml))
      try {
        var atInsertPoint = false
        var atEOF = false
        val marker = if (insertionMarker == "") endOfWebapp else insertionMarker

        while (!atInsertPoint && !atEOF) {
          val line = webXmlReader.readLine
          if (line == null) atEOF = true
          else if (line.indexOf(marker) >= 0) {
            atInsertPoint = true
          } else {
            mergedWebXmlWriter.println(line)
          }
        }

        val fragmentWebXmlReader = new BufferedReader(new FileReader(fragmentWebXml))
        try {
          IO.copy(fragmentWebXmlReader, mergedWebXmlWriter)
          if (marker == endOfWebapp) mergedWebXmlWriter.println(endOfWebapp)
          IO.copy(webXmlReader, mergedWebXmlWriter)
        } finally {
          if (fragmentWebXmlReader != null) fragmentWebXmlReader.close()
        }

      } finally {
        webXmlReader.close()
        mergedWebXmlWriter.close()
      }
    }

    def getWebXmlFile(): File = {
      val baseDirCan = baseDir.getCanonicalFile
      val defaultWebAppSrcDir = (baseDirCan / "src" / "main" / "webapp").getCanonicalFile
      val webAppSrcDir = file(webAppSourceDirectory).getCanonicalFile
      val defaultWebXml = (defaultWebAppSrcDir / "web.xml").getCanonicalFile
      val webXmlFile = webXml.getCanonicalFile

      if (webXmlFile.compareTo(defaultWebXml) != 0) webXml else webAppSrcDir / "web.xml"
    }

    def insureOutputDirectoryExists(): Unit = {
      // For some reason JspC doesn't like it if the dir doesn't
      // already exist and refuses to create the web.xml fragment
      val generatedSourceDirectoryFile = file(generatedClasses)

      if (generatedSourceDirectoryFile.exists()) {
        FileUtils.deleteDirectory(generatedSourceDirectoryFile)
      }

      val _ = generatedSourceDirectoryFile.mkdirs()
    }

    def getClassesDir(classesDir: String): String = classesDir + (if (classesDir.endsWith(File.pathSeparator)) "" else File.separator)

    def compile(): Unit = {

      val webAppUrls = {
        Resource.toURL(file(getClassesDir(classesDirectory.getCanonicalPath))) :: artifacts.map(Resource.toURL).toList
      }

      val webAppClassPath = webAppUrls
        .map(a => new File(a.toURI).getCanonicalPath)
        .mkString(File.pathSeparator)

      val jspc = new JspC
      jspc.setWebXmlFragment(webXmlFragment)
      jspc.setUriroot(webAppSourceDirectory)
      jspc.setOutputDir(generatedClasses)
      jspc.setClassPath(webAppClassPath)
      jspc.setSystemClassPath(webAppClassPath)
      jspc.setCompile(true)
      jspc.setCompilerSourceVM(sourceJVM)
      jspc.setCompilerTargetVM(targetJVM)
      jspc.setPackage("jsp")
      jspc.setSmapSuppressed(true)
      jspc.setSmapDumped(false)

      val jspFiles = {
        FileUtils
          .getFileNames(
            file(webAppSourceDirectory),
            includes,
            excludes,
            false)
          .asScala
          .mkString(",")
      }

      jspc.setJspFiles(jspFiles)

      jspc.execute()
    }

    insureOutputDirectoryExists()
    compile()
    if (webXml.exists()) updateWebXml()
  }
}