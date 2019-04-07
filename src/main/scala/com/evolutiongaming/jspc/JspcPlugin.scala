package com.evolutiongaming.jspc

import java.io.File

import sbt.Keys._
import sbt._

object JspcPlugin extends AutoPlugin {

  object autoImport {
    lazy val jspcGeneratedClasses = settingKey[String]("generated jsp classes directoy name")
    lazy val jspcIncludes         = settingKey[String]("includes pattern for jsp precompile")
    lazy val jspcExcludes         = settingKey[String]("excludes pattern for jsp precompile")
    lazy val jspcTargetJarName    = settingKey[String]("The output file name")
    lazy val jspcSourceJVM        = settingKey[String]("JSP compiler source java version")
    lazy val jspcTargetJVM        = settingKey[String]("JSP compiler target java version")

    lazy val jspc = taskKey[File]("Compiles JSP")
  }

  import autoImport._
  import JarUtility._

  override def projectSettings: Seq[Def.Setting[_]] = Seq (
    jspcTargetJarName    := s"${name.value}_jsp.jar",
    jspcGeneratedClasses := "generated-jsp-classes",
    jspcIncludes         := """**/*.jsp""",
    jspcExcludes         := """**/.git/**""",
    jspcSourceJVM        := "1.8",
    jspcTargetJVM        := "1.8",
    jspc                 := {
      val files = (fullClasspath in Runtime).value
      val targetPath = target.value
      val classPath = (classDirectory in Compile).value
      val sourcePath = sourceDirectory.value
      val generatedJspDirName = jspcGeneratedClasses.value
      val stream = streams.value

      val generatedClasses = targetPath / generatedJspDirName
      val out = targetPath / autoImport.jspcTargetJarName.value

      BuildJsp(
        generatedClasses = generatedClasses.getPath,
        classesDirectory = classPath,
        webAppSourceDirectory = (sourcePath / "main" / "webapp").getPath,
        artifacts = files.files,
        webXmlFragment = (targetPath / "webfrag.xml").getPath,
        baseDir = baseDirectory.value,
        includes = jspcIncludes.value,
        excludes = jspcExcludes.value,
        sourceJVM = jspcSourceJVM.value,
        targetJVM = jspcTargetJVM.value)

      val mappings = Path.allSubpaths(generatedClasses).toSeq
      
      val cachePath = stream.cacheDirectory / "jspc"

      val packageConf = new sbt.Package.Configuration(mappings, out, Seq())
      pack(packageConf, cachePath, stream.log)

      out
    }
  )
}