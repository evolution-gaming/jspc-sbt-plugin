# sbt-jspc-plugin [![Build Status](https://travis-ci.org/evolution-gaming/sbt-jspc-plugin.svg)](https://travis-ci.org/evolution-gaming/sbt-jspc-plugin) [![Coverage Status](https://coveralls.io/repos/evolution-gaming/sbt-jspc-plugin/badge.svg)](https://coveralls.io/r/evolution-gaming/sbt-jspc-plugin) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/bd1e6d9f30a14352913fec06b3a04ad1)](https://www.codacy.com/app/evolution-gaming/sbt-jspc-plugin?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=evolution-gaming/sbt-jspc-plugin&amp;utm_campaign=Badge_Grade) [![version](https://api.bintray.com/packages/evolutiongaming/maven/sbt-jspc-plugin/images/download.svg)](https://bintray.com/evolutiongaming/maven/sbt-jspc-plugin/_latestVersion) [![License: MIT](https://img.shields.io/badge/License-MIT-yellowgreen.svg)](https://opensource.org/licenses/MIT)

JavaServer Pages (JSP) under sbt build. Currently Jetty8 is used a an implementation for compiler.

## Keys

outputFileName - output jar file name, by default ${project_name}_jsp.jar is used.

expected path to web.xml is
``` sourceDirectory / "main" / "webapp" / "WEB-INF" / "web.xml" ```

expected path to JSP is 
``` sourceDirectory / "main" / "webapp" ```

## Installation

Create a file in your project called project/license.sbt with the following contents:

```scala
externalResolvers += Resolver.bintrayIvyRepo("evolutiongaming", "sbt-plugins") 
 
addSbtPlugin( "com.evolutiongaming" % "sbt-jspc-plugin" % "1.0.0")
```

## Usage
```scala
lazy val web = 
Project("web", file("web")).enablePlugins(JspcPlugin)
```

then ```sbt web\jspc``` will compile JSP under ``` ```