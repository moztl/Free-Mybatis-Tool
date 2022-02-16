package com.tianlei.mybatis.generate;

import com.intellij.psi.PsiMethod;
import com.tianlei.mybatis.dom.model.GroupTwo;
import com.tianlei.mybatis.dom.model.Mapper;
import org.jetbrains.annotations.NotNull;

public class InsertGenerator extends StatementGenerator {

    public InsertGenerator(@NotNull String... patterns) {
        super(patterns);
    }

    @NotNull
    @Override
    protected GroupTwo getTarget(@NotNull Mapper mapper, @NotNull PsiMethod method) {
        return mapper.addInsert();
    }

    @NotNull
    @Override
    public String getId() {
        return "InsertGenerator";
    }

    @NotNull
    @Override
    public String getDisplayText() {
        return "Insert Statement";
    }
}
