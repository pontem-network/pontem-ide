// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

import com.intellij.openapi.diagnostic.LogLevel
import com.intellij.openapi.diagnostic.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.intellij.build.BuildTasks
import org.jetbrains.intellij.build.IdeaProjectLoaderUtil
import org.jetbrains.intellij.build.impl.BuildContextImpl

// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
object PontemIdeInstallersBuildTarget {
  @JvmStatic
  fun main(args: Array<String>) {
    @Suppress("RAW_RUN_BLOCKING")
    runBlocking(Dispatchers.Default) {
      val communityHome = IdeaProjectLoaderUtil.guessCommunityHome(javaClass)
      val context = BuildContextImpl.createContext(
        communityHome = communityHome,
        projectHome = communityHome.communityRoot,
        productProperties = PontemIdeProperties(communityHome.communityRoot),
      )
      BuildTasks.create(context).buildDistributions()
    }
  }
}