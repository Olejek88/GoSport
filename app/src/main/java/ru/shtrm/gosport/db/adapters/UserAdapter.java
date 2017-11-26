package ru.shtrm.gosport.db.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.db.realm.User;

import static ru.shtrm.gosport.utils.RoundedImageView.getResizedBitmap;

public class UserAdapter extends RealmBaseAdapter<User> implements ListAdapter {
    private static final String TABLE_NAME = "User";
    private static final int MAX_USERS = 100;

    private Context context;
    protected LayoutInflater inflater;

    //private int counter=0;
    private String taskTemplateUuid;
    private boolean[] visibility = new boolean[MAX_USERS];
    private boolean[] completed = new boolean[MAX_USERS];

    public UserAdapter(@NonNull Context context, RealmResults<User> data, String taskTemplateUuid) {
        super(data);
        this.context = context;
        this.taskTemplateUuid = taskTemplateUuid;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (adapterData != null) {
            if (adapterData.size() < MAX_USERS) {
                return adapterData.size();
            } else {
                return MAX_USERS;
            }
        } else {
            return 0;
        }
    }

    @Override
    public User getItem(int position) {
        User user;
        if (adapterData != null) {
            user = adapterData.get(position);
            return user;
        }

        return null;
    }

    /*
    @Override
    public long getItemId(int position) {
        if (adapterData != null) {
            return adapterData.get(position).get_id();
        } else {
            return 0;
        }
    }*/

    public void setItemVisibility(int position) {
        visibility[position] = !visibility[position];
    }

    public void setItemEnable(int position, boolean enable) {
        if (position < MAX_USERS) {
            completed[position] = enable;
        }
    }

    public boolean getItemEnable(int position) {
        return completed[position];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        User user;
        viewHolder = new ViewHolder();
        if (adapterData == null) return convertView;
        if (position > MAX_USERS) {
            Log.d("Debug", "position > max_users");
            return convertView;
        }

        user = adapterData.get(position);
        if (convertView == null) {
            if (parent.getId() == R.id.simple_spinner) {
            }

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    public static class ViewHolder {
        TextView name;
    }
}
