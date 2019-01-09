package ru.shtrm.gosport.serverapi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import ru.shtrm.gosport.GoSportApplication;
import ru.shtrm.gosport.db.realm.Amplua;
import ru.shtrm.gosport.db.realm.Level;
import ru.shtrm.gosport.db.realm.LocalFiles;
import ru.shtrm.gosport.db.realm.ReferenceUpdate;
import ru.shtrm.gosport.db.realm.Sport;
import ru.shtrm.gosport.db.realm.Stadium;
import ru.shtrm.gosport.db.realm.Team;
import ru.shtrm.gosport.db.realm.Training;
import ru.shtrm.gosport.db.realm.User;
import ru.shtrm.gosport.rest.GSportAPIFactory;
import ru.shtrm.gosport.ui.MainActivity;

public class DataManager {
    private static final String TAG = "DataManager";

    /*
    private static class ReferenceName {
        public static String OperationResult = "OperationResult";
    }*/

    public static void updateReferences () {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // получаем справочники, обновляем всё несмотря на то что часть данных будет дублироваться
                final Date currentDate = new Date();
                String changedDate;
                String referenceName;

                // Sport
                referenceName = Sport.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    retrofit2.Response<List<Sport>> response = GSportAPIFactory.
                            getSportService().sport(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Sport> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // Amplua
                referenceName = Amplua.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    retrofit2.Response<List<Amplua>> response = GSportAPIFactory.
                            getAmpluaService().amplua(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Amplua> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // Level
                referenceName = Level.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    retrofit2.Response<List<Level>> response = GSportAPIFactory.
                            getLevelService().level(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Level> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // Stadium
                referenceName = Stadium.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    retrofit2.Response<List<Stadium>> response = GSportAPIFactory.
                            getStadiumService().stadium(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Stadium> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // Team
                referenceName = Team.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    retrofit2.Response<List<Team>> response = GSportAPIFactory.
                            getTeamService().team(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Team> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // Players
                referenceName = User.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    retrofit2.Response<List<User>> response = GSportAPIFactory.
                            getUserService().user(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<User> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }
        });
        thread.start();
    }

    /**
     * Получение объектов с сервера
     */
    private void getObjects (final Context context) {
        @SuppressLint("StaticFieldLeak") AsyncTask<String[], Integer, List<Training>> aTask =
                new AsyncTask<String[], Integer, List<Training>>() {
            @Override
            protected List<Training> doInBackground(String[]... params) {
                // обновляем справочники
                updateReferences();
                List<String> args = java.util.Arrays.asList(params[0]);

                // запрашиваем тренировки
                Call<List<Training>> call = GSportAPIFactory.getTrainingService().training();
                List<Training> result;
                try {
                    retrofit2.Response<List<Training>> response = call.execute();
                    result = response.body();
                } catch (Exception e) {
                    Log.d(TAG, e.getLocalizedMessage());
                    return null;
                }
                // список файлов для загрузки
                List<FilePath> files = new ArrayList<>();
                // строим список изображений для загрузки
                if (result != null) {
                    for (Training training : result) {
                        Stadium stadium = training.getStadium();
                        // общий путь до файлов на сервере
                        String basePath = "/storage/";
                        // общий путь до файлов локальный
                        String basePathLocal = "/files/";
                        if (stadium != null) {
                            // урл изображения стадиона
                            files.add(new FilePath(stadium.getImage(), basePath, basePathLocal));
                        }
                        Team team = training.getTeam();
                        if (team != null) {
                            // урл изображения команды
                            files.add(new FilePath(team.getPhoto(), basePath, basePathLocal));
                        }
                    }
                }

                // загружаем файлы
                int filesCount = 0;
                for (FilePath path : files) {
                    Call<ResponseBody> anotherCall =
                            GSportAPIFactory.getFileDownload().
                                    getFile(GoSportApplication.serverUrl + path.urlPath + path.fileName);
                    try {
                        retrofit2.Response<ResponseBody> r = anotherCall.execute();
                        ResponseBody trueImgBody = r.body();
                        if (trueImgBody == null) {
                            continue;
                        }
                        filesCount++;
                        publishProgress(filesCount);
                        File file = new File(context.getExternalFilesDir(path.localPath), path.fileName);
                        if (!file.getParentFile().exists()) {
                            if (!file.getParentFile().mkdirs()) {
                                Log.e(TAG, "Не удалось создать папку " +
                                        file.getParentFile().toString() +
                                        " для сохранения файла изображения!");
                                continue;
                            }
                        }

                        FileOutputStream fos = new FileOutputStream(file);
                        fos.write(trueImgBody.bytes());
                        fos.close();
                    } catch (Exception e) {
                        Log.e(TAG, e.getLocalizedMessage());
                    }
                }

                return result;
            }

            @Override
            protected void onPostExecute(List<Training> trainings) {
                super.onPostExecute(trainings);
                if (trainings == null) {
                    // сообщаем описание неудачи
                    Toast.makeText(context, "Ошибка при получении тренировок", Toast.LENGTH_LONG).show();
                } else {
                    int count = trainings.size();
                    // собщаем количество полученных нарядов
                    if (count > 0) {
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(trainings);
                        realm.commitTransaction();
                        realm.close();
                        //addToJournal("Клиент успешно получил " + count + " нарядов");
                        Toast.makeText(context, "Количество тренировок " + count, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Тренировок нет.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
            }
        };

        aTask.execute();
    }

    /**
     * Метод для отправки файлов загруженных пользователем
     */
    private static class sendFiles extends AsyncTask<LocalFiles[], Void, List<String>> {
        private WeakReference<MainActivity> activityReference;
        private Context mContext;
        List <LocalFiles> files;

        // only retain a weak reference to the activity
/*
        sendFiles(MainActivity context, List <LocalFiles> filesToSend) {
            activityReference = new WeakReference<>(context);
            files = filesToSend;
            mContext = context;
        }
*/

        @NonNull
        private RequestBody createPartFromString(String descriptionString) {
            return RequestBody.create(MultipartBody.FORM, descriptionString);
        }

        @NonNull
        private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
            File file = new File(fileUri.getPath());
            String type = null;
            String extension = MimeTypeMap.getFileExtensionFromUrl(fileUri.getPath());
            if (extension != null) {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            }

            MediaType mediaType = MediaType.parse(type);
            RequestBody requestFile = RequestBody.create(mediaType, file);
            MultipartBody.Part part = MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
            return part;
        }

        @Override
        protected List<String> doInBackground(LocalFiles[]... lists) {
            List<String> sendFiles = new ArrayList<>();
            for (LocalFiles file : lists[0]) {
                RequestBody descr = createPartFromString("Photos due execution operation.");
                Uri uri = null;
                try {
                    uri = Uri.fromFile(new File(
                            mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                            file.getFileName()));
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                List<MultipartBody.Part> list = new ArrayList<>();
                String fileUuid = file.getUuid();
                String formId = "file[" + fileUuid + "]";
                list.add(prepareFilePart(formId, uri));
                list.add(MultipartBody.Part.createFormData(formId + "[_id]", String.valueOf(file.get_id())));
                list.add(MultipartBody.Part.createFormData(formId + "[uuid]", file.getUuid()));
                list.add(MultipartBody.Part.createFormData(formId + "[userUuid]", file.getUser().getUuid()));
                list.add(MultipartBody.Part.createFormData(formId + "[object]", file.getObject()));
                list.add(MultipartBody.Part.createFormData(formId + "[fileName]", file.getFileName()));
                list.add(MultipartBody.Part.createFormData(formId + "[createdAt]", String.valueOf(file.getCreatedAt())));
                list.add(MultipartBody.Part.createFormData(formId + "[changedAt]", String.valueOf(file.getChangedAt())));
                // запросы делаем по одному, т.к. может сложиться ситуация когда будет попытка отправить
                // объём данных превышающий ограничения на отправку POST запросом на сервере
                Call<ResponseBody> call = GSportAPIFactory.getFileDownload().uploadFiles(descr, list);
                try {
                    retrofit2.Response response = call.execute();
                    ResponseBody result = (ResponseBody) response.body();
                    if (response.isSuccessful()) {
                        Log.d(TAG, "result" + result.contentType());
                        sendFiles.add(file.getFileName());
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }
            return sendFiles;
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            // get a reference to the activity if it is still there
            MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            super.onPostExecute(strings);
            // TODO: нужно придумать более правильный механизм передачи данных для отправки и обработки результата
            // пока сделано по тупому
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            for (String item : strings) {
                LocalFiles file = realm.where(LocalFiles.class).equalTo("filename", item).findFirst();
                file.setSent(true);
            }

            realm.commitTransaction();
            realm.close();

        }
        //LocalFiles[] sendFiles = files.toArray(new LocalFiles[]{});
        //task.execute(sendFiles);
    }

    private static void sendObjects (List < Training > trainings) {
        AsyncTask<Training[], Void, String> task = new AsyncTask<Training[], Void, String>() {
            @Override
            protected String doInBackground(Training[]... lists) {
                List<Training> args = Arrays.asList(lists[0]);
                Call<ResponseBody> call = GSportAPIFactory.getTrainingService().sendTraining(args);
                try {
                    retrofit2.Response response = call.execute();
                    Log.d(TAG, "response = " + response);
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
        };

        Training[] trainingsArray = trainings.toArray(new Training[]{});
        task.execute(trainingsArray);
    }

    /*
    private void sendUser () {
        AsyncTask<User[], Void, String> task = new AsyncTask<User[], Void, String>() {
            @Override
            protected String doInBackground(User[]... users) {
                AuthorizedUser authorizedUser = AuthorizedUser.getInstance();
                User user = realmDB.where(User.class).equalTo("uuid", authorizedUser.getUuid()).findFirst();
                Call<ResponseBody> call = GSportAPIFactory.getUserService().sendUser(user);
                try {
                    retrofit2.Response response = call.execute();
                    Log.d(TAG, "response = " + response);
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
        };

        task.execute();
    }*/

    class FilePath {
        String fileName;
        String urlPath;
        String localPath;

        FilePath(String name, String url, String local) {
            fileName = name;
            urlPath = url;
            localPath = local;
        }
    }
}
