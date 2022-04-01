package com.tianlei.mybatis.util;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PropertyUtil;
import com.intellij.psi.util.PsiTreeUtil;
import com.tianlei.mybatis.annotation.Annotation;
import com.tianlei.mybatis.dom.model.IdDomElement;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public final class JavaUtils {

    private JavaUtils() {
        throw new UnsupportedOperationException();
    }

    public static boolean isModelClazz(@Nullable PsiClass clazz) {
        return null != clazz && !clazz.isAnnotationType() && !clazz.isInterface() && !clazz.isEnum() && clazz.isValid();
    }

    @NotNull
    public static Optional<PsiField> findSettablePsiField(
            @NotNull final PsiClass clazz,
            @Nullable final String propertyName) {
        final PsiField field = PropertyUtil.findPropertyField(clazz, propertyName, false);
        return field != null ? Optional.of(field) : Optional.empty();
    }

    @NotNull
    public static PsiField[] findSettablePsiFields(final @NotNull PsiClass clazz) {
        final PsiField[] fields = clazz.getAllFields();
        final List<PsiField> settableFields = new ArrayList<>(fields.length);

        for (final PsiField f : fields) {
            final PsiModifierList modifiers = f.getModifierList();

            if (modifiers != null && (
                    modifiers.hasModifierProperty(PsiModifier.STATIC) ||
                            modifiers.hasModifierProperty(PsiModifier.FINAL))) {
                continue;
            }

            settableFields.add(f);
        }

        return settableFields.toArray(new PsiField[0]);
    }

    public static boolean isElementWithinInterface(@Nullable PsiElement element) {
        if (element instanceof PsiClass && ((PsiClass) element).isInterface()) {
            return true;
        }
        PsiClass type = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        return Optional.ofNullable(type).isPresent() && type.isInterface();
    }

    @NotNull
    public static Optional<PsiClass> findClazz(@NotNull Project project, @NotNull String clazzName) {
        return Optional.ofNullable(JavaPsiFacade.getInstance(project).findClass(clazzName, GlobalSearchScope.allScope(project)));
    }

    @NotNull
    public static Optional<PsiClass[]> findClazzes(@NotNull Project project, @NotNull String clazzName) {
        return Optional.ofNullable(JavaPsiFacade.getInstance(project).findClasses(clazzName, GlobalSearchScope.allScope(project)));
    }

    @NotNull
    public static Optional<PsiClass> findClazzWithModule(@NotNull Project project, @NotNull String clazzName, @NotNull Module module) {
        return Optional.ofNullable(JavaPsiFacade.getInstance(project).findClass(clazzName, GlobalSearchScope.moduleScope(module)));
    }

    @NotNull
    public static Optional<PsiClass[]> findClazzesWithModule(@NotNull Project project, @NotNull String clazzName, @NotNull Module module) {
        return Optional.ofNullable(JavaPsiFacade.getInstance(project).findClasses(clazzName, GlobalSearchScope.moduleScope(module)));
    }

    @NotNull
    public static Optional<PsiMethod> findMethod(@NotNull Project project, @Nullable String clazzName, @Nullable String methodName, Module module) {
        if (StringUtils.isBlank(clazzName) && StringUtils.isBlank(methodName)) {
            return Optional.empty();
        }
        Optional<PsiClass> clazz;

        if (Objects.isNull(module)) {
            clazz = findClazz(project, clazzName);
        } else {
            clazz = findClazzWithModule(project, clazzName, module);
        }

        if (clazz.isPresent()) {
            PsiMethod[] methods = clazz.get().findMethodsByName(methodName, true);
            return ArrayUtils.isEmpty(methods) ? Optional.empty() : Optional.of(methods[0]);
        }
        return Optional.empty();
    }

    @NotNull
    public static Optional<PsiMethod[]> findMethods(@NotNull Project project, @Nullable String clazzName, @Nullable String methodName, Module module) {
        if (StringUtils.isBlank(clazzName) && StringUtils.isBlank(methodName)) {
            return Optional.empty();
        }
        Optional<PsiClass[]> clazzes;

        if (Objects.isNull(module)) {
            clazzes = findClazzes(project, clazzName);
        } else {
            clazzes = findClazzesWithModule(project, clazzName, module);
        }

        if (clazzes.isPresent()) {
            List<PsiMethod> collect = Arrays.stream(clazzes.get())
                    .map(psiClass -> psiClass.findMethodsByName(methodName, true))
                    .flatMap(Arrays::stream)
                    .collect(Collectors.toList());
            return collect.isEmpty() ? Optional.empty() : Optional.of(collect.toArray(new PsiMethod[0]));

        }
        return Optional.empty();
    }

    @NotNull
    public static Optional<PsiMethod> findMethod(@NotNull Project project, @NotNull IdDomElement element) {
        return findMethod(project, MapperUtils.getNamespace(element), MapperUtils.getId(element), MapperUtils.getMapper(element).getModule());
    }

    @NotNull
    public static Optional<PsiMethod[]> findMethods(@NotNull Project project, @NotNull IdDomElement element) {
        return findMethods(project, MapperUtils.getNamespace(element), MapperUtils.getId(element), MapperUtils.getMapper(element).getModule());
    }

    public static boolean isAnnotationPresent(@NotNull PsiModifierListOwner target, @NotNull Annotation annotation) {
        PsiModifierList modifierList = target.getModifierList();
        return null != modifierList && null != modifierList.findAnnotation(annotation.getQualifiedName());
    }

    @NotNull
    public static Optional<PsiAnnotation> getPsiAnnotation(@NotNull PsiModifierListOwner target, @NotNull Annotation annotation) {
        PsiModifierList modifierList = target.getModifierList();
        return null == modifierList ? Optional.empty() : Optional.ofNullable(modifierList.findAnnotation(annotation.getQualifiedName()));
    }

    @NotNull
    public static Optional<PsiAnnotationMemberValue> getAnnotationAttributeValue(@NotNull PsiModifierListOwner target,
                                                                                 @NotNull Annotation annotation,
                                                                                 @NotNull String attrName) {
        if (!isAnnotationPresent(target, annotation)) {
            return Optional.empty();
        }
        Optional<PsiAnnotation> psiAnnotation = getPsiAnnotation(target, annotation);
        return psiAnnotation.isPresent() ? Optional.ofNullable(psiAnnotation.get().findAttributeValue(attrName)) : Optional.empty();
    }

    @NotNull
    public static Optional<PsiAnnotationMemberValue> getAnnotationValue(@NotNull PsiModifierListOwner target, @NotNull Annotation annotation) {
        return getAnnotationAttributeValue(target, annotation, "value");
    }

    public static Optional<String> getAnnotationValueText(@NotNull PsiModifierListOwner target, @NotNull Annotation annotation) {
        Optional<PsiAnnotationMemberValue> annotationValue = getAnnotationValue(target, annotation);
        return annotationValue.isPresent() ? Optional.of(annotationValue.get().getText().replaceAll("\"", "")) : Optional.empty();
    }

    public static boolean isAnyAnnotationPresent(@NotNull PsiModifierListOwner target, @NotNull Set<Annotation> annotations) {
        for (Annotation annotation : annotations) {
            if (isAnnotationPresent(target, annotation)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAllParameterWithAnnotation(@NotNull PsiMethod method, @NotNull Annotation annotation) {
        PsiParameter[] parameters = method.getParameterList().getParameters();
        for (PsiParameter parameter : parameters) {
            if (!isAnnotationPresent(parameter, annotation)) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasImportClazz(@NotNull PsiJavaFile file, @NotNull String clazzName) {
        PsiImportList importList = file.getImportList();
        if (null == importList) {
            return false;
        }
        PsiImportStatement[] statements = importList.getImportStatements();
        for (PsiImportStatement tmp : statements) {
            if (null != tmp && tmp.getQualifiedName().equals(clazzName)) {
                return true;
            }
        }
        return false;
    }

}
