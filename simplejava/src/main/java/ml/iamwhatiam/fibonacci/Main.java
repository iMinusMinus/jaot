package ml.iamwhatiam.fibonacci;

import java.lang.reflect.Proxy;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static final Properties props = new Properties();

    private static final Logger logger = Logger.getLogger("main");

    private static final String DEFAULT_GENERATION_KEY = "fibonacci.generation";

    private static final String DEFAULT_ALGORITHM_IMPL_KEY = "fibonacci.algorithm.impl";

    public static void main(String[] args) {
        long start = System.nanoTime();
        initProperties();
        int gap = Integer.parseInt(props.getProperty(DEFAULT_GENERATION_KEY));
        if(gap < 2 || gap > 80) {
            throw new IllegalArgumentException("参数必须在2~80之间");
        }
        String fqcn = props.getProperty(DEFAULT_ALGORITHM_IMPL_KEY);
        FibonacciSequence serial;
        try {
            Class klazz = Class.forName(fqcn); // dynamic class loading
            serial = (FibonacciSequence) klazz.getDeclaredConstructor().newInstance(); // reflection query/invoke
        } catch (Exception ignore) {
            logger.log(Level.WARNING, ignore.getLocalizedMessage(), ignore);
            Class[] intfs = {FibonacciSequence.class};
            serial = (FibonacciSequence) Proxy.newProxyInstance(Main.class.getClassLoader(), intfs, new FibonacciInvocationHandler()); // proxy
        }
        long total  = serial.gen(gap);
        // 80: 23416728348467685
        // 30: 832040
        logger.info("result: " + total);
        logger.info("cost [" + (System.nanoTime() - start) / 1000 + "]ms");
    }

    private static void initProperties() {
        try {
            props.load(Main.class.getResourceAsStream("/fibonacci.properties")); // classpath resource
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }
        String defaultGeneration = System.getProperty(DEFAULT_GENERATION_KEY);
        if(defaultGeneration != null) {
            props.setProperty(DEFAULT_GENERATION_KEY, defaultGeneration);
        }
        String defaultAlgorithm = System.getProperty(DEFAULT_ALGORITHM_IMPL_KEY);
        if(defaultAlgorithm != null) {
            props.setProperty(DEFAULT_ALGORITHM_IMPL_KEY, defaultAlgorithm);
        }
    }

}
