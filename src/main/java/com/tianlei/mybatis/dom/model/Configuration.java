package com.tianlei.mybatis.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Configuration extends DomElement {

    @NotNull
    @SubTagList("typeAliases")
    List<TypeAliases> getTypeAliases();

}
