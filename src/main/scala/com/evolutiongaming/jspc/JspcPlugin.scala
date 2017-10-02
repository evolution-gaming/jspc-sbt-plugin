package com.evolutiongaming.jspc

import java.io.File

import sbt.Keys._
import sbt._

object JspcPlugin extends AutoPlugin {

  object autoImport {
    lazy val generatedJspClassesDirName = settingKey[String]("generated-jsp-classes")
    lazy val jspcIncludes = settingKey[String]("includes pattern for jsp precompile")
    lazy val jspcExcludes = settingKey[String]("excludes pattern for jsp precompile")
    lazy val jspc = taskKey[File]("Compiles JSP")
    lazy val outputFileName = settingKey[String]("The output file name")
  }

  import autoImport._
  import JarUtility._

  override def projectSettings: Seq[Def.Setting[_]] = Seq (
    outputFileName := s"${name.value}_jsp.jar",
    jspc := {
      val files = (fullClasspath in Runtime).value
      val targetPath = target.value
      val classPath = (classDirectory in Compile).value
      val sourcePath = sourceDirectory.value
      val generatedJspDirName = generatedJspClassesDirName.value
      val baseDirectoryValue = baseDirectory.value
      val stream = streams.value

      val generatedClasses = targetPath / generatedJspDirName
      val out: sbt.File = targetPath / autoImport.outputFileName.value

      JspcUtils(
        generatedClasses = generatedClasses.getPath,
        classesDirectory = classPath,
        webAppSourceDirectory = (sourcePath / "main" / "webapp").getPath,
        artifacts = files.files,
        webXmlFragment = (targetPath / "webfrag.xml").getPath,
        baseDir = baseDirectoryValue)
        .execute()

      val mappings = Path.allSubpaths(generatedClasses).toSeq
      
      val cachePath: File = stream.cacheDirectory / "jspc"

      val packageConf = new sbt.Package.Configuration(mappings, out, Seq())
      pack(packageConf, cachePath, stream.log)

      out
    }
  )
}