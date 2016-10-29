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
import android.support.v4.util.Pair;
import android.util.Log;

import org.erlymon.litvak.core.model.Model;
import org.erlymon.litvak.core.model.ModelImpl;
import org.erlymon.litvak.core.model.api.util.tuple.Triple;
import org.erlymon.litvak.core.model.data.Device;
import org.erlymon.litvak.core.model.data.Position;
import org.erlymon.litvak.core.model.data.User;
import org.erlymon.litvak.core.view.SignInView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import io.realm.Realm;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 5/4/16.
 */
public class SignInPresenterImpl implements SignInPresenter {
    private static final Logger logger = LoggerFactory.getLogger(SignInPresenterImpl.class);
    private Model model;
    private Realm realmdb;

    private SignInView view;
    private Subscription subscription = Subscriptions.empty();


    public SignInPresenterImpl(Context context, SignInView view) {
        this.view = view;
        this.model = new ModelImpl(context);
        this.realmdb = Realm.getDefaultInstance();
    }

    @Override
    public void onSignInButtonClick() {

        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        view.showProgressDialog();
        subscription = model.createSession(view.getLogin(), view.getPassword())
                .subscribeOn(Schedulers.io())
                //.flatMap(user -> model.getDevices().flatMap(devices -> Observable.just(new Pair<>(user, devices))))
                .flatMap(user -> Observable.zip(model.getDevices(), model.getLatestPositions(), (devices, positions) -> new Triple<>(user, devices, positions)).asObservable())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(result -> realmdb.executeTransaction(realm -> {
                    realm.copyToRealmOrUpdate(result.first);
                    realm.copyToRealmOrUpdate(Arrays.asList(result.second));
                    realm.copyToRealmOrUpdate(Arrays.asList(result.third));
                }))
                .subscribe(new Observer<Triple<User,Device[], Position[]>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        logger.error(Log.getStackTraceString(e));
                        view.showError(e.getMessage());
                        view.hideProgressDialog();
                    }

                    @Override
                    public void onNext(Triple<User,Device[], Position[]> result) {
                        view.showData(result.first);
                        view.hideProgressDialog();
                    }
                });
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
