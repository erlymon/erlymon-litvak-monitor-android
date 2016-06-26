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
package org.erlymon.litvak.core.model.data;

import android.content.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 4/5/16.
 */
public class Storage {
    private static final Logger logger = LoggerFactory.getLogger(Storage.class);
    private final RealmConfiguration realmConfig;
    private Realm realmdb;

    public Storage(Context context, String storageName) {
        realmConfig = new RealmConfiguration.Builder(context) // Beware this is the app context
                .deleteRealmIfMigrationNeeded()
                .name(storageName)                    // So always use a unique name
                //.setModules(new AllAnimalsModule())           // Always use explicit modules in library projects
                .build();

        // Reset Realm
        //Realm.deleteRealm(realmConfig);
    }

    public void open() {
        logger.info("open");
        // Don't use Realm.setDefaultInstance() in library projects. It is unsafe as app developers can override the
        // default configuration. So always use explicit configurations in library projects.
        realmdb = Realm.getInstance(realmConfig);
    }

    public long getNoOfDevices() {
        return realmdb.where(Device.class).count();
    }

    public RealmResults<Device> getDevices() {
        return realmdb.where(Device.class).findAll();
    }

    public RealmResults<Device> getDevicesSorted() {
        return realmdb.where(Device.class).findAllSorted("name");
    }

    public RealmResults<Device> getDevicesByVisible(boolean visible) {
        return realmdb.where(Device.class).equalTo("visible", visible).findAll();
    }


    public Device getDeviceById(long id) {
        return realmdb.where(Device.class).equalTo("id", id).findFirst();
    }

    public Position getPositionByDeviceId(long id) {
        return realmdb.where(Position.class).equalTo("device.id", id).findAll().last();
    }

    public User getFirstUser() {
        return realmdb.where(User.class).findFirst();
    }

    public User getUserById(long id) {
        return realmdb.where(User.class).equalTo("id", id).findFirst();
    }

    public RealmResults<User> getUsersSorted() {
        return realmdb.where(User.class).findAllSorted("name");
    }

    public Server getFirstServer() {
        return realmdb.where(Server.class).findFirst();
    }

    public void createOrUpdateDevice(final Device device) {
        realmdb.executeTransaction(realm -> realm.copyToRealmOrUpdate(device));
    }

    public void createOrUpdateDevices(final List<Device> devices) {
        realmdb.executeTransaction(realm -> realm.copyToRealmOrUpdate(devices));
    }

    public void createOrUpdateDevices(final Device[] devices) {
        logger.debug("createOrUpdateDevices: " + realmdb);
        realmdb.executeTransaction(realm -> realm.copyToRealmOrUpdate(Arrays.asList(devices)));
    }

    public void createOrUpdatePositions(final List<Position> positions) {
        realmdb.executeTransaction(realm -> realm.copyToRealmOrUpdate(positions));
    }

    public void createOrUpdatePositions(final Position[] positions) {
        realmdb.executeTransaction(realm -> realm.copyToRealmOrUpdate(Arrays.asList(positions)));
    }

    public void createOrUpdateServer(final Server server) {
        realmdb.executeTransaction(realm -> realm.copyToRealmOrUpdate(server));
    }

    public void createOrUpdateUser(final User user) {
        realmdb.executeTransaction(realm -> realm.copyToRealmOrUpdate(user));
    }

    public void createOrUpdateUsers(final User[] users) {
        logger.debug("createOrUpdateUsers: " + realmdb);
        realmdb.executeTransaction(realm -> realm.copyToRealmOrUpdate(Arrays.asList(users)));
    }

    public void removeDevice(final long deviceId) {
        realmdb.executeTransaction(realm -> realm.where(Device.class).equalTo("id", deviceId).findFirst().deleteFromRealm());
    }

    public void removeUser(final long userId) {
        realmdb.executeTransaction(realm -> realm.where(User.class).equalTo("id", userId).findFirst().deleteFromRealm());
    }

    public void deleteAll() {
        // Reset Realm
        realmdb.executeTransaction(realm -> realm.deleteAll());
    }

    public void close() {
        logger.info("close");
        realmdb.close();
    }

    public Realm getRealm() {
        return realmdb;
    }
}
