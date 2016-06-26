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

import org.slf4j.LoggerFactory

import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.content_signup.*
import org.erlymon.litvak.core.model.data.User
import org.erlymon.litvak.core.presenter.UserPresenter
import org.erlymon.litvak.core.presenter.UserPresenterImpl
import org.erlymon.litvak.core.view.UserView
import org.erlymon.litvak.monitor.R


class SignUpActivity : BaseActivity<UserPresenter>(), UserView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        setSupportActionBar(toolbar)

        presenter = UserPresenterImpl(this, this)
        fab_account_save.setOnClickListener { v -> presenter?.onSaveButtonClick() }
    }

    override fun showData(data: User) {
        val intent = Intent()
        intent.putExtra("user", user)
        setResult(RESULT_OK, intent)
        finish()
    }


    override fun getUser(): User? {
        val user = User()
        user.id = 0;
        user.login = name.text.toString()
        user.password = password.text.toString()
        return user;
    }

    override fun showError(error: String) {
        makeToast(toolbar, error)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(SignUpActivity::class.java)
    }
}
