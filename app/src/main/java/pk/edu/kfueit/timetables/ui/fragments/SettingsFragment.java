package pk.edu.kfueit.timetables.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import pk.edu.kfueit.timetables.R;
import pk.edu.kfueit.timetables.TimeTableInfoSelectActivity;
import pk.edu.kfueit.timetables.TimeTableVersionActivity;

public class SettingsFragment extends Fragment {



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        root.findViewById(R.id.btnSetMyTimeTable).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TimeTableVersionActivity.class);
                startActivity(intent);
            }
        });
        return root;
    }

}
