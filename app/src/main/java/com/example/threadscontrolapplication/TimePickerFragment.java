package com.example.threadscontrolapplication;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
//this class is used by both event creation and event update fragments
public class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {


        Button time;
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }
        public void setTime(Button time)
        {
            this.time=time;
        }
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            this.time.setText(hourOfDay+":"+minute);
        }
}
