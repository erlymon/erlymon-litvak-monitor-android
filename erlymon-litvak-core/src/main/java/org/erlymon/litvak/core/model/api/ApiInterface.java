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
package org.erlymon.litvak.core.model.api;

import com.google.gson.JsonObject;

import org.erlymon.litvak.core.model.data.Command;
import org.erlymon.litvak.core.model.data.Device;
import org.erlymon.litvak.core.model.data.Position;
import org.erlymon.litvak.core.model.data.User;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 5/4/16.
 */

public interface ApiInterface {
    ////
    @POST("login")
    Observable<User> createSession(@Body String[] params);

    @GET("logout")
    Observable<Boolean> deleteSession();

    @GET("getDevices")
    Observable<Device[]> getDevices();

    @POST("addDevice")
    Observable<Device> createDevice(@Body Device[] params);

    @POST("updateDevice")
    Observable<Device> updateDevice(@Body Device[] params);

    @POST("removeDevice")
    Observable<Device> deleteDevice(@Body Device[] params);

    @GET("getLatestPositions")
    Observable<Position[]> getLatestPositions();

    @POST("getPositions")
    Observable<Position[]> getPositions(@Body Object[] params);

    @POST("register")
    Observable<User> register(@Body String[] params);

    @POST("updateUser")
    Observable<Void> updateUser(@Body User[] params);

    /*
    --> POST http://64.137.174.107/traccar/rest/sendCommand http/1.1
    Content-Type: application/json; charset=UTF-8
    Content-Length: 57
    [
      {
        "deviceId": 2,
        "type": "engineResume"
      }
    ]
    Answer:
    "{\"reason\":\"java.lang.RuntimeException: Command engineResume is not supported in protocol hunterpro\",\"success\":false}"
     */

    @POST("sendCommand")
    Observable<String> sendCommand(@Body Object[] params);
}