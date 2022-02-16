package com.tianlei.mybatis.generate;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.tianlei.mybatis.dom.model.GroupTwo;
import com.tianlei.mybatis.dom.model.Mapper;
import com.tianlei.mybatis.dom.model.Select;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SelectGenerator extends StatementGenerator {

    public SelectGenerator(@NotNull String... patterns) {
        super(patterns);
    }

    @NotNull
    @Override
    protected GroupTwo getTarget(@NotNull Mapper mapper, @NotNull PsiMethod method) {
        Select select = mapper.addSelect();
        setupResultType(method, select);
        return select;
    }

    private void setupResultType(PsiMethod method, Select select) {
        Optional<PsiClass> clazz = StatementGenerator.getSelectResultType(method);
        if (clazz.isPresent()) {
            select.getResultType().setValue(clazz.get());
        }
    }

    @NotNull
    @Override
    public String getId() {
        return "SelectGenerator";
    }

    @NotNull
    @Override
    public String getDisplayText() {
        return "Select Statement";
    }
}
