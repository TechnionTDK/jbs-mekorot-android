package technion.com.testapplication.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.util.HashSet;
import java.util.Set;

import technion.com.testapplication.R;
import technion.com.testapplication.utils.PreferencesUtils;

/**
 * Created by tomerlevinson on 21/01/2018.
 * Settings include font size and font family.
 */
public class SettingsActivity extends AppCompatActivity {
    public static final String FONT_NUMBER_ONE = "fonts/keteryg-medium-webfont.ttf";
    public static final String FONT_NUMBER_TWO = "fonts/shofarregular-webfont.ttf";
    public static final String FONT_NUMBER_THREE = "fonts/stamashkenazclm-webfont.ttf";
    public static final String FONT_NUMBER_FOUR = "fonts/FrankRuhlLibre-Regular.ttf";
    public static final String FONT_NUMBER_FIVE = "fonts/miriwin-webfont.ttf";
    public static final String DEFAULT_FONT = "sans-serif-light";
    public static final String DEFAULT_FONT_DISPLAY_NAME = "ללא פונט";
    public static final String FONT_NUMBER_ONE_DISPLAY_NAME = "כתר";
    public static final String FONT_NUMBER_TWO_DISPLAY_NAME = "שופר";
    public static final String FONT_NUMBER_THREE_DISPLAY_NAME = "אברהם";
    public static final String FONT_NUMBER_FOUR_DISPLAY_NAME = "פרנק";
    public static final String FONT_NUMBER_FIVE_DISPLAY_NAME = "מירי";
    public static final String FONT_SIZE_SMALL = "16";
    public static final String FONT_SIZE_MEDIUM = "18";
    public static final String FONT_SIZE_LARGE = "20";
    public static final String FONT_SIZE_SMALL_DISPLAY_NAME = "קטן";
    public static final String FONT_SIZE_MEDIUM_DISPLAY_NAME = "בינוני";
    public static final String FONT_SIZE_LARGE_DISPLAY_NAME = "גדול";
    public static final String PREFERENCES_FILE_NAME = "settings";
    public static final String SELECTED_FONT_KEY = "selected_font";
    public static final String SELECTED_FONT_SIZE_KEY = "selected_font_size";


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        final Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        View backButton = findViewById(R.id.go_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setSetFontSizeDialog();
        setFontFamilyDialog();
    }

    /**
     * Setting up the font family dialog.
     */
    private void setFontFamilyDialog() {
        LinearLayout setFontSizeLL = (LinearLayout) findViewById(R.id.choose_font);
        setFontSizeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                String[] fontSizes = new String[]{DEFAULT_FONT_DISPLAY_NAME,
                        FONT_NUMBER_ONE_DISPLAY_NAME,
                        FONT_NUMBER_TWO_DISPLAY_NAME,
                        FONT_NUMBER_THREE_DISPLAY_NAME,
                        FONT_NUMBER_FOUR_DISPLAY_NAME,
                        FONT_NUMBER_FIVE_DISPLAY_NAME};
                Set<String> chosenBeforeFontSet = PreferencesUtils.retrieveStoredStringSet(
                        PREFERENCES_FILE_NAME, SELECTED_FONT_KEY,
                        getApplicationContext());
                int selectedFont = 0;
                if (chosenBeforeFontSet != null) {
                    for (String chosenBeforeFont : chosenBeforeFontSet) {
                        if (chosenBeforeFont.equals(FONT_NUMBER_ONE)) {
                            selectedFont = 1;
                        } else if (chosenBeforeFont.equals(FONT_NUMBER_TWO)) {
                            selectedFont = 2;
                        } else if (chosenBeforeFont.equals(FONT_NUMBER_THREE)) {
                            selectedFont = 3;
                        } else if (chosenBeforeFont.equals(FONT_NUMBER_FOUR)) {
                            selectedFont = 4;
                        } else if (chosenBeforeFont.equals(FONT_NUMBER_FIVE)) {
                            selectedFont = 5;
                        }
                    }
                }
                builder.setSingleChoiceItems(fontSizes, selectedFont, null);
                builder.setCancelable(false);
                builder.setTitle(getResources().getString(R.string.choose_font_style));
                builder.setPositiveButton(getResources().getString(R.string.choose_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                Set<String> newParamSet = new HashSet<>();
                                if (selectedPosition == 1) {
                                    newParamSet.add(FONT_NUMBER_ONE);
                                } else if (selectedPosition == 2) {
                                    newParamSet.add(FONT_NUMBER_TWO);
                                } else if (selectedPosition == 3) {
                                    newParamSet.add(FONT_NUMBER_THREE);
                                } else if (selectedPosition == 4) {
                                    newParamSet.add(FONT_NUMBER_FOUR);
                                } else if (selectedPosition == 5) {
                                    newParamSet.add(FONT_NUMBER_FIVE);
                                }
                                PreferencesUtils.storeStringSet(PREFERENCES_FILE_NAME,
                                        SELECTED_FONT_KEY, newParamSet, true,
                                        getApplicationContext());
                            }
                        });

                builder.setNegativeButton(getResources().getString(R.string.cancel_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

    }

    /**
     * Setting up the font size dialog.
     */
    private void setSetFontSizeDialog() {
        LinearLayout setFontSizeLL = (LinearLayout) findViewById(R.id.choose_font_size);
        setFontSizeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                String[] fontSizes = new String[]{
                        FONT_SIZE_SMALL_DISPLAY_NAME,
                        FONT_SIZE_MEDIUM_DISPLAY_NAME,
                        FONT_SIZE_LARGE_DISPLAY_NAME
                };
                Set<String> chosenBeforeFontSizeSet = PreferencesUtils.retrieveStoredStringSet(
                        PREFERENCES_FILE_NAME, SELECTED_FONT_SIZE_KEY,
                        getApplicationContext());
                int selectedFontSize = 0;
                if (chosenBeforeFontSizeSet != null) {
                    for (String chosenBeforeFont : chosenBeforeFontSizeSet) {
                        if (chosenBeforeFont.equals(FONT_SIZE_SMALL)) {
                            selectedFontSize = 0;
                        } else if (chosenBeforeFont.equals(FONT_SIZE_MEDIUM)) {
                            selectedFontSize = 1;
                        } else if (chosenBeforeFont.equals(FONT_SIZE_LARGE)) {
                            selectedFontSize = 2;
                        }
                    }
                }
                builder.setSingleChoiceItems(fontSizes, selectedFontSize, null);
                builder.setCancelable(false);
                builder.setTitle(getResources().getString(R.string.choose_font_size));
                builder.setPositiveButton(getResources().getString(R.string.choose_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                Set<String> newParamSet = new HashSet<>();
                                if (selectedPosition == 0) {
                                    newParamSet.add(FONT_SIZE_SMALL);
                                } else if (selectedPosition == 1) {
                                    newParamSet.add(FONT_SIZE_MEDIUM);
                                } else if (selectedPosition == 2) {
                                    newParamSet.add(FONT_SIZE_LARGE);
                                }
                                PreferencesUtils.storeStringSet(PREFERENCES_FILE_NAME,
                                        SELECTED_FONT_SIZE_KEY, newParamSet, true,
                                        getApplicationContext());
                            }
                        });

                builder.setNegativeButton(getResources().getString(R.string.cancel_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }
}
