package pk.edu.kfueit.timetables.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import org.json.JSONObject;

import java.util.List;

import pk.edu.kfueit.timetables.OnSwipeTouchListener;
import pk.edu.kfueit.timetables.R;
import pk.edu.kfueit.timetables.handlers.BackgroundListHandler;
import pk.edu.kfueit.timetables.handlers.BackgroundTimeTableHandler;
import pk.edu.kfueit.timetables.parser.TimeTable;
import pk.edu.kfueit.timetables.threads.BackgroundListFetcher;
import pk.edu.kfueit.timetables.threads.BackgroundTimeTableFetcher;
import pk.edu.kfueit.timetables.ui.model.TimeTableAdapter;

public class TimeTableFragment extends Fragment implements BackgroundListHandler{

    TimeTableAdapter timeTableAdapter;

    public final static String TABLE_TYPE_ARG = "timetable_type";
    public final static String TABLE_OP_ARG = "timetable_operation";

    String timeTableType;
    String timeTableOperation;
    View root;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_timetable, container, false);



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


        Bundle args = getArguments();
        timeTableType = args.getString(TABLE_TYPE_ARG);
        timeTableOperation = args.getString(TABLE_OP_ARG);


        if(timeTableOperation.equals(TimeTable.OP_SEARCH)){
            root.findViewById(R.id.llWeeklyButtons).setVisibility(View.GONE);
        }

        root.findViewById(R.id.btnMonTable).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeTableAdapter.setDay(1);
            }
        });

        root.findViewById(R.id.btnTueTable).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeTableAdapter.setDay(2);
            }
        });
        root.findViewById(R.id.btnWedTable).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeTableAdapter.setDay(3);
            }
        });
        root.findViewById(R.id.btnThuTable).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeTableAdapter.setDay(4);
            }
        });
        root.findViewById(R.id.btnFriTable).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeTableAdapter.setDay(5);
            }
        });
        root.findViewById(R.id.btnSatTable).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeTableAdapter.setDay(6);
            }
        });
        root.findViewById(R.id.btnSunTable).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeTableAdapter.setDay(7);
            }
        });

        BackgroundListFetcher listFetcher = new BackgroundListFetcher(getContext(),true, this);
        listFetcher.execute(timeTableType);
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

    @Override
    public void handleList(List<String> tempList) {
        if(tempList != null) {
            if(timeTableOperation.equals(TimeTable.OP_SEARCH)){
                tempList.add(0, String.format("Search for All \"%s\" in whole university which are free now [Will take few minutes]", timeTableType.contains("sets") ? "classes" : timeTableType+"s"));
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            final String listItems[] = tempList.toArray(new String[tempList.size()]);
            builder.setTitle("Choose an item from following");
            builder.setItems(listItems, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String selectedItem = listItems[which];
                    BackgroundTimeTableFetcher tableFetcher = new BackgroundTimeTableFetcher(getContext(),true, new BackgroundTimeTableHandler() {
                        @Override
                        public void handleTable(JSONObject timeTable) {
                            timeTableAdapter.setTimeTable(timeTable);
                        }
                    });
                    tableFetcher.execute(timeTableOperation, timeTableType, selectedItem);
                }
            });
            builder.create().show();
        }
    }

}
