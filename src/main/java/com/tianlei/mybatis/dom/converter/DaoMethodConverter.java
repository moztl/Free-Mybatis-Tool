package com.tianlei.mybatis.dom.converter;

import com.intellij.psi.PsiMethod;
import com.intellij.util.xml.ConvertContext;
import com.tianlei.mybatis.dom.model.Mapper;
import com.tianlei.mybatis.util.JavaUtils;
import com.tianlei.mybatis.util.MapperUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

public class DaoMethodConverter extends ConverterAdaptor<PsiMethod> {

    @Nullable
    @Override
    public PsiMethod fromString(@Nullable @NonNls String id, ConvertContext context) {
        Mapper mapper = MapperUtils.getMapper(context.getInvocationElement());
        return JavaUtils.findMethod(context.getProject(), MapperUtils.getNamespace(mapper), id).orElse(null);
    }

}