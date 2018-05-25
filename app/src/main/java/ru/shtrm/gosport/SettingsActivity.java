package ru.shtrm.gosport;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.DexFile;


public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceClickListener {
    private static final String TAG = "GoSportSettings";
    private static String appVersion;
    private PreferenceScreen basicSettingScr;
    private PreferenceScreen playerSettingScr;

    @SuppressWarnings("deprecation")
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        AppBarLayout bar;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent();
            bar = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.toolbar_settings, root, false);
            root.addView(bar, 0);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
            bar = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.toolbar_settings, root, false);
            root.addView(bar, 0);
        } else {
            ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
            ListView content = (ListView) root.getChildAt(0);
            root.removeAllViews();
            bar = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.toolbar_settings, root, false);

            int height;
            TypedValue tv = new TypedValue();
            if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
                height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            } else {
                height = bar.getHeight();
            }

            content.setPadding(0, height, 0, 0);

            root.addView(content);
            root.addView(bar);
        }

        Toolbar Tbar = (Toolbar) bar.getChildAt(0);

        Tbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            appVersion = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            appVersion = "unknown";
        }
        setupSimplePreferencesScreen();
        // элемент интерфейса со списком драйверов считывателей
        basicSettingScr = (PreferenceScreen) this.findPreference("preferenceBasicScreen");
    }

    @SuppressWarnings("deprecation")
    private void setupSimplePreferencesScreen() {
        addPreferencesFromResource(R.xml.pref_general);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        // Allow super to try and create a view first
        final View result = super.onCreateView(name, context, attrs);
        if (result != null) {
            return result;
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            //toolbar.setBackgroundColor(getResources().getColor(R.color.larisaBlueColor));
            //toolbar.setSubtitle("Обслуживание и ремонт");
            toolbar.setTitleTextColor(Color.WHITE);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // If we're running pre-L, we need to 'inject' our tint aware Views in place of the
            // standard framework versions
            switch (name) {
                case "EditText":
                    return new AppCompatEditText(this, attrs);
                case "Spinner":
                    return new AppCompatSpinner(this, attrs);
                case "CheckBox":
                    return new AppCompatCheckBox(this, attrs);
                case "RadioButton":
                    return new AppCompatRadioButton(this, attrs);
                case "CheckedTextView":
                    return new AppCompatCheckedTextView(this, attrs);
            }
        }

        return null;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        super.onPreferenceTreeClick(preferenceScreen, preference);

        if (preference != null) {
            if (preference instanceof PreferenceScreen) {
                Dialog dialog = ((PreferenceScreen) preference).getDialog();
                if (dialog != null) {
                    Window window = dialog.getWindow();
                    if (window != null) {
                        Drawable.ConstantState constantState = this.getWindow().getDecorView().getBackground().getConstantState();
                        if (constantState != null) {
                            window.getDecorView().setBackgroundDrawable(constantState.newDrawable());
                        }
                    }
                }

                setUpNestedScreen((PreferenceScreen) preference);
            }
        }

        return false;
    }

    public void setUpNestedScreen(PreferenceScreen preferenceScreen) {
        final Dialog dialog = preferenceScreen.getDialog();

        AppBarLayout appBar;

        View listRoot = dialog.findViewById(android.R.id.list);
        ViewGroup mRootView = (ViewGroup) dialog.findViewById(android.R.id.content);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LinearLayout root = (LinearLayout) dialog.findViewById(android.R.id.list).getParent().getParent();
            appBar = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.toolbar_settings, root, false);
            root.addView(appBar, 0);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            LinearLayout root = (LinearLayout) dialog.findViewById(android.R.id.list).getParent();
            appBar = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.toolbar_settings, root, false);
            root.addView(appBar, 0);
        } else {
            ListView content = (ListView) mRootView.getChildAt(0);
            mRootView.removeAllViews();

            LinearLayout LL = new LinearLayout(this);
            LL.setOrientation(LinearLayout.VERTICAL);

            ViewGroup.LayoutParams LLParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            LL.setLayoutParams(LLParams);

            appBar = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.toolbar_settings, mRootView, false);

            LL.addView(appBar);
            LL.addView(content);

            mRootView.addView(LL);
        }

        if (listRoot != null) {
            listRoot.setPadding(0, listRoot.getPaddingTop(), 0, listRoot.getPaddingBottom());
        }

        Toolbar Tbar = (Toolbar) appBar.getChildAt(0);

        Tbar.setTitle(preferenceScreen.getTitle());
        Tbar.setTitleTextColor(Color.WHITE);
        Tbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        this.findPreference(getResources().getString(R.string.serverUrl))
                .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(final Preference preference) {
                        final EditTextPreference URLPreference = (EditTextPreference) findPreference(getString(R.string.serverUrl));
                        final AlertDialog dialog = (AlertDialog) URLPreference.getDialog();
                        URLPreference.getEditText().setError(null);
                        dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String errorMessage;
                                        String text = URLPreference.getEditText().getText().toString();

                                        try {
                                            URL tURL = new URL(text);
                                            //String tURL2 = tURL.toString().replaceAll("/+$", "");
                                            URLPreference.getEditText().setText(tURL.toString().replaceAll("/+$", ""));
                                            errorMessage = null;
                                        } catch (MalformedURLException e) {
                                            if (!text.isEmpty()) {
                                                errorMessage = "Не верный URL!";
                                            } else {
                                                errorMessage = null;
                                            }
                                        }

                                        EditText edit = URLPreference.getEditText();
                                        if (errorMessage == null) {
                                            edit.setError(null);
                                            URLPreference.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                                            dialog.dismiss();
                                            GoSportApplication.serverUrl = edit.getText().toString();
                                        } else {
                                            edit.setError(errorMessage);
                                        }
                                    }
                                });

                        return true;
                    }
                });

    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        // do what ever you want with this key
        if (key.equals(getString(R.string.serverUrl))) {
            final EditTextPreference URLPreference = (EditTextPreference) findPreference(getString(R.string.serverUrl));
            final AlertDialog dialog = (AlertDialog) URLPreference.getDialog();
            URLPreference.getEditText().setError(null);
            dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String errorMessage;
                            String text = URLPreference.getEditText().getText().toString();
                            try {
                                URL tURL = new URL(text);
                                URLPreference.getEditText().setText(tURL.toString().replaceAll("/+$", ""));
                                errorMessage = null;
                            } catch (MalformedURLException e) {
                                if (!text.isEmpty()) {
                                    errorMessage = "Не верный URL!";
                                } else {
                                    errorMessage = null;
                                }
                            }

                            EditText edit = URLPreference.getEditText();
                            if (errorMessage == null) {
                                edit.setError(null);
                                URLPreference.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                                dialog.dismiss();
                                GoSportApplication.serverUrl = edit.getText().toString();
                            } else {
                                edit.setError(errorMessage);
                            }
                        }
                    });
        }

        return true;
    }
}
