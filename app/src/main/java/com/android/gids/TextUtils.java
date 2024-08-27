package com.android.gids;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;

public class TextUtils {

    private static final int MAX_WIDTH_DP = 350; // Maximum width in dp

    // Convert dp to pixels
    private static int dpToPx(Context context, int dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }

    public static String splitText(Context context, String text) {
        if (text.isEmpty()) {
            return text;
        }

        // Create a Paint object to measure text width
        Paint paint = new Paint();
        paint.setTextSize(16 * context.getResources().getDisplayMetrics().scaledDensity); // Adjust text size as needed

        // Convert maxWidth from dp to pixels
        int maxWidthPx = dpToPx(context, MAX_WIDTH_DP);

        // Measure the text width
        float textWidth = paint.measureText(text);
        Log.d("TextSplit", "Text width: " + textWidth);

        if (textWidth <= maxWidthPx) {
            return text; // No need to split if the text fits within the width
        }

        // Split text by words
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            String testLine = line.toString() + (line.length() > 0 ? " " : "") + word;
            float lineWidth = paint.measureText(testLine);

            if (lineWidth > maxWidthPx) {
                // If adding this word exceeds the width, add the line to the result and start a new line
                result.append(line.toString().trim()).append("\n");
                line.setLength(0); // Clear the line
                line.append(word);
            } else {
                // Add word to the current line
                line.append(line.length() > 0 ? " " : "").append(word);
            }
        }

        // Add the last line
        result.append(line.toString().trim());

        return result.toString();
    }
}
