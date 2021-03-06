package ru.shtrm.gosport.db.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.db.realm.Level;
import ru.shtrm.gosport.db.realm.Sport;

public class LevelAdapter extends RealmBaseAdapter<Level> implements ListAdapter {
    public static final String TABLE_NAME = "Level";
    private Context context;
    protected LayoutInflater inflater;
    protected Sport sport;

    public LevelAdapter(@NonNull Context context, RealmResults<Level> data, Sport sport) {
        super(data);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.sport = sport;
    }

    @Override
    public int getCount() {
        RealmResults<Level> rows;
        Realm realm = Realm.getDefaultInstance();
        if (sport!=null) {
            rows = realm.where(Level.class).equalTo("sport.uuid", sport.getUuid()).findAll();
        }
        else {
            rows = realm.where(Level.class).findAll();
        }
        realm.close();
        return rows.size();
    }

    @Override
    public Level getItem(int position) {
        if (adapterData != null && adapterData.size()>0) {
            return adapterData.get(position);
        }
        return null;
    }

    /*
    @Override
    public long getItemId(int position) {
        Level level;
        if (adapterData != null && adapterData.size()>0) {
            level = adapterData.get(position);
            return level.get_id();
        }
        return 0;
    }*/

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            if (parent.getId() == R.id.simple_spinner ||
                    parent.getId() == R.id.profile_football_level ||
                    parent.getId() == R.id.profile_hockey_level ||
                    parent.getId() == R.id.training_add_level) {
                convertView = inflater.inflate(R.layout.simple_spinner_item, parent, false);
                viewHolder.title = convertView.findViewById(R.id.spinner_item);
                viewHolder.title.setTextColor(context.getResources().getColor(R.color.larisaTextColor));
                convertView.setTag(viewHolder);
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Level level;
        if (adapterData != null && viewHolder.title !=null && adapterData.size()>0) {
                level = adapterData.get(position);
                if (level != null)
                    viewHolder.title.setText(level.getTitle());
                viewHolder.title.setTextColor(context.getResources().getColor(R.color.larisaTextColor));
        }

        if (convertView == null) {
            TextView textView = new TextView(context);
            if (adapterData != null && adapterData.size()>0) {
                level = adapterData.get(position);
                if (level != null)
                    textView.setText(level.getTitle());
                textView.setTextSize(16);
                textView.setTextColor(context.getResources().getColor(R.color.larisaTextColor));
                textView.setPadding(10,10,10,10);
            }
            return textView;
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, null, parent);
    }

    private static class ViewHolder {
        TextView uuid;
        TextView title;
        TextView sport;
        ImageView icon;
    }
}
