package technion.com.testapplication.utils;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import technion.com.testapplication.R;

/**
 * Created by Haggai on 28/03/2018.
 */

public class OneButtonAlert {

    private AlertDialog alertDialog;

    public OneButtonAlert(Context context, String message, String buttonText, final Runnable onClick) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View promptView = layoutInflater.inflate(R.layout.alert_one_btn, null);
        alertDialog = new AlertDialog.Builder(context).create();
        TextView infoText = (TextView) promptView.findViewById(R.id.txt_info);
        infoText.setText(message);
        final Button btnAlert = (Button) promptView.findViewById(R.id.alert_btn);
        btnAlert.setText(buttonText);
        btnAlert.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (onClick != null)
                {
                    onClick.run();
                }
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(promptView);
    }

    public void show()
    {
        alertDialog.show();
    }

}
