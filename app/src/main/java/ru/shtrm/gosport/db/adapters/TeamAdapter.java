package ru.shtrm.gosport.db.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmList;
import io.realm.RealmResults;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.db.realm.Team;

public class TeamAdapter extends RealmBaseAdapter<Team> implements ListAdapter {
    public static final String TABLE_NAME = "Team";

    public TeamAdapter(@NonNull Context context, RealmResults<Team> data) {
        super(context, data);
    }

    public TeamAdapter(@NonNull Context context, RealmList<Team> data) {
        super(context, data);
    }

    @Override
    public int getCount() {
        if (adapterData != null) {
            return adapterData.size();
        }
        return 0;
    }

    public void setFilter(String text, Realm realmDB) {
        if (adapterData != null) {
            adapterData = realmDB.where(Team.class).equalTo("name",text).or().contains("name",text, Case.INSENSITIVE).findAll();
            notifyDataSetChanged();
        }
    }

    @Override
    public Team getItem(int position) {
        if (adapterData != null) {
            return adapterData.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        Team contragent;
        if (adapterData != null) {
            contragent = adapterData.get(position);
            return contragent.get_id();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            if (parent.getId() == R.id.reference_listView) {
                convertView = inflater.inflate(R.layout.simple_spinner_item, parent, false);
                viewHolder = new ViewHolder();
                convertView.setTag(viewHolder);
            } else {
                convertView = inflater.inflate(R.layout.simple_item, parent, false);
                viewHolder = new ViewHolder();
                convertView.setTag(viewHolder);
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Team contragent;
        if (adapterData != null) {
            contragent = adapterData.get(position);
            if (contragent != null) {
            }
        }
        return convertView;
    }


    private static class ViewHolder {
        ImageView icon;
        TextView uuid;
        TextView name;
        TextView description;
        TextView phone;
        TextView type;
        TextView latitude;
        TextView longitude;
        TextView parent;
    }

}
