package com.evolutiongaming.jspc

import java.io.File

import sbt.Package
import sbt.util.CacheStoreFactory

object JarUtility {

  def pack(conf: Package.Configuration, cacheStoreFactory: File, log: sbt.util.Logger): Unit = {
    Package(conf, CacheStoreFactory(cacheStoreFactory), log)
  }
}
