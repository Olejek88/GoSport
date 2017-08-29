package ru.shtrm.gosport;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class AboutDialog extends Dialog {

    private static final String TAG = "AboutDialog";
    private Context mContext = null;

    AboutDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        TextView tv;
        setContentView(R.layout.about);

        tv = (TextView) findViewById(R.id.legal_text);
        tv.setText(R.string.application_full_name);
        tv = (TextView) findViewById(R.id.info_text);
        Linkify.addLinks(tv, Linkify.ALL);
    }
}
