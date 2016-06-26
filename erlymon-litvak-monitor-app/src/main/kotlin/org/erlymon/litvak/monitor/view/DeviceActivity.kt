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

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import io.realm.RealmList
import kotlinx.android.synthetic.main.activity_device.*
import kotlinx.android.synthetic.main.content_device.*
import org.erlymon.litvak.core.model.data.Device
import org.erlymon.litvak.core.model.data.Maintenance
import org.erlymon.litvak.core.model.data.Sensor
import org.erlymon.litvak.core.presenter.DevicePresenter
import org.erlymon.litvak.core.presenter.DevicePresenterImpl
import org.erlymon.litvak.core.view.DeviceView
import org.erlymon.litvak.monitor.R
import org.slf4j.LoggerFactory


class DeviceActivity : BaseActivity<DevicePresenter>(), DeviceView {

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        presenter = DevicePresenterImpl(this, this)


        val device = intent.getParcelableExtra<Device>("device")
        logger.debug("DEVICE ID: " + device?.id + " DEVICE: " + device?.toString())
        name.setText(device?.name)
        identifier.setText(device?.uniqueId)

        fab_device_save.setOnClickListener {
            presenter?.onSaveButtonClick()
        }
    }

    override fun showData(data: Device) {
        logger.debug(data.toString())
        val intent = Intent()
        intent.putExtra("device", data)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun getDevice(): Device {
        var device = intent.getParcelableExtra<Device>("device")
        if (device == null) {
            device = Device()
            device.sensors = RealmList<Sensor>();
            device.maintenances = RealmList<Maintenance>();
        }
        device.name = name.text.toString()
        device.uniqueId = identifier.text.toString()
        return device
    }

    override fun showError(error: String) {
        makeToast(toolbar, error)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(DeviceActivity::class.java)
    }
}
