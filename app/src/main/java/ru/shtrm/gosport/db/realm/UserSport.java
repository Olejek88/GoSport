package ru.shtrm.gosport.db.realm;

import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;

public class UserSport extends RealmObject {
    private String _id;
    private String uuid;
    private User user;
    private Sport sport;
    private Amplua amplua;
    private Team team;
    private Level level;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Sport getSport() {
        return sport;
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    public Amplua getAmplua() {
        return amplua;
    }

    public void setAmplua(Amplua amplua) {
        this.amplua = amplua;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
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

    static public ArrayList<UserSport> getUserSportsByTraining(Realm realmDB, Training training) {
        ArrayList<UserSport> userSports = new ArrayList<>();
        RealmResults<UserTraining> userTrainings = realmDB.where(UserTraining.class).
                equalTo("training.uuid", training.getUuid()).
                findAll();
        for (UserTraining userTraining : userTrainings) {
            UserSport userSport = realmDB.where(UserSport.class).
                    equalTo("user.uuid", userTraining.getUser().getUuid()).
                    findFirst();
            userSports.add(userSport);
        }
        return userSports;
    }

}
