package ru.shtrm.gosport.db.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.db.realm.Notification;
import ru.shtrm.gosport.db.realm.Training;

public class NotificationAdapter extends RealmBaseAdapter<Notification> implements ListAdapter {
    public static final String TABLE_NAME = "Notification";

    public NotificationAdapter(@NonNull Context context, RealmResults<Notification> data) {
        super(context, data);
    }

    @Override
    public int getCount() {
        if (adapterData != null) {
            return adapterData.size();
        }
        return 0;
    }

    public RealmResults<Notification> getAllItems() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Notification> result = realm.where(Notification.class).findAll();
        realm.close();
        return result;
    }

    @Override
    public Notification getItem(int position) {
        if (adapterData != null) {
            return adapterData.get(position);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        Notification notification;
        if (adapterData != null) {
            notification = adapterData.get(position);
            return notification.get_id();
        }

        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            if (parent.getId() == R.id.reference_listView) {
                convertView = inflater.inflate(R.layout.listview, parent, false);
                viewHolder.training = (TextView) convertView.findViewById(R.id.lv_firstLine);
                convertView.setTag(viewHolder);
            }

            if (parent.getId() == R.id.simple_spinner) {
                convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
                viewHolder.training = (TextView) convertView.findViewById(android.R.id.text1);
                convertView.setTag(viewHolder);
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Notification notification;
        if (adapterData != null && viewHolder.training != null) {
            notification = adapterData.get(position);
            if (notification != null) {
                viewHolder.training.setText(notification.getTraining().getTitle());
            }
        }

        if (convertView == null) {
            TextView titleView = new TextView(context);
            TextView dateView = new TextView(context);
            if (adapterData != null) {
                notification = adapterData.get(position);
                if (notification != null) {
                    titleView.setText(notification.getTraining().getTitle());
                    dateView.setText(notification.getDate().toString());
                }

                titleView.setTextSize(16);
                dateView.setPadding(5, 5, 5, 5);
            }
        }

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, null, parent);
    }

    private static class ViewHolder {
        TextView training;
        TextView date;
        boolean view;
    }

}
