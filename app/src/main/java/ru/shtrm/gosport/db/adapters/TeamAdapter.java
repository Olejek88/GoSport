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

import java.util.Date;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmList;
import io.realm.RealmResults;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.db.realm.Level;
import ru.shtrm.gosport.db.realm.Team;
import ru.shtrm.gosport.utils.MainFunctions;
import ru.shtrm.gosport.utils.RoundedImageView.*;

import static ru.shtrm.gosport.utils.RoundedImageView.getResizedBitmap;

public class TeamAdapter extends RealmBaseAdapter<Team> implements ListAdapter {
    public static final String TABLE_NAME = "Team";
    private Context context;
    protected LayoutInflater inflater;

    public TeamAdapter(@NonNull Context context, RealmResults<Team> data) {
        super(data);
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public TeamAdapter(@NonNull Context context, RealmList<Team> data) {
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

    public void setFilter(String text, Realm realmDB) {
        if (adapterData != null) {
            adapterData = realmDB.where(Team.class).equalTo("title",text).or().contains("title",text, Case.INSENSITIVE).findAll();
            notifyDataSetChanged();
        }
    }

    @Override
    public Team getItem(int position) {
        if (adapterData != null && adapterData.size()>0) {
            return adapterData.get(position);
        }
        return null;
    }

    /*
    @Override
    public long getItemId(int position) {
        Team team;
        if (adapterData != null && adapterData.size()>0) {
            team = adapterData.get(position);
            return team.get_id();
        }
        return 0;
    }*/

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            if (parent.getId() == R.id.simple_spinner ||
                    parent.getId() == R.id.profile_football_team ||
                    parent.getId() == R.id.profile_hockey_team ||
                    parent.getId() == R.id.training_add_team) {
                convertView = inflater.inflate(R.layout.simple_spinner_item, parent, false);
                viewHolder.name = (TextView) convertView.findViewById(R.id.spinner_item);
                viewHolder.name.setTextColor(context.getResources().getColor(R.color.larisaTextColor));
                convertView.setTag(viewHolder);
            }
            else {
                convertView = inflater.inflate(R.layout.team_reference_item_layout, parent, false);
                viewHolder.icon = (ImageView) convertView.findViewById(R.id.eril_image);
                viewHolder.name = (TextView) convertView.findViewById(R.id.eril_title);
                viewHolder.type = (TextView) convertView.findViewById(R.id.eril_type);
                viewHolder.level = (TextView) convertView.findViewById(R.id.eril_division);
                convertView.setTag(viewHolder);
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Team team;
        TextView textView = new TextView(context);
        if (adapterData != null && adapterData.size()>0) {
            team = adapterData.get(position);
            if (parent.getId() == R.id.simple_spinner || parent.getId() == R.id.profile_football_team || parent.getId() == R.id.profile_hockey_team || parent.getId() == R.id.training_add_team) {
                viewHolder.name.setText(team.getTitle());
                textView.setText(team.getTitle());
                textView.setTextSize(16);
                textView.setPadding(10, 10, 10, 10);
                textView.setTextColor(context.getResources().getColor(R.color.larisaTextColor));
            } else {
                viewHolder.name.setText(team.getTitle());
                if (team.getSport() != null)
                    viewHolder.type.setText(context.
                            getResources().getString(R.string.sport,team.getSport().getTitle()));
                if (team.getLevel() != null)
                    viewHolder.level.setText(context.
                            getResources().getString(R.string.level,team.getLevel().getTitle()));
                String path = MainFunctions.getUserImagePath(context);
                if (team.getPhoto()!=null) {
                    Bitmap image_bitmap = getResizedBitmap(path,
                            team.getPhoto(), 300, 0, team.getChangedAt().getTime());
                    if (image_bitmap != null) {
                        viewHolder.icon.setImageBitmap(image_bitmap);
                    } else {
                        viewHolder.icon.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.no_image));
                    }
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
        ImageView icon;
        TextView name;
        TextView level;
        TextView type;
    }

}
