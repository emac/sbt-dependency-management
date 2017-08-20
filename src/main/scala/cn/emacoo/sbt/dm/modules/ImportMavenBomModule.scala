package cn.emacoo.sbt.dm.modules

import java.io.{File, PrintWriter}

import cn.emacoo.sbt.dm.ivy.IvyProxy
import sbt.Path._
import sbt.{Logger, ModuleID}

import scala.collection.mutable
import scala.util.{Failure, Success}

/**
  * @author Emac
  * @since 2017-08-07
  */
object ImportMavenBomModule {

  val BASE_DIR = "import-boms"
  val PATCH_FILE = "dependency-overrides-patch"

  /**
    * Tries to resolve the given BOM modules and generate a dependencyOverrides patch based on the managed dependencies information in the BOMs.
    *
    * @param boms
    * @param libraryDependencies
    * @param dependencyOverrides
    * @param ivyProxy
    * @param log
    * @return
    */
  def generatePatch(boms: Set[ModuleID], libraryDependencies: Seq[ModuleID], dependencyOverrides: Set[ModuleID], ivyProxy: IvyProxy, log: Logger): Set[ModuleID] = {
    // 1 resolve BOMs into ModuleIDs
    val bomDependencies = mutable.Set[ModuleID]()
    boms.foreach(bom => {
      ivyProxy.resolveBom(bom) match {
        case Success(dependencies) => bomDependencies ++= dependencies
        case Failure(exception) => log.warn(exception.getMessage)
      }
    })
    // 2 generate a dependencyOverrides patch
    val patch = bomDependencies.filter(m => {
      // remove those not in libraryDependencies, i.e., bomDependencies override libraryDependencies
      libraryDependencies.exists(l => l.organization == m.organization && l.name == m.name)
    }).filter(m => {
      // remove those already in dependencyOverrides except for the same revision for they might be added by this plugin, i.e., dependencyOverrides override bomDependencies
      !dependencyOverrides.exists(d => d.organization == m.organization && d.name == m.name) ||
        dependencyOverrides.exists(d => d.organization == m.organization && d.name == m.name && d.revision == m.revision)
    }).toSet

    patch
  }

  def writePatch(patch: Set[ModuleID], targetDir: File) = {
    val baseDir = targetDir / BASE_DIR
    baseDir.mkdirs()
    val patchFile = baseDir / PATCH_FILE
    if (patchFile.exists()) {
      patchFile.delete()
    }
    val pw = new PrintWriter(patchFile)
    try
      patch.foreach(m => pw.println(m.toString))
    finally
      pw.close()
  }

  def readPatch(targetDir: File): Set[ModuleID] = {
    val patchFile = targetDir / BASE_DIR / PATCH_FILE
    if (!patchFile.isFile) {
      return Set.empty
    }
    io.Source.fromFile(patchFile).getLines().map(line => {
      val parts = line.split(":")
      ModuleID(parts(0), parts(1), parts(2))
    }).toSet
  }
}
