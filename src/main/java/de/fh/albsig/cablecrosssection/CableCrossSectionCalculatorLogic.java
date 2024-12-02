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
     * Computes the cross-section of a cable.
     *
     * @param length            The length of the cable in meters.
     * @param material          The material of the cable ("Copper" or "Aluminum").
     * @param installationType  The installation type (e.g., "Wall", "Conduit").
     * @param systemType        The system type (e.g., "AC Single-phase").
     * @param voltage           The voltage in volts.
     * @param wattage           The power in watts.
     * @return The computed cross-section in mm².
     */
    public double computeCrossSection(double length, String material, String installationType,
                                      String systemType, double voltage, double wattage) {
        double resistanceFactor = systemType.contains("Three-phase") ? Math.sqrt(3) : 1.0;
        double materialResistivity = "Copper".equals(material) ? 0.017 : 0.028; // Ohm mm²/m
        return (length * wattage) / (resistanceFactor * voltage * materialResistivity);
    }

    /**
     * Computes the power loss of a cable.
     *
     * @param length       The length of the cable in meters.
     * @param material     The material of the cable ("Copper" or "Aluminum").
     * @param crossSection The cross-section of the cable in mm².
     * @param current      The current in amperes.
     * @return The power loss in watts.
     */
    public double computePowerLoss(double length, String material, double crossSection, double current) {
        double resistivity = "Copper".equals(material) ? 0.017 : 0.028; // Ohm mm²/m
        return (2 * resistivity * length * current * current) / crossSection;
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
        return "Greater than 6.0 mm² (consult a professional)";
    }
}
