package ru.shtrm.gosport.utils;

import java.util.Date;

import io.realm.Realm;
import ru.shtrm.gosport.db.realm.Sport;
import ru.shtrm.gosport.db.realm.Level;
import ru.shtrm.gosport.db.realm.Amplua;
import ru.shtrm.gosport.db.realm.Team;

public class LoadTestData {
    public static Sport sport_hockey, sport_football;
    public static Level level_hockey, level_football;
    public static Amplua amplua;
    public static Team team;

    public static void LoadAllTestData() {

        final Realm realmDB;
        realmDB = Realm.getDefaultInstance();

        final String sportHockeyUuid = "4462ed77-9bf0-4542-b127-f4ecefce49da";
        final String sportFootballUuid = "5562ed77-9bf0-4542-b127-f4ecefce49da";

        final String noTeamUuid = "5562ed77-9bf0-4542-b127-f4ecffffaaaa";
        final String noTeamUuid2 = "5562ed77-9bf0-4542-b127-f4ecffffbbbb";

        final String levelHockeyNewbee = "4462ed77-9bf0-4542-b127-f4ecefce4aaa";
        final String levelHockeyDebutant = "5562ed77-9bf0-4542-b127-f4ecefce4aada";
        final String levelHockeyLubitel = "1dd8d4f8-5c98-4444-86ed-97aabc2059f6";
        final String levelHockeyLubitelPlus = "1dd8d4f8-5c98-5445-86ed-97ddbc2059f8";
        final String levelHockeyRazryadnik = "1dd8d4f8-5c98-5445-86ed-97ddbc2559f8";
        final String levelHockeyMaster = "1dd8d4f8-5c98-5445-86ed-97debc2559f8";
        final String levelHockeyVeteran = "1dd8d4f8-5c98-5445-86ed-97daaa2559f8";

        final String ampluaHockeyGoalie = "4462ed77-9bf0-4542-b127-f4ecefce444a";
        final String ampluaHockeyDefender = "4462ed77-9bf0-4542-b127-f4eceece423a";
        final String ampluaHockeyForward = "4462ed77-9bf0-4542-b127-f4ecedce423a";
        final String ampluaHockeyUniversal = "4462ed77-9bf0-4542-b127-f4eceace423a";

        final String levelFootballNewbee = "1462ed77-9bf0-4542-b127-f4ecefce4aaa";
        final String levelFootballDebutant = "1562ed77-9bf0-4542-b127-f4ecefce4aada";
        final String levelFootballLubitel = "2dd8d4f8-5c98-4444-86ed-97aabc2059f6";
        final String levelFootballLubitelPlus = "1dd8d4f8-5c98-5445-86ed-97ddbc4559f8";
        final String levelFootballRazryadnik = "2dd8d4f8-5c98-5445-86ed-97ddbc2559f8";
        final String levelFootballMaster = "2dd8d4f8-5c98-5445-86ed-97debc2559f8";
        final String levelFootballVeteran = "2dd8d4f8-5c98-5445-86ed-97daaa2559f8";

        final String ampluaFootballGoalie = "3462ed77-9bf0-4542-b127-f4ecefce444a";
        final String ampluaFootballDefender = "3462ed77-9bf0-4542-b127-f4eceece423a";
        final String ampluaFootballForward = "3462ed77-9bf0-4542-b127-f4ecedce423a";
        final String ampluaFootballUniversal = "3462ed77-9bf0-4542-b127-f4eceace423a";

        // Sport --------------------
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                sport_hockey = realmDB.createObject(Sport.class, 1);
                sport_hockey.setUuid(sportHockeyUuid);
                sport_hockey.setTitle("Хоккей");
                sport_hockey.setChangedAt(new Date());
                sport_hockey.setCreatedAt(new Date());
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                sport_football = realmDB.createObject(Sport.class, 2);
                sport_football.setUuid(sportFootballUuid);
                sport_football.setTitle("Футбол");
                sport_football.setChangedAt(new Date());
                sport_football.setCreatedAt(new Date());
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                team = realmDB.createObject(Team.class, 1);
                team.setUuid(noTeamUuid);
                team.setTitle("Не в команде");
                team.setSport(sport_hockey);
                team.setDescription("");
                team.setPhoto("");
                team.setChangedAt(new Date());
                team.setCreatedAt(new Date());
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                team = realmDB.createObject(Team.class, 2);
                team.setUuid(noTeamUuid);
                team.setTitle("Не в команде");
                team.setDescription("");
                team.setSport(sport_football);
                team.setPhoto("");
                team.setChangedAt(new Date());
                team.setCreatedAt(new Date());
            }
        });

        // Level --------------------
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                level_hockey = realmDB.createObject(Level.class,1);
                level_hockey.setUuid(levelHockeyNewbee);
                level_hockey.setTitle("Новичок");
                level_hockey.setSport(sport_hockey);
                level_hockey.setChangedAt(new Date());
                level_hockey.setCreatedAt(new Date());
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                level_hockey = realmDB.createObject(Level.class,2);
                level_hockey.setUuid(levelHockeyDebutant);
                level_hockey.setTitle("Дебютант");
                level_hockey.setSport(sport_hockey);
                level_hockey.setChangedAt(new Date());
                level_hockey.setCreatedAt(new Date());
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                level_hockey = realmDB.createObject(Level.class,3);
                level_hockey.setUuid(levelHockeyLubitel);
                level_hockey.setTitle("Любитель");
                level_hockey.setSport(sport_hockey);
                level_hockey.setChangedAt(new Date());
                level_hockey.setCreatedAt(new Date());
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                level_hockey = realmDB.createObject(Level.class,4);
                level_hockey.setUuid(levelHockeyLubitelPlus);
                level_hockey.setTitle("Любитель+");
                level_hockey.setSport(sport_hockey);
                level_hockey.setChangedAt(new Date());
                level_hockey.setCreatedAt(new Date());
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                level_hockey = realmDB.createObject(Level.class,5);
                level_hockey.setUuid(levelHockeyRazryadnik);
                level_hockey.setTitle("Разрядник");
                level_hockey.setSport(sport_hockey);
                level_hockey.setChangedAt(new Date());
                level_hockey.setCreatedAt(new Date());
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                level_hockey = realmDB.createObject(Level.class,6);
                level_hockey.setUuid(levelHockeyMaster);
                level_hockey.setTitle("Мастер");
                level_hockey.setSport(sport_hockey);
                level_hockey.setChangedAt(new Date());
                level_hockey.setCreatedAt(new Date());
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                level_hockey = realmDB.createObject(Level.class,7);
                level_hockey.setUuid(levelHockeyVeteran);
                level_hockey.setTitle("Ветеран");
                level_hockey.setSport(sport_hockey);
                level_hockey.setChangedAt(new Date());
                level_hockey.setCreatedAt(new Date());
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                level_hockey = realmDB.createObject(Level.class,8);
                level_hockey.setUuid(levelFootballNewbee);
                level_hockey.setTitle("Новичок");
                level_hockey.setSport(sport_football);
                level_hockey.setChangedAt(new Date());
                level_hockey.setCreatedAt(new Date());
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                level_hockey = realmDB.createObject(Level.class,9);
                level_hockey.setUuid(levelFootballDebutant);
                level_hockey.setTitle("Дебютант");
                level_hockey.setSport(sport_football);
                level_hockey.setChangedAt(new Date());
                level_hockey.setCreatedAt(new Date());
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                level_hockey = realmDB.createObject(Level.class,10);
                level_hockey.setUuid(levelFootballLubitel);
                level_hockey.setTitle("Любитель");
                level_hockey.setSport(sport_football);
                level_hockey.setChangedAt(new Date());
                level_hockey.setCreatedAt(new Date());
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                level_hockey = realmDB.createObject(Level.class,11);
                level_hockey.setUuid(levelFootballLubitelPlus);
                level_hockey.setTitle("Любитель+");
                level_hockey.setSport(sport_football);
                level_hockey.setChangedAt(new Date());
                level_hockey.setCreatedAt(new Date());
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                level_hockey = realmDB.createObject(Level.class,12);
                level_hockey.setUuid(levelFootballRazryadnik);
                level_hockey.setTitle("Разрядник");
                level_hockey.setSport(sport_football);
                level_hockey.setChangedAt(new Date());
                level_hockey.setCreatedAt(new Date());
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                level_hockey = realmDB.createObject(Level.class,13);
                level_hockey.setUuid(levelFootballMaster);
                level_hockey.setTitle("Мастер");
                level_hockey.setSport(sport_football);
                level_hockey.setChangedAt(new Date());
                level_hockey.setCreatedAt(new Date());
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                level_hockey = realmDB.createObject(Level.class,14);
                level_hockey.setUuid(levelFootballVeteran);
                level_hockey.setTitle("Ветеран");
                level_hockey.setSport(sport_football);
                level_hockey.setChangedAt(new Date());
                level_hockey.setCreatedAt(new Date());
            }
        });

        // Amplua --------------------
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                amplua = realmDB.createObject(Amplua.class,1);
                amplua.setUuid(ampluaHockeyGoalie);
                amplua.setTitle("Вратарь");
                amplua.setSport(sport_hockey);
                amplua.setChangedAt(new Date());
                amplua.setCreatedAt(new Date());
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                amplua = realmDB.createObject(Amplua.class,2);
                amplua.setUuid(ampluaHockeyDefender);
                amplua.setTitle("Защитник");
                amplua.setSport(sport_hockey);
                amplua.setChangedAt(new Date());
                amplua.setCreatedAt(new Date());
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                amplua = realmDB.createObject(Amplua.class,3);
                amplua.setUuid(ampluaHockeyForward);
                amplua.setTitle("Нападающий");
                amplua.setSport(sport_hockey);
                amplua.setChangedAt(new Date());
                amplua.setCreatedAt(new Date());
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                amplua = realmDB.createObject(Amplua.class,4);
                amplua.setUuid(ampluaHockeyUniversal);
                amplua.setTitle("Универсал");
                amplua.setSport(sport_hockey);
                amplua.setChangedAt(new Date());
                amplua.setCreatedAt(new Date());
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                amplua = realmDB.createObject(Amplua.class,5);
                amplua.setUuid(ampluaFootballGoalie);
                amplua.setTitle("Вратарь");
                amplua.setSport(sport_football);
                amplua.setChangedAt(new Date());
                amplua.setCreatedAt(new Date());
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                amplua = realmDB.createObject(Amplua.class,6);
                amplua.setUuid(ampluaFootballDefender);
                amplua.setTitle("Защитник");
                amplua.setSport(sport_football);
                amplua.setChangedAt(new Date());
                amplua.setCreatedAt(new Date());
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                amplua = realmDB.createObject(Amplua.class,7);
                amplua.setUuid(ampluaFootballForward);
                amplua.setTitle("Нападающий");
                amplua.setSport(sport_football);
                amplua.setChangedAt(new Date());
                amplua.setCreatedAt(new Date());
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                amplua = realmDB.createObject(Amplua.class,8);
                amplua.setUuid(ampluaFootballUniversal);
                amplua.setTitle("Универсал");
                amplua.setSport(sport_football);
                amplua.setChangedAt(new Date());
                amplua.setCreatedAt(new Date());
            }
        });


        realmDB.close();
    }

    public static void DeleteSomeData() {
        final Realm realmDB;
        realmDB = Realm.getDefaultInstance();
        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realmDB.where(Sport.class).findAll().deleteAllFromRealm();
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realmDB.where(Amplua.class).findAll().deleteAllFromRealm();
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realmDB.where(Level.class).findAll().deleteAllFromRealm();
            }
        });

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realmDB.where(Team.class).findAll().deleteAllFromRealm();
            }
        });

        realmDB.close();
    }

}
