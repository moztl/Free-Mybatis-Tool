package com.tianlei.mybatis.inspection;

import com.intellij.codeInspection.LocalQuickFix;
import org.jetbrains.annotations.NotNull;

public abstract class GenericQuickFix implements LocalQuickFix {

    @NotNull
    @Override
    public String getFamilyName() {
        return getName();
    }

}
