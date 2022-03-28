package ml.iamwhatiam.fibonacci;

/**
 * 单纯的递归算法的时间复杂度为n^2
 */
public class RecursiveAlgorithm implements FibonacciSequence {

    @Override
    public long gen(int nth) {
        return nth < 2 ? nth : gen(nth - 1) + gen(nth - 2);
    }
}
