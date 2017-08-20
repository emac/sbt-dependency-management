package cn.emacoo.sbt.dm

import cn.emacoo.sbt.dm.DmKeys._
import cn.emacoo.sbt.dm.ivy.IvyProxy
import cn.emacoo.sbt.dm.modules.ImportMavenBomModule
import sbt.Keys._
import sbt.Setting

/**
  * @author Emac
  * @since 2017-08-07
  */
object DmDefaults {

  /*
   * Module: Import Maven BOMs
   */
  lazy val importBomsSettings: Seq[Setting[_]] = Seq(
    bomDependencies := Set.empty,
    importBoms := {
      val patch = ImportMavenBomModule.generatePatch(bomDependencies.value, libraryDependencies.value, dependencyOverrides.value, IvyProxy(ivySbt.value, streams.value.log), streams.value.log)
      ImportMavenBomModule.writePatch(patch, target.value)
      patch
    },
    dependencyOverrides ++= ImportMavenBomModule.readPatch(target.value)
  )
}
