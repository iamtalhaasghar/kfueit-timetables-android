package pk.edu.kfueit.timetables.threads;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;

import pk.edu.kfueit.timetables.Data;
import pk.edu.kfueit.timetables.MainActivity;
import pk.edu.kfueit.timetables.R;
import pk.edu.kfueit.timetables.parser.TimeTable;

public class TimeTableVersionFetcher extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... voids) {
        return null;
    }

    /*
    private ArrayList<String> tempList;
    ProgressDialog progressDialog;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setMessage("Fetching All Time Table Versions...");
        progressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... objects) {
        String cookieValue = getSharedPreferences(Data.TIME_TABLE_PREFERENCE, Context.MODE_PRIVATE)
                .getString(Data.COOKIE_NAME, "");
        TimeTable timeTable = new TimeTable(cookieValue);
        tempList = timeTable.getTimeTableVersions();
        return null;
    }

    @Override
    protected void onPostExecute(Void object) {
        super.onPostExecute(object);

        if(tempList != null) {

            final String timeTableNames[] = tempList.toArray(new String[tempList.size()]);

            // setup the alert builder
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Choose a Time Table Version");
            builder.setItems(timeTableNames, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // user checked an item
                    final TextView txtVersion = findViewById(R.id.time_table_version);
                    txtVersion.setText(timeTableNames[which]);

                }
            });
            //builder.create().show();
        }
        else{
            Toast.makeText(getApplicationContext(), R.string.toast_text_no_internet_connection,Toast.LENGTH_SHORT).show();
        }
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

     */
}
