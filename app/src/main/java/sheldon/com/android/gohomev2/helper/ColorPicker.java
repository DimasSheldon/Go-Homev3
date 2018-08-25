package sheldon.com.android.gohomev2.helper;

import android.graphics.Color;

import java.util.HashMap;
import java.util.Map;

public class ColorPicker {
    private static String color;
    private static Map<String, String> colorLightMap = new HashMap<String, String>() {
        {
            // aqua
            put("aqua", "#00ffff");
            // black
            put("black", "#0a0a0a");
            // blue
            put("blue", "#0000ff");
            // gray
            put("gray", "#a9a9a9");
            // green
            put("green", "#008000");
            // maroon
            put("maroon", "#800000");
            // navy
            put("navy", "#000080");
            // orange
            put("orange", "#ffa500");
            // purple
            put("purple", "#800080");
            // red
            put("red", "#ff0000");
            // teal
            put("teal", "#008080");
            // white
            put("white", "#ffffff");
            // yellow
            put("yellow", "#ffff00");
        }
    };

    private static Map<String, String> colorDarkMap = new HashMap<String, String>() {
        {
            // aqua
            put("darkaqua", "#00cbcc");
            // black
            put("darkblack", "#000000");
            // blue
            put("darkblue", "#00008b");
            // gray
            put("darkgray", "#7a7a7a");
            // green
            put("darkgreen", "#006400");
            // maroon
            put("darkmaroon", "#4f0000");
            // navy
            put("darknavy", "#000053");
            // orange
            put("darkorange", "#ff8c00");
            // purple
            put("darkpurple", "#4f0053");
            // red
            put("darkred", "#c20000");
            // teal
            put("darkteal", "#005354");
            // white
            put("darkwhite", "#cccccc");
            // yellow
            put("darkyellow", "#c7cc00");
        }
    };

    public static int pickColorLight(String responseColor) {
        color = responseColor.split("-")[1];

        return Color.parseColor(colorLightMap.get(color));
    }

    public static int pickColorDark(String responseColor) {
        color = responseColor.split("-")[1];

        return Color.parseColor(colorDarkMap.get("dark" + color));
    }
}
