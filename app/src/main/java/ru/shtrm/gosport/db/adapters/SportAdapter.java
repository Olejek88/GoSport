package ru.shtrm.gosport.db.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import ru.shtrm.gosport.db.realm.Amplua;
import ru.shtrm.gosport.db.realm.Sport;
import ru.shtrm.gosport.db.realm.Team;
import ru.shtrm.gosport.utils.MainFunctions;

import static ru.shtrm.gosport.utils.RoundedImageView.getResizedBitmap;

public class SportAdapter extends RealmBaseAdapter<Sport> implements ListAdapter {
    public static final String TABLE_NAME = "Sport";
    private Context context;
    protected LayoutInflater inflater;

    public SportAdapter(@NonNull Context context, RealmResults<Sport> data) {
        super(data);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Sport> rows = realm.where(Sport.class).findAll();
        realm.close();
        return rows.size();
    }

    @Override
    public Sport getItem(int position) {
        if (adapterData != null) {
            return adapterData.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        Sport sport;
        if (adapterData != null) {
            sport = adapterData.get(position);
            return sport.get_id();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            if (parent.getId() == R.id.simple_spinner) {
                convertView = inflater.inflate(R.layout.simple_spinner_item, parent, false);
                viewHolder.title = (TextView) convertView.findViewById(R.id.spinner_item);
                convertView.setTag(viewHolder);
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Sport sport;
        if (adapterData != null && viewHolder.title !=null) {
            sport = adapterData.get(position);
            if (sport != null) {
                viewHolder.title.setText(sport.getTitle());
            }
        }

        if (convertView == null) {
            TextView textView = new TextView(context);
            if (adapterData != null) {
                sport = adapterData.get(position);
                if (sport != null)
                    textView.setText(sport.getTitle());
                textView.setTextSize(16);
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
    }
}
