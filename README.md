# Dependency Management Plugin for SBT

Inspired by [dependency-management-plugin](https://github.com/spring-gradle-plugins/dependency-management-plugin) for Gradle, this plugin aims to ease the dependency management work by leveraging the power of [Maven BOM](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#Importing_Dependencies), especially for large SBT projects with hundreds of dependencies. 

Current version: `1.0.0-SNAPSHOT`

# How to use?

1.Add plugin in plugins.sbt

```
addSbtPlugin("cn.emacoo" % "dependency-management" % "1.0.0-SNAPSHOT")
```

2.Configure plugin in build.sbt

```scala
import cn.emacoo.sbt.dm.DmKeys._

bomDependencies ++= Set(
  "io.spring.platform" % "platform-bom" % "1.0.1.RELEASE"
)

libraryDependencies ++= Seq(
  "org.springframework.integration" % "spring-integration-core" % "_"
)
```

Design notes:

- Unlike Maven/Gradle, to add a library dependency to a SBT project, you must give a concrete version, i.e., you cannot bypass the version. So here a workaround is to give a fake version "_", which will be later replaced with a concrete version resolved from the imported BOM dependencies by this plugin.
- A library's version is resolved in the following sequence, dependencyOverrides, bomDependencies and libraryDependencies.

3.Build project

```
sbt clean importBoms;sbt compile
```

Limitation:

- In current version, you'll have to run SBT build twice (with different tasks) to make it work. The short reason is there's a cycle dependency between dependencyOverrides and this plugin. I will try to solve this problem in future version. Stay tuned.  
