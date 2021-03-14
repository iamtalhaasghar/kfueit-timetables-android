package pk.edu.kfueit.timetables;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.List;

import pk.edu.kfueit.timetables.handlers.BackgroundListHandler;
import pk.edu.kfueit.timetables.handlers.BackgroundTimeTableHandler;
import pk.edu.kfueit.timetables.parser.TimeTable;
import pk.edu.kfueit.timetables.threads.BackgroundListFetcher;
import pk.edu.kfueit.timetables.threads.BackgroundTimeTableFetcher;

public class TimeTableInfoSelectActivity extends AppCompatActivity {



    String timeTableType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_info_select);

        final RadioButton rbTeacher = findViewById(R.id.rbTeacher);
        Button btnContinue = findViewById(R.id.btnContinue);

        final Context context = TimeTableInfoSelectActivity.this;


        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeTableType = TimeTable.CLASSES_VAR;
                if(rbTeacher.isChecked()){
                    timeTableType = TimeTable.TEACHERS_VAR;
                }
                final Data appData = new Data(context);
                final String timeTableVersion = appData.getTimeTableVersion();
                BackgroundListFetcher listFetcher = new BackgroundListFetcher(context, true, new BackgroundListHandler() {
                    @Override
                    public void handleList(List<String> tempList) {
                        if(tempList != null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            final String listItems[] = tempList.toArray(new String[tempList.size()]);
                            builder.setTitle("Choose an item from following");
                            builder.setItems(listItems, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final String timeTableName = listItems[which];

                                    appData.saveTimeTableType(timeTableType);
                                    appData.saveTimeTableName(timeTableName);


                                    BackgroundTimeTableFetcher tableFetcher = new BackgroundTimeTableFetcher(context, true,new BackgroundTimeTableHandler() {
                                        @Override
                                        public void handleTable(JSONObject timeTable) {

                                            appData.saveTimeTable(timeTable.toString());
                                            Toast.makeText(context, "Your TimeTable has been saved. You can view this time table offline now.", Toast.LENGTH_LONG).show();

                                            Intent intent = new Intent(context, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                    tableFetcher.execute(TimeTable.OP_VIEW, timeTableType, timeTableName, timeTableVersion);

                                }
                            });
                            builder.create().show();
                        }
                    }
                });

                listFetcher.execute(timeTableType, timeTableVersion);

            }
        });
    }

}
