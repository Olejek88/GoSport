package ru.toir.mobile.db.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
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

public class LevelAdapter extends RealmBaseAdapter<Level> implements ListAdapter {
    public static final String TABLE_NAME = "Level";

    public LevelAdapter(@NonNull Context context, int resId, RealmResults<Level> data) {
        super(context, data);
    }

    @Override
    public int getCount() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Level> rows = realm.where(Level.class).findAll();
        realm.close();
        return rows.size();
    }

    @Override
    public Level getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (parent.getId() == R.id.simple_spinner) {
            TextView textView = (TextView) View.inflate(context, android.R.layout.simple_spinner_item, null);
            Level level = adapterData.get(position);
            textView.setText(level.getTitle());
            return textView;
        }
        if (parent.getId() == R.id.reference_listView) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.listview, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.title);
                viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Level level = adapterData.get(position);
            viewHolder.title.setText(level.getTitle());
            viewHolder.sport.setText(level.getSport().getTitle());
            if (level.getSport().getTitle().equals("Хоккей")) viewHolder.icon.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.hockey_32));
            if (level.getSport().getTitle().equals("Футбол")) viewHolder.icon.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.football_32));
            return convertView;
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView uuid;
        TextView title;
        TextView sport;
        ImageView icon;
    }
}
