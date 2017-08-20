package cn.emacoo.sbt.dm

import cn.emacoo.sbt.dm.DmDefaults._
import sbt._

object DependencyManagementPlugin extends AutoPlugin {

  override def trigger = allRequirements

  override lazy val projectSettings = importBomsSettings
}