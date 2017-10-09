package ru.shtrm.gosport.db.realm;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Olejek
 *         Created on 2.05.17.
 */
public class Amplua extends RealmObject {
    @PrimaryKey
    private long _idd;
    private String uuid;
    private String name;
    private RealmList<Sport> sports;
    private Date createdAt;
    private Date changedAt;

    public long get_idd() {
        return _idd;
    }

    public void set_idd(long _idd) {
        this._idd = _idd;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RealmList<Sport> getSport() {
        return sports;
    }

    public void setSport(RealmList<Sport> sports) {
        this.sports = sports;
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
}
