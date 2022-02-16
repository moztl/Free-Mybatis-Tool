package com.tianlei.mybatis.dom.model;

import com.intellij.psi.PsiClass;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import com.tianlei.mybatis.dom.converter.AliasConverter;
import org.jetbrains.annotations.NotNull;

public interface Association extends GroupFour, ResultMapGroup, PropertyGroup {

    @NotNull
    @Attribute("javaType")
    @Convert(AliasConverter.class)
    GenericAttributeValue<PsiClass> getJavaType();
}
