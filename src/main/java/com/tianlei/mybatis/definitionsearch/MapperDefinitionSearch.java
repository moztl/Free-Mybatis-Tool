package com.tianlei.mybatis.definitionsearch;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiTypeParameterListOwner;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.Processor;
import com.intellij.util.xml.DomElement;
import com.tianlei.mybatis.service.JavaService;
import org.jetbrains.annotations.NotNull;

public class MapperDefinitionSearch extends QueryExecutorBase<XmlElement, PsiElement> {

    public MapperDefinitionSearch() {
        super(true);
    }

    @Override
    public void processQuery(@NotNull PsiElement queryParameters, @NotNull Processor<? super XmlElement> consumer) {
        if (!(queryParameters instanceof PsiTypeParameterListOwner)) return;
        Processor<DomElement> processor = domElement -> consumer.process(domElement.getXmlElement());
        JavaService.getInstance(queryParameters.getProject()).process(queryParameters, processor);
    }
}
