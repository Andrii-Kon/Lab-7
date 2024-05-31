import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import java.util.Stack;

/**
 * Клас Calculator створює графічний інтерфейс користувача для калькулятора.
 */
public class Calculator extends JFrame implements ActionListener {

    private JTextField display;
    private JPanel panel;
    private JButton[] buttons;
    private String[] labels = {"sin", "cos", "tan", "^", "C", "MS", "1", "2", "3", "+", "MR", "4", "5", "6", "-", "MC", "7", "8", "9", "*", ".", "0", "(", ")", "/", "="};
    private Double memory = null;

    /**
     * Конструктор Calculator ініціалізує графічний інтерфейс користувача.
     */
    public Calculator() {
        setTitle("Калькулятор");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        display = new JTextField();
        display.setFont(new Font("Arial", Font.BOLD, 20));
        display.setHorizontalAlignment(JTextField.RIGHT);

        panel = new JPanel();
        panel.setLayout(new GridLayout(6, 4, 5, 5));

        buttons = new JButton[labels.length];
        for (int i = 0; i < labels.length; i++) {
            buttons[i] = new JButton(labels[i]);
            buttons[i].addActionListener(this);
            panel.add(buttons[i]);
        }

        getContentPane().add(display, BorderLayout.NORTH);
        getContentPane().add(panel, BorderLayout.CENTER);

        pack();
        setVisible(true);
    }

    /**
     * Метод actionPerformed обробляє дії користувача.
     *
     * @param e об'єкт події
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();

        if (Character.isDigit(actionCommand.charAt(0)) || ".".equals(actionCommand) || "(".equals(actionCommand) || ")".equals(actionCommand)) {
            try {
                int caretPosition = display.getCaretPosition();
                Document document = display.getDocument();
                document.insertString(caretPosition, actionCommand, null);
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        }

        if ("+".equals(actionCommand) || "-".equals(actionCommand) || "*".equals(actionCommand) || "/".equals(actionCommand) || "^".equals(actionCommand)) {
            try {
                int caretPosition = display.getCaretPosition();
                Document document = display.getDocument();
                document.insertString(caretPosition, actionCommand, null);
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        }

        if ("sin".equals(actionCommand) || "cos".equals(actionCommand) || "tan".equals(actionCommand)) {
            try {
                String expression = display.getText();
                double result = applyTrigonometricFunction(actionCommand, Double.parseDouble(expression));
                display.setText(String.valueOf(result));
            } catch (Exception ex) {
                display.setText("Помилка");
            }
        }

        if ("=".equals(actionCommand)) {
            try {
                String expression = display.getText();
                double result = evaluateExpression(expression);
                display.setText(String.valueOf(result));
            } catch (Exception ex) {
                display.setText("Помилка");
            }
        }

        if ("C".equals(actionCommand)) {
            display.setText("");
        }

        if ("MS".equals(actionCommand)) {
            try {
                memory = Double.parseDouble(display.getText());
            } catch (NumberFormatException ex) {
                display.setText("Помилка");
            }
        }

        if ("MR".equals(actionCommand)) {
            if (memory != null) {
                display.setText(String.valueOf(memory));
            } else {
                display.setText("Пам'ять порожня");
            }
        }

        if ("MC".equals(actionCommand)) {
            memory = null;
        }
    }

    /**
     * Метод evaluateExpression обчислює вираз.
     *
     * @param expression вираз для обчислення
     * @return результат обчислення виразу
     */
    private double evaluateExpression(String expression) {
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

    /**
     * Метод precedence визначає пріоритет оператора.
     *
     * @param operator оператор
     * @return пріоритет оператора
     */
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

    /**
     * Метод applyOperator застосовує оператор до двох операндів.
     *
     * @param operator оператор
     * @param leftOperand лівий операнд
     * @param rightOperand правий операнд
     * @return результат застосування оператора
     */
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

    /**
     * Метод applyTrigonometricFunction застосовує тригонометричну функцію до операнда.
     *
     * @param function тригонометрична функція
     * @param operand операнд
     * @return результат застосування функції
     */
    private double applyTrigonometricFunction(String function, double operand) {
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

    /**
     * Головний метод програми.
     *
     * @param args аргументи командного рядка
     */
    public static void main(String[] args) {
        new Calculator();
    }
}
