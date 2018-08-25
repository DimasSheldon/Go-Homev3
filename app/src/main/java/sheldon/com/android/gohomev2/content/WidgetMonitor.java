package sheldon.com.android.gohomev2.content;

import sheldon.com.android.gohomev2.helper.ColorPicker;
import sheldon.com.android.gohomev2.helper.IconPicker;

public class WidgetMonitor {
    private String label, value;
    private int icon, cvColor, iconColor;
    private String updateIndicator;

    //WidgetMonitor("ion ion-waterdrop", "HUMIDITY", "bg-blue", "NA")
    public WidgetMonitor(String icon, String label, String color, String value, String updateIndicator) {
        setIcon(icon);
        setColor(color);
        this.label = label;
        this.value = value;
        this.updateIndicator = updateIndicator;

    }

    public void setIcon(String responseIcon) {
        this.icon = IconPicker.pickIconLight(responseIcon);
    }

    public void setColor(String responseColor) {
        this.cvColor = ColorPicker.pickColorLight(responseColor);
        this.iconColor = ColorPicker.pickColorDark(responseColor);
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setUpdateIndicator(String updateIndicator) {
        this.updateIndicator = updateIndicator;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public int getIcon() {
        return icon;
    }

    public int cvColor() {
        return cvColor;
    }

    public int iconColor() {
        return iconColor;
    }

    public String getUpdateIndicator() {
        return updateIndicator;
    }
}
