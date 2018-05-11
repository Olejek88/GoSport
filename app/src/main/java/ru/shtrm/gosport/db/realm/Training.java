package ru.shtrm.gosport.db.realm;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Training extends RealmObject {
    @PrimaryKey
    private String _id;
    private String uuid;
    private String name;
    private User user;
    private Team team;
    private String comment;
    private int cost;
    private int players;
    private Sport sport;
    private Level level;
    private Stadium stadium;
    private Date date;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getPlayers() {
        return players;
    }

    public void setPlayers(int players) {
        this.players = players;
    }

    public Sport getSport() {
        return sport;
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Stadium getStadium() {
        return stadium;
    }

    public void setStadium(Stadium stadium) {
        this.stadium = stadium;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public String getDateFormatted(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        return formatter.format(date);
    }
}
