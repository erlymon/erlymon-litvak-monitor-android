/*
 * Copyright (c) 2016, Sergey Penkovsky <sergey.penkovsky@gmail.com>
 *
 * This file is part of TraccarLitvakM (fork Erlymon Monitor).
 *
 * TraccarLitvakM (fork Erlymon Monitor) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TraccarLitvakM (fork Erlymon Monitor) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TraccarLitvakM (fork Erlymon Monitor).  If not, see <http://www.gnu.org/licenses/>.
 */
package org.erlymon.litvak.monitor.view.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import org.erlymon.litvak.monitor.R;


/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 01.06.15.
 */
public class TimePickerDialogFragment extends BaseDialogFragment {

    public static TimePickerDialogFragment newInstance(int hour, int minute) {
        Bundle args = new Bundle();
        args.putInt("hour", hour);
        args.putInt("minute", minute);
        TimePickerDialogFragment picker = new TimePickerDialogFragment();
        picker.setArguments(args);
        return picker;
    }

    // Use this instance of the interface to deliver action events
    TimePickerDialog.OnTimeSetListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (TimePickerDialog.OnTimeSetListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement TimePickerDialog.OnTimeSetListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        int hour = getArguments().getInt("hour", 0);
        int minute = getArguments().getInt("minute", 0);

        // Create a new instance of TimePickerDialog and return it
        TimePickerDialog dialog =  new TimePickerDialog(getActivity(), isBrokenSamsungDevice() ? android.R.style.Theme_Holo_Light_Dialog : R.style.AppTheme_Dialog, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                try {
                    mListener.onTimeSet(view, hourOfDay, minute);
                } catch (NullPointerException e) {}
            }
        }, hour, minute,  DateFormat.is24HourFormat(getActivity()));

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP || isBrokenSamsungDevice()) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        return dialog;
    }
}
