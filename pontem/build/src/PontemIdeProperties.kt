// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import org.jetbrains.intellij.build.*
import org.jetbrains.intellij.build.impl.PluginLayout
import java.nio.file.Files
import java.nio.file.Path

class PontemIdeProperties(communityHome: Path) : ProductProperties() {

  override val baseFileName: String get() = "pontem"

  override val customProductCode: String get() = "PONT"

  init {
    // from base
    reassignAltClickToMultipleCarets = true
    useSplash = true
    //productLayout.addPlatformSpec(TEST_FRAMEWORK_WITH_JAVA_RT)
    buildCrossPlatformDistribution = true
    //mavenArtifacts.additionalModules = mavenArtifacts.additionalModules.addAll(
    //  listOf(
    //    "intellij.java.compiler.antTasks",
    //    "intellij.platform.testFramework.common",
    //    "intellij.platform.testFramework.junit5",
    //    "intellij.platform.testFramework",
    //  ))

    // community edition
    platformPrefix = "Pontem"
    applicationInfoModule = "pontem.ide"
    brandingResourcePaths = listOf(communityHome.resolve("pontem/resources"))
    customJvmMemoryOptions = persistentMapOf("-Xms" to "256m", "-Xmx" to "1500m")
    scrambleMainJar = false
    buildSourcesArchive = true

    /* main module for JetBrains Client isn't available in the intellij-community project,
   so this property is set only when PyCharm Community is built from the intellij-ultimate project. */
    embeddedJetBrainsClientMainModule = null

    productLayout.mainModules = listOf("pontem.ide.main")
    //productLayout.mainModules = listOf("intellij.pycharm.community.main")
    productLayout.productApiModules = listOf("intellij.xml.dom")
    productLayout.productImplementationModules = listOf(
      "intellij.platform.main",
      "intellij.xml.dom.impl",
      //"pontem.ide",
    )
    productLayout.bundledPluginModules = mutableListOf(
      "intellij.platform.images",
      "intellij.toml",
      //"pontem.ide.customization"
    )
    //productLayout.bundledPluginModules.add("intellij.python.community.plugin")
    //productLayout.bundledPluginModules.add("intellij.toml")
    //productLayout.bundledPluginModules.add("pontem.ide.customization")
    //productLayout.bundledPluginModules.add("intellij-move.plugin")
    //productLayout.bundledPluginModules.add("intellij.pycharm.community.customization")
    //productLayout.bundledPluginModules.addAll(
    //  Files.readAllLines(communityHome.resolve("pontem/build/bundled-plugin-list.txt")))

    //productLayout.pluginLayouts = CommunityRepositoryModules.COMMUNITY_REPOSITORY_PLUGINS
    productLayout.pluginLayouts =
      persistentListOf(
        PluginLayout.plugin("intellij.toml"),
        //PluginLayout.plugin("pontem.ide.customization"),
        //PluginLayout.plugin("intellij-move.plugin")
      )
    //productLayout.pluginLayouts = CommunityRepositoryModules.COMMUNITY_REPOSITORY_PLUGINS.add(
    //  PluginLayout.plugin(listOf(
    //    "pontem.ide.customization",
    //    //"intellij.pycharm.community.customization",
    //    //"intellij.pycharm.community.ide.impl",
    //  )
    //  ))
    //productLayout.pluginModulesToPublish = persistentSetOf("intellij-move.main")
    //productLayout.pluginModulesToPublish = persistentSetOf("intellij.python.community.plugin")
  }

  override suspend fun copyAdditionalFiles(context: BuildContext, targetDirectory: String) {
    copyAdditionalFilesBlocking(context, targetDirectory)
  }

  private fun copyAdditionalFilesBlocking(context: BuildContext, targetDirectory: String) {
    // add module sources (to use plugin as dependency)
    //val tasks = BuildTasks.create(context)
    //tasks.zipSourcesOfModulesBlocking(
    //  modules = listOf(
    //    //"intellij.python.community",
    //    "pontem.ide"
    //  ),
    //  targetFile = Path.of("$targetDirectory/lib/src/pontem-openapi-src.zip")
    //)

    // keymap reference card, see python/help/
    //FileSet(Path.of(getKeymapReferenceDirectory(context)))
    //  .include("*.pdf")
    //  .copyToDir(Path.of(targetDirectory, "help"))

    FileSet(context.paths.communityHomeDir)
      .include("LICENSE.txt")
      .include("NOTICE.txt")
      .copyToDir(Path.of(targetDirectory, "license"))
  }

  private fun getKeymapReferenceDirectory(context: BuildContext) = "${context.paths.projectHome}/python/help"

  override fun getSystemSelector(appInfo: ApplicationInfoProperties, buildNumber: String): String {
    return "PontemIDE${appInfo.majorVersion}.${appInfo.minorVersionMainPart}"
  }

  override fun getBaseArtifactName(appInfo: ApplicationInfoProperties, buildNumber: String): String = "pontem-$buildNumber"

  override fun createWindowsCustomizer(projectHome: String): WindowsDistributionCustomizer? {
    return null
    //return PyCharmCommunityWindowsDistributionCustomizer(Path.of(projectHome))
  }
  //override fun createWindowsCustomizer(projectHome: String): WindowsDistributionCustomizer {
  //  return PyCharmCommunityWindowsDistributionCustomizer(Path.of(projectHome))
  //}

  override fun createLinuxCustomizer(projectHome: String): LinuxDistributionCustomizer {
    return PontemLinuxDistributionCustomizer(Path.of(projectHome))
    //return object : PyCharmCommunityLinuxDistributionCustomizer(Path.of(projectHome)) {
    //  init {
    //    snapName = "pycharm-community"
    //    snapDescription = "Python IDE for professional developers. Save time while PyCharm takes care of the routine. " +
    //     "Focus on bigger things and embrace the keyboard-centric approach to get the most of PyCharm’s many productivity features."
    //  }
    //}
  }

  override fun createMacCustomizer(projectHome: String): MacDistributionCustomizer? {
    return null
    //return PyCharmCommunityMacDistributionCustomizer(Path.of(projectHome))
  }

  override fun getOutputDirectoryName(appInfo: ApplicationInfoProperties) = "pontem-ide"
  //override fun getOutputDirectoryName(appInfo: ApplicationInfoProperties) = "pycharm-ce"
}

private class PyCharmCommunityWindowsDistributionCustomizer(projectHome: Path) : WindowsDistributionCustomizer() {
  init {
    icoPath = "$projectHome/pontem/resources/pontem256.ico"
    //icoPathForEAP = "$projectHome/pontem/resources/PyCharmCore_EAP.ico"
    installerImagesPath = "$projectHome/pontem/build/resources"
    fileAssociations = listOf("move")
  }

  override fun getFullNameIncludingEdition(appInfo: ApplicationInfoProperties) = "Pontem IDE"

  override fun getUninstallFeedbackPageUrl(appInfo: ApplicationInfoProperties): String {
    return "https://www.jetbrains.com/pycharm/uninstall/?version=${appInfo.productCode}-${appInfo.majorVersion}.${appInfo.minorVersion}"
  }
}

private open class PontemLinuxDistributionCustomizer(projectHome: Path) : LinuxDistributionCustomizer() {
  init {
    iconPngPath = "$projectHome/pontem/resources/pontem128.png"
  }

  override fun getRootDirectoryName(appInfo: ApplicationInfoProperties, buildNumber: String): String {
    return "pontem-ide-${if (appInfo.isEAP) buildNumber else appInfo.fullVersion}"
  }
}

private class PyCharmCommunityMacDistributionCustomizer(projectHome: Path) : MacDistributionCustomizer() {

  init {
    icnsPath = "$projectHome/pontem/resources/pontem256.icns"
    bundleIdentifier = "com.pontem.ide"
    dmgImagePath = "$projectHome/pontem/build/dmg_background.tiff"
  }

  override fun getRootDirectoryName(appInfo: ApplicationInfoProperties, buildNumber: String): String {
    val suffix = if (appInfo.isEAP) " ${appInfo.majorVersion}.${appInfo.minorVersion} EAP" else ""
    return "Pontem IDE ${suffix}.app"
  }

  override fun getCustomIdeaProperties(appInfo: ApplicationInfoProperties): Map<String, String> {
    return mapOf("ide.mac.useNativeClipboard" to "false")
  }
}
