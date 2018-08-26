package sheldon.com.android.gohomev2.helper;

import android.annotation.SuppressLint;
import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import sheldon.com.android.gohomev2.activities.MainActivity;

public class IconPicker {
    @SuppressLint("StaticFieldLeak")
    private static Context context = MainActivity.getActivityContext();

    private static Map<String, String> iconsLight = new HashMap<String, String>() {
        {
            put("ion ion-ios-snowy", "ic_widget_snowy_white");
            put("ion ios-exit", "ic_widget_door_white");
            put("ion ion-outlet", "ic_widget_outlet_white");
            put("ion ion-waterdrop", "ic_widget_waterdrop_white");
            put("ion ion-ios-lightbulb", "ic_widget_lightbulb_white");
            put("ion ion-flash", "ic_widget_flash_white");
            put("ion ios-flower-outline", "ic_widget_flower_white");
            put("ion ion-thermometer", "ic_widget_temperature_white");
            put("ion ion-ios-monitor-outline", "ic_widget_monitor_white");
            put("default", "ic_widget_default_white");
        }
    };

    private static Map<String, String> iconsDark = new HashMap<String, String>() {
        {
            put("ion ion-ios-snowy", "ic_widget_snowy");
            put("ion ios-exit", "ic_widget_door");
            put("ion ion-outlet", "ic_widget_outlet");
            put("ion ion-waterdrop", "ic_widget_waterdrop");
            put("ion ion-ios-lightbulb", "ic_widget_lightbulb");
            put("ion ion-flash", "ic_widget_flash");
            put("ion ios-flower-outline", "ic_widget_flower");
            put("ion ion-thermometer", "ic_widget_temperature");
            put("ion ion-ios-monitor-outline", "ic_widget_monitor");
            put("default", "ic_widget_default");
        }
    };

    public static int pickIconLight(String responseIcon) {
        return context.getResources().getIdentifier(iconsLight.get(responseIcon),
                "mipmap", context.getPackageName());
    }

    public static int pickIconDark(String responseIcon) {
        return context.getResources().getIdentifier(iconsDark.get(responseIcon),
                "mipmap", context.getPackageName());
    }
}
