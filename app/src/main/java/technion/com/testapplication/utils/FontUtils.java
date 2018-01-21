package technion.com.testapplication.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.widget.TextView;

import java.util.Set;

import technion.com.testapplication.activities.SettingsActivity;

/**
 * Created by tomerlevinson on 22/01/2018.
 */
public class FontUtils {
    public static void setTextFont(TextView tv, Context context) {
        String selectedFont = null;
        Set<String> selectedFontSet = PreferencesUtils.retrieveStoredStringSet(
                SettingsActivity.PREFERENCES_FILE_NAME, SettingsActivity.SELECTED_FONT_KEY,
                context);
        if (selectedFontSet != null && selectedFontSet.size() > 0) {
            for (String param : selectedFontSet) {
                selectedFont = param;
            }
        } else {
            tv.setTypeface(Typeface.create(SettingsActivity.DEFAULT_FONT, Typeface.NORMAL));
        }
        if (selectedFont != null) {
            Typeface custom_font = Typeface.createFromAsset(context.getAssets(),
                    selectedFont);
            tv.setTypeface(custom_font);
        }
    }

    public static void setTextSize(TextView tv, Context context) {
        String selectedFontSize = null;
        Set<String> selectedFontSizeSet = PreferencesUtils.retrieveStoredStringSet(
                SettingsActivity.PREFERENCES_FILE_NAME, SettingsActivity.SELECTED_FONT_SIZE_KEY,
                context);
        if (selectedFontSizeSet != null) {
            for (String param : selectedFontSizeSet) {
                selectedFontSize = param;
            }
        } else {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                    Integer.parseInt(SettingsActivity.FONT_SIZE_SMALL));
        }
        if (selectedFontSize != null) {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                    Integer.parseInt(selectedFontSize));
        }
    }
}
