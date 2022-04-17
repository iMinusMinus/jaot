package ml.iamwhatiam.fibonacci;

@FunctionalInterface
public interface FibonacciSequence {

    long gen(int nth);
}
