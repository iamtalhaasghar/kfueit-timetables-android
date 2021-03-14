package pk.edu.kfueit.timetables;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.List;

import pk.edu.kfueit.timetables.handlers.BackgroundListHandler;
import pk.edu.kfueit.timetables.handlers.BackgroundTimeTableHandler;
import pk.edu.kfueit.timetables.parser.TimeTable;
import pk.edu.kfueit.timetables.threads.BackgroundListFetcher;
import pk.edu.kfueit.timetables.threads.BackgroundTimeTableFetcher;
import pk.edu.kfueit.timetables.threads.BackgroundTimeTableVersionFetcher;

public class TimeTableVersionActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_version);


        Button btnShowList = findViewById(R.id.btnShowList);

        final Context context = TimeTableVersionActivity.this;


        btnShowList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BackgroundTimeTableVersionFetcher listFetcher = new BackgroundTimeTableVersionFetcher(context, true, new BackgroundListHandler() {
                    @Override
                    public void handleList(List<String> tempList) {
                        if(tempList != null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            final String listItems[] = tempList.toArray(new String[tempList.size()]);
                            builder.setTitle("Choose a Time Table Version");
                            builder.setItems(listItems, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final String timeTableVersion = listItems[which];
                                    final Data appData = new Data(context);
                                    appData.saveTimeTableVersion(timeTableVersion);

                                    Intent intent = new Intent(context, TimeTableInfoSelectActivity.class);
                                    startActivity(intent);
                                    finish();

                                }
                            });
                            builder.create().show();
                        }
                    }
                });
                listFetcher.execute();

            }
        });
    }

}
