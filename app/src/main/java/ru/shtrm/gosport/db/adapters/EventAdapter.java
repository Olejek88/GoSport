package ru.shtrm.gosport.db.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.shtrm.gosport.db.realm.Event;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.db.realm.Sport;

public class EventAdapter extends RealmBaseAdapter<Event> implements ListAdapter {

    public static final String TABLE_NAME = "Event";

    public EventAdapter(@NonNull Context context, RealmResults<Event> data) {
        super(context, data);
    }

    @Override
    public int getCount() {
        if (adapterData != null) {
            return adapterData.size();
        }
        else return 0;
    }

    @Override
    public Event getItem(int position) {
        if (adapterData != null) {
            return adapterData.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        Event event;
        if (adapterData != null) {
            event = adapterData.get(position);
            return event.get_id();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        viewHolder = new EventAdapter.ViewHolder();
        if (convertView == null) {
            if (parent.getId() == R.id.simple_spinner) {
                convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
                viewHolder.title = (TextView) convertView.findViewById(android.R.id.text1);
                convertView.setTag(viewHolder);
            }
            else {
                convertView = inflater.inflate(R.layout.simple_listview, parent, false);
                viewHolder = new EventAdapter.ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.emi_title);
                viewHolder.description = (TextView) convertView.findViewById(R.id.emi_title);
                viewHolder.date = (TextView) convertView.findViewById(R.id.eril_last_operation_date);
                viewHolder.sport = (TextView) convertView.findViewById(R.id.emi_title);
                convertView.setTag(viewHolder);
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Event event;
        if (adapterData != null) {
            event = adapterData.get(position);
            if (event != null) {
                if (event.getDate() != null) {
                    String sDate = new SimpleDateFormat("dd.MM.yy HH:mm:ss", Locale.US).format(event.getDate());
                    viewHolder.date.setText(sDate);
                }
                if (event.getSport() != null) {
                    viewHolder.sport.setText(event.getSport().getTitle());
                }
            }
        }
        return convertView;
    }


    private static class ViewHolder {
        TextView uuid;
        TextView title;
        TextView description;
        TextView date;
        TextView sport;
    }
}
