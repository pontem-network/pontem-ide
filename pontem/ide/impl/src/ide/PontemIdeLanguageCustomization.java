// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package ide;

import com.intellij.lang.IdeLanguageCustomization;
import com.intellij.lang.Language;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class PontemIdeLanguageCustomization extends IdeLanguageCustomization {
  @NotNull
  @Override
  public List<Language> getPrimaryIdeLanguages() {
    return ContainerUtil.createMaybeSingletonList(findMoveLanguageByID());
  }

  @Nullable
  private static Language findMoveLanguageByID() {
    return Language.findLanguageByID("Move");
  }
}
