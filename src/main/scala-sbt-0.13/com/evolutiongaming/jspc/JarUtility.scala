package com.evolutiongaming.jspc

import java.io.File
import sbt.Package

object JarUtility {
  def pack(conf: Package.Configuration, cacheStoreFactory: File, log: sbt.Logger): Unit = {
    Package(conf, cacheStoreFactory, log)
  }
}