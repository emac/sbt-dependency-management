package cn.emacoo.sbt.dm

import sbt._

object XParent extends AutoPlugin {

  override def trigger = allRequirements

  lazy val xtask = taskKey[String]("xtask")
  lazy val xparam = settingKey[String]("xparam")

  override lazy val projectSettings = Seq(
    xtask := {
      XTask(xparam.value)
    },
    xparam := "foo"
  )

  object XTask {
    def apply(param: String): String = {
      println("=======================")
      println(param)
      println("=======================")
      param
    }
  }
}