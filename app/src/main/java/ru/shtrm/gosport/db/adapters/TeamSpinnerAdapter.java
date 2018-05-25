package ru.shtrm.gosport.db.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmList;
import io.realm.RealmResults;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.db.realm.Team;
import ru.shtrm.gosport.utils.MainFunctions;

public class TeamSpinnerAdapter extends ArrayAdapter<Team> {
    public static final String TABLE_NAME = "Team";
    private Context context;
    protected LayoutInflater inflater;
    ArrayList<Team> adapterData;

    public TeamSpinnerAdapter(@NonNull Context context, int layoutResourceId, ArrayList<Team> data) {
        super(context,layoutResourceId,data);
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.adapterData = data;
    }

    @Override
    public int getCount() {
        if (adapterData != null) {
            return adapterData.size();
        }
        return 0;
    }

    @Override
    public Team getItem(int position) {
        if (adapterData != null && adapterData.size()>0) {
            return adapterData.get(position);
        }
        return null;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.simple_spinner_item, parent, false);
            viewHolder.name = convertView.findViewById(R.id.spinner_item);
            viewHolder.name.setTextColor(context.getResources().getColor(R.color.larisaTextColor));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Team team;
        TextView textView = new TextView(context);
        if (adapterData != null && adapterData.size()>0) {
            team = adapterData.get(position);
            viewHolder.name.setText(team.getTitle());
            textView.setText(team.getTitle());
            textView.setTextSize(16);
            textView.setPadding(10, 10, 10, 10);
            textView.setTextColor(context.getResources().getColor(R.color.larisaTextColor));
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getView(position, null, parent);
    }

    private static class ViewHolder {
        TextView name;
    }

}
