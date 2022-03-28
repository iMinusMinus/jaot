package ml.iamwhatiam.fibonacci;

public class MatrixAlgorithm implements FibonacciSequence {

    @Override
    public long gen(int nth) {
        long[][]  A = {{1, 1}, {1, 0}};
        matrixPower(A, nth - 1);
        return A[0][0];
    }

    private void matrixPower(long[][] A, int N) {
        if (N <= 1) {
            return;
        }
        matrixPower(A, N/2);
        multiply(A, A);

        long[][] B = new long[][]{{1, 1}, {1, 0}};
        if (N % 2 != 0) {
            multiply(A, B);
        }
    }

    private void multiply(long[][] A, long[][] B) {
        long x = A[0][0] * B[0][0] + A[0][1] * B[1][0];
        long y = A[0][0] * B[0][1] + A[0][1] * B[1][1];
        long z = A[1][0] * B[0][0] + A[1][1] * B[1][0];
        long w = A[1][0] * B[0][1] + A[1][1] * B[1][1];

        A[0][0] = x;
        A[0][1] = y;
        A[1][0] = z;
        A[1][1] = w;
    }

}
