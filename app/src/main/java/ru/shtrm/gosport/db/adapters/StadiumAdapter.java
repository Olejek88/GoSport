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

import io.realm.RealmBaseAdapter;
import io.realm.RealmList;
import io.realm.RealmResults;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.db.realm.Stadium;

import static ru.shtrm.gosport.utils.RoundedImageView.getResizedBitmap;
import static ru.shtrm.gosport.utils.RoundedImageView.getRoundedBitmap;

public class StadiumAdapter extends RealmBaseAdapter<Stadium> implements ListAdapter {

    public static final String TABLE_NAME = "Stadium";
    private Context context;
    protected LayoutInflater inflater;

    public StadiumAdapter(@NonNull Context context, RealmResults<Stadium> data) {
        super(data);
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }
    public StadiumAdapter(@NonNull Context context, RealmList<Stadium> data) {
        super(data);
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public int getCount() {
        return adapterData.size();
    }

    @Override
    public Stadium getItem(int position) {
        if (adapterData != null) {
            return adapterData.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        Stadium stadium;
        if (adapterData != null) {
            stadium = adapterData.get(position);
            return stadium.get_id();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.simple_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) convertView.findViewById(R.id.eril_image);
            viewHolder.title = (TextView) convertView.findViewById(R.id.eril_title);
            viewHolder.objectType = (TextView) convertView.findViewById(R.id.eril_type);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Stadium stadium;
        if (adapterData != null) {
            stadium = adapterData.get(position);
            if (stadium != null) {
                viewHolder.title.setText(stadium.getTitle());
                String path = context.getExternalFilesDir("/objects") + File.separator;
                Bitmap image_bitmap = getResizedBitmap(path, stadium.getImage(), 300, 0, stadium.getChangedAt().getTime());
                if (image_bitmap != null) {
                    viewHolder.image.setImageBitmap(image_bitmap);
                } else {
                    viewHolder.image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.no_image));
                }
                viewHolder.title.setText(stadium.getTitle());
                viewHolder.objectType.setText(stadium.getSport().getTitle());
                viewHolder.descr.setText(stadium.getDescription());
                viewHolder.latitude.setText(String.valueOf(stadium.getLatitude()));
                viewHolder.longitude.setText(String.valueOf(stadium.getLongitude()));
            }
        }
        return convertView;
    }


    private static class ViewHolder {
        TextView uuid;
        ImageView image;
        TextView objectType;
        TextView title;
        TextView descr;
        TextView latitude;
        TextView longitude;
    }
}
