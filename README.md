# sbt-jspc-plugin [![Build Status](https://travis-ci.org/evolution-gaming/jspc-sbt-plugin.svg)](https://travis-ci.org/evolution-gaming/jspc-sbt-plugin) [ ![version](https://api.bintray.com/packages/evolutiongaming/sbt-plugins/sbt-jspc-plugin/images/download.svg) ](https://bintray.com/evolutiongaming/sbt-plugins/sbt-jspc-plugin/_latestVersion) [![License: MIT](https://img.shields.io/badge/License-MIT-yellowgreen.svg)](https://opensource.org/licenses/MIT)

JavaServer Pages (JSP) under sbt build. Currently Jetty8 is used a an implementation for compiler.

## Keys

outputFileName - output jar file name, by default ${project_name}_jsp.jar is used.

expected path to web.xml is
``` sourceDirectory / "main" / "webapp" / "WEB-INF" / "web.xml" ```

expected path to JSP is 
``` sourceDirectory / "main" / "webapp" ```

## Installation

Create a file in your project called project/license.sbt with the following contents:

```
externalResolvers += Resolver.bintrayIvyRepo("evolutiongaming", "sbt-plugins") 
 
addSbtPlugin( "com.evolutiongaming" % "sbt-jspc-plugin" % "0.8.15")
```

## Usage
```
lazy val web = 
Project("web", file("web")).enablePlugins(JspcPlugin)
```

then ```sbt web\jspc``` will compile JSP under ``` ```