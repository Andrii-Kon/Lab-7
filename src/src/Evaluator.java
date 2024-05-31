import java.util.Stack;

public class Evaluator {

    public double evaluateExpression(String expression) {
        try {
            expression = expression.replaceAll("\\s+", "");
            String[] tokens = expression.split("(?=[-+*/^()])|(?<=[-+*/^()])");
            Stack<Double> operands = new Stack<>();
            Stack<String> operators = new Stack<>();
            boolean expectNumber = true;
            for (String token : tokens) {
                if (token.isEmpty()) {
                    continue;
                }
                if (token.equals("-") && expectNumber) {
                    operands.push(-1.0);
                    operators.push("*");
                } else if (Character.isDigit(token.charAt(0)) || token.equals(".")) {
                    operands.push(Double.parseDouble(token));
                    expectNumber = false;
                } else if (token.equals("(")) {
                    operators.push(token);
                    expectNumber = true;
                } else if (token.equals(")")) {
                    while (!operators.isEmpty() && !operators.peek().equals("(")) {
                        double rightOperand = operands.pop();
                        double leftOperand = operands.pop();
                        double result = applyOperator(operators.pop(), leftOperand, rightOperand);
                        operands.push(result);
                    }
                    operators.pop(); // Remove the '('
                    expectNumber = false;
                } else {
                    while (!operators.isEmpty() && precedence(token) <= precedence(operators.peek())) {
                        double rightOperand = operands.pop();
                        double leftOperand = operands.pop();
                        double result = applyOperator(operators.pop(), leftOperand, rightOperand);
                        operands.push(result);
                    }
                    operators.push(token);
                    expectNumber = true;
                }
            }
            while (!operators.isEmpty()) {
                double rightOperand = operands.pop();
                double leftOperand = operands.pop();
                double result = applyOperator(operators.pop(), leftOperand, rightOperand);
                operands.push(result);
            }
            return operands.pop();
        } catch (Exception e) {
            return 0;
        }
    }

    private int precedence(String operator) {
        switch (operator) {
            case "^":
                return 4;
            case "*":
            case "/":
                return 3;
            case "+":
            case "-":
                return 2;
            default:
                return 1;
        }
    }

    private double applyOperator(String operator, double leftOperand, double rightOperand) {
        switch (operator) {
            case "^":
                return Math.pow(leftOperand, rightOperand);
            case "*":
                return leftOperand * rightOperand;
            case "/":
                return leftOperand / rightOperand;
            case "+":
                return leftOperand + rightOperand;
            case "-":
                return leftOperand - rightOperand;
            default:
                throw new IllegalArgumentException("Invalid operator: " + operator);
        }
    }
}
