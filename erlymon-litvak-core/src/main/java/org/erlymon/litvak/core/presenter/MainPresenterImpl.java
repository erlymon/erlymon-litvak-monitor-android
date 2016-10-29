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
package org.erlymon.litvak.core.presenter;


import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;

import org.erlymon.litvak.core.model.Model;
import org.erlymon.litvak.core.model.ModelImpl;
import org.erlymon.litvak.core.model.data.Device;
import org.erlymon.litvak.core.model.data.Position;
import org.erlymon.litvak.core.view.MainView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import io.realm.Realm;
import io.realm.RealmObject;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 5/4/16.
 */
public class MainPresenterImpl implements MainPresenter {
    private static final Logger logger = LoggerFactory.getLogger(MainPresenterImpl.class);
    private Model model;
    private Realm realmdb;

    private MainView view;
    private Subscription subscription = Subscriptions.empty();

    public MainPresenterImpl(Context context, MainView view) {
        this.view = view;
        this.model = new ModelImpl(context);
        this.realmdb = Realm.getDefaultInstance();
    }

    @Override
    public void onDeleteSessionButtonClick() {

        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        view.showProgressDialog();
        subscription = model.deleteSession()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(o -> realmdb.executeTransaction(realm -> realm.deleteAll()))
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        view.hideProgressDialog();
                        logger.error(Log.getStackTraceString(e));
                        view.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(Boolean data) {
                        view.showCompleted();
                        view.hideProgressDialog();
                    }
                });
    }

    @Override
    public void onDeleteDeviceButtonClick() {
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        view.showProgressDialog();
        Device device = new Device();
        device.setId(view.getDeviceId());
        subscription = model.deleteDevice(device)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(o -> realmdb.executeTransaction(realm -> {
                    try {
                        realm.where(Device.class).equalTo("id", view.getDeviceId()).findFirst().deleteFromRealm();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }))
                .subscribe(new Observer<Device>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        view.hideProgressDialog();
                        logger.error(Log.getStackTraceString(e));
                        view.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(Device data) {
                        view.hideProgressDialog();
                        view.showRemoveDeviceCompleted();
                    }
                });
    }

    @Override
    public void onSendCommandButtonClick() {
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        view.showProgressDialog();
        subscription = model.createCommand(view.getCommand())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Void>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        view.hideProgressDialog();
                        logger.error(Log.getStackTraceString(e));
                        view.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(Void data) {
                        view.hideProgressDialog();
                        view.showCommandCompleted();
                    }
                });
    }

    @Override
    public void onGetPostionByCache() {
        try {
            if (!subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }

            subscription = realmdb.where(Position.class)
                    .equalTo("device.id", view.getDeviceId())
                    .findFirst()
                    .asObservable()
                    .flatMap((Func1<RealmObject, Observable<Position>>) realmObject -> realmObject.asObservable())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Position>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            logger.error(Log.getStackTraceString(e));
                            view.showError(e.getMessage());
                        }

                        @Override
                        public void onNext(Position data) {
                            logger.debug(data.toString());
                            view.showPosition(data);
                        }
                    });
        } catch (NullPointerException e) {
            logger.error(Log.getStackTraceString(e));
            view.showError(e.getMessage());
        }
    }

    @Override
    public void onStop() {
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        if (!realmdb.isClosed()) {
            realmdb.close();
        }
    }
}
