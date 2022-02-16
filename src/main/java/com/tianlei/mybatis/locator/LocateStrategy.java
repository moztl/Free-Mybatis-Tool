package com.tianlei.mybatis.locator;

import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;

public abstract class LocateStrategy {

    public abstract boolean apply(@NotNull PsiClass clazz);

}
