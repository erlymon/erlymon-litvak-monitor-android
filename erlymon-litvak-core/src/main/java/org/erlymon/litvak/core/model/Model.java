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


import com.google.gson.JsonObject;

import org.erlymon.litvak.core.model.data.Command;
import org.erlymon.litvak.core.model.data.Device;
import org.erlymon.litvak.core.model.data.Position;
import org.erlymon.litvak.core.model.data.User;

import java.util.Date;

import rx.Observable;

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 5/4/16.
 */
public interface Model {
    Observable<User> createSession(String login, String password);

    Observable<Boolean> deleteSession();

    Observable<Device[]> getDevices();

    Observable<Device> createDevice(Device device);

    Observable<Device> updateDevice(Device device);

    Observable<Device> deleteDevice(Device device);

    Observable<Position[]> getLatestPositions();

    Observable<Position[]> getPositions(Device device, Date from, Date to, boolean flag);

    Observable<User> register(User user);

    Observable<Void> updateUser(User user);

    Observable<JsonObject> createCommand(Command command);
}
