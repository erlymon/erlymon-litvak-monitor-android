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

import org.erlymon.litvak.core.model.Model;
import org.erlymon.litvak.core.model.ModelImpl;
import org.erlymon.litvak.core.model.data.Device;
import org.erlymon.litvak.core.view.DeviceView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.realm.Realm;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 5/4/16.
 */
public class DevicePresenterImpl implements DevicePresenter {
    private static final Logger logger = LoggerFactory.getLogger(DevicePresenterImpl.class);
    private Model model;
    private Realm realmdb;

    private DeviceView view;
    private Subscription subscription = Subscriptions.empty();

    public DevicePresenterImpl(Context context, DeviceView view) {
        this.view = view;
        this.model = new ModelImpl(context);
        this.realmdb = Realm.getDefaultInstance();
    }

    @Override
    public void onSaveButtonClick() {

        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        view.showProgressDialog();
        if (view.getDevice().getId() == null) {
            subscription = model.createDevice(view.getDevice())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(device -> realmdb.executeTransactionAsync(realm -> realm.copyToRealmOrUpdate(device)))
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
                            view.showData(data);
                        }
                    });
        } else {
            subscription = model.updateDevice(view.getDevice())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(device -> realmdb.executeTransactionAsync(realm -> realm.copyToRealmOrUpdate(device)))
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
                            view.showData(data);
                        }
                    });
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
