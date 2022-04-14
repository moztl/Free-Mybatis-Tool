package com.tianlei.mybatis.util;

import com.intellij.openapi.util.IconLoader;
import com.intellij.util.PlatformIcons;

import javax.swing.*;

public interface Icons {

    Icon MYBATIS_LOGO = IconLoader.getIcon("/javaee/persistenceId.png", Icons.class);

    Icon PARAM_COMPLETION_ICON = PlatformIcons.PARAMETER_ICON;

    Icon MAPPER_LINE_MARKER_ICON = IconLoader.getIcon("/images/mapper_method.png", Icons.class);

    Icon STATEMENT_LINE_MARKER_ICON = IconLoader.getIcon("/images/statement.png", Icons.class);

    Icon SPRING_INJECTION_ICON = IconLoader.getIcon("/images/injection.png", Icons.class);
}