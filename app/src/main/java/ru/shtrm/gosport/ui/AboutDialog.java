package ru.shtrm.gosport.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;

import ru.shtrm.gosport.R;

class AboutDialog extends Dialog {
    AboutDialog(Context context) {
        super(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.about_dialog);
        TextView tv=findViewById(R.id.legal_text);
        if (tv!=null) {
            Linkify.addLinks(tv, Linkify.ALL);
        }
    }
}
