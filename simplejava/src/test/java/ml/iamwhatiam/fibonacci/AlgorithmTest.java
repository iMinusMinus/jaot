package ml.iamwhatiam.fibonacci;

import java.util.Random;

public class AlgorithmTest {

    public static void main(String[] args) {
        AlgorithmTest $ = new AlgorithmTest();
        $.testLoop();
        $.testRecursion();
        $.testMatrixPower();
        $.testRandom();
    }

    public void testLoop() {
        long result = new LoopAlgorithm().gen(30);
        System.out.println(result);
        assert result == 832040;
    }

    public void testRecursion() {
        long result = new RecursiveAlgorithm().gen(30);
        System.out.println(result);
        assert result == 832040;
    }

    public void testMatrixPower() {
        long result = new MatrixAlgorithm().gen(30);
        System.out.println(result);
        assert result == 832040;
    }

    public void testRandom() {
        int random = new Random().nextInt(80);
        long r1 = new LoopAlgorithm().gen(random);
        long r2 = new RecursiveAlgorithm().gen(random);
        long r3 = new MatrixAlgorithm().gen(random);
        System.out.println("generation: [" + random + "], loop result: " + r1 + ", recursion result: " + r2 + ". matrix power result: " + r3);
    }
}
