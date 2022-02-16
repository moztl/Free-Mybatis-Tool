package com.tianlei.mybatis.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.Required;
import com.intellij.util.xml.SubTagList;

import java.util.List;

public interface Discriminator extends DomElement {

    @Required
    @SubTagList("case")
    List<Case> getCases();

}
