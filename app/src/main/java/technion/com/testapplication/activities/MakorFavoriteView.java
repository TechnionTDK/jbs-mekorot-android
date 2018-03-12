package technion.com.testapplication.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import technion.com.testapplication.JBSQueries;
import technion.com.testapplication.R;
import technion.com.testapplication.utils.FontUtils;

/**
 * Created by tomerlevinson on 03/03/2018.
 */
public class MakorFavoriteView extends AppCompatActivity {
    private static final String FULL_TEXT = "טקסט מלא";
    private static final String SITE_URL = "קישור לאתר";
    private String mMakorTitle;
    private String mMakorText;
    private String mMakorUri;
    private ShareActionProvider mShareActionProvider;
    Intent shareIntent;
    private ActionMenuView amvMenu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.favorite_menu, amvMenu.getMenu());
        return true;
    }

    private Intent createShareIntent(boolean fullText) {
        shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        if (fullText) {
            shareIntent.putExtra(Intent.EXTRA_TEXT, mMakorText);
        } else {
            String makorUri = mMakorUri;
            makorUri = makorUri.substring(makorUri.lastIndexOf("/") + 1);
            makorUri = "jbr:" + makorUri;
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    JBSQueries.READ_URL + makorUri);
        }
        return shareIntent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_favorite:
                return true;
            case R.id.action_share:
                AlertDialog.Builder builder = new AlertDialog.Builder(MakorFavoriteView.this);
                String[] options = new String[]{FULL_TEXT, SITE_URL};
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
                return true;
            case R.id.action_info:
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
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
        final Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        amvMenu = (ActionMenuView) myToolbar.findViewById(R.id.amvMenu);
        amvMenu.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return onOptionsItemSelected(menuItem);
            }
        });
        TextView toolbarTitleTV = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitleTV.setText(mMakorTitle);
        TextView makorText = (TextView) findViewById(R.id.makor_text);
        makorText.setText(mMakorText);

        // Set text font from shared preferences.
        FontUtils.setTextFont(makorText, getApplicationContext());

        // Set text size from shared prefernces.
        FontUtils.setTextSize(makorText, getApplicationContext());
    }

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