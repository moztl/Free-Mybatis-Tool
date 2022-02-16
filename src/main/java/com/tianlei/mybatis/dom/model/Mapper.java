package com.tianlei.mybatis.dom.model;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Mapper extends DomElement {

    @NotNull
    @SubTagsList({"insert", "update", "delete", "select"})
    List<IdDomElement> getDaoElements();

    @Required
    @NameValue
    @NotNull
    @Attribute("namespace")
    GenericAttributeValue<String> getNamespace();

    @NotNull
    @SubTagList("resultMap")
    List<ResultMap> getResultMaps();

    @NotNull
    @SubTagList("parameterMap")
    List<ParameterMap> getParameterMaps();

    @NotNull
    @SubTagList("sql")
    List<Sql> getSqls();

    @NotNull
    @SubTagList("insert")
    List<Insert> getInserts();

    @NotNull
    @SubTagList("update")
    List<Update> getUpdates();

    @NotNull
    @SubTagList("delete")
    List<Delete> getDeletes();

    @NotNull
    @SubTagList("select")
    List<Select> getSelects();

    @SubTagList("select")
    Select addSelect();

    @SubTagList("update")
    Update addUpdate();

    @SubTagList("insert")
    Insert addInsert();

    @SubTagList("delete")
    Delete addDelete();
}
