package ru.shtrm.gosport.db.realm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.annotations.PrimaryKey;
import ru.shtrm.gosport.model.AuthorizedUser;

public class User extends RealmObject {
    @PrimaryKey
    private long id;
    private String _id;
    private String uuid;
    private String name;
    private String login;
    private String pass;
    private int type;
    private int age;
    private Date birthDate;
    private boolean active;
    private String image;
    private String phone;
    private String vk;
    private Date createdAt;
    private Date changedAt;


    public long getid() {
        return id;
    }

    public void setid(long id) {
        this.id = id;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public String getBirthDateFormatted(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        return formatter.format(birthDate);
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getVk() {
        return vk;
    }

    public void setVk(String vk) {
        this.vk = vk;
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

    public User getActiveUser (Realm realmDB) {
        return realmDB.where(User.class).
                equalTo("uuid", AuthorizedUser.getInstance().getUuid()).findFirst();
    }

    static public ArrayList<User> getUsersByTraining(Realm realmDB, Training training) {
        ArrayList<User> users = new ArrayList<>();
        RealmResults<UserTraining> userTrainings = realmDB.where(UserTraining.class).
                equalTo("training.uuid", training.getUuid()).
                findAllSorted("name", Sort.DESCENDING);
        for (UserTraining userTraining : userTrainings) {
            users.add(userTraining.getUser());
        }
        return users;
    }
}
