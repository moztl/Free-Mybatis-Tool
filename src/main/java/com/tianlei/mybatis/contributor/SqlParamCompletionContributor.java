package com.tianlei.mybatis.contributor;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.injected.editor.DocumentWindow;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.tianlei.mybatis.dom.model.IdDomElement;
import com.tianlei.mybatis.util.DomUtils;
import com.tianlei.mybatis.util.MapperUtils;

import java.util.List;
import java.util.Optional;

public class SqlParamCompletionContributor extends CompletionContributor {

    @Override
    public void fillCompletionVariants(CompletionParameters parameters, final CompletionResultSet result) {
        if (parameters.getCompletionType() != CompletionType.BASIC) {
            return;
        }

        PsiElement position = parameters.getPosition();
        PsiFile topLevelFile = position.getContainingFile().getOriginalFile();
        if (DomUtils.isMybatisFile(topLevelFile)) {
            if (shouldAddElement(position.getContainingFile(), parameters.getOffset())) {
                process(topLevelFile, result, position);
            }
        }
    }

    private void process(PsiFile xmlFile, CompletionResultSet result, PsiElement position) {
        PsiFile psiFile = position.getContainingFile();
        InjectedLanguageManager injectedLanguageManager = InjectedLanguageManager.getInstance(position.getProject());
        List<DocumentWindow> documentWindows = injectedLanguageManager.getCachedInjectedDocumentsInRange(psiFile, position.getTextRange());
        DocumentWindow documentWindow = documentWindows.isEmpty() ? null : documentWindows.get(0);
        if (null != documentWindow) {
            int offset = documentWindow.injectedToHost(position.getTextOffset());
            Optional<IdDomElement> idDomElement = MapperUtils.findParentIdDomElement(xmlFile.findElementAt(offset));
            if (idDomElement.isPresent()) {
                TestParamContributor.addElementForPsiParameter(position.getProject(), result, idDomElement.get());
                result.stopHere();
            }
        }
    }

    private boolean shouldAddElement(PsiFile file, int offset) {
        String text = file.getText();
        for (int i = offset - 1; i > 0; i--) {
            char c = text.charAt(i);
            if (c == '{' && text.charAt(i - 1) == '#') return true;
        }
        return false;
    }
}