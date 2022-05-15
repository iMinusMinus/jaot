package org.springframework.aop.framework;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.RecomputeFieldValue;
import com.oracle.svm.core.annotate.TargetClass;
import org.springframework.nativex.substitutions.OnlyIfPresent;

import java.lang.reflect.Method;

@TargetClass(className = "org.springframework.aop.framework.AopProxyUtils", onlyWith = { OnlyIfPresent.class })
final class Target_AopProxyUtils {

    @Alias
    @RecomputeFieldValue(kind = RecomputeFieldValue.Kind.FromAlias)
    private static Method isSealedMethod = null;
}
