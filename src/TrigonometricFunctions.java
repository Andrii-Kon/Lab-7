public class TrigonometricFunctions {

    public static double applyTrigonometricFunction(String function, double operand) {
        switch (function) {
            case "sin":
                return Math.sin(Math.toRadians(operand));
            case "cos":
                return Math.cos(Math.toRadians(operand));
            case "tan":
                return Math.tan(Math.toRadians(operand));
            default:
                throw new IllegalArgumentException("Invalid function: " + function);
        }
    }
}