package ru.shtrm.gosport.ui.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import ru.shtrm.gosport.R;
import ru.shtrm.gosport.databinding.StadiumInfoFragmentBinding;
import ru.shtrm.gosport.db.adapters.TrainingAdapter;
import ru.shtrm.gosport.db.realm.Stadium;
import ru.shtrm.gosport.db.realm.Training;
import ru.shtrm.gosport.ui.MainActivity;
import ru.shtrm.gosport.utils.MainFunctions;

import static ru.shtrm.gosport.utils.RoundedImageView.getResizedBitmap;

public class StadiumInfoFragment extends Fragment {
    private Realm realmDB;
    private Context mainActivityConnector = null;

    public static StadiumInfoFragment newInstance() {
        return (new StadiumInfoFragment());
    }
    StadiumInfoFragmentBinding binding;
    FloatingActionButton fab;

    private ImageView tv_stadium_image;
    private ListView tv_stadium_trainings;

    private String stadium_uuid;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,
                R.layout.stadium_info_fragment,
                container,false);
        View view = binding.getRoot();
        Toolbar toolbar = ((AppCompatActivity)mainActivityConnector).findViewById(R.id.toolbar);
        toolbar.setSubtitle("Информация по площадке");

        realmDB = Realm.getDefaultInstance();
        Bundle b = getArguments();
        assert b != null;
        stadium_uuid = b.getString("uuid");

        tv_stadium_image = view.findViewById(R.id.stadium_image);
        tv_stadium_trainings = view.findViewById(R.id.trainings_listView);
        fab = view.findViewById(R.id.stadium_edit_button);

        initView();
        return view;
    }

    private void initView() {
        final Stadium stadium = realmDB.where(Stadium.class).
                equalTo("uuid", stadium_uuid).findFirst();
        if (stadium == null) {
            Toast.makeText(getContext(), "Неизвестный стадион: " + stadium_uuid,
                    Toast.LENGTH_LONG).show();
            return;
        }
        binding.setStadium(stadium);

        // TODO разобраться с binding на imageview
        String path = MainFunctions.getPicturesDirectory(mainActivityConnector);
        Bitmap stadium_bitmap = getResizedBitmap(path, stadium.getImage(),
                0, 600, stadium.getChangedAt().getTime());
        if (stadium_bitmap != null) {
            tv_stadium_image.setImageBitmap(stadium_bitmap);
        }

        // TODO разобраться с binding на listview
        RealmResults<Training> trainings = realmDB.where(Training.class).
                equalTo("stadium.uuid", stadium.getUuid()).
                findAllSorted("date", Sort.DESCENDING);
        if (trainings.size() > 2) {
            trainings.subList(0, 5);
        }
        final TrainingAdapter trainingAdapter =
                new TrainingAdapter(mainActivityConnector, trainings, stadium.getSport());
        tv_stadium_trainings.setAdapter(trainingAdapter);

        MainFunctions.setListViewHeightBasedOnChildren(tv_stadium_trainings);

        // TODO разобраться с binding на FAB
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("uuid", stadium.getUuid());
                Fragment f = FragmentAddStadium.newInstance();
                f.setArguments(bundle);
                ((MainActivity) mainActivityConnector).getSupportFragmentManager().beginTransaction().
                        replace(R.id.frame_container, f).commit();
            }
        });

        tv_stadium_trainings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id)
            {
                Training training = trainingAdapter.getItem(pos);
                if (training != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("uuid", training.getUuid());
                    TrainingInfoFragment trainingInfoFragment = TrainingInfoFragment.newInstance();
                    trainingInfoFragment.setArguments(bundle);
                    ((MainActivity) mainActivityConnector).getSupportFragmentManager().beginTransaction().
                            replace(R.id.frame_container, trainingInfoFragment).commit();
                }
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivityConnector = getActivity();
        // TODO решить что делать если контекст не приехал
        if (mainActivityConnector == null)
            onDestroyView();
    }
}

