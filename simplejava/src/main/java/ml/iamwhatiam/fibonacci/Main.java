package ml.iamwhatiam.fibonacci;

public class Main {

    public static void main(String[] args) {
        long start = System.nanoTime();
        int gap = 80;
        if(args.length > 0) {
            gap = Integer.parseInt(args[0]);
        }
        if(gap < 2 || gap > 80) {
            throw new IllegalArgumentException("参数必须在2~80之间");
        }
        int alg = 0;
        if(args.length > 1) {
            alg = Integer.parseInt(args[1]);
        }
        FibonacciSequence serial;
        if(alg < 0) {
            serial = new RecursiveAlgorithm();
        } else if(alg > 0) {
            serial = new MatrixAlgorithm();
        } else {
            serial = new LoopAlgorithm();
        }
        long total  = serial.gen(gap);
        // 80: 23416728348467685
        // 30: 832040
        System.out.println(total);
        System.out.println((System.nanoTime() - start) / 1000);
    }

}
