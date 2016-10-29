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
import android.view.View
import kotlinx.android.synthetic.main.activity_user.*
import kotlinx.android.synthetic.main.content_user.*
import org.erlymon.litvak.core.model.data.User
import org.erlymon.litvak.core.model.data.UserSettings
import org.erlymon.litvak.core.presenter.UserPresenter
import org.erlymon.litvak.core.presenter.UserPresenterImpl
import org.erlymon.litvak.core.view.UserView
import org.erlymon.litvak.monitor.R
import org.slf4j.LoggerFactory

class UserActivity : BaseActivity<UserPresenter>(), UserView {
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
        setContentView(R.layout.activity_user)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        presenter = UserPresenterImpl(this, this)

        val session = intent.getParcelableExtra<User>("session")
        val user = intent.getParcelableExtra<User>("user")
        logger.debug("USER ID: " + user?.id + " USER: " + user?.toString())
        name.setText(user?.login)
        password.setText(user?.password)
        admin.setChecked(if (user?.admin != null) (user?.admin as Boolean) else false)
        admin.setEnabled(session?.admin as Boolean)
        map.setText(user?.userSettings?.mapType)
        speedUnit.setText(user?.userSettings?.speedUnit)
        latitude.setText(if (user?.userSettings?.centerLatitude != null) user?.userSettings?.centerLatitude.toString() else 0.0.toString())
        longitude.setText(if (user?.userSettings?.centerLongitude != null) user?.userSettings?.centerLongitude.toString() else 0.0.toString())
        zoom.setText(if (user?.userSettings?.zoomLevel != null) user?.userSettings?.zoomLevel.toString() else 0.toString())

        fab_account_save.setOnClickListener {
            presenter?.onSaveButtonClick()
        }
    }

    override fun showProgressDialog() {
        progressDialog?.show()
    }

    override fun hideProgressDialog() {
        progressDialog?.hide()
    }

    override fun showData(data: User) {
        logger.debug(user.toString())
        val intent = Intent()
        intent.putExtra("user", user)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun showError(error: String) {
        makeToast(toolbar, error)
    }

    override fun getUser(): User {
        var user = intent.getParcelableExtra<User>("user")
        if (user == null) {
            user = User()
            user.userSettings = user.userSettings
        }
        user.login = name.text.toString()
        user.password = password.text.toString()
        user.admin = admin.isChecked
        /*
        user.userSettings.mapType = map.text.toString()
        user.userSettings.speedUnit = speedUnit.text.toString()
        user.userSettings.centerLatitude = if (latitude.text.length > 0) latitude.text.toString().toDouble() else 0.0
        user.userSettings.centerLongitude = if (longitude.text.length > 0) longitude.text.toString().toDouble() else 0.0
        user.userSettings.zoomLevel = if (zoom.text.length > 0) zoom.text.toString().toInt() else 0
        */
        //user.twelveHourFormat = twelveHourFormat.isChecked
        return user
    }

    companion object {
        private val logger = LoggerFactory.getLogger(UserActivity::class.java)
    }
}
