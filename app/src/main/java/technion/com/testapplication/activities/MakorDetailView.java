package technion.com.testapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import technion.com.testapplication.R;

/**
 * Created by tomerlevinson on 23/12/2017.
 */
public class MakorDetailView extends AppCompatActivity {
    private String mMakorTitle;
    private String mMakorAuthor;
    private String mMakorText;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_view_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_favorite:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;
            case R.id.action_share:
                return true;

            case R.id.action_info:
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.makor_detail_view);
        Intent receivedIntent = getIntent();
        mMakorText = receivedIntent.getStringExtra(getResources().getString(R.string.makor_text));
        mMakorAuthor = receivedIntent.getStringExtra(
                getResources().getString(R.string.makor_author));
        mMakorTitle = receivedIntent.getStringExtra(getResources().getString(R.string.makor_title));
        getWindow().getDecorView().setBackgroundColor(
                ContextCompat.getColor(this, R.color.LightBlue));
        final Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView toolbarTitleTV = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitleTV.setText(mMakorTitle);
        TextView makorText = (TextView) findViewById(R.id.makor_text);
        makorText.setText(mMakorText);
        makorText.setMovementMethod(new ScrollingMovementMethod());
    }
}