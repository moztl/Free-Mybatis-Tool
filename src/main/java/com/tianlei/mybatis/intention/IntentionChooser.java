package com.tianlei.mybatis.intention;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public interface IntentionChooser {

    boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file);

}
