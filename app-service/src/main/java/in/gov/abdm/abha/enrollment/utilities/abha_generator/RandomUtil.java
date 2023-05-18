package in.gov.abdm.abha.enrollment.utilities.abha_generator;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@UtilityClass
@Slf4j
public class RandomUtil {
    private static final String SECURE_RANDOM_ALGO = "NativePRNG";
    public static final String NO_ALGO_FOUND_TO_CREATE_SECURE_RANDOM_INSTANCE = "No algo ({}) found to create secure random instance, Trying to create Random instance using another way for windows machine";
    private static SecureRandom secureRandom;
    static {
        try {
            secureRandom = SecureRandom.getInstance(SECURE_RANDOM_ALGO);
        } catch (NoSuchAlgorithmException e) {
            log.error(NO_ALGO_FOUND_TO_CREATE_SECURE_RANDOM_INSTANCE, SECURE_RANDOM_ALGO,e);
            secureRandom = new SecureRandom();
        }
    }

    /**
     * Get a random integer between a range (inclusive).
     *
     * @param maximum
     * @param minimum
     * @return
     */
    public int getRandomInteger(int maximum, int minimum) {
        return secureRandom.nextInt(maximum - minimum) + minimum;
    }

    /**
     * Get a random Long between a range (inclusive).
     *
     * @param maximum
     * @param minimum
     * @return
     */
    public long getRandomLong(long maximum, long minimum) {
        return secureRandom.nextInt((int) (maximum - minimum)) + minimum;
    }

    /**
     * Get a random double between a range (inclusive).
     *
     * @return
     */
    public double getRandomDouble() {
        return secureRandom.nextDouble();
    }

    /**
     * Get a random number
     *
     * @param bound
     * @return
     */
    public int getRandomNumber(int bound) {
        return secureRandom.nextInt(bound);
    }
}
