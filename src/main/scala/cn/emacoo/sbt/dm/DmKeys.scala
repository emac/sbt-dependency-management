package cn.emacoo.sbt.dm

import sbt.{ModuleID, SettingKey, TaskKey}

/**
  * @author Emac
  * @since 2017-08-07
  */
object DmKeys {

  /*
   * Module: Import Maven BOMs
   */
  lazy val bomDependencies = SettingKey[Set[ModuleID]]("bom-dependencies", "Declares Maven BOM dependencies")

  lazy val importBoms = TaskKey[Set[ModuleID]]("import-boms", "Adds extra dependency overrides to dependencyOverrides based on the managed dependencies information in bomDependencies")
}
