package pk.edu.kfueit.timetables.ui.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pk.edu.kfueit.timetables.R;
import pk.edu.kfueit.timetables.parser.TimeTable;

import java.sql.Time;
import java.util.Locale;

import static pk.edu.kfueit.timetables.parser.TimeTable.*;

public class TimeTableAdapter extends RecyclerView.Adapter {

    private int day;
    private Context context;
    private JSONObject timeTableData;
    public TimeTableAdapter(Context context){
        this(context, TimeTable.whatIsToday());
    }

    private TimeTableAdapter(Context context, int day) {
        this.context = context;
        this.day = day;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;

            // set the data in items
            try {
                String dayText = TimeTable.getDay(day);
                JSONObject dayData = timeTableData.getJSONObject(dayText);
                JSONArray lectures = dayData.getJSONArray(LECTURES_HEADING);
                int totalLectures = dayData.getInt(TOTAL_LEC_HEADING);


                if (position == 0) {
                    myViewHolder.item1TextView.setText(timeTableData.getString(TimeTable.VERSION_HEADING));
                    myViewHolder.item2TextView.setText(timeTableData.getString(TimeTable.NAME_HEADING));
                    myViewHolder.item3TextView.setText(String.format(Locale.US,
                            "%s%s - %s Lecture%s",
                            day == TimeTable.whatIsToday() ? "(Today) " : "",
                            TimeTable.getDay(day),
                            totalLectures == 0 ? "No" : String.valueOf(totalLectures),
                            totalLectures == 1 ? "" : "s"));
                    myViewHolder.swipeInfoLayout.setVisibility(View.VISIBLE);
                    myViewHolder.rowTimeSection.setVisibility(View.GONE);
                    if(timeTableData.getString(TYPE_HEADING).equals(TYPE_INCOMPLETE)){
                        myViewHolder.rowTimeSection.setVisibility(View.GONE);
                        myViewHolder.swipeInfoLayout.setVisibility(View.GONE);
                        myViewHolder.item3TextView.setVisibility(View.GONE);
                    }
                } else {
                    // if it was Gone previously...
                    myViewHolder.item3TextView.setVisibility(View.VISIBLE);
                    myViewHolder.rowTimeSection.setVisibility(View.VISIBLE);
                    int lecturePosition = position - 1; // because one space was occupied by static card

                    JSONObject lecture = lectures.getJSONObject(lecturePosition);
                    int lectureNumber = lecture.getInt(LEC_NUM_HEADING);
                    String itemText = lecture.getString(DATA_ITEM_1);

                    // no lecture here
                    if (lectureNumber == -1) {
                        myViewHolder.rowTimeSection.setVisibility(View.GONE);
                    }
                    // nor it is a break
                    else if (lectureNumber != 0) {
                        itemText = String.format("%d. %s", lectureNumber, itemText);
                    }

                    myViewHolder.item1TextView.setText(itemText);
                    myViewHolder.item2TextView.setText(lecture.getString(DATA_ITEM_2));
                    myViewHolder.item3TextView.setText(lecture.getString(DATA_ITEM_3));
                    myViewHolder.startTime.setText(lecture.getString(TimeTable.START_HEADING));
                    myViewHolder.endTime.setText(lecture.getString(TimeTable.END_HEADING));
                }
            } catch (JSONException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            // implement setOnClickListener event on item view.
            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // open another activity on item click

                    //Intent intent = new Intent(context, SecondActivity.class);
                    //intent.putExtra("image", personImages.get(position)); // put image data in Intent
                    //context.startActivity(intent); // start Intent
                    //Toast.makeText(context,String.valueOf(position),  Toast.LENGTH_SHORT).show();
                }
            });


    }

    @Override
    public int getItemCount() {

        if(timeTableData == null){
            return 0;
        }
        else {

            // "plus one" is for the space of one static card

            try {
                JSONObject dayData = timeTableData.getJSONObject(TimeTable.getDay(day));
                int count = dayData.getJSONArray(LECTURES_HEADING).length();
                return count + 1;
            } catch (JSONException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return 1;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // init the item view's
        TextView item1TextView;
        TextView item2TextView;
        TextView item3TextView;
        TextView startTime;
        TextView endTime;
        LinearLayout rowTimeSection;
        LinearLayout swipeInfoLayout;

        public MyViewHolder(View itemView) {
            super(itemView);

            // get the reference of item view's
            item1TextView = (TextView) itemView.findViewById(R.id.row_item_1);
            item2TextView = (TextView) itemView.findViewById(R.id.row_item_2);
            item3TextView = (TextView) itemView.findViewById(R.id.row_item_3);
            startTime = (TextView) itemView.findViewById(R.id.row_start_time);
            endTime = (TextView) itemView.findViewById(R.id.row_end_time);
            rowTimeSection = itemView.findViewById(R.id.row_time_section);
            swipeInfoLayout = itemView.findViewById(R.id.swipe_info_layout);




        }
    }

    public void incrementDay(){
        changeDay(1);
    }

    public void decrementDay(){
        changeDay(-1);
    }

    private void changeDay(int change){
        try {
            if(!timeTableData.getString(TYPE_HEADING).equals(TYPE_INCOMPLETE)){
                day += change;
                if(day < 1){
                    day = 1;
                }
                int totalDays = TimeTable.totalDays();
                if(day > totalDays){
                    day = totalDays;
                }
            }
        } catch (JSONException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        notifyDataSetChanged();
    }

    public void setTimeTable(JSONObject timeTableData){
        if(timeTableData != null){
            this.timeTableData = timeTableData;
            notifyDataSetChanged();
        }
    }

    public void setDay(int dayNumber){
        try {
            if(timeTableData!=null && !timeTableData.getString(TYPE_HEADING).equals(TYPE_INCOMPLETE)){
                day = dayNumber;
                if(day < 1){
                    day = 1;
                }
                int totalDays = TimeTable.totalDays();
                if(day > totalDays){
                    day = totalDays;
                }
            }
        } catch (JSONException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        notifyDataSetChanged();
    }

}