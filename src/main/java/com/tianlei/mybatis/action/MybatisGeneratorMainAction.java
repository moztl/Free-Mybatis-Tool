package com.tianlei.mybatis.action;


import com.intellij.database.psi.DbTable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import com.tianlei.mybatis.ui.MybatisGeneratorMainUI;

public class MybatisGeneratorMainAction extends AnAction {


    /**
     * 点击后打开插件主页面
     */
    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiElement[] psiElements = e.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        if (psiElements == null || psiElements.length == 0) {
            Messages.showMessageDialog("Please select one or more tables", "Notice", Messages.getInformationIcon());
            return;
        }
        for (PsiElement psiElement : psiElements) {
            if (!(psiElement instanceof DbTable)) {
                Messages.showMessageDialog("Please select one or more tables", "Notice", Messages.getInformationIcon());
                return;
            }
        }
        new MybatisGeneratorMainUI(e);
    }

}
