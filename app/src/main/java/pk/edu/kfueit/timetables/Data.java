package pk.edu.kfueit.timetables;

import android.content.Context;
import android.content.SharedPreferences;


public class Data {
    private final String PREFERENCE_NAME = "time_table_data";
    private final String TIME_TABLE = "my_time_table";
    private final String TIME_TABLE_TYPE = "timetable_type";
    private final String TIME_TABLE_NAME = "timetable_name";
    private final String TIME_TABLE_VERSION = "timetable_version";
    private final String COOKIE_NAME = "ci_session";
    private final String ROLL_NUMBER = "roll_number";
    private final String PASSWORD = "password";

    private Context context;

    public Data(Context context){
        this.context = context;
    }

    private SharedPreferences getSharedPreference(){
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor getEditor(){
        return getSharedPreference().edit();
    }

    public void saveTimeTable(String timeTable){
        getEditor().putString(TIME_TABLE, timeTable).commit();
    }

    public String getTimeTable(){
        return getSharedPreference().getString(TIME_TABLE,null);
    }

    public boolean isTimeTablePresent(){
        return getTimeTable() != null;
    }

    public void saveCookieValue(String cookieValue){
        getEditor().putString(COOKIE_NAME, cookieValue).commit();
    }

    public String getCookieValue(){
        return getSharedPreference().getString(COOKIE_NAME,null);
    }

    public boolean isCookiePresent(){
        return getCookieValue() != null;
    }

    public String getCookieName(){return COOKIE_NAME;}

    public void saveTimeTableType(String value){
        getEditor().putString(TIME_TABLE_TYPE, value).commit();
    }

    public String getTimeTableType(){
        return getSharedPreference().getString(TIME_TABLE_TYPE,null);
    }

    public void saveTimeTableName(String value){
        getEditor().putString(TIME_TABLE_NAME, value).commit();
    }

    public String getTimeTableName(){
        return getSharedPreference().getString(TIME_TABLE_NAME,null);
    }

    public void saveTimeTableVersion(String value){
        getEditor().putString(TIME_TABLE_VERSION, value).commit();
    }

    public String getTimeTableVersion(){
        return getSharedPreference().getString(TIME_TABLE_VERSION,null);
    }

    public void saveLoginData(String rollNumber, String pass){
        getEditor().putString(ROLL_NUMBER, rollNumber).putString(PASSWORD, pass).commit();
    }

    public String getPassword() {
        return getSharedPreference().getString(PASSWORD, "");
    }

    public String getRollNumber() {
        return getSharedPreference().getString(ROLL_NUMBER, "");
    }
}
