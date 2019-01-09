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
import io.realm.RealmList;
import io.realm.RealmResults;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.db.realm.Sport;
import ru.shtrm.gosport.db.realm.Training;

public class TrainingAdapter extends RealmBaseAdapter<Training> implements ListAdapter {

    public static final String TABLE_NAME = "Training";
    private Context context;
    protected LayoutInflater inflater;

    public TrainingAdapter(@NonNull Context context, RealmResults<Training> data, Sport sport) {
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
            if (parent.getId() == R.id.trainings_listView) {
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
                convertView = inflater.inflate(R.layout.simple_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.title = convertView.findViewById(R.id.training_text_name);
                viewHolder.comment = convertView.findViewById(R.id.training_comment);
                viewHolder.cost = convertView.findViewById(R.id.training_cost);
                viewHolder.date = convertView.findViewById(R.id.training_date);
                viewHolder.level = convertView.findViewById(R.id.training_sport_level);
                viewHolder.sport = convertView.findViewById(R.id.training_sport);
                viewHolder.contact = convertView.findViewById(R.id.training_contact);
                viewHolder.stadium = convertView.findViewById(R.id.training_stadium);
                viewHolder.team = convertView.findViewById(R.id.training_team);
                convertView.setTag(viewHolder);
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Training training;
        if (adapterData != null) {
            training = adapterData.get(position);
            if (training != null) {
                if (parent.getId() == R.id.trainings_listView) {
                    viewHolder.icon.setImageResource(
                            Sport.getResourceIdBySport (training.getSport()));
                }
                else {
                    if (viewHolder.title!=null)
                        viewHolder.title.setText(training.getTitle());
                    if (viewHolder.comment!=null)
                        viewHolder.comment.setText(training.getComment());
                }
                viewHolder.contact.setText(context.getResources().getString(R.string.training_contact,
                        training.getUser().getName(),training.getUser().getPhone()));
                if (training.getTeam()!=null)
                    viewHolder.team.setText(context.getResources().getString(R.string.training_team,
                            training.getTeam().getTitle()));
                if (training.getStadium()!=null)
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
