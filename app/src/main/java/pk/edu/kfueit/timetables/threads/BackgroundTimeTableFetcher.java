package pk.edu.kfueit.timetables.threads;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import pk.edu.kfueit.timetables.Data;
import pk.edu.kfueit.timetables.LoginActivity;
import pk.edu.kfueit.timetables.R;
import pk.edu.kfueit.timetables.WelcomeActivity;
import pk.edu.kfueit.timetables.exception.EmptyListException;
import pk.edu.kfueit.timetables.exception.LoginException;
import pk.edu.kfueit.timetables.exception.NoTimeTableException;
import pk.edu.kfueit.timetables.handlers.BackgroundTimeTableHandler;
import pk.edu.kfueit.timetables.parser.TimeTable;

public class BackgroundTimeTableFetcher extends AsyncTask<String, Void, JSONObject> {

    ProgressDialog progressDialog;
    BackgroundTimeTableHandler timeTableHandler;
    Context context;
    boolean gotException;
    String exMessage;
    boolean forceLogin;
    public BackgroundTimeTableFetcher(Context context,boolean forceLogin,
                                      BackgroundTimeTableHandler timeTableHandler){
        this.timeTableHandler = timeTableHandler;
        progressDialog = new ProgressDialog(context);
        this.context = context;
        gotException = false;
        this.forceLogin = forceLogin;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Time Table...");
        progressDialog.show();
    }

    @Override
    protected JSONObject doInBackground(String... args) {
        try {
            if (args.length >= 3) {
                String timeTableOperation = args[0];
                String timeTableType = args[1];
                String timeTableOf = args[2];
                TimeTable timeTable = new TimeTable(context);
                if (timeTableOperation.equals(TimeTable.OP_SEARCH)) {
                    if (timeTableOf.contains("Search")) {
                        return timeTable.searchFreeResourcesOfType(timeTableType);

                    } else {
                        return timeTable.getCompleteResourceData(timeTableType, timeTableOf);
                    }
                } else {
                    return timeTable.fetch(timeTableType, timeTableOf);
                }
            }
        }
        catch (EmptyListException | LoginException | NoTimeTableException e) {
            gotException = true;
            exMessage = e.getMessage();
        }

        return null;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
        timeTableHandler.handleTable(jsonObject);
        if(gotException){
            Toast.makeText(context, exMessage,Toast.LENGTH_SHORT).show();
            if(forceLogin && LoginActivity.isConnectedToInternet(context)){
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
            }
        }
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
}
