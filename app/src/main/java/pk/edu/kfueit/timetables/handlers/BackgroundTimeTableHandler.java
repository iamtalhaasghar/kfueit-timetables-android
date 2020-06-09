package pk.edu.kfueit.timetables.handlers;

import org.json.JSONObject;

public interface BackgroundTimeTableHandler {
    void handleTable(JSONObject timeTable);
}
