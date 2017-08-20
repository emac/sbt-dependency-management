package cn.emacoo.sbt.dm.ivy

import org.apache.ivy.core.module.id.ModuleRevisionId
import org.apache.ivy.core.resolve.ResolveOptions
import sbt.{IvySbt, Logger, ModuleID, ResolveException}

import scala.collection.JavaConversions._
import scala.util.Try

/**
  * Wraps {@code IvySbt} to perform various Ivy actions.
  *
  * @author Emac
  * @since 2017-08-08
  */
case class IvyProxy(ivySbt: IvySbt, log: Logger) {

  val RESOLVE_CONF_BOM = "*(public)"
  val REGEX_INFO_MAVEN_DEPENDENCY_MANAGEMENT = "m:dependency.management__(.+)__(.+)__version".r

  /**
    * Tries to resolve the given BOM module. If it succeeds, turns each managed dependency into a {@code ModuleID}.
    *
    * @param bom
    * @return
    */
  def resolveBom(bom: ModuleID): Try[Set[ModuleID]] = Try {
    ivySbt.withIvy(log) { ivy =>
      val mrId = ModuleRevisionId.newInstance(bom.organization, bom.name, bom.revision)
      var bomModule = ivy.findModule(mrId)
      // resolve if not found
      if (bomModule == null) {
        val options = new ResolveOptions()
        options.setConfs(Array(RESOLVE_CONF_BOM))
        ivy.resolve(mrId, options, bom.revision.contains("SNAPSHOT"))
        bomModule = ivy.findModule(mrId)
      }
      // failed to resolve
      if (bomModule == null) {
        throw new ResolveException(Seq("Failed to resolve BOM module"), Seq(bom))
      }
      // turn each managed dependency into a module
      bomModule.getDescriptor.getExtraInfo.flatMap(i =>
        i._1 match {
          case REGEX_INFO_MAVEN_DEPENDENCY_MANAGEMENT(groupId, artifactId) => Some(ModuleID(groupId, artifactId, i._2.toString))
          case _ => None
        }
      ).toSet
    }
  }

}
