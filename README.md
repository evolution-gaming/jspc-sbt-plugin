# jspc-sbt-plugin

JavaServer Pages (JSP) under sbt build.

## Keys

outputFileName - output jar file name, by default ${project_name}_jsp.jar is used.

expected path to web.xml is
``` sourceDirectory / "main" / "webapp" / "WEB-INF" / "web.xml" ```

expected path to JSP is 
``` sourceDirectory / "main" / "webapp" ```

## Example
```
lazy val web = 
Project("web", file("web")).enablePlugins(JspcPlugin)
```

then ```sbt web\jspc``` will compile JSP under ``` ```