package de.fh.albsig.cablecrosssection;


/**
 * CableCrossSectionCalculatorLogic provides methods for calculating cable cross-section
 * sizes, power loss, and recommending standard wiring sizes for electrical installations.
 * The calculations are based on various parameters such as voltage, current, cable length,
 * and material properties.
 *
 * <p>Features include:</p>
 * <ul>
 *   <li>Parsing standard voltage values from string inputs.</li>
 *   <li>Computing cable cross-section sizes for single-phase and three-phase systems.</li>
 *   <li>Calculating power loss of a cable based on its material and usage parameters.</li>
 *   <li>Recommending standard wiring sizes based on computed cross-section values.</li>
 * </ul>
 *
 * <p>This class supports materials like copper and aluminum for calculations, and
 * applies standard formulas used in electrical engineering.</p>
 *
 * <p><strong>Note:</strong> Consult a professional for installations exceeding
 * the recommended size or for high-current/long-distance requirements.</p>
 *
 * @author SB
 * @version 1.0
 */

public class CableCrossSectionCalculatorLogic {

    /**
     * Parses the selected voltage string into a double.
     *
     * @param voltageSelection The voltage selection (e.g., "230V").
     * @return The numeric value of the voltage.
     */
    public double parseStandardVoltage(String voltageSelection) {
        return switch (voltageSelection) {
            case "110V" -> 110;
            case "220V" -> 220;
            case "230V" -> 230;
            case "380V" -> 380;
            case "400V" -> 400;
            case "415V" -> 415;
            case "12V" -> 12;
            case "24V" -> 24;
            case "48V" -> 48;
            default -> throw new IllegalArgumentException("Invalid voltage selection");
        };
    }

    /**
     * Computes the cable cross-section for a three-phase system.
     * Formula: A = 1.732 * L * ((kW * 1000) / (U * 1.732)) * cos(φ) / (y * U_a)
     *
     * @param length         The cable length (L) in meters.
     * @param powerInKiWa      The power (kW).
     * @param voltage        The system voltage (U) in volts.
     * @param cosPhi         The power factor (cos φ).
     * @param conductivity   The material conductivity (y) in S/m (e.g., 56 for copper).
     * @param voltageDrop    The allowable voltage drop (U_a) in volts.
     * @return The required cross-section in mm².
     */
    public double computeThreePhaseCrossSection(double length, double powerInKiWa,
                                                double voltage, double cosPhi,
                                                double conductivity, double voltageDrop) {
        double current = (powerInKiWa * 1000) / (voltage * 1.732); // Current (I) in amperes
        return (1.732 * length * current * cosPhi) / (conductivity * voltageDrop);
    }

    /**
     * Computes the cable cross-section for a single-phase system.
     * Formula: A = (2 * L * I * cos(φ)) / (y * U_a)
     *

     * @param length         The cable length (L) in meters.
     * @param current        The current (I) in amperes.
     * @param cosPhi         The power factor (cos φ).
     * @param conductivity   The material conductivity (y) in S/m (e.g., 56 for copper).
     * @param voltageDrop    The allowable voltage drop (U_a) in volts.
     * @return The required cross-section in mm².
     */
    public double computeSinglePhaseCrossSection(double length, double current, double cosPhi,
                                                 double conductivity, double voltageDrop) {
        return (2 * length * current * cosPhi) / (conductivity * voltageDrop);
    }

    /**
     * Computes the power loss of a cable.
     * Formula: Power Loss = (2 * ρ * L * I²) / A
     * where ρ is the material resistivity (Ohm mm²/m).
     *
     * @param length       The cable length (L) in meters.
     * @param current      The current (I) in amperes.
     * @param material     The material of the cable ("Copper" or "Aluminum").
     * @param crossSection The cross-section of the cable (A) in mm².
     * @return The power loss in watts.
     */
    public double computePowerLoss(double length, double current,
                                   String material, double crossSection) {
        double resistivity = "Copper".equals(material) ? 0.017 : 0.028; // Resistivity in Ohm mm²/m
        return (2 * resistivity * length * Math.pow(current, 2)) / crossSection;
    }

    /**
     * Recommends a standard wiring size based on the cross-section.
     *
     * @param crossSection The computed cross-section in mm².
     * @return The recommended standard wiring size.
     */
    public String getRecommendedStandardWiring(double crossSection) {
        if (crossSection <= 1.5) {
            return "1.5 mm²";
        } else if (crossSection <= 2.5) {
            return "2.5 mm²";
        } else if (crossSection <= 4.0) {
            return "4.0 mm²";
        } else if (crossSection <= 6.0) {
            return "6.0 mm²";
        } else if (crossSection <= 10.0) {
            return "10.0 mm²";
        } else if (crossSection <= 16.0) {
            return "16.0 mm²";
        } else {
            return "Greater than 16.0 mm² (consult a professional)";
        }
    }

}
