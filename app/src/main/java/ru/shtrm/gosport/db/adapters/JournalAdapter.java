package ru.shtrm.gosport.db.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.db.realm.Journal;

public class JournalAdapter extends RealmBaseAdapter<Journal> implements ListAdapter {
    public static final String TABLE_NAME = "Journal";
    private Context context;
    protected LayoutInflater inflater;

    public JournalAdapter(@NonNull Context context, RealmResults<Journal> data) {
        super(data);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (adapterData != null) {
            return adapterData.size();
        }
        return 0;
    }

    public RealmResults<Journal> getAllItems() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Journal> result = realm.where(Journal.class).findAll();
        realm.close();
        return result;
    }

    @Override
    public Journal getItem(int position) {
        if (adapterData != null) {
            return adapterData.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        Journal journal;
        if (adapterData != null) {
            journal = adapterData.get(position);
            return journal.get_id();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.simple_item, parent, false);
            viewHolder.date = (TextView) convertView.findViewById(R.id.eril_last_operation_date);
            viewHolder.descr = (TextView) convertView.findViewById(R.id.material_drawer_description);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Journal journal;
        if (adapterData != null && viewHolder.date != null) {
            String sDate;
            journal = adapterData.get(position);
            if (journal != null) {
                sDate = new SimpleDateFormat("dd.MM.yy HH:mm:ss", Locale.US).format(journal.getDate());
                viewHolder.date.setText(sDate);
                viewHolder.descr.setText(journal.getDescription());
            }
        }

        if (convertView == null) {
            TextView textView = new TextView(context);
            if (adapterData != null) {
                journal = adapterData.get(position);
                if (journal != null)
                    textView.setText(journal.getDate().toString());
                textView.setTextSize(16);
                textView.setPadding(5, 5, 5, 5);
            }
            return textView;
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView date;
        TextView descr;
    }

}
