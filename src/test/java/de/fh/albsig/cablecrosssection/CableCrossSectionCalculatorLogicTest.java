package de.fh.albsig.cablecrosssection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CableCrossSectionCalculatorLogicTest {

    @Test
    void testComputeThreePhaseCrossSection_ValidInputs() {
        // Arrange
        CableCrossSectionCalculatorLogic calculator = new CableCrossSectionCalculatorLogic();
        double length = 50; // in meters
        double current = 100; // in amperes
        double voltage = 400; // in volts
        double cosPhi = 0.9; // power factor
        double conductivity = 56; // in S/m (for copper)
        double voltageDrop = 5; // in volts

        // Act
        double result = calculator.computeThreePhaseCrossSection(length, current, voltage, cosPhi, conductivity, voltageDrop);

        // Assert
        assertEquals(28.29, result, 0.5);
    }

    @Test
    void testComputeThreePhaseCrossSection_NegativeLength() {
        // Arrange
        CableCrossSectionCalculatorLogic calculator = new CableCrossSectionCalculatorLogic();

        // Assert
        assertThrows(IllegalArgumentException.class, () -> {
            // Act
            calculator.computeThreePhaseCrossSection(-50, 100, 400, 0.9, 56, 5);
        });
    }

    @Test
    void testComputeThreePhaseCrossSection_ZeroCurrent() {
        // Arrange
        CableCrossSectionCalculatorLogic calculator = new CableCrossSectionCalculatorLogic();
        double length = 50;
        double current = 0; // zero current
        double voltage = 400;
        double cosPhi = 0.9;
        double conductivity = 56;
        double voltageDrop = 5;

        // Act
        double result = calculator.computeThreePhaseCrossSection(length, current, voltage, cosPhi, conductivity, voltageDrop);

        // Assert
        assertEquals(0.0, result, 0.5);
    }

    @Test
    void testComputeSinglePhaseCrossSection_ValidInputs() {
        // Arrange
        CableCrossSectionCalculatorLogic calculator = new CableCrossSectionCalculatorLogic();
        double length = 30; // in meters
        double current = 50; // in amperes
        double cosPhi = 0.8; // power factor
        double conductivity = 56; // in S/m (for copper)
        double voltageDrop = 10; // in volts

        // Act
        double result = calculator.computeSinglePhaseCrossSection(length, current, cosPhi, conductivity, voltageDrop);

        // Assert
        assertEquals(4.29, result, 0.01);
    }

    @Test
    void testComputePowerLoss_ValidInputs() {
        // Arrange
        CableCrossSectionCalculatorLogic calculator = new CableCrossSectionCalculatorLogic();
        double length = 100; // in meters
        double current = 75; // in amperes
        String material = "Copper";
        double crossSection = 10; // in mm²

        // Act
        double result = calculator.computePowerLoss(length, current, material, crossSection);

        // Assert
        assertEquals(191.25, result, 0.01);
    }

    @Test
    void testComputePowerLoss_InvalidMaterial() {
        // Arrange
        CableCrossSectionCalculatorLogic calculator = new CableCrossSectionCalculatorLogic();

        // Assert
        assertThrows(IllegalArgumentException.class, () -> {
            // Act
            calculator.computePowerLoss(100, 75, "Steel", 10);
        });
    }

    @Test
    void testGetRecommendedStandardWiring() {
        // Arrange
        CableCrossSectionCalculatorLogic calculator = new CableCrossSectionCalculatorLogic();

        // Act & Assert
        assertEquals("1.5 mm²", calculator.getRecommendedStandardWiring(1.2));
        assertEquals("2.5 mm²", calculator.getRecommendedStandardWiring(2.0));
        assertEquals("4.0 mm²", calculator.getRecommendedStandardWiring(3.5));
        assertEquals("6.0 mm²", calculator.getRecommendedStandardWiring(5.0));
        assertEquals("10.0 mm²", calculator.getRecommendedStandardWiring(8.5));
        assertEquals("16.0 mm²", calculator.getRecommendedStandardWiring(12.0));
        assertEquals("Greater than 16.0 mm² (consult a professional)", calculator.getRecommendedStandardWiring(20.0));
    }
}
