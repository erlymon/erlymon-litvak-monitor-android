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
import org.erlymon.litvak.core.model.data.User;
import org.erlymon.litvak.core.view.UserView;
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
public class UserPresenterImpl implements UserPresenter {
    private static final Logger logger = LoggerFactory.getLogger(UserPresenterImpl.class);
    private Model model;
    private Realm realmdb;

    private UserView view;
    private Subscription subscription = Subscriptions.empty();

    public UserPresenterImpl(Context context, UserView view) {
        this.view = view;
        this.model = new ModelImpl(context);
        this.realmdb = Realm.getDefaultInstance();
    }

    @Override
    public void onSaveButtonClick() {

        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        if (view.getUser().getId() == 0) {
            subscription = model.register(view.getUser())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(user -> realmdb.executeTransactionAsync(realm -> realm.copyToRealmOrUpdate(user)))
                    .subscribe(new Observer<User>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            logger.error(Log.getStackTraceString(e));
                            view.showError(e.getMessage());
                        }

                        @Override
                        public void onNext(User data) {
                            view.showData(data);
                        }
                    });
        } else {
            subscription = model.updateUser(view.getUser())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(user -> realmdb.executeTransactionAsync(realm -> realm.copyToRealmOrUpdate(view.getUser())))
                    .subscribe(new Observer<Void>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            logger.error(Log.getStackTraceString(e));
                            view.showError(e.getMessage());
                        }

                        @Override
                        public void onNext(Void data) {
                            view.showData(view.getUser());
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
