package ru.shtrm.gosport.db.realm;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import ru.shtrm.gosport.R;

public class Sport extends RealmObject {
    @PrimaryKey
    private String _id;
    private String uuid;
    private String name;
    private Date createdAt;
    private Date changedAt;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTitle() {
        return name;
    }

    public void setTitle(String name) {
        this.name = name;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(Date changedAt) {
        this.changedAt = changedAt;
    }

    public static int getResourceIdBySport (Sport sport) {
        if (sport.getTitle().equals("Хоккей"))
            return R.drawable.menu_hockey;
        if (sport.getTitle().equals("Футбол"))
            return R.drawable.user_football;
        if (sport.getTitle().equals("Волейбол"))
            return R.drawable.menu_volleyball;
        if (sport.getTitle().equals("Баскетбол"))
            return R.drawable.menu_basketball;
        return R.drawable.menu_user;
    }
}
