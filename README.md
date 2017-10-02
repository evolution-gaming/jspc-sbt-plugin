# jspc-sbt-plugin

JavaServer Pages (JSP) under sbt build.

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
 
addSbtPlugin( "com.evolutiongaming" % "sbt-jspc-plugin" % "0.8.12")
```

## Usage
```
lazy val web = 
Project("web", file("web")).enablePlugins(JspcPlugin)
```

then ```sbt web\jspc``` will compile JSP under ``` ```