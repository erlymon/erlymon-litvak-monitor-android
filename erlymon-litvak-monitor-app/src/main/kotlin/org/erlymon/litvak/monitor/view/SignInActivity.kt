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

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.PopupMenu

import org.slf4j.LoggerFactory

import kotlinx.android.synthetic.main.activity_signin.*
import kotlinx.android.synthetic.main.content_signin.*
import org.erlymon.litvak.core.model.api.ApiModule
import org.erlymon.litvak.core.model.data.Server
import org.erlymon.litvak.core.model.data.User
import org.erlymon.litvak.core.presenter.SignInPresenter
import org.erlymon.litvak.core.presenter.SignInPresenterImpl
import org.erlymon.litvak.core.view.SignInView
import org.erlymon.litvak.monitor.MainPref
import org.erlymon.litvak.monitor.R
import org.erlymon.litvak.monitor.view.fragment.SettingsDialogFragment


class SignInActivity : BaseActivity<SignInPresenter>(), SignInView, SettingsDialogFragment.ServerConfigListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        presenter = SignInPresenterImpl(this, this)

        sign_in_login.setText(MainPref.login)
        sign_in_password.setText(MainPref.password);
        sign_in_button.setOnClickListener { v ->
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Check Permissions Now
                ActivityCompat.requestPermissions(this,  Array(1, { i -> Manifest.permission.WRITE_EXTERNAL_STORAGE }),  REQUEST_WRITE_STORAGE);
            } else {
                // permission has been granted, continue as usual
                presenter?.onSignInButtonClick()
            }

        }
        sign_up_button.setOnClickListener { v ->
            val intent = Intent(this@SignInActivity, SignUpActivity::class.java)
            startActivity(intent)
        }
        serverConfig.setOnClickListener { v ->
            val popupMenu = PopupMenu(this@SignInActivity, serverConfig)
            popupMenu.inflate(R.menu.settings_popupmenu)
            popupMenu.setOnMenuItemClickListener{ item ->
                when (item.itemId) {
                    R.id.action_settings -> {
                        val dialog = SettingsDialogFragment.newInstance(MainPref.dns, MainPref.sslOrTls)
                        dialog.show(supportFragmentManager, "settings_dialog")
                        true
                    }
                    R.id.action_about -> {
                        startActivity(Intent(this@SignInActivity, AboutActivity::class.java))
                        true
                    }
                    else -> {
                        true
                    }
                }
            }
            popupMenu.show()
        }

        //presenter?.onGetServer()
    }

    override fun onChangeServerConfig(dns: String, sslOrTls: Boolean) {
        storage?.deleteAll()
        MainPref.dns = dns
        MainPref.sslOrTls = sslOrTls
        ApiModule.getInstance().init(applicationContext, MainPref.dns, MainPref.sslOrTls)
        //presenter?.onGetServer()
    }
/*
    override fun showServer(server: Server) {
        logger.debug("showServer => " + server.toString())
        storage?.createOrUpdateServer(server)
        presenter?.onGetSession()
    }

    override fun showSession(user: User) {
        logger.debug("showSession => " + user.toString())
        storage?.createOrUpdateUser(user)

        val intent = Intent(this@SignInActivity, MainActivity::class.java)
                .putExtra("server", storage?.firstServer)
                .putExtra("session", user)
        startActivity(intent)
    }
*/
    override fun showData(user: User) {
        logger.debug("showData => " + user.toString())

        MainPref.login = sign_in_login.text.toString()
        MainPref.password = sign_in_password.text.toString()
        // write data to storage
        //storage?.createOrUpdateServer(data?.first)
        storage?.createOrUpdateUser(user)

        val intent = Intent(this@SignInActivity, MainActivity::class.java)
                .putExtra("server", storage?.firstServer)
                .putExtra("session", user)
        startActivity(intent)
    }

    override fun showError(error: String) {
        makeToast(ll_sign_in, error)
    }

    override fun getLogin(): String {
        return sign_in_login.text.toString()
    }

    override fun getPassword(): String {
        return sign_in_password.text.toString()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_WRITE_STORAGE) {
            if(grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We can now safely use the API we requested access to
                presenter?.onSignInButtonClick()
            } else {
                // Permission was denied or request was cancelled
                makeToast(ll_sign_in, getString(R.string.errorPermissionRationale))
            }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(SignInActivity::class.java)
        private val REQUEST_WRITE_STORAGE = 1
    }
}
