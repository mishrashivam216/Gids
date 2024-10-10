package com.android.gids;

import android.util.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FunctionProcessor {

//    public static void main(String[] args) {
//        // Example input
//        String expression = "MISS_INTDEC(MISS_SUM(factory_area,storage_area,laboratory_area), 1, 3)";
//        List<Float> answerList = Arrays.asList(100.0f, 200.0f, 150.0f); // Example values
//
//        // Call function processor
//        String result = processMISSFunction(expression, answerList);
//        System.out.println("Formatted Result: " + result);
//    }

    // Method to process MISS functions (SUM, MULTIPLY, etc.)
    public static String processMISSFunction(String expression, List<Float> answerList) {
        // Step 1: Extract function and arguments
        String innerFunctionName = extractInnerFunctionName(expression);
        int[] arguments = extractArgumentsFromExpression(expression);
        int decimalPlaces = arguments[0];
        int numberOfDigits = arguments[1];

        // Step 2: Perform the inner function operation (SUM, MULTIPLY, MINUS, etc.)
        float computedValue = 0.0f;


        if (expression.contains("MISS_SUM")) {
            computedValue = answerList.stream().reduce(0.0f, Float::sum);
        }

        if (expression.contains("MISS_MULTIPLY")) {

            if (answerList.size() == 0) {
                computedValue = 0;
            } else {
                computedValue = answerList.stream().reduce(1.0f, (a, b) -> a * b);
            }
        }


        if (expression.contains("MISS_MINUS")) {
            computedValue = answerList.stream().reduce((a, b) -> a - b).orElse(0.0f);
        }

        if (expression.contains("MISS_DIVIDE")) {
            computedValue = answerList.stream().reduce((a, b) -> a / b).orElse(0.0f);
        }


        // Step 3: Apply rounding and formatting
        // float roundedValue = roundToDecimal(computedValue, decimalPlaces);
        String formattedResult = formatNumberToDigits(computedValue, numberOfDigits);

        if (decimalPlaces == 0) {
            float floatValue = Float.parseFloat(formattedResult);
            int intValue = (int) floatValue;
            return String.valueOf(intValue);
        }

        return formattedResult;
    }

    // Utility method to extract the inner function name (e.g., MISS_SUM, MISS_MULTIPLY)
//    private static String extractInnerFunctionName(String expression) {
//        Pattern pattern = Pattern.compile("MISS_(\\w+)\\(");
//        Matcher matcher = pattern.matcher(expression);
//        if (matcher.find()) {
//            return matcher.group(1);
//        }
//        throw new IllegalArgumentException("No inner function found in expression: " + expression);
//    }

    public static String extractInnerFunctionName(String expression) {
        // Find the opening parenthesis for the first argument
        int startIndex = expression.indexOf('(');

        // Find the first comma or closing parenthesis, indicating the end of the inner function
        int endIndex = expression.indexOf(',', startIndex);
        if (endIndex == -1) {
            // If there's no comma, the closing parenthesis marks the end of the function
            endIndex = expression.indexOf(')', startIndex);
        }

        // Extract the inner function name between the parentheses
        if (startIndex != -1 && endIndex != -1) {
            return expression.substring(0, startIndex).trim();
        }

        return "";
    }


    // Utility method to extract decimalPlaces and numberOfDigits from expression
    private static int[] extractArgumentsFromExpression(String expression) {
        // Ensure we are working with a trimmed version of the expression
        expression = expression.trim();

        // Regular expression to capture the entire MISS_INTDEC function with parameters
        Pattern pattern = Pattern.compile("MISS_INTDEC\\((.*)\\)$");
        Matcher matcher = pattern.matcher(expression);

        if (matcher.find()) {
            String innerExpression = matcher.group(1).trim(); // Get the content inside the parentheses

            // Regular expression to capture the last two numeric values (arguments)
            Pattern lastArgsPattern = Pattern.compile(",\\s*(\\d+)\\s*,\\s*(\\d+)\\s*$");
            Matcher argsMatcher = lastArgsPattern.matcher(innerExpression);

            if (argsMatcher.find()) {
                int decimalPlaces = Integer.parseInt(argsMatcher.group(1));
                int numberOfDigits = Integer.parseInt(argsMatcher.group(2));
                return new int[]{decimalPlaces, numberOfDigits};
            }
        }

        return new int[]{0, 0}; // Default values if extraction fails
    }


    // Utility method to round a float to the specified decimal places
    private static float roundToDecimal(float value, int decimalPlaces) {
        BigDecimal bd = new BigDecimal(Float.toString(value));
        bd = bd.setScale(decimalPlaces, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    // Utility method to format a float to a fixed number of decimal places (digits)
    private static String formatNumberToDigits(float value, int numberOfDigits) {
        StringBuilder formatPattern = new StringBuilder("#.");
        for (int i = 0; i < numberOfDigits; i++) {
            formatPattern.append("0");
        }
        DecimalFormat df = new DecimalFormat(formatPattern.toString());
        return df.format(value);
    }
}
