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

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.erlymon.litvak.core.model.data.*
import org.erlymon.litvak.core.presenter.MainPresenter
import org.erlymon.litvak.core.presenter.MainPresenterImpl
import org.erlymon.litvak.core.view.MainView
import org.erlymon.litvak.monitor.R
import org.erlymon.litvak.monitor.view.adapter.CustomFragmentPagerAdapter
import org.erlymon.litvak.monitor.view.fragment.ConfirmDialogFragment
import org.erlymon.litvak.monitor.view.fragment.SendCommandDialogFragment
import org.osmdroid.util.GeoPoint

import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity<MainPresenter>(),
        MainView,
        NavigationView.OnNavigationItemSelectedListener,
        DevicesFragment.OnActionDeviceListener,
        ConfirmDialogFragment.ConfirmDialogListener,
        SendCommandDialogFragment.SendCommandDialogListener {
    private var service: ScheduledExecutorService? = null;
    private var pagerAdapter: CustomFragmentPagerAdapter? = null

    private var mAccountNameView: TextView? = null
    private var deviceId: Long = 0
    private var command: Command? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        service = Executors.newSingleThreadScheduledExecutor()
        presenter = MainPresenterImpl(this, this)

        val linearLayout = nav_view.getHeaderView(0) as LinearLayout
        mAccountNameView = linearLayout.getChildAt(1) as TextView

        val session = intent.getParcelableExtra<User>("session")
        mAccountNameView?.text = session?.login

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.setDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)


        pagerAdapter = CustomFragmentPagerAdapter(supportFragmentManager)
        pagerAdapter?.addPage(MapFragment())
        pagerAdapter?.addPage(DevicesFragment())
        view_pager.setAdapter(pagerAdapter)

        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageSelected(position: Int) {
                logger.debug("onPageSelected, position = " + position)
                when (position) {
                    0 -> {
                        fab.visibility = View.GONE
                        supportActionBar?.setTitle(R.string.mapTitle)
                    }
                    1 -> {
                        fab.visibility = View.VISIBLE
                        supportActionBar?.setTitle(R.string.settingsDevices)
                    }
                    2 -> {
                        fab.visibility = View.VISIBLE
                        supportActionBar?.setTitle(R.string.settingsUsers)
                    }
                }
            }

            override fun onPageScrolled(position: Int, positionOffset: Float,
                                        positionOffsetPixels: Int) {
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })

        fab.setOnClickListener {
            when (view_pager.currentItem) {
                1 -> {
                    logger.debug("Start DeviceActivity")
                    val intent = Intent(this@MainActivity, DeviceActivity::class.java)
                            .putExtra("session", intent.getParcelableExtra<User>("session"))
                    startActivityForResult(intent, REQUEST_CODE_CREATE_OR_UPDATE_DEVICE)
                }
            }
        }

        presenter?.onLoadDevices()
    }

    override fun onDestroy() {
        service?.shutdown()
        super.onDestroy();
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            //super.onBackPressed();
            if (backPressed + 2000 > System.currentTimeMillis()) {
                presenter?.onDeleteSessionButtonClick()
            } else {
                Toast.makeText(baseContext, getString(R.string.sharedBackPressed), Toast.LENGTH_SHORT).show()
                backPressed = System.currentTimeMillis()
            }
        }
    }

    override fun showDevices(devices: Array<Device>) {
        logger.debug("showDevices => " + devices.size)
        storage?.createOrUpdateDevices(devices)
        presenter?.onLoadLastPositions()
    }

    override fun showLastPositions(positions: Array<out Position>) {
        logger.debug("showLastPositions => " + positions.size)
        storage?.createOrUpdatePositions(positions)

        positions.forEach { it ->
            storage?.createOrUpdateDevice(it.device)
        }

        service?.schedule({
            presenter?.onLoadLastPositions()
        }, 10, TimeUnit.SECONDS)
    }

    override fun showCompleted() {
        storage?.deleteAll()
        finish()
    }

    override fun showRemoveDeviceCompleted() {
        storage?.removeDevice(deviceId)
        deviceId = 0
    }

    override fun getDevice(): Device? {
        val device = Device()
        device.id = deviceId
        return device
    }

    override fun getCommand(): Command? {
        command?.deviceId = deviceId
        return command
    }

    override fun showError(error: String) {
        makeToast(toolbar, error)
    }

    @SuppressWarnings("StatementWithEmptyBody")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId
        when (item.itemId) {
            R.id.nav_map -> {
                view_pager.setCurrentItem(0)
            }
            R.id.nav_devices -> {
                view_pager.setCurrentItem(1)
            }
            R.id.nav_account -> {
                val intent = Intent(this@MainActivity, UserActivity::class.java)
                        .putExtra("session", intent.getParcelableExtra<User>("session"))
                        .putExtra("user", intent.getParcelableExtra<User>("session"))
                startActivityForResult(intent, REQUEST_CODE_UPDATE_ACCOUNT)
            }
            R.id.nav_about -> {
                startActivity(Intent(this@MainActivity, AboutActivity::class.java))
            }
            R.id.nav_sign_out -> {
                presenter?.onDeleteSessionButtonClick()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_UPDATE_SERVER ->
                if (resultCode == RESULT_OK) {
                    val server = data?.getParcelableExtra<Server>("server")
                    intent.putExtra("server", server)
                    storage?.createOrUpdateServer(server)
                }
            REQUEST_CODE_UPDATE_ACCOUNT ->
                if (resultCode == RESULT_OK) {
                    val user = data?.getParcelableExtra<User>("user")
                    intent.putExtra("session", user)
                    storage?.createOrUpdateUser(user)
                    mAccountNameView?.text = user?.login
                   // mAccountEmailView?.text = user?.
                }
            REQUEST_CODE_CREATE_OR_UPDATE_DEVICE ->
                if (resultCode == RESULT_OK) {
                    val device = data?.getParcelableExtra<Device>("device")
                    logger.debug("DEVICE: " + device!!.getName())
                    storage?.createOrUpdateDevice(device)
                }
        }
    }

    override fun onEditDevice(device: Device) {
        val intent = Intent(this@MainActivity, DeviceActivity::class.java)
                .putExtra("session", intent.getParcelableExtra<User>("session"))
                .putExtra("device", device)
        startActivityForResult(intent, REQUEST_CODE_CREATE_OR_UPDATE_DEVICE)
    }

    override fun onRemoveDevice(device: Device) {
        deviceId = device.id
        val dialogFragment = ConfirmDialogFragment.newInstance(R.string.deviceTitle, R.string.sharedRemoveConfirm)
        dialogFragment.show(supportFragmentManager, "remove_item_dialog")
    }

    override fun onLoadPositions(device: Device) {
        val intent = Intent(this@MainActivity, PositionsActivity::class.java)
                .putExtra("session", intent.getParcelableExtra<User>("session"))
                .putExtra("device", device)
        startActivity(intent)
    }


    override fun onShowOnMap(device: Device) {
        try {
            val position = storage?.getPositionByDeviceId(device.id)
            (pagerAdapter?.getItem(0) as MapFragment).animateTo(GeoPoint(position?.latitude as Double, position?.longitude as Double), 15)
            view_pager.setCurrentItem(0)
            nav_view.setCheckedItem(R.id.nav_map)
        } catch (e: Exception) {
            logger.warn(Log.getStackTraceString(e))
        }
    }

    override fun onSendCommand(device: Device) {
        deviceId = device.getId();
        val dialogFragment = SendCommandDialogFragment.newInstance(device.id)
        dialogFragment.show(supportFragmentManager, "send_command_dialog")
    }

    override fun onPositiveClick(dialog: DialogInterface, which: Int) {
        if (deviceId > 0) {
            presenter?.onDeleteDeviceButtonClick()
        }
    }

    override fun onSendCommand(command: Command?) {
        this.command = command;
        presenter?.onSendCommandButtonClick()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(MainActivity::class.java)
        private var backPressed: Long = 0

        val REQUEST_CODE_UPDATE_SERVER = 1
        val REQUEST_CODE_UPDATE_ACCOUNT = 2
        val REQUEST_CODE_CREATE_OR_UPDATE_DEVICE = 3
    }
}
