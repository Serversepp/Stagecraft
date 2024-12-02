package de.fh.albsig.cablecrosssection;

public class CableCrossSectionCalculatorLogic {

    /**
     * Parses the selected voltage string into a double.
     *
     * @param voltageSelection The voltage selection (e.g., "230V").
     * @return The numeric value of the voltage.
     */
    public double parseStandardVoltage(String voltageSelection) {
        switch (voltageSelection) {
            case "110V": return 110;
            case "220V": return 220;
            case "230V": return 230;
            case "380V": return 380;
            case "400V": return 400;
            case "415V": return 415;
            case "12V": return 12;
            case "24V": return 24;
            case "48V": return 48;
            default: throw new IllegalArgumentException("Invalid voltage selection");
        }
    }

    /**
     * Computes the cable cross-section for a three-phase system.
     *
     * Formula: A = 1.732 * L * ((kW * 1000) / (U * 1.732)) * cos(φ) / (y * U_a)
     *
     * @param length         The cable length (L) in meters.
     * @param powerInKW      The power (kW).
     * @param voltage        The system voltage (U) in volts.
     * @param cosPhi         The power factor (cos φ).
     * @param conductivity   The material conductivity (y) in S/m (e.g., 56 for copper, 37 for aluminum).
     * @param voltageDrop    The allowable voltage drop (U_a) in volts.
     * @return The required cross-section in mm².
     */
    public double computeThreePhaseCrossSection(double length, double powerInKW, double voltage,
                                                double cosPhi, double conductivity, double voltageDrop) {
        double current = (powerInKW * 1000) / (voltage * 1.732); // Current (I) in amperes
        return (1.732 * length * current * cosPhi) / (conductivity * voltageDrop);
    }

    /**
     * Computes the cable cross-section for a single-phase system.
     *
     * Formula: A = (2 * L * I * cos(φ)) / (y * U_a)
     *
     * @param length         The cable length (L) in meters.
     * @param current        The current (I) in amperes.
     * @param cosPhi         The power factor (cos φ).
     * @param conductivity   The material conductivity (y) in S/m (e.g., 56 for copper, 37 for aluminum).
     * @param voltageDrop    The allowable voltage drop (U_a) in volts.
     * @return The required cross-section in mm².
     */
    public double computeSinglePhaseCrossSection(double length, double current, double cosPhi,
                                                 double conductivity, double voltageDrop) {
        return (2 * length * current * cosPhi) / (conductivity * voltageDrop);
    }

    /**
     * Computes the power loss of a cable.
     *
     * Formula: Power Loss = (2 * ρ * L * I²) / A
     * where ρ is the material resistivity (Ohm mm²/m).
     *
     * @param length       The cable length (L) in meters.
     * @param current      The current (I) in amperes.
     * @param material     The material of the cable ("Copper" or "Aluminum").
     * @param crossSection The cross-section of the cable (A) in mm².
     * @return The power loss in watts.
     */
    public double computePowerLoss(double length, double current, String material, double crossSection) {
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
        if (crossSection <= 1.5) return "1.5 mm²";
        if (crossSection <= 2.5) return "2.5 mm²";
        if (crossSection <= 4.0) return "4.0 mm²";
        if (crossSection <= 6.0) return "6.0 mm²";
        if (crossSection <= 10.0) return "10.0 mm²";
        if (crossSection <= 16.0) return "16.0 mm²";
        return "Greater than 16.0 mm² (consult a professional)";
    }
}
