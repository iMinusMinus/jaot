package ml.iamwhatiam.fibonacci;

public class LoopAlgorithm implements FibonacciSequence {

    @Override
    public long gen(int nth) {
        long prev = 0, next = 1, result = 0;
        for(int i = 1; i < nth; i++) {
            result = prev + next;
            prev = next;
            next = result;
        }
        return result;
    }
}
