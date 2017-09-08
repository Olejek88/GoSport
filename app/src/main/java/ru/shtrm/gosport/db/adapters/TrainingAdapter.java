package ru.shtrm.gosport.db.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.RealmBaseAdapter;
import io.realm.RealmList;
import io.realm.RealmResults;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.db.realm.Level;
import ru.shtrm.gosport.db.realm.Sport;
import ru.shtrm.gosport.db.realm.Stadium;
import ru.shtrm.gosport.db.realm.Team;
import ru.shtrm.gosport.db.realm.Training;
import ru.shtrm.gosport.db.realm.User;

import static ru.shtrm.gosport.utils.RoundedImageView.getResizedBitmap;

public class TrainingAdapter extends RealmBaseAdapter<Training> implements ListAdapter {

    public static final String TABLE_NAME = "Training";
    private Context context;
    protected LayoutInflater inflater;

    public TrainingAdapter(@NonNull Context context, RealmResults<Training> data) {
        super(data);
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public TrainingAdapter(@NonNull Context context, RealmList<Training> data) {
        super(data);
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public int getCount() {
        if (adapterData != null) {
            return adapterData.size();
        }
        return 0;
    }

    @Override
    public Training getItem(int position) {
        if (adapterData != null) {
            return adapterData.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        Training training;
        if (adapterData != null) {
            training = adapterData.get(position);
            return training.get_id();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            if (parent.getId() == R.id.eril_status) {
                convertView = inflater.inflate(R.layout.simple_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.icon = (ImageView) convertView.findViewById(R.id.eril_image);
                convertView.setTag(viewHolder);
            } else {
                convertView = inflater.inflate(R.layout.simple_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.icon = (ImageView) convertView.findViewById(R.id.eril_image);
                convertView.setTag(viewHolder);
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Training training;
        if (adapterData != null) {
            training = adapterData.get(position);
            if (training != null) {
                viewHolder.title.setText(training.getTitle());
                if (parent.getId() == R.id.eril_status) {
                    if (training.getLevel().get_id() == 1)
                        viewHolder.icon.setImageResource(R.drawable.football_32);
                } else {
                }
            }
        }
        return convertView;
    }


    private static class ViewHolder {
        ImageView icon;
        TextView uuid;
        TextView title;
        TextView team;
        TextView comment;
        TextView cost;
        TextView sport;
        TextView level;
        TextView stadium;
        TextView date;
    }
}
