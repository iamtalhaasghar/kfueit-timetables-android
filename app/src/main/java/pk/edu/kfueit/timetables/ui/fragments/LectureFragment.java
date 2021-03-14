package pk.edu.kfueit.timetables.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.parser.Line;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Locale;

import pk.edu.kfueit.timetables.LoginActivity;
import pk.edu.kfueit.timetables.OnSwipeTouchListener;
import pk.edu.kfueit.timetables.R;
import pk.edu.kfueit.timetables.Data;
import pk.edu.kfueit.timetables.handlers.BackgroundTimeTableHandler;
import pk.edu.kfueit.timetables.parser.TimeTable;
import pk.edu.kfueit.timetables.threads.BackgroundTimeTableFetcher;
import pk.edu.kfueit.timetables.ui.model.TimeTableAdapter;

import static pk.edu.kfueit.timetables.parser.TimeTable.DATA_ITEM_1;
import static pk.edu.kfueit.timetables.parser.TimeTable.DATA_ITEM_2;
import static pk.edu.kfueit.timetables.parser.TimeTable.DATA_ITEM_3;
import static pk.edu.kfueit.timetables.parser.TimeTable.LECTURES_HEADING;
import static pk.edu.kfueit.timetables.parser.TimeTable.LEC_NUM_HEADING;
import static pk.edu.kfueit.timetables.parser.TimeTable.TOTAL_LEC_HEADING;
import static pk.edu.kfueit.timetables.parser.TimeTable.TYPE_HEADING;
import static pk.edu.kfueit.timetables.parser.TimeTable.TYPE_INCOMPLETE;

public class LectureFragment extends Fragment{

    TimeTableAdapter timeTableAdapter;
    Data appData;

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

        appData = new Data(getContext());

        if(LoginActivity.isConnectedToInternet(getContext())){
            String timeTableType = appData.getTimeTableType();
            String timeTableName = appData.getTimeTableName();
            String timeTableVersion = appData.getTimeTableVersion();
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
            tableFetcher.execute(TimeTable.OP_VIEW, timeTableType, timeTableName, timeTableVersion);
        }

        if(appData.isTimeTablePresent()){
            try {
                timeTableAdapter.setTimeTable(new JSONObject(appData.getTimeTable()));
                System.out.println(new JSONObject(appData.getTimeTable()).toString(3));
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

        FloatingActionButton fab = root.findViewById(R.id.download_fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createPdf();


                /*
                String message = "Are you a Student or Teacher of KFUEIT?\nDo you want an app to view your Timetable" +
                        " offline (no more logins)?\nDo you want to SUBMIT COMPLAINTS to VC OFFICE directly?\nDon`t wait:\n\n" +
                        "https://play.google.com/store/apps/details?id="+getActivity().getPackageName();

                Intent sendIntent = new Intent(android.content.Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, message);
                sendIntent.setType("text/plain");
                sendIntent.setPackage("com.whatsapp");
                if (sendIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(sendIntent);
                }
                else{
                    sendIntent.setPackage("com.whatsapp.w4b");
                    if (sendIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(sendIntent);
                    }
                }
                */
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

    private void createPdf(){

        Document timeTableDoc = new Document();
        try {
            JSONObject timeTableData = new JSONObject(appData.getTimeTable());
            String timetableName = timeTableData.getString(TimeTable.NAME_HEADING);
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/time_tables";

            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();

            File file = new File(dir, timetableName+".pdf");
            FileOutputStream fOut = null;
            try {
                fOut = new FileOutputStream(file);
                PdfWriter.getInstance(timeTableDoc, fOut);
            } catch (FileNotFoundException | DocumentException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            //open the document
            timeTableDoc.open();



            String line = timeTableData.getString(TimeTable.VERSION_HEADING) + "\n" +
                    timetableName + "\n\n******************************\n\n\n\n";


            Paragraph paragraph = new Paragraph(line);
            timeTableDoc.add(paragraph);


            for (int day = 1 ; day <= 7; day++) {
                // set the data in items

                    String dayText = TimeTable.getDay(day);
                    JSONObject dayData = timeTableData.getJSONObject(dayText);
                    JSONArray lectures = dayData.getJSONArray(LECTURES_HEADING);
                    int totalLectures = dayData.getInt(TOTAL_LEC_HEADING);


                    for (int position = 0; position <= totalLectures; position++) {
                        if(position == 0){
                            line = String.format(Locale.US,
                                    "%s - %s Lecture%s",
                                    TimeTable.getDay(day),
                                    totalLectures == 0 ? "No" : String.valueOf(totalLectures),
                                    totalLectures == 1 ? "" : "s")+"\n\n\n";
                            paragraph = new Paragraph(line);
                            timeTableDoc.add(paragraph);

                        }else {

                            int lecturePosition = position - 1; // because one space was occupied by static card

                            JSONObject lecture = lectures.getJSONObject(lecturePosition);
                            int lectureNumber = lecture.getInt(LEC_NUM_HEADING);
                            String itemText = lecture.getString(DATA_ITEM_1);


                            // nor it is a break
                            if (lectureNumber != 0) {
                                itemText = String.format("%d. %s", lectureNumber, itemText);
                            }

                            line = itemText + "\n" +
                            lecture.getString(DATA_ITEM_2) + "\n" +
                            lecture.getString(DATA_ITEM_3) + "\n" +
                            lecture.getString(TimeTable.START_HEADING) + " to " +
                                    lecture.getString(TimeTable.END_HEADING)+"\n\n";
                            paragraph = new Paragraph(line);
                            paragraph.setAlignment(Element.ALIGN_CENTER);
                            timeTableDoc.add(paragraph);
                        }
                    }

                paragraph = new Paragraph("\n\n******************************\n\n");
                timeTableDoc.add(paragraph);


            }
            Toast.makeText(getContext(), "Time Table was saved successfully in : " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (JSONException | DocumentException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }finally {
            timeTableDoc.close();
        }



    }


}
