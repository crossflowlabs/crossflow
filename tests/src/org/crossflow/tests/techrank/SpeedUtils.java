package org.crossflow.tests.techrank;

import java.util.Random;

public class SpeedUtils {

    private static double getNoisedValue(double mean, double stdDev) {
        return new Random().nextGaussian() * stdDev + mean;
    }

    public static long getSpeed(double maxSpeed) {
        if (maxSpeed <= 0) {
            throw new IllegalArgumentException("Max speed must be positive");
        }
        // Mean of the gaussian distribution is 85% of max speed, std dev is 5% of max speed
        return (long) getNoisedValue(maxSpeed * 0.85, maxSpeed / 20);
    }
}
