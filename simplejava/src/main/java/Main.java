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
        long total  = fibonacci(gap);
        // 80: 23416728348467685
        // 30: 832040
        System.out.println(total);
        System.out.println((System.nanoTime() - start) / 1000);
    }

    private static long fibonacci(long gap) {
        if(gap == 1L || gap == 2L) {
            return 1L;
        }
        return fibonacci(gap - 1) + fibonacci(gap - 2);
    }
}
