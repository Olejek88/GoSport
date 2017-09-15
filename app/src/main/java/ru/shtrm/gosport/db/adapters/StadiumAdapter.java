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

import java.io.File;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmList;
import io.realm.RealmResults;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.db.realm.Sport;
import ru.shtrm.gosport.db.realm.Stadium;
import ru.shtrm.gosport.utils.MainFunctions;

import static ru.shtrm.gosport.utils.RoundedImageView.getResizedBitmap;
import static ru.shtrm.gosport.utils.RoundedImageView.getRoundedBitmap;

public class StadiumAdapter extends RealmBaseAdapter<Stadium> implements ListAdapter {

    public static final String TABLE_NAME = "Stadium";
    private Context context;
    protected LayoutInflater inflater;
    protected Sport sport;

    public StadiumAdapter(@NonNull Context context, RealmResults<Stadium> data, Sport sport) {
        super(data);
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        sport = sport;
    }
    public StadiumAdapter(@NonNull Context context, RealmList<Stadium> data) {
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

    @Override
    public long getItemId(int position) {
        Stadium stadium;
        if (adapterData != null && adapterData.size()>0) {
            stadium = adapterData.get(position);
            return stadium.get_id();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            if (parent.getId() == R.id.simple_spinner || parent.getId() == R.id.training_add_stadium) {
                convertView = inflater.inflate(R.layout.simple_spinner_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.spinner_item);
                convertView.setTag(viewHolder);
            }
            else if (parent.getId() == R.id.gps_listView) {
                convertView = inflater.inflate(R.layout.stadium_item_layout, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.image = (ImageView) convertView.findViewById(R.id.stadium_image);
                viewHolder.title = (TextView) convertView.findViewById(R.id.stadium_title);
                viewHolder.sport = (TextView) convertView.findViewById(R.id.stadium_type);
                viewHolder.address = (TextView) convertView.findViewById(R.id.stadium_address);
                convertView.setTag(viewHolder);
            } else {
                convertView = inflater.inflate(R.layout.stadium_layout, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.image = (ImageView) convertView.findViewById(R.id.stadium_image);
                viewHolder.descr = (TextView) convertView.findViewById(R.id.stadium_text_description);
                viewHolder.title = (TextView) convertView.findViewById(R.id.stadium_title);
                viewHolder.sport = (TextView) convertView.findViewById(R.id.stadium_type);
                viewHolder.address = (TextView) convertView.findViewById(R.id.stadium_address);
                convertView.setTag(viewHolder);
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Stadium stadium;
        if (adapterData != null && adapterData.size()>0) {
            stadium = adapterData.get(position);
            if (stadium != null) {
                if (parent.getId() == R.id.simple_spinner || parent.getId() == R.id.training_add_stadium) {
                    viewHolder.title.setText(stadium.getTitle());
                }
                else {
                    viewHolder.title.setText(stadium.getTitle());
                    String path = MainFunctions.getUserImagePath(context);
                    Bitmap image_bitmap = getResizedBitmap(path, stadium.getImage(), 600, 0, stadium.getChangedAt().getTime());
                    if (image_bitmap != null) {
                        viewHolder.image.setImageBitmap(image_bitmap);
                    }
                    if (viewHolder.descr!=null)
                        viewHolder.descr.setText(stadium.getDescription());
                    viewHolder.sport.setText(stadium.getSport().getTitle());
                    viewHolder.address.setText(stadium.getAddress());
                    //viewHolder.latitude.setText(String.valueOf(stadium.getLatitude()));
                    //viewHolder.longitude.setText(String.valueOf(stadium.getLongitude()));
                }
            }
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, null, parent);
    }

    private static class ViewHolder {
        TextView uuid;
        ImageView image;
        TextView sport;
        TextView title;
        TextView descr;
        TextView address;
        TextView latitude;
        TextView longitude;
    }
}
