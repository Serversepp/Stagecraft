package de.fh.albsig;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class SpeakerDelayCalcControllerTest {

    @Test
    void testCalculateOneDimensional() {
        SpeakerDelayCalcController calculator = new SpeakerDelayCalcController();
        double xInCm = 343.0; // 343 cm => 3.43 m => ~0.010 s => ~10 ms
        double result = calculator.calculateOneDimensional(xInCm);
        // Erwartung: ~10 ms (abh. von speedOfSound=343 m/s)
        assertEquals(10.0, result, 0.5);
    }

    @Test
    void testCalculateTwoDimensional() {
        SpeakerDelayCalcController calculator = new SpeakerDelayCalcController();
        // 2D-Distanz: sqrt(100^2 + 100^2) = ~141.42 cm => 1.4142 m => 1.4142 / 343 s => ~4.12 ms
        double xInCm = 100;
        double yInCm = 100;
        double result = calculator.calculateTwoDimensional(xInCm, yInCm);
        assertEquals(4.12, result, 0.5);
    }

    @Test
    void testCalculateThreeDimensional() {
        SpeakerDelayCalcController calculator = new SpeakerDelayCalcController();
        // 3D-Distanz: sqrt(100^2 + 100^2 + 100^2) = ~173.21 cm => 1.7321 m => ~5.05 ms
        double xInCm = 100;
        double yInCm = 100;
        double zInCm = 100;
        double result = calculator.calculateThreeDimensional(xInCm, yInCm, zInCm);
        assertEquals(5.05, result, 0.5);
    }
}