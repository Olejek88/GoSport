package ru.shtrm.gosport.db.adapters;

import android.content.Context;
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
import ru.shtrm.gosport.db.realm.Level;
import ru.shtrm.gosport.db.realm.Sport;

public class AmpluaAdapter extends RealmBaseAdapter<Amplua> implements ListAdapter {
    public static final String TABLE_NAME = "Amplua";
    private Context context;
    protected LayoutInflater inflater;
    protected Sport sport;

    public AmpluaAdapter(@NonNull Context context, RealmResults<Amplua> data, Sport sport) {
        super(data);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.sport = sport;
    }

    @Override
    public int getCount() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Amplua> rows = realm.where(Amplua.class).equalTo("sport.uuid",sport.getUuid()).findAll();
        realm.close();
        return rows.size();
    }

    @Override
    public Amplua getItem(int position) {
        if (adapterData != null) {
            return adapterData.get(position);
        }
        return null;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new AmpluaAdapter.ViewHolder();
            if (parent.getId() == R.id.reference_listView) {
                convertView = inflater.inflate(R.layout.listview, parent, false);
                viewHolder.title = (TextView) convertView.findViewById(R.id.lv_firstLine);
                viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
                convertView.setTag(viewHolder);
            }
            if (parent.getId() == R.id.simple_spinner ||
                    parent.getId() == R.id.profile_football_amplua ||
                    parent.getId() == R.id.profile_hockey_amplua) {
                convertView = inflater.inflate(R.layout.simple_spinner_item, parent, false);
                viewHolder.title = (TextView) convertView.findViewById(R.id.spinner_item);
                viewHolder.title.setTextColor(context.getResources().getColor(R.color.larisaTextColor));
                convertView.setTag(viewHolder);
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Amplua amplua;
        if (adapterData != null && viewHolder.title !=null) {
            amplua = adapterData.get(position);
            if (amplua != null)
                viewHolder.title.setText(amplua.getName());
        }

        if (convertView == null) {
            TextView textView = new TextView(context);
            if (adapterData != null) {
                amplua = adapterData.get(position);
                if (amplua != null)
                    textView.setText(amplua.getName());
                textView.setTextSize(16);
                textView.setTextColor(context.getResources().getColor(R.color.larisaTextColor));
                textView.setPadding(10,10,10,10);
            }
            return textView;
        }
        //if (amplua.getSport().getTitle().equals("Хоккей")) viewHolder.icon.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.hockey_32));
        //if (amplua.getSport().getTitle().equals("Футбол")) viewHolder.icon.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.football_32));
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
