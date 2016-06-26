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
package org.erlymon.litvak.core.model;

import android.content.Context;

import com.google.gson.JsonObject;

import org.erlymon.litvak.core.model.api.ApiModule;
import org.erlymon.litvak.core.model.api.util.QueryDate;
import org.erlymon.litvak.core.model.data.Command;
import org.erlymon.litvak.core.model.data.Device;
import org.erlymon.litvak.core.model.data.Position;
import org.erlymon.litvak.core.model.data.User;

import java.util.Date;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 5/4/16.
 */
public class ModelImpl implements Model {
    ApiModule apiModule = ApiModule.getInstance();

    public ModelImpl(Context context) {
    }


    @Override
    public Observable<User> createSession(String login, String password) {
        return apiModule.getApi().createSession(new String[] {login, password})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> deleteSession() {
        return apiModule.getApi().deleteSession()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<User> register(User user) {
        return apiModule.getApi().register(new String[] {user.getLogin(), user.getPassword()})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Void> updateUser(User user) {
        return apiModule.getApi().updateUser(new User[] {user})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Device[]> getDevices() {
        return apiModule.getApi().getDevices()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Device> createDevice(Device device) {
        return apiModule.getApi().createDevice(new Device[] {device})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Device> updateDevice(Device device) {
        return apiModule.getApi().updateDevice(new Device[] {device})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Device> deleteDevice(Device device) {
        return apiModule.getApi().deleteDevice(new Device[] {device})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Position[]> getLatestPositions() {
        return apiModule.getApi().getLatestPositions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Position[]> getPositions(Device device, Date from, Date to, boolean flag) {
        return apiModule.getApi().getPositions(new Object[] {device, new QueryDate(from).toString(), new QueryDate(to).toString(), flag})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<JsonObject> createCommand(Command command) {
        return apiModule.getApi().sendCommand(command)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
