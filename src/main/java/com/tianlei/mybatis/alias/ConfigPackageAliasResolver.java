package com.tianlei.mybatis.alias;

import com.google.common.collect.Lists;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.Processor;
import com.tianlei.mybatis.dom.model.Package;
import com.tianlei.mybatis.util.MapperUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class ConfigPackageAliasResolver extends PackageAliasResolver {

    public ConfigPackageAliasResolver(Project project) {
        super(project);
    }

    @NotNull
    @Override
    public Collection<String> getPackages(@Nullable PsiElement element) {
        final ArrayList<String> result = Lists.newArrayList();
        MapperUtils.processConfiguredPackage(project, new Processor<Package>() {
            @Override
            public boolean process(Package pkg) {
                result.add(pkg.getName().getStringValue());
                return true;
            }
        });
        return result;
    }

}
