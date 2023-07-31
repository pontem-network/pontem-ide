// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package ide;

import com.intellij.lang.IdeLanguageCustomization;
import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;
import org.move.lang.MoveLanguage;

import java.util.Collections;
import java.util.List;

public class PontemIdeLanguageCustomization extends IdeLanguageCustomization {
  @NotNull
  @Override
  public List<Language> getPrimaryIdeLanguages() {
    return Collections.singletonList(MoveLanguage.INSTANCE);
  }
}
