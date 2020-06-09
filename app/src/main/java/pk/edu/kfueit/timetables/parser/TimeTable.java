/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pk.edu.kfueit.timetables.parser;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

import pk.edu.kfueit.timetables.Data;
import pk.edu.kfueit.timetables.exception.EmptyListException;
import pk.edu.kfueit.timetables.exception.LoginException;
import pk.edu.kfueit.timetables.exception.NoTimeTableException;

/**
 * Format of the json { "url":"https://www.kfueit.edu.pk", "name":"BSCS6th B",
 * "version":"KFUEIT Time Table w.e.f from dd mm, yyyy", "day_of_week":{
 * "lectures":[ { "lec_num":1, "item1":"data1", "item2":"data2",
 * "item3":"data3", "start":"1200", "end":"2300" } ], "total_lec" : 10 } }
 *
 *
 */
/**
 *
 * @author programmer
 */
public class TimeTable {

    private static String daysOfWeek[] = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    private static int toSkip[] = new int[daysOfWeek.length];

    private final static String TIME_TABLE_URL = "https://my.kfueit.edu.pk/users/testtable";
    private final static String LINK_FORMAT = "%s?%s=%s&%s=%s&%s=%s";
    private final static String FILTER = "filter";

    private final static String TIME_TABLE_VERSIONS_VAR = "timetablename";
    public final static String DEPARTMENTS_VAR = "department";
    public final static String CLASSES_VAR = "sets";
    public final static String TEACHERS_VAR = "teacher";
    public final static String ROOMS_VAR = "room";
    private final static String SUBJECTS_VAR = "subject";

    public final static String VERSION_HEADING = "version";
    public final static String NAME_HEADING = "name";
    public final static String URL_HEADING = "url";
    public final static String TYPE_HEADING = "type";
    public final static String LECTURES_HEADING = "lectures";
    public final static String TOTAL_LEC_HEADING = "total_lec";
    public final static String DATA_ITEM_1 = "item1";
    public final static String DATA_ITEM_2 = "item2";
    public final static String DATA_ITEM_3 = "item3";
    public final static String START_HEADING = "start";
    public final static String END_HEADING = "end";
    public final static String LEC_NUM_HEADING = "lec_num";


    public final static String OP_VIEW = "view";
    public final static String OP_SEARCH = "search";
    public final static String TYPE_COMPLETE = "complete";
    public final static String TYPE_INCOMPLETE = "incomplete";

    private Context context;

    public TimeTable(Context context) {
        this.context = context;
    }

    public static int totalDays(){
        return daysOfWeek.length;
    }

    public ArrayList<String> getTimeTableVersions() throws LoginException, EmptyListException {
        return getSelectOptions(TIME_TABLE_VERSIONS_VAR);
    }

    public String getLatestTimeTableVersion() throws LoginException, EmptyListException {
        ArrayList<String> allTimeTableNames = getTimeTableVersions();
        return allTimeTableNames.get(allTimeTableNames.size() - 1);
    }

    public ArrayList<String> getDepartments() throws LoginException, EmptyListException {
        return getSelectOptions(DEPARTMENTS_VAR);
    }

    public ArrayList<String> getClasses() throws LoginException, EmptyListException {
        return getSelectOptions(CLASSES_VAR);
    }

    public ArrayList<String> getTeachers() throws LoginException, EmptyListException {
        return getSelectOptions(TEACHERS_VAR);
    }

    public ArrayList<String> getRooms() throws LoginException, EmptyListException {
        return getSelectOptions(ROOMS_VAR);
    }

    public ArrayList<String> getSubjects() throws LoginException, EmptyListException {
        return getSelectOptions(SUBJECTS_VAR);
    }

    private ArrayList<String> getSelectOptions(String name) throws LoginException, EmptyListException {

        ArrayList<String> options = new ArrayList<>();
        Document webPage = getHtml(TIME_TABLE_URL);
        Element selectTag = webPage.getElementsByAttributeValue("name", name).first();
        for (Element option : selectTag.getElementsByTag("option")) {
            String optionText = option.text().trim();
            if (!optionText.isEmpty() && !optionText.equalsIgnoreCase("none")) {
                options.add(optionText);
            }
        }
        if(options.isEmpty()){
            throw new EmptyListException(name);
        }
        return options;
    }

    private Document getHtml(String url) throws LoginException {
        Data appData = new Data(context);

        if(!appData.isCookiePresent()){
            throw new LoginException("Cookie not found. Unable to Connect to 'my.kfueit' server.");
        }

        Connection websiteConnection = Jsoup.connect(url)
            .sslSocketFactory(CustomSSLSocketFactory.socketFactory())
            .cookie(appData.getCookieName(), appData.getCookieValue());

        Document webPage;
        try {
            webPage = websiteConnection.get();
        } catch (IOException e) {
            throw new LoginException("Error while connecting to 'my.kfueit' server. " + e.getMessage());
        }
        if(webPage == null || webPage.getElementById("basic-table") == null){
            throw new LoginException("Unable to Connect to 'my.kfueit' server. Reason: Empty Response or Session Expired.");
        }
        return webPage;
    }

    public ArrayList<String> getListOf(String query) throws LoginException, EmptyListException {

        ArrayList<String> itemList;

        if (query.equalsIgnoreCase(ROOMS_VAR)) {
            itemList = getRooms();
        } else if (query.equalsIgnoreCase(TEACHERS_VAR)) {
            itemList = getTeachers();
        } else if (query.equalsIgnoreCase(DEPARTMENTS_VAR)) {
            itemList = getDepartments();
        } else if (query.equalsIgnoreCase(SUBJECTS_VAR)) {
            itemList = getSubjects();
        }else if(query.equalsIgnoreCase(CLASSES_VAR)){
            itemList = getClasses();
        }
        else{
            throw new EmptyListException(query);
        }

        return itemList;
    }

    private String urlOfClass(String className) throws LoginException, EmptyListException {
        String url = String.format(LINK_FORMAT, TIME_TABLE_URL,
                FILTER, "class",
                TIME_TABLE_VERSIONS_VAR, getLatestTimeTableVersion(), CLASSES_VAR, className);
        return url;
    }

    private String urlOfRoom(String roomName) throws LoginException, EmptyListException {
        String url = String.format(LINK_FORMAT, TIME_TABLE_URL,
                FILTER, ROOMS_VAR,
                TIME_TABLE_VERSIONS_VAR, getLatestTimeTableVersion(), ROOMS_VAR, roomName);
        return url;
    }

    private String urlOfTeacher(String teacherName) throws LoginException, EmptyListException {
        String url = String.format(LINK_FORMAT, TIME_TABLE_URL,
                FILTER, TEACHERS_VAR,
                TIME_TABLE_VERSIONS_VAR, getLatestTimeTableVersion(), TEACHERS_VAR, teacherName);
        return url;
    }

    private String urlOfSubject(String subjectName) throws LoginException, EmptyListException {
        String url = String.format(LINK_FORMAT, TIME_TABLE_URL,
                FILTER, SUBJECTS_VAR,
                TIME_TABLE_VERSIONS_VAR, getLatestTimeTableVersion(), SUBJECTS_VAR, subjectName);
        return url;
    }

    private String urlOf(String type, String name) throws LoginException, EmptyListException {
        String url = urlOfClass(name);
        if (type.equalsIgnoreCase(ROOMS_VAR)) {
            url = urlOfRoom(name);
        } else if (type.equalsIgnoreCase(TEACHERS_VAR)) {
            url = urlOfTeacher(name);
        } else if (type.equalsIgnoreCase(SUBJECTS_VAR)) {
            url = urlOfSubject(name);
        }
        return url;
    }


    public JSONObject fetch(String tableType, String query)
            throws LoginException, EmptyListException, NoTimeTableException {

        String url = urlOf(tableType, query);
        try {
            Document webPage = getHtml(url);

            Element table = webPage.getElementById("basic-table");
            Element tbody = table.getElementsByTag("tbody").last();

            JSONObject timeTable = new JSONObject();
            timeTable.put(URL_HEADING, url);

            timeTable.put(VERSION_HEADING, getLatestTimeTableVersion());
            timeTable.put(NAME_HEADING, query);
            timeTable.put(TYPE_HEADING, TYPE_COMPLETE);
            for (Element tr : tbody.getElementsByTag("tr")) {
                Elements elements = tr.getElementsByTag("td");
                for (int i = 0, counter = 0; i < elements.size(); ) {
                    Element td = elements.get(i);
                    String cellType = td.attr("class").trim();
                    if (toSkip[counter] == 0) {
                        if (cellType.equalsIgnoreCase(CellTypes.TIME_SIDE)) {
                            i++;
                        } else if (cellType.equalsIgnoreCase(CellTypes.FIXED_HEIGHT)) {
                            counter++;
                            i++;
                        } else if (cellType.equalsIgnoreCase(CellTypes.LIGHT_GREEN)) {
                            String lectureData = td.html().trim();
                            lectureData = lectureData.replaceAll("&amp;", "&");
                            lectureData = lectureData.replaceAll("\\u2013", "-");
                            Scanner sc = new Scanner(lectureData);
                            sc = sc.useDelimiter("<br>");
                            String dataItem1 = sc.next();
                            String dataItem2 = sc.next();
                            String dataItem3 = sc.next();
                            String time[] = sc.next().split("-");
                            String startTime = time[0].trim();
                            String endTime = time[1].trim();

                            JSONObject lecture = new JSONObject();
                            lecture.put(DATA_ITEM_1, dataItem1);
                            lecture.put(DATA_ITEM_2, dataItem2);
                            lecture.put(DATA_ITEM_3, dataItem3);
                            lecture.put(START_HEADING, startTime);
                            lecture.put(END_HEADING, endTime);

                            String key = daysOfWeek[counter];
                            if (!timeTable.has(key)) {
                                JSONArray lecturesArray = new JSONArray();
                                lecture.put(LEC_NUM_HEADING, 1);
                                lecturesArray.put(lecture);
                                JSONObject dayData = new JSONObject();
                                dayData.put(LECTURES_HEADING, lecturesArray);
                                dayData.put(TOTAL_LEC_HEADING, 1);
                                timeTable.put(key, dayData);
                            } else {
                                JSONObject dayData = timeTable.getJSONObject(key);
                                int lectNum = dayData.getInt(TOTAL_LEC_HEADING) + 1;
                                lecture.put(LEC_NUM_HEADING, lectNum);
                                dayData.put(TOTAL_LEC_HEADING, lectNum);
                                dayData.accumulate(LECTURES_HEADING, lecture);
                                timeTable.put(key, dayData);
                            }

                            toSkip[counter] = getSpan(td) - 1;
                            counter++;
                            i++;

                        } else if (cellType.equalsIgnoreCase(CellTypes.BREAK_TIME)) {
                            String cellData = td.html().trim();
                            Scanner sc = new Scanner(cellData);
                            sc = sc.useDelimiter("<br>");
                            String dataItem = sc.next();
                            String time[] = sc.next().split("-");

                            JSONObject lecture = new JSONObject();
                            lecture.put(DATA_ITEM_1, dataItem);
                            lecture.put(DATA_ITEM_2, "Break");
                            lecture.put(DATA_ITEM_3, "Salah (Prayer) is better than sleep.");
                            lecture.put(START_HEADING, time[0].trim());
                            lecture.put(END_HEADING, time[1].trim());

                            String key = daysOfWeek[counter];
                            if (!timeTable.has(key)) {
                                JSONArray lecturesArray = new JSONArray();
                                lecture.put(LEC_NUM_HEADING, 0);
                                lecturesArray.put(lecture);
                                JSONObject dayData = new JSONObject();
                                dayData.put(LECTURES_HEADING, lecturesArray);
                                dayData.put(TOTAL_LEC_HEADING, 0);
                                timeTable.put(key, dayData);
                            } else {
                                JSONObject dayData = timeTable.getJSONObject(key);
                                lecture.put(LEC_NUM_HEADING, 0);
                                dayData.accumulate(LECTURES_HEADING, lecture);
                                timeTable.put(key, dayData);
                            }

                            toSkip[counter] = getSpan(td) - 1;
                            counter++;
                            i++;
                        }

                    } else {
                        toSkip[counter]--;
                        counter++;
                    }
                }

            }
            for (String day : daysOfWeek) {
                if (!timeTable.has(day)) {
                    JSONObject dayData = new JSONObject();
                    JSONArray lecturesArray = new JSONArray();
                    JSONObject lecture = new JSONObject();
                    lecture.put(DATA_ITEM_1, "No Lecture here");
                    lecture.put(DATA_ITEM_2, "You might want to update your time table");
                    lecture.put(DATA_ITEM_3, "Last Updated: " + lastUpdatedTime());
                    lecture.put(START_HEADING, "--");
                    lecture.put(END_HEADING, "--");
                    lecture.put(LEC_NUM_HEADING, -1);
                    lecturesArray.put(lecture);
                    dayData.put(LECTURES_HEADING, lecturesArray);
                    dayData.put(TOTAL_LEC_HEADING, 0);
                    timeTable.put(day, dayData);
                }
            }
            return timeTable;

        } catch (JSONException e) {
            throw new NoTimeTableException(query, e.getMessage());
        }
    }

    private int getSpan(Element td) {
        String rowspanText = td.attr("rowspan").trim();
        int rowspan = Integer.parseInt(rowspanText);
        return rowspan;
    }

    public static String getDay(int number) {
        return daysOfWeek[number - 1];
    }

    public static int whatIsToday() {
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("u");
        int day = Integer.parseInt(simpleDateFormat.format(today));
        return day;
    }

    private static String currentTimeText() {
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mma");
        return simpleDateFormat.format(today);
    }

    private static Date currentTime(String text) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mma");
        try {
            return simpleDateFormat.parse(text);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String lastUpdatedTime() {
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d LLL hh:mm a");
        return simpleDateFormat.format(today);
    }


    private static Date parseTime(String text) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("k:m");
        try {
            return simpleDateFormat.parse(text);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONObject fetchResourceData(String tableType, String query, String day, String time,
                                         boolean sendDataEvenIfNotFree)
            throws LoginException, EmptyListException, NoTimeTableException {

        JSONObject timeTable = fetch(tableType, query);

        JSONObject resourceData = new JSONObject();
        boolean isFree = true;
        try {

            resourceData.put(DATA_ITEM_1, query);
            resourceData.put(DATA_ITEM_2, String.format("on %s at %s", day, time));
            resourceData.put(DATA_ITEM_3, "unknown");
            resourceData.put(START_HEADING, "--");
            resourceData.put(END_HEADING, "--");
            resourceData.put(LEC_NUM_HEADING, -1);

            JSONObject todaysTable = timeTable.getJSONObject(day);
            JSONArray todaysLectures = todaysTable.getJSONArray(LECTURES_HEADING);

            for (int i = 0; isFree && i < todaysLectures.length(); i++) {
                JSONObject tempLecture = todaysLectures.getJSONObject(i);
                int lectureNumber = tempLecture.getInt(LEC_NUM_HEADING);
                if (lectureNumber != 0 && lectureNumber != -1) {
                    String start = tempLecture.getString(START_HEADING);
                    String end = tempLecture.getString(END_HEADING);
                    Date startTime = parseTime(start);
                    Date endTime = parseTime(end);
                    Date currentTime = currentTime(time);
                    if (!currentTime.before(startTime) && currentTime.before(endTime)) {
                        isFree = false;
                        resourceData.put(START_HEADING, start);
                        resourceData.put(END_HEADING, end);
                    }
                }
            }
            resourceData.put(DATA_ITEM_3, "Is Free? : " + (isFree ? "YES" : "NO"));

        } catch (JSONException e) {
            throw new NoTimeTableException(query, e.getMessage());
        }

        if(!isFree){
            if(!sendDataEvenIfNotFree){
                resourceData = null;
            }
        }
        return resourceData;
    }

    public JSONObject getCompleteResourceData(String tableType, String query) throws LoginException, EmptyListException, NoTimeTableException {

        String today = getDay(whatIsToday());
        String currentTime = currentTimeText();

        JSONObject data = fetchResourceData(tableType, query, today, currentTime, true);
        JSONArray dataArray = new JSONArray();
        dataArray.put(data);
        return  encapsulateData(dataArray, today);

    }

    private JSONObject encapsulateData(JSONArray dataArray, String day) throws LoginException, EmptyListException, NoTimeTableException {

        try {
            JSONObject dataItem = new JSONObject();
            dataItem.put(LECTURES_HEADING, dataArray);
            dataItem.put(TOTAL_LEC_HEADING, 0);
            JSONObject completeData = new JSONObject();
            completeData.put(VERSION_HEADING, getLatestTimeTableVersion());
            completeData.put(NAME_HEADING, "Search Results");
            completeData.put(TYPE_HEADING,TYPE_INCOMPLETE);
            completeData.put(day, dataItem);
            return  completeData;
        } catch (JSONException e) {
            throw new NoTimeTableException("search results", e.getMessage());
        }
    }

    public JSONObject searchFreeResourcesOfType(String resourceName) throws LoginException, EmptyListException, NoTimeTableException {
        ArrayList<String> theList = getListOf(resourceName);
        JSONArray dataArray = new JSONArray();
        String today = getDay(whatIsToday());
        for (String item : theList) {
            String currentTime = currentTimeText();
            JSONObject data = fetchResourceData(resourceName, item, today, currentTime, false);
            if(data != null){
                dataArray.put(data);
            }
        }
        return encapsulateData(dataArray, today);
    }

}
