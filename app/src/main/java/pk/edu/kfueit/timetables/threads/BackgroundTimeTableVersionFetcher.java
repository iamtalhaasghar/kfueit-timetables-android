package pk.edu.kfueit.timetables.threads;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.ArrayList;

import pk.edu.kfueit.timetables.LoginActivity;
import pk.edu.kfueit.timetables.exception.EmptyListException;
import pk.edu.kfueit.timetables.exception.LoginException;
import pk.edu.kfueit.timetables.handlers.BackgroundListHandler;
import pk.edu.kfueit.timetables.parser.TimeTable;

public class BackgroundTimeTableVersionFetcher extends AsyncTask<Void, Void, ArrayList<String>> {

    ProgressDialog progressDialog;
    BackgroundListHandler listHandler;
    Context context;
    boolean gotException;
    String exMessage;
    boolean forceLogin;

    public BackgroundTimeTableVersionFetcher(Context context, boolean forceLogin, BackgroundListHandler listHandler){
        progressDialog = new ProgressDialog(context);
        this.listHandler = listHandler;
        this.context = context;
        gotException = false;
        this.forceLogin = forceLogin;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Time Table Versions List...");
        progressDialog.show();
    }

    @Override
    protected ArrayList<String> doInBackground(Void...args) {

        TimeTable timeTable = new TimeTable(context, null);
        try {
            return timeTable.getTimeTableVersions();
        } catch (EmptyListException | LoginException e) {
            gotException = true;
            exMessage = e.getMessage();
        }

        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<String> tempList) {
        super.onPostExecute(tempList);
        listHandler.handleList(tempList);
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
