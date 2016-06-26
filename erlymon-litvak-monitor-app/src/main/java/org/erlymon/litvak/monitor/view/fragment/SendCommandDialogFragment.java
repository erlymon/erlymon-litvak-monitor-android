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
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import org.erlymon.litvak.core.model.data.Command;
import org.erlymon.litvak.monitor.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 09.03.16.
 */
public class SendCommandDialogFragment extends BaseDialogFragment {
    private static final Logger logger = LoggerFactory.getLogger(SendCommandDialogFragment.class);
    private AppCompatSpinner type;
    private AutoCompleteTextView frequency;
    private AutoCompleteTextView unit;
    private String[] types;

    public static SendCommandDialogFragment newInstance(long deviceId) {
        SendCommandDialogFragment newFragment = new SendCommandDialogFragment();
        Bundle args = new Bundle();
        args.putLong("deviceId", deviceId);
        newFragment.setArguments(args);
        return newFragment;
    }

    /* The activity that creates an instance of this dialog fragment must
 * implement this interface in order to receive event callbacks.
 * Each method passes the DialogFragment in case the host needs to query it. */
    public interface SendCommandDialogListener {
        void onSendCommand(Command command);
    }

    // Use this instance of the interface to deliver action events
    SendCommandDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface

        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (SendCommandDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement SendCommandDialogFragment");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        logger.debug("onCreateDialog");
        types = getResources().getStringArray(R.array.send_command_value);

        LayoutInflater factory = LayoutInflater.from(getContext());
        final View view = factory.inflate(R.layout.fragment_send_command, null);

        type = (AppCompatSpinner) view.findViewById(R.id.s_type);
        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String cmdType = types[type.getSelectedItemPosition()];
                frequency.setVisibility(cmdType.equals("positionPeriodic") ? View.VISIBLE : View.GONE);
                unit.setVisibility(cmdType.equals("positionPeriodic") ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        frequency = (AutoCompleteTextView) view.findViewById(R.id.tv_frequency);
        unit = (AutoCompleteTextView) view.findViewById(R.id.tv_unit);

        Dialog dialog = new AlertDialog.Builder(getActivity(), isBrokenSamsungDevice() ? android.R.style.Theme_Holo_Light_Dialog : R.style.AppTheme_Dialog)
                .setTitle(R.string.commandTitle)
                .setView(view)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String cmdType = types[type.getSelectedItemPosition()];
                                long deviceId = getArguments().getLong("deviceId");
                                Command command = new Command();
                                command.setDeviceId(deviceId);
                                command.setType(cmdType);
                                try {
                                    if (cmdType.equals("positionPeriodic")) {
                                        int freq = Integer.parseInt(String.valueOf(frequency.getText()));
                                        freq *= Integer.parseInt(String.valueOf(unit.getText()));
                                        Map<String, Object> attrs = new HashMap<>();
                                        attrs.put("frequency", freq);
                                        command.setAttributes(attrs);
                                    }
                                } catch (NumberFormatException e) {
                                    logger.warn(Log.getStackTraceString(e));
                                }
                                mListener.onSendCommand(command);
                            }
                        }
                )
                .create();

        return dialog;
    }
}
