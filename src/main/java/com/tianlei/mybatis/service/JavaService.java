package com.tianlei.mybatis.service;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.util.CommonProcessors;
import com.intellij.util.Processor;
import com.intellij.util.xml.DomElement;
import com.tianlei.mybatis.dom.model.IdDomElement;
import com.tianlei.mybatis.dom.model.Mapper;
import com.tianlei.mybatis.util.JavaUtils;
import com.tianlei.mybatis.util.MapperUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class JavaService {

    private Project project;

    private JavaPsiFacade javaPsiFacade;

    private EditorService editorService;

    public JavaService(Project project) {
        this.project = project;
        this.javaPsiFacade = JavaPsiFacade.getInstance(project);
        this.editorService = EditorService.getInstance(project);
    }

    public static JavaService getInstance(@NotNull Project project) {
        return project.getService(JavaService.class);
    }

    public Optional<PsiClass> getReferenceClazzOfPsiField(@NotNull PsiElement field) {
        if (!(field instanceof PsiField)) {
            return Optional.empty();
        }
        PsiType type = ((PsiField) field).getType();
        return type instanceof PsiClassReferenceType ? Optional.ofNullable(((PsiClassReferenceType) type).resolve()) : Optional.empty();
    }

    public Optional<DomElement> findStatement(@Nullable PsiMethod method) {
        CommonProcessors.FindFirstProcessor<DomElement> processor = new CommonProcessors.FindFirstProcessor<DomElement>();
        process(method, processor);
        return processor.isFound() ? Optional.ofNullable(processor.getFoundValue()) : Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public void process(@NotNull PsiMethod psiMethod, @NotNull Processor<IdDomElement> processor) {
        PsiClass psiClass = psiMethod.getContainingClass();
        if (null == psiClass) return;
        String id = psiClass.getQualifiedName() + "." + psiMethod.getName();
        String clazzModule = psiClass.getResolveScope().getDisplayName();
        for (Mapper mapper : MapperUtils.findMappers(psiMethod.getProject())) {
            String mapperModule = mapper.getModule().getModuleScope().getDisplayName();
            for (IdDomElement idDomElement : mapper.getDaoElements()) {
                if (clazzModule.equals(mapperModule) && MapperUtils.getIdSignature(idDomElement).equals(id)) {
                    processor.process(idDomElement);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void process(@NotNull PsiClass clazz, @NotNull Processor<Mapper> processor) {
        String ns = clazz.getQualifiedName();
        String clazzModule = clazz.getResolveScope().getDisplayName();
        for (Mapper mapper : MapperUtils.findMappers(clazz.getProject())) {
            String mapperModule = mapper.getModule().getModuleScope().getDisplayName();
            if (clazzModule.equals(mapperModule) && MapperUtils.getNamespace(mapper).equals(ns)) {
                processor.process(mapper);
            }
        }
    }

    public void process(@NotNull PsiElement target, @NotNull Processor processor) {
        if (target instanceof PsiMethod) {
            process((PsiMethod) target, processor);
        } else if (target instanceof PsiClass) {
            process((PsiClass) target, processor);
        }
    }

    public <T> Optional<T> findWithFindFirstProcessor(@NotNull PsiElement target) {
        CommonProcessors.FindFirstProcessor<T> processor = new CommonProcessors.FindFirstProcessor<T>();
        process(target, processor);
        return Optional.ofNullable(processor.getFoundValue());
    }

    public void importClazz(PsiJavaFile file, String clazzName) {
        if (!JavaUtils.hasImportClazz(file, clazzName)) {
            Optional<PsiClass> clazz = JavaUtils.findClazz(project, clazzName);
            PsiImportList importList = file.getImportList();
            if (clazz.isPresent() && null != importList) {
                PsiElementFactory elementFactory = javaPsiFacade.getElementFactory();
                PsiImportStatement statement = elementFactory.createImportStatement(clazz.get());
                importList.add(statement);
                editorService.format(file, statement);
            }
        }
    }
}

