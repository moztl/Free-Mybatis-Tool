package com.tianlei.mybatis.intention;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.util.PsiTreeUtil;
import com.tianlei.mybatis.annotation.Annotation;
import com.tianlei.mybatis.util.JavaUtils;
import org.jetbrains.annotations.NotNull;

public class GenerateParamChooser extends JavaFileIntentionChooser {

    public static final JavaFileIntentionChooser INSTANCE = new GenerateParamChooser();

    @Override
    public boolean isAvailable(@NotNull PsiElement element) {
        PsiParameter parameter = PsiTreeUtil.getParentOfType(element, PsiParameter.class);
        PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        return (null != parameter && !JavaUtils.isAnnotationPresent(parameter, Annotation.PARAM)) ||
                (null != method && !JavaUtils.isAllParameterWithAnnotation(method, Annotation.PARAM));
    }
}
