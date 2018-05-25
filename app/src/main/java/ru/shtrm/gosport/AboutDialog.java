package ru.shtrm.gosport;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;

class AboutDialog extends Dialog {
    private Context mContext;

    AboutDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        TextView tv;
        setContentView(R.layout.about);

        tv = findViewById(R.id.legal_text);
        tv.setText(R.string.application_full_name);
        tv = findViewById(R.id.info_text);
        tv.setText(mContext.getResources().getString(R.string.author));
        Linkify.addLinks(tv, Linkify.ALL);
    }
}
