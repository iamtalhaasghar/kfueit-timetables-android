package pk.edu.kfueit.timetables.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import pk.edu.kfueit.timetables.LoginActivity;
import pk.edu.kfueit.timetables.OnSwipeTouchListener;
import pk.edu.kfueit.timetables.R;
import pk.edu.kfueit.timetables.Data;
import pk.edu.kfueit.timetables.handlers.BackgroundTimeTableHandler;
import pk.edu.kfueit.timetables.parser.TimeTable;
import pk.edu.kfueit.timetables.threads.BackgroundTimeTableFetcher;
import pk.edu.kfueit.timetables.ui.model.TimeTableAdapter;

import static android.content.Context.MODE_PRIVATE;

public class LectureFragment extends Fragment{

    TimeTableAdapter timeTableAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_lecture, container, false);



        // get the reference of RecyclerView
        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        // set a LinearLayoutManager with default vertical orientaion
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView
        timeTableAdapter = new TimeTableAdapter(getContext());
        recyclerView.setAdapter(timeTableAdapter);

        LinearLayout timeTableLayout = root.findViewById(R.id.timetable_layout);

        OnSwipeTouchListener swipeTouchListener = new OnSwipeTouchListener(getContext()) {

            @Override
            public void onSwipeRight() {
                yesterDayTimeTable();
            }

            @Override
            public void onSwipeLeft() {
                tomorrowTimeTable();
            }
        };

        recyclerView.setOnTouchListener(swipeTouchListener);
        timeTableLayout.setOnTouchListener(swipeTouchListener);

        final Data appData = new Data(getContext());

        if(LoginActivity.isConnectedToInternet(getContext())){
            String timeTableType = appData.getTimeTableType();
            String timeTableName = appData.getTimeTableName();
            BackgroundTimeTableFetcher tableFetcher = new BackgroundTimeTableFetcher(getContext(), false,
                    new BackgroundTimeTableHandler() {
                        @Override
                        public void handleTable(JSONObject timeTable) {
                            if(timeTable != null) {
                                appData.saveTimeTable(timeTable.toString());
                                timeTableAdapter.setTimeTable(timeTable);
                            }
                            else{
                                Toast.makeText(getContext(), "AUTOMATIC TIMETABLE UPDATE FAILURE. Falling back to offline version.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            tableFetcher.execute(TimeTable.OP_VIEW, timeTableType, timeTableName);
        }

        if(appData.isTimeTablePresent()){
            try {
                timeTableAdapter.setTimeTable(new JSONObject(appData.getTimeTable()));
            } catch (JSONException e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(getContext(), "Your Offline Time Table does not exists or is corrupted. Restart Application.", Toast.LENGTH_LONG).show();
        }


        root.findViewById(R.id.btnMonLec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeTableAdapter.setDay(1);
            }
        });

        root.findViewById(R.id.btnTueLec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeTableAdapter.setDay(2);
            }
        });
        root.findViewById(R.id.btnWedLec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeTableAdapter.setDay(3);
            }
        });
        root.findViewById(R.id.btnThuLec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeTableAdapter.setDay(4);
            }
        });
        root.findViewById(R.id.btnFriLec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeTableAdapter.setDay(5);
            }
        });
        root.findViewById(R.id.btnSatLec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeTableAdapter.setDay(6);
            }
        });
        root.findViewById(R.id.btnSunLec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeTableAdapter.setDay(7);
            }
        });

        return root;
    }




    public void yesterDayTimeTable(){
        if(timeTableAdapter != null){
            timeTableAdapter.decrementDay();
        }
    }

    public void tomorrowTimeTable(){
        if(timeTableAdapter != null){
           timeTableAdapter.incrementDay();

        }
    }



}
