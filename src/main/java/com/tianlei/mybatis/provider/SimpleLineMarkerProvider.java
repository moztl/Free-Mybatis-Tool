package com.tianlei.mybatis.provider;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Collection;
import java.util.Optional;

public abstract class SimpleLineMarkerProvider<F extends PsiElement, T> extends RelatedItemLineMarkerProvider {

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        if (!isTheElement(element)) {
            return;
        }
        Optional<? extends T[]> processResult = apply((F) element);
        if (processResult.isPresent()) {
            T[] arrays = processResult.get();
            NavigationGutterIconBuilder navigationGutterIconBuilder = NavigationGutterIconBuilder.create(getIcon());
            if (arrays.length > 0) {
                navigationGutterIconBuilder.setTooltipTitle(getTooltip(arrays[0], element));
            }
            navigationGutterIconBuilder.setTargets(arrays);
            RelatedItemLineMarkerInfo<PsiElement> lineMarkerInfo = navigationGutterIconBuilder.createLineMarkerInfo(element);
            result.add(lineMarkerInfo);
        }
    }

    public abstract boolean isTheElement(@NotNull PsiElement element);

    @NotNull
    public abstract Optional<? extends T[]> apply(@NotNull F from);

    @NotNull
    public abstract String getTooltip(T array, @NotNull PsiElement target);

    @NotNull
    public abstract Icon getIcon();

}
