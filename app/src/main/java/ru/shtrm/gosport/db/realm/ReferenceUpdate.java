package ru.shtrm.gosport.db.realm;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;


public class ReferenceUpdate extends RealmObject {
    @PrimaryKey
    private String referenceName;
    private Date updateDate;

    private static Date lastChanged(String name) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<ReferenceUpdate> list = realm.where(ReferenceUpdate.class).
                equalTo("referenceName", name).findAll();
        Date value;
        if (list.isEmpty()) {
            value = new Date(0);
        } else {
            value = list.first().updateDate;
        }

        realm.close();
        return value;
    }

    public static String lastChangedAsStr(String name) {
        Date date = ReferenceUpdate.lastChanged(name);
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(date);
    }

    /**
     * @param referenceName Название справочника (используем имя класса).
     * @param list          Список объектов справочника для сохранения в базу.
     * @param updateDate    Дата обновления содержимого справочника.
     */
    public static void saveReferenceData(String referenceName, final List list, Date updateDate) {
        Realm realm = Realm.getDefaultInstance();

        final ReferenceUpdate item = new ReferenceUpdate();
        item.setReferenceName(referenceName);
        item.setUpdateDate(updateDate);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(list);
                realm.copyToRealmOrUpdate(item);
            }
        });
        //realm.copyToRealmOrUpdate(list);
        //realm.copyToRealmOrUpdate(item);
        //realm.commitTransaction();
        realm.close();
    }

    /**
     * @param referenceName Название справочника (используем имя класса).
     * @param updateDate    Дата обновления содержимого справочника.
     */
    public static void saveReferenceData(String referenceName, Date updateDate) {
        Realm realm = Realm.getDefaultInstance();

        ReferenceUpdate item = new ReferenceUpdate();
        item.setReferenceName(referenceName);
        item.setUpdateDate(updateDate);

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(item);
        realm.commitTransaction();
        realm.close();
    }

    public String getReferenceName() {
        return referenceName;
    }

    private void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    private void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}
