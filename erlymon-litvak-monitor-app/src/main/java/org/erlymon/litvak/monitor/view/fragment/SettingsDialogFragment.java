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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;

import org.erlymon.litvak.monitor.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 09.03.16.
 */
public class SettingsDialogFragment extends BaseDialogFragment {
    private static final Logger logger = LoggerFactory.getLogger(SettingsDialogFragment.class);
    private AutoCompleteTextView dns;
    private CheckBox sslOrTls;

    public static SettingsDialogFragment newInstance(String dns, boolean sslOrTls) {
        SettingsDialogFragment newFragment = new SettingsDialogFragment();
        Bundle args = new Bundle();
        args.putString("dns", dns);
        args.putBoolean("sslOrTls", sslOrTls);
        newFragment.setArguments(args);
        return newFragment;
    }

    /* The activity that creates an instance of this dialog fragment must
 * implement this interface in order to receive event callbacks.
 * Each method passes the DialogFragment in case the host needs to query it. */
    public interface ServerConfigListener {
        void onChangeServerConfig(String dns, boolean sslOrTls);
    }

    // Use this instance of the interface to deliver action events
    ServerConfigListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface

        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (ServerConfigListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ServerConfigFragment");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        logger.debug("onCreateDialog");

        LayoutInflater factory = LayoutInflater.from(getContext());
        final View view = factory.inflate(R.layout.fragment_settings, null);

        dns = (AutoCompleteTextView) view.findViewById(R.id.dns);
        dns.setText(getArguments().getString("dns"));

        sslOrTls = (CheckBox) view.findViewById(R.id.sslOrTls);
        sslOrTls.setChecked(getArguments().getBoolean("sslOrTls"));

        Dialog dialog = new AlertDialog.Builder(getActivity(), isBrokenSamsungDevice() ? android.R.style.Theme_Holo_Light_Dialog : R.style.AppTheme_Dialog)
                .setTitle(R.string.settingsTitle)
                .setView(view)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mListener.onChangeServerConfig(
                                        dns.getText().toString(),
                                        sslOrTls.isChecked()
                                );
                            }
                        }
                )
                .create();

        return dialog;
    }
}
