package pk.edu.kfueit.timetables.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import pk.edu.kfueit.timetables.MainActivity;
import pk.edu.kfueit.timetables.R;
import pk.edu.kfueit.timetables.ui.model.HomeViewModel;

public class ContactFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_contact, container, false);

        final EditText etSubject = root.findViewById(R.id.etSubject);
        final EditText etDescription = root.findViewById(R.id.etDescription);
        Button btnSend = root.findViewById(R.id.btnSend);



        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subject = etSubject.getText().toString();
                String description = etDescription.getText().toString();
                if(!subject.isEmpty()){
                    if(!description.isEmpty()){
                        description += "\n\n\nComplaint Submitted Via:\nKFUEIT TimeTables App\n"+"https://play.google.com/store/apps/details?id="+getActivity().getPackageName();
                        sendEmail("vc@kfueit.edu.pk",subject,description);
                    }
                    else{
                        Toast.makeText(getContext(),
                                "Please enter some details about your complaint.", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getContext(),
                            "Please enter subject of your complaint.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return root;
    }

    private void sendEmail(String to, String subject, String description) {

        String[] TO = {to};
        String[] CC = {};
        Intent emailIntent = new Intent(Intent.ACTION_VIEW);

        Uri data = Uri.parse("mailto:?subject=" + subject+ "&body=" + description + "&to=" + to);
        emailIntent.setData(data);


        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(),
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

}
