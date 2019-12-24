package ru.shtrm.gosport.rest;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import ru.shtrm.gosport.GoSportApplication;
import ru.shtrm.gosport.db.realm.Amplua;
import ru.shtrm.gosport.db.realm.Event;
import ru.shtrm.gosport.db.realm.Level;
import ru.shtrm.gosport.db.realm.ReferenceUpdate;
import ru.shtrm.gosport.db.realm.Sport;
import ru.shtrm.gosport.db.realm.Stadium;
import ru.shtrm.gosport.db.realm.Team;
import ru.shtrm.gosport.model.AuthorizedUser;
import ru.shtrm.gosport.serverapi.ReferenceListSrv;
import ru.shtrm.gosport.serverapi.TokenSrv;

public class ReferenceProcessor {

    private static String dateFormat = "yyyy-MM-dd'T'HH:mm:ss";
    private Context mContext;

    /**
     * @param context context
     */
    public ReferenceProcessor(Context context) throws Exception {

        mContext = context;

        if (GoSportApplication.serverUrl.equals("")) {
            throw new Exception("URL сервера не указан!");
        }
    }


    /**
     * Получаем amplua
     *
     * @param bundle bundle
     * @return Bundle
     */
    public Bundle getAmplua(@SuppressWarnings("unused") Bundle bundle) {

        Bundle result;

        if (!checkToken()) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE, "Нет связи с сервером.");
            return result;
        }

        String referenceName = Amplua.class.getSimpleName();
        String lastChangedAt = ReferenceUpdate.lastChangedAsStr(referenceName);
        Date updateDate = new Date();
        Call<List<Amplua>> call = GSportAPIFactory.getAmpluaService()
                .amplua(lastChangedAt);
        try {
            retrofit2.Response<List<Amplua>> response = call.execute();
            ReferenceUpdate.saveReferenceData(referenceName, response.body(), updateDate);
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, true);
            return result;
        } catch (IOException e) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE, "Ошибка получения данных справочника.");
            return result;
        }
    }

    /**
     * Получаем события/соревнования
     *
     * @param bundle bundle
     * @return Bundle
     */
    public Bundle getEvents(@SuppressWarnings("unused") Bundle bundle) {

        Bundle result;

        if (!checkToken()) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE, "Нет связи с сервером.");
            return result;
        }

        String referenceName = Event.class.getSimpleName();
        String lastChangedAt = ReferenceUpdate.lastChangedAsStr(referenceName);
        Date updateDate = new Date();
        Call<List<Event>> call = GSportAPIFactory.getEventService()
                .events(lastChangedAt);
        try {
            retrofit2.Response<List<Event>> response = call.execute();
            ReferenceUpdate.saveReferenceData(referenceName, response.body(), updateDate);
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, true);
            return result;
        } catch (IOException e) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE, "Ошибка получения данных справочника.");
            return result;
        }
    }

    /**
     * Получаем уровни
     *
     * @param bundle bundle
     * @return Bundle
     */
    public Bundle getLevels(@SuppressWarnings("unused") Bundle bundle) {

        Bundle result;

        if (!checkToken()) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE, "Нет связи с сервером.");
            return result;
        }

        String referenceName = Level.class.getSimpleName();
        String lastChangedAt = ReferenceUpdate.lastChangedAsStr(referenceName);
        Date updateDate = new Date();
        Call<List<Level>> call = GSportAPIFactory.getLevelService()
                .level(lastChangedAt);
        try {
            retrofit2.Response<List<Level>> response = call.execute();
            ReferenceUpdate.saveReferenceData(referenceName, response.body(), updateDate);
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, true);
            return result;
        } catch (IOException e) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE, "Ошибка получения данных справочника.");
            return result;
        }
    }

    /**
     * Получаем виды спорта
     *
     * @param bundle bundle
     * @return Bundle
     */
    public Bundle getSports(@SuppressWarnings("unused") Bundle bundle) {

        Bundle result;

        if (!checkToken()) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE, "Нет связи с сервером.");
            return result;
        }

        String referenceName = Sport.class.getSimpleName();
        String lastChangedAt = ReferenceUpdate.lastChangedAsStr(referenceName);
        Date updateDate = new Date();
        Call<List<Sport>> call = GSportAPIFactory.getSportService()
                .sport(lastChangedAt);
        try {
            retrofit2.Response<List<Sport>> response = call.execute();
            ReferenceUpdate.saveReferenceData(referenceName, response.body(), updateDate);
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, true);
            return result;
        } catch (IOException e) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE, "Ошибка получения данных справочника.");
            return result;
        }
    }

    /**
     * Получаем площадки
     *
     * @param bundle bundle
     * @return Bundle
     */
    public Bundle getStadiums(@SuppressWarnings("unused") Bundle bundle) {

        Bundle result;

        if (!checkToken()) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE, "Нет связи с сервером.");
            return result;
        }

        String referenceName = Stadium.class.getSimpleName();
        String lastChangedAt = ReferenceUpdate.lastChangedAsStr(referenceName);
        Date updateDate = new Date();
        Call<List<Stadium>> call = GSportAPIFactory.getStadiumService()
                .stadium(lastChangedAt);
        try {
            retrofit2.Response<List<Stadium>> response = call.execute();
            ReferenceUpdate.saveReferenceData(referenceName, response.body(), updateDate);
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, true);
            return result;
        } catch (IOException e) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE, "Ошибка получения данных справочника.");
            return result;
        }
    }

    /**
     * Получаем команды
     *
     * @param bundle bundle
     * @return Bundle
     */
    public Bundle getTeams(@SuppressWarnings("unused") Bundle bundle) {

        Bundle result;

        if (!checkToken()) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE, "Нет связи с сервером.");
            return result;
        }

        String referenceName = Team.class.getSimpleName();
        String lastChangedAt = ReferenceUpdate.lastChangedAsStr(referenceName);
        Date updateDate = new Date();
        Call<List<Team>> call = GSportAPIFactory.getTeamService()
                .team(lastChangedAt);
        try {
            retrofit2.Response<List<Team>> response = call.execute();
            ReferenceUpdate.saveReferenceData(referenceName, response.body(), updateDate);
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, true);
            return result;
        } catch (IOException e) {
            result = new Bundle();
            result.putBoolean(IServiceProvider.RESULT, false);
            result.putString(IServiceProvider.MESSAGE, "Ошибка получения данных справочника.");
            return result;
        }
    }

    /**
     * @param referenceName referenceName
     * @return String
     */
    private String getReferenceURL(String referenceName) {

        String referenceUrl = null;
        String url = GoSportApplication.serverUrl.concat("/api/references");
        String jsonString;

        jsonString = getReferenceData(url);
        if (jsonString != null) {
            Gson gson = new GsonBuilder().create();
            // разбираем полученные данные
            ArrayList<ReferenceListSrv> list = gson.fromJson(jsonString,
                    new TypeToken<ArrayList<ReferenceListSrv>>() {
                        @SuppressWarnings("unused")
                        private static final long serialVersionUID = 1;
                    }.getType());
            for (ReferenceListSrv item : list) {
                if (item.getReferenceName().equals(referenceName)) {
                    referenceUrl = item.getLinks().get(0).getLink();
                    break;
                }
            }
        }
        return referenceUrl;
    }

    /**
     * Получаем токен. Метод использульзуется для проверки наличия токена, так
     * как может сложится ситуация когда пользователь вошел в систему но токен
     * не получил из за отсутствия связи.
     *
     * @return boolean
     */
    private boolean checkToken() {

        AuthorizedUser au = AuthorizedUser.getInstance();
        if (au.getToken() == null) {
            Call<TokenSrv> call = GSportAPIFactory.getTokenService().tokenByUUID(au.getUuid(), TokenSrv.Type.LABEL);
            try {
                retrofit2.Response<TokenSrv> response = call.execute();
                TokenSrv token = response.body();
                assert token != null;
                au.setToken(token.getAccessToken());
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return true;
        }
    }

    /**
     * Делает запрос по переданному url и возвращает строку данных
     *
     * @param url url
     * @return String
     */
    private String getReferenceData(String url) {

        try {
            URI requestUri = new URI(url);
            Log.d("test", "requestUri = " + requestUri.toString());

            Map<String, List<String>> headers = new ArrayMap<>();
            List<String> tList = new ArrayList<>();

            tList.add(AuthorizedUser.getInstance().getBearer());
            headers.put("Authorization", tList);

            Request request = new Request(RestClient.Method.GET, requestUri, headers, null);
            Response response = new RestClient().execute(request);

            if (response.mStatus == 200) {
                String jsonString = new String(response.mBody);
                Log.d("test", jsonString);
                return jsonString;
            } else {
                throw new Exception(
                        "Не удалось получить данные справочника. URL: " + url);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
