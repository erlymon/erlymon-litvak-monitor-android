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
package org.erlymon.litvak.monitor.view

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.PopupMenu
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import org.slf4j.LoggerFactory

import kotlinx.android.synthetic.main.fragment_devices.*
import org.erlymon.litvak.core.model.data.Device
import org.erlymon.litvak.monitor.R
import org.erlymon.litvak.monitor.view.adapter.DevicesAdapter

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 4/7/16.
 */
class DevicesFragment : BaseFragment() {

    interface OnActionDeviceListener {
        fun onEditDevice(device: Device)
        fun onRemoveDevice(device: Device)
        fun onLoadPositions(device: Device)
        fun onShowOnMap(device: Device)
        fun onSendCommand(device: Device)
    }

    private var listener: OnActionDeviceListener? = null


    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface

        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = context as OnActionDeviceListener?
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(context!!.toString() + " must implement OnActionDeviceListener")
        }

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_devices, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lv_devices.adapter = DevicesAdapter(context, storage.devicesSorted)
        lv_devices.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val device = lv_devices.getItemAtPosition(position) as Device
                val popupMenu = PopupMenu(context, view as View)
                popupMenu.inflate(R.menu.fragment_devices_popupmenu)
                popupMenu.setOnMenuItemClickListener(OnExecPopupMenuItem(device))
                popupMenu.show()
            }
        }
    }

    private inner class OnExecPopupMenuItem(internal var device: Device) : PopupMenu.OnMenuItemClickListener {

        override fun onMenuItemClick(item: MenuItem): Boolean {
            // Toast.makeText(PopupMenuDemoActivity.this,
            // item.toString(), Toast.LENGTH_LONG).show();
            // return true;
            when (item.itemId) {
                R.id.action_device_edit -> {
                    listener!!.onEditDevice(device)
                    return true
                }
                R.id.action_device_remove -> {
                    listener!!.onRemoveDevice(device)
                    return true
                }
                R.id.action_device_positions -> {
                    listener!!.onLoadPositions(device)
                    return true
                }
                R.id.action_show_on_map -> {
                    listener!!.onShowOnMap(device)
                    return true
                }
                R.id.action_send_command -> {
                    listener!!.onSendCommand(device)
                    return true
                }
                else -> return false
            }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(DevicesFragment::class.java)

        fun newIntent(devices: Array<out Device>?) : DevicesFragment {
            val fragment = DevicesFragment()
            val args = Bundle()
            args.putParcelableArray("devices", devices)
            fragment.arguments = args
            return fragment;
        }
    }
}