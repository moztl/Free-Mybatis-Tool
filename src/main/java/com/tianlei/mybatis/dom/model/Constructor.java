package com.tianlei.mybatis.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;

import java.util.List;

public interface Constructor extends DomElement {

    @SubTagList("arg")
    List<Arg> getArgs();

    @SubTagList("idArg")
    List<IdArg> getIdArgs();
}
