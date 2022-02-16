package com.tianlei.mybatis.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;

import java.util.List;

public interface Cache extends DomElement {

    @SubTagList("property")
    List<Property> getProperties();

}
