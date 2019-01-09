package ru.shtrm.gosport.db.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.db.realm.Sport;
import ru.shtrm.gosport.db.realm.Training;
import ru.shtrm.gosport.db.realm.UserTraining;

public class UserTrainingListAdapter extends RealmBaseAdapter<UserTraining> implements ListAdapter {

    public static final String TABLE_NAME = "Training";
    private Context context;
    protected LayoutInflater inflater;

    public UserTrainingListAdapter(@NonNull Context context, RealmResults<UserTraining> data) {
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
    public UserTraining getItem(int position) {
        if (adapterData != null) {
            return adapterData.get(position);
        }
        return null;
    }

    /*
    @Override
    public long getItemId(int position) {
        Training training;
        if (adapterData != null) {
            training = adapterData.get(position);
            return training.get_id();
        }
        return 0;
    }*/

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.training_item_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.icon = convertView.findViewById(R.id.training_sport_icon);
            viewHolder.cost = convertView.findViewById(R.id.training_cost);
            viewHolder.date = convertView.findViewById(R.id.training_date);
            viewHolder.stadium = convertView.findViewById(R.id.training_stadium);
            viewHolder.level = convertView.findViewById(R.id.training_sport_level);
            viewHolder.sport = convertView.findViewById(R.id.training_sport);
            viewHolder.contact = convertView.findViewById(R.id.training_contact);
            viewHolder.team = convertView.findViewById(R.id.training_sport_team);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        UserTraining userTraining;
        Training training;
        if (adapterData != null) {
            userTraining = adapterData.get(position);
            if (userTraining != null) {
                training = userTraining.getTraining();
                if (training!=null) {
                    viewHolder.icon.setImageResource(
                            Sport.getResourceIdBySport (userTraining.getTraining().getSport()));
                    if (viewHolder.title != null)
                        viewHolder.title.setText(training.getTitle());
                    if (viewHolder.comment != null)
                        viewHolder.comment.setText(training.getComment());
                    viewHolder.contact.setText(context.getResources().getString(R.string.training_contact,
                            training.getUser().getName(), training.getUser().getPhone()));
                    if (training.getTeam() != null)
                        viewHolder.team.setText(context.getResources().getString(R.string.training_team,
                                training.getTeam().getTitle()));
                    if (training.getStadium() != null)
                        viewHolder.stadium.setText(training.getStadium().getTitle());
                    viewHolder.cost.setText(context.getResources().getString(R.string.training_cost,
                            training.getCost()));
                    viewHolder.sport.setText(training.getSport().getTitle());
                    Date lDate = training.getDate();
                    if (lDate != null) {
                        String sDate = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.US).format(lDate);
                        viewHolder.date.setText(sDate);
                    } else {
                        viewHolder.date.setText(R.string.not_started);
                    }
                    viewHolder.level.setText(training.getLevel().getTitle());
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
        TextView contact;
        TextView date;
    }
}
