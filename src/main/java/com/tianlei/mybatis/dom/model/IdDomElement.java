package com.tianlei.mybatis.dom.model;

import com.intellij.util.xml.*;

public interface IdDomElement extends DomElement {

    @Required
    @NameValue
    @Attribute("id")
    GenericAttributeValue<String> getId();

    void setValue(String content);
}
