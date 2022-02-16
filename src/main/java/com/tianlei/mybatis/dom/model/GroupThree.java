package com.tianlei.mybatis.dom.model;

import com.intellij.util.xml.SubTagList;

import java.util.List;

public interface GroupThree extends GroupTwo {

    @SubTagList("selectKey")
    List<SelectKey> getSelectKey();

}
