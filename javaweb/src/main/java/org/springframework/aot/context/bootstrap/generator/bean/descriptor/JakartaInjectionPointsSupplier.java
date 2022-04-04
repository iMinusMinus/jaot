package org.springframework.aot.context.bootstrap.generator.bean.descriptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class JakartaInjectionPointsSupplier extends InjectionPointsSupplier {

    private final Set<Class<? extends Annotation>> autowiredAnnotationTypes = new LinkedHashSet<>(4);

    JakartaInjectionPointsSupplier(ClassLoader classLoader) {
        super(classLoader);
        autowiredAnnotationTypes.add(Autowired.class);
        autowiredAnnotationTypes.add(Value.class);
        Class<? extends Annotation> jsr310Inject = safeGetJsr330Inject(classLoader);
        if (jsr310Inject != null) {
            autowiredAnnotationTypes.add(jsr310Inject);
        }
        autowiredAnnotationTypes.add(Resource.class);
    }

    private static Class<? extends Annotation> safeGetJsr330Inject(ClassLoader classLoader) {
        try {
            return (Class<? extends Annotation>) ClassUtils.forName("javax.inject.Inject", classLoader);
        } catch (ClassNotFoundException var2) {
            return null;
        }
    }

    List<BeanInstanceDescriptor.MemberDescriptor<?>> detectInjectionPoints(Class<?> clazz) {
        List<BeanInstanceDescriptor.MemberDescriptor<?>> result = new ArrayList<>();
        if (!AnnotationUtils.isCandidateClass(clazz, this.autowiredAnnotationTypes)) {
            return Collections.emptyList();
        }
        Class<?> targetClass = clazz;
        do {
            final List<BeanInstanceDescriptor.MemberDescriptor<?>> currElements = new ArrayList<>();
            ReflectionUtils.doWithLocalFields(targetClass, field -> {
                MergedAnnotation<?> ann = findAutowiredAnnotation(field);
                if (ann != null) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        if (log.isInfoEnabled()) {
                            log.info("Autowired annotation is not supported on static fields: " + field);
                        }
                        return;
                    }
                    boolean required = determineRequiredStatus(ann);
                    currElements.add(new BeanInstanceDescriptor.MemberDescriptor<>(field, required));
                }
            });
            ReflectionUtils.doWithLocalMethods(targetClass, method -> {
                Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
                if (!BridgeMethodResolver.isVisibilityBridgeMethodPair(method, bridgedMethod)) {
                    return;
                }
                MergedAnnotation<?> ann = findAutowiredAnnotation(bridgedMethod);
                if (ann != null && method.equals(ClassUtils.getMostSpecificMethod(method, clazz))) {
                    if (Modifier.isStatic(method.getModifiers())) {
                        if (log.isInfoEnabled()) {
                            log.info("Autowired annotation is not supported on static methods: " + method);
                        }
                        return;
                    }
                    if (method.getParameterCount() == 0) {
                        if (log.isInfoEnabled()) {
                            log.info("Autowired annotation should only be used on methods with parameters: " +
                                    method);
                        }
                    }
                    boolean required = determineRequiredStatus(ann);
                    currElements.add(new BeanInstanceDescriptor.MemberDescriptor<>(method, required));
                }
            });
            result.addAll(0, currElements);
            targetClass = targetClass.getSuperclass();
        }
        while (targetClass != null && targetClass != Object.class);
        return result;
    }

    @Nullable
    private MergedAnnotation<?> findAutowiredAnnotation(AccessibleObject ao) {
        MergedAnnotations annotations = MergedAnnotations.from(ao);
        for (Class<? extends Annotation> type : this.autowiredAnnotationTypes) {
            MergedAnnotation<?> annotation = annotations.get(type);
            if (annotation.isPresent()) {
                return annotation;
            }
        }
        return null;
    }

}
