package ru.shtrm.gosport.db.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmList;
import io.realm.RealmResults;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.db.realm.Sport;
import ru.shtrm.gosport.db.realm.Stadium;

public class StadiumSpinnerAdapter extends RealmBaseAdapter<Stadium> implements ListAdapter {

    public static final String TABLE_NAME = "Stadium";
    private Context context;
    protected LayoutInflater inflater;
    protected Sport sport;

    public StadiumSpinnerAdapter(@NonNull Context context, RealmResults<Stadium> data, Sport sport) {
        super(data);
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.sport = sport;
    }
    public StadiumSpinnerAdapter(@NonNull Context context, RealmList<Stadium> data) {
        super(data);
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public int getCount() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Stadium> rows;
        if (sport!=null)
            rows = realm.where(Stadium.class).equalTo("sport.uuid",sport.getUuid()).findAll();
        else
            rows = realm.where(Stadium.class).findAll();
        realm.close();
        return rows.size();
   }

    @Override
    public Stadium getItem(int position) {
        if (adapterData != null && adapterData.size()>0) {
            return adapterData.get(position);
        }
        return null;
    }

    /*
    @Override
    public long getItemId(int position) {
        Stadium stadium;
        if (adapterData != null && adapterData.size()>0) {
            stadium = adapterData.get(position);
            return stadium.get_id();
        }
        return 0;
    }*/

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.simple_spinner_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.spinner_item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Stadium stadium;
        if (adapterData != null && adapterData.size()>0) {
            stadium = adapterData.get(position);
            if (stadium != null) {
                viewHolder.title.setText(stadium.getTitle());
            }
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, null, parent);
    }

    private static class ViewHolder {
        TextView title;
    }
}
