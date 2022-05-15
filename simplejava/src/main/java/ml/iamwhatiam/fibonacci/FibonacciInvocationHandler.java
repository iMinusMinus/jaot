package ml.iamwhatiam.fibonacci;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;

public class FibonacciInvocationHandler implements InvocationHandler {

    private final long[] cached;

    public FibonacciInvocationHandler() {
        cached = new long[80];
        cached[0] = 1;
        cached[1] = 1;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(method.getDeclaringClass() == Object.class) {
            if(method.getName().equals("hashCode")) {
                return Objects.hashCode(this);
            } else if(method.getName().equals("toString")) {
                return Objects.toString(this);
            } else if(method.getName().equals("equals")) {
                return this == args[0];
            }
        }
        if(method.getName().equals("gen")) {
            return memorizeRecursive((Integer) args[0] - 1);
        }
        return null;
    }

    private long memorizeRecursive(int nth) {
        if(cached[nth] != 0) {
            return cached[nth];
        }
        cached[nth - 2] = memorizeRecursive(nth - 2);
        cached[nth - 1] = memorizeRecursive(nth - 1);
        cached[nth] = cached[nth - 1] + cached[nth - 2];
        return cached[nth];
    }
}
