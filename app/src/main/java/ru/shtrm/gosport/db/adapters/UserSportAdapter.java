package ru.shtrm.gosport.db.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.logging.Level;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.db.realm.Amplua;
import ru.shtrm.gosport.db.realm.Sport;
import ru.shtrm.gosport.db.realm.Team;
import ru.shtrm.gosport.db.realm.User;
import ru.shtrm.gosport.db.realm.UserSport;

public class UserSportAdapter extends RealmBaseAdapter<UserSport> implements ListAdapter {
    public static final String TABLE_NAME = "UserSport";
    private Context context;
    protected LayoutInflater inflater;

    public UserSportAdapter(@NonNull Context context, int resId, RealmResults<UserSport> data) {
        super(data);
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public int getCount() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<UserSport> rows = realm.where(UserSport.class).findAll();
        realm.close();
        return rows.size();
    }

    @Override
    public UserSport getItem(int position) {
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
            UserSport userSport = adapterData.get(position);
            textView.setText(userSport.getSport().getTitle());
            return textView;
        }

        if (parent.getId() == R.id.aboutDescription) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.listview, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.sport = (TextView) convertView.findViewById(R.id.info_text);
                viewHolder.amplua = (TextView) convertView.findViewById(R.id.info_text);
                viewHolder.team = (TextView) convertView.findViewById(R.id.info_text);
                viewHolder.level = (TextView) convertView.findViewById(R.id.info_text);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            UserSport userSport = adapterData.get(position);
            viewHolder.user.setText(userSport.getUser().getName());
            viewHolder.sport.setText(userSport.getSport().getTitle());
            viewHolder.amplua.setText(userSport.getAmplua().getName());
            viewHolder.team.setText(userSport.getTeam().getTitle());
            viewHolder.level.setText(userSport.getLevel().getTitle());
            return convertView;
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView user;
        TextView sport;
        TextView amplua;
        TextView team;
        TextView level;
    }
}
