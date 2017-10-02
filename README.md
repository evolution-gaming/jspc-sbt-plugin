# jspc-sbt-plugin

JavaServer Pages (JSP) under sbt build.

## Keys

outputFileName - output jar file name, by default ${project_name}_jsp.jar is used

## Example
```
lazy val web = (Project("web", file("web"))
enablePlugins JspcPlugin
```

then 

```sbt web\jspc```

will compile jspc 