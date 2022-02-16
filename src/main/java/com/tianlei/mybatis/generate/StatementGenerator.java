package com.tianlei.mybatis.generate;

import com.google.common.base.Function;
import com.google.common.collect.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.CommonProcessors.CollectProcessor;
import com.tianlei.mybatis.dom.model.GroupTwo;
import com.tianlei.mybatis.dom.model.Mapper;
import com.tianlei.mybatis.service.JavaService;
import com.tianlei.mybatis.setting.MybatisSetting;
import com.tianlei.mybatis.service.EditorService;
import com.tianlei.mybatis.ui.ListSelectionListener;
import com.tianlei.mybatis.ui.UiComponentFacade;
import com.tianlei.mybatis.util.CollectionUtils;
import com.tianlei.mybatis.util.JavaUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public abstract class StatementGenerator {

    public static final StatementGenerator UPDATE_GENERATOR = new UpdateGenerator("update", "modify", "set");

    public static final StatementGenerator SELECT_GENERATOR = new SelectGenerator("select", "get", "look", "find", "list", "search", "count", "query");

    public static final StatementGenerator DELETE_GENERATOR = new DeleteGenerator("del", "cancel");

    public static final StatementGenerator INSERT_GENERATOR = new InsertGenerator("insert", "add", "new");

    public static final Set<StatementGenerator> ALL = ImmutableSet.of(UPDATE_GENERATOR, SELECT_GENERATOR, DELETE_GENERATOR, INSERT_GENERATOR);

    private static final Function<Mapper, String> FUN = new Function<Mapper, String>() {
        @Override
        public String apply(Mapper mapper) {
            VirtualFile vf = mapper.getXmlTag().getContainingFile().getVirtualFile();
            if (null == vf) return "";
            return vf.getCanonicalPath();
        }
    };

    public static Optional<PsiClass> getSelectResultType(@Nullable PsiMethod method) {
        if (null == method) {
            return Optional.empty();
        }
        PsiType returnType = method.getReturnType();
        if (returnType instanceof PsiPrimitiveType && returnType != PsiType.VOID) {
            return JavaUtils.findClazz(method.getProject(), ((PsiPrimitiveType) returnType).getBoxedTypeName());
        } else if (returnType instanceof PsiClassReferenceType) {
            PsiClassReferenceType type = (PsiClassReferenceType) returnType;
            if (type.hasParameters()) {
                PsiType[] parameters = type.getParameters();
                if (parameters.length == 1) {
                    type = (PsiClassReferenceType) parameters[0];
                }
            }
            return Optional.ofNullable(type.resolve());
        }
        return Optional.empty();
    }

    private static void doGenerate(@NotNull final StatementGenerator generator, @NotNull final PsiMethod method) {
        (new WriteCommandAction.Simple(method.getProject(), new PsiFile[]{method.getContainingFile()}) {
            protected void run() throws Throwable {
                generator.execute(method);
            }
        }).execute();
    }

    public static void applyGenerate(@Nullable final PsiMethod method) {
        if (null == method) return;
        final Project project = method.getProject();
        final Object[] generators = getGenerators(method);
        if (1 == generators.length) {
            ((StatementGenerator) generators[0]).execute(method);
        } else {
            JBPopupFactory.getInstance().createListPopup(
                    new BaseListPopupStep("[ Statement type for method: " + method.getName() + "]", generators) {
                        @Override
                        public PopupStep onChosen(Object selectedValue, boolean finalChoice) {
                            return this.doFinalStep(new Runnable() {
                                public void run() {
                                    WriteCommandAction.runWriteCommandAction(project, new Runnable() {
                                        public void run() {
                                            StatementGenerator.doGenerate((StatementGenerator) selectedValue, method);
                                        }
                                    });
                                }
                            });
                        }
                    }
            ).showInFocusCenter();
        }
    }

    @NotNull
    public static StatementGenerator[] getGenerators(@NotNull PsiMethod method) {
        GenerateModel model = MybatisSetting.getInstance().getStatementGenerateModel();
        String target = method.getName();
        List<StatementGenerator> result = Lists.newArrayList();
        for (StatementGenerator generator : ALL) {
            if (model.matchesAny(generator.getPatterns(), target)) {
                result.add(generator);
            }
        }
        return CollectionUtils.isNotEmpty(result) ? result.toArray(new StatementGenerator[result.size()]) : ALL.toArray(new StatementGenerator[ALL.size()]);
    }

    private Set<String> patterns;

    public StatementGenerator(@NotNull String... patterns) {
        this.patterns = Sets.newHashSet(patterns);
    }

    public void execute(@NotNull final PsiMethod method) {
        PsiClass psiClass = method.getContainingClass();
        if (null == psiClass) return;
        CollectProcessor processor = new CollectProcessor();
        JavaService.getInstance(method.getProject()).process(psiClass, processor);
        final List<Mapper> mappers = Lists.newArrayList(processor.getResults());
        if (1 == mappers.size()) {
            setupTag(method, (Mapper) Iterables.getOnlyElement(mappers, (Object) null));
        } else if (mappers.size() > 1) {
            Collection<String> paths = Collections2.transform(mappers, FUN);
            UiComponentFacade.getInstance(method.getProject()).showListPopup("Choose target mapper xml to generate", new ListSelectionListener() {
                @Override
                public void selected(int index) {
                    setupTag(method, mappers.get(index));
                }

                @Override
                public boolean isWriteAction() {
                    return true;
                }
            }, paths.toArray(new String[paths.size()]));
        }
    }

    private void setupTag(PsiMethod method, Mapper mapper) {
        GroupTwo target = getTarget(mapper, method);
        target.getId().setStringValue(method.getName());
        target.setValue(" ");
        XmlTag tag = target.getXmlTag();
        int offset = tag.getTextOffset() + tag.getTextLength() - tag.getName().length() + 1;
        EditorService editorService = EditorService.getInstance(method.getProject());
        editorService.format(tag.getContainingFile(), tag);
        editorService.scrollTo(tag, offset);
    }

    @Override
    public String toString() {
        return this.getDisplayText();
    }

    @NotNull
    protected abstract GroupTwo getTarget(@NotNull Mapper mapper, @NotNull PsiMethod method);

    @NotNull
    public abstract String getId();

    @NotNull
    public abstract String getDisplayText();

    public Set<String> getPatterns() {
        return patterns;
    }

    public void setPatterns(Set<String> patterns) {
        this.patterns = patterns;
    }

}
