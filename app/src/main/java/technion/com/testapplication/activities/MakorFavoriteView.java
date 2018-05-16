package technion.com.testapplication.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import technion.com.testapplication.JBSQueries;
import technion.com.testapplication.R;
import technion.com.testapplication.utils.FontUtils;

/**
 * Created by tomerlevinson on 03/03/2018.
 * Shows the chosen favorite Makor from the favorites tab.
 */
public class MakorFavoriteView extends AppCompatActivity {
    private String mMakorTitle;
    private String mMakorText;
    private String mMakorUri;
    Intent shareIntent;
    private static final String MAKOR_URI_DELIMITER = "/";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.favorite_menu, menu);
        return true;
    }

    private Intent createShareIntent(boolean fullText) {
        shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        if (fullText) {
            shareIntent.putExtra(Intent.EXTRA_TITLE, mMakorTitle);
            shareIntent.putExtra(Intent.EXTRA_TEXT, mMakorText);
        } else {
            String makorUri = mMakorUri;
            makorUri = makorUri.substring(makorUri.lastIndexOf(MAKOR_URI_DELIMITER) + 1);
            makorUri = getResources().getString(R.string.jbr_prefix) + makorUri;
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    JBSQueries.READ_URL + makorUri);
        }
        return shareIntent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    /**
     * Sets the toolbar for the activity.
     */
    private void setToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        View goBack = findViewById(R.id.go_back);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        View shareButton = findViewById(R.id.share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MakorFavoriteView.this);
                String[] options = new String[]{getResources().getString(
                        R.string.full_text_share_option), getResources().getString(
                        R.string.link_to_text_share_option)};
                int selectedFont = 0;
                builder.setSingleChoiceItems(options, selectedFont, null);
                builder.setCancelable(true);
                builder.setTitle(getApplicationContext().getResources().getString(
                        R.string.choose_sharing_option));
                builder.setPositiveButton(
                        getApplicationContext().getResources().getString(R.string.choose_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                if (selectedPosition == 0) {
                                    startActivity(createShareIntent(true));
                                } else {
                                    startActivity(createShareIntent(false));
                                }
                            }
                        });

                builder.setNegativeButton(
                        getApplicationContext().getResources().getString(R.string.cancel_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        TextView toolbarTitleTV = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitleTV.setText(mMakorTitle);
    }

    /**
     * Sets the makor text for the activity with the relevant
     * size and family font settings.
     */
    private void setMakorText() {
        TextView makorText = (TextView) findViewById(R.id.makor_text);
        makorText.setText(mMakorText);

        // Set text font from shared preferences.
        FontUtils.setTextFont(makorText, getApplicationContext());

        // Set text size from shared prefernces.
        FontUtils.setTextSize(makorText, getApplicationContext());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.makor_favorite_view);
        Intent receivedIntent = getIntent();
        mMakorText = receivedIntent.getStringExtra(getResources().getString(R.string.makor_text));
        mMakorTitle = receivedIntent.getStringExtra(getResources().getString(R.string.makor_title));
        mMakorUri = receivedIntent.getStringExtra(getResources().getString(R.string.makor_uri));
        getWindow().getDecorView().setBackgroundColor(
                ContextCompat.getColor(this, R.color.MakorDetailViewBG));
        setToolbar();
        setMakorText();
    }

    /**
     * Added in order to take care of font size and font family settings
     * in case of change from the settings activity.
     */
    @Override
    protected void onResume() {
        super.onResume();
        TextView makorText = (TextView) findViewById(R.id.makor_text);
        // Set text font from shared preferences.
        FontUtils.setTextFont(makorText, getApplicationContext());

        // Set text size from shared prefernces.
        FontUtils.setTextSize(makorText, getApplicationContext());
    }
}
