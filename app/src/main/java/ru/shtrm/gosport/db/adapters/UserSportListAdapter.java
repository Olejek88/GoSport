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

import ru.shtrm.gosport.R;
import ru.shtrm.gosport.db.realm.UserSport;
import ru.shtrm.gosport.utils.MainFunctions;

public class UserSportListAdapter extends ArrayAdapter<UserSport> implements ListAdapter {
    private Context context;
    protected LayoutInflater inflater;
    ArrayList<UserSport> adapterData;

    public UserSportListAdapter(@NonNull Context context, int layoutResourceId,
                                ArrayList<UserSport> data) {
        super(context,layoutResourceId, data);
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
    public UserSport getItem(int position) {
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
            convertView = inflater.inflate(R.layout.user_item_list_layout, parent, false);
            viewHolder.icon = convertView.findViewById(R.id.user_image);
            viewHolder.name = convertView.findViewById(R.id.user_name);
            viewHolder.type = convertView.findViewById(R.id.user_type);
            viewHolder.level = convertView.findViewById(R.id.user_level);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        UserSport userSport;
        if (adapterData != null && adapterData.size()>0) {
            userSport = adapterData.get(position);
            viewHolder.name.setText(userSport.getUser().getName());
            viewHolder.type.setText(userSport.getAmplua().getName());
            if (userSport.getLevel() != null)
                    viewHolder.level.setText(userSport.getLevel().getTitle());
            String path = MainFunctions.getUserImagePath(context);
            if (userSport.getUser().getImage()!=null) {
                Bitmap image_bitmap = MainFunctions.getBitmapByPath(path, userSport.getUser().getImage());
                if (image_bitmap != null) {
                    viewHolder.icon.setImageBitmap(image_bitmap);
                } else {
                    viewHolder.icon.setImageBitmap
                            (BitmapFactory.decodeResource(context.getResources(), R.drawable.no_image));
                    }
            }
        }
        return convertView;
    }

    private static class ViewHolder {
        ImageView icon;
        TextView name;
        TextView level;
        TextView type;
    }

}
