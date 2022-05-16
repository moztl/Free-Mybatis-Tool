package com.tianlei.mybatis.provider;

import com.google.common.collect.ImmutableList;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import com.tianlei.mybatis.dom.model.*;
import com.tianlei.mybatis.util.Icons;
import com.tianlei.mybatis.util.JavaUtils;
import com.tianlei.mybatis.util.MapperUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Objects;
import java.util.Optional;

public class StatementLineMarkerProvider extends SimpleLineMarkerProvider<XmlToken, PsiElement> {

    private static final String MAPPER_CLASS = Mapper.class.getSimpleName().toLowerCase();
    private static final ImmutableList<String> TARGET_TYPES = ImmutableList.of(
            Select.class.getSimpleName().toLowerCase(),
            Update.class.getSimpleName().toLowerCase(),
            Insert.class.getSimpleName().toLowerCase(),
            Delete.class.getSimpleName().toLowerCase()
    );

    @Override
    public boolean isTheElement(@NotNull PsiElement element) {
        return element instanceof XmlToken
                && MapperUtils.isElementWithinMybatisFile(element)
                && isTargetType((XmlToken) element);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public Optional<? extends PsiElement[]> apply(@NotNull XmlToken from) {
        DomElement domElement = DomUtil.getDomElement(from);
        if (Objects.isNull(domElement)) {
            return Optional.empty();
        } else if (domElement instanceof IdDomElement) {
            return JavaUtils.findMethods(from.getProject(), (IdDomElement) domElement);
        } else {
            XmlTag xmlTag = domElement.getXmlTag();
            if (Objects.isNull(xmlTag)) {
                return Optional.empty();
            }
            String namespace = xmlTag.getAttributeValue("namespace");
            if (StringUtils.isEmpty(namespace)) {
                return Optional.empty();
            }
            return Objects.nonNull(domElement.getModule()) ?
                    JavaUtils.findClazzesWithModule(from.getProject(), namespace, domElement.getModule())
                    : JavaUtils.findClazzes(from.getProject(), namespace);
        }
    }

    private boolean isTargetType(XmlToken token) {
        Boolean targetType = false;
        if (MAPPER_CLASS.equals(token.getText())) {
            PsiElement nextSibling = token.getNextSibling();
            if (nextSibling instanceof PsiWhiteSpace) {
                targetType = true;
            }
        }
        if (targetType == false) {
            if (TARGET_TYPES.contains(token.getText())) {
                PsiElement parent = token.getParent();
                if (parent instanceof XmlTag) {
                    PsiElement nextSibling = token.getNextSibling();
                    if (nextSibling instanceof PsiWhiteSpace) {
                        targetType = true;
                    }
                }
            }
        }
        return targetType;
    }

    @NotNull
    @Override
    public String getTooltip(PsiElement element, @NotNull PsiElement target) {
        String text = null;
        if (element instanceof PsiMethod) {
            PsiMethod psiMethod = (PsiMethod) element;
            PsiClass containingClass = psiMethod.getContainingClass();
            if (containingClass != null) {
                text = containingClass.getName() + "#" + psiMethod.getName();
            }
        }
        if (text == null && element instanceof PsiClass) {
            PsiClass psiClass = (PsiClass) element;
            text = psiClass.getQualifiedName();
        }
        if (text == null) {
            text = target.getContainingFile().getText();
        }
        return "Data access object found - " + text;
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return Icons.STATEMENT_LINE_MARKER_ICON;
    }

}
