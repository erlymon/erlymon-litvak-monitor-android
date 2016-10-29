package org.erlymon.litvak.core.presenter;

import android.content.Context;
import android.util.Log;

import org.erlymon.litvak.core.model.Model;
import org.erlymon.litvak.core.model.ModelImpl;
import org.erlymon.litvak.core.model.data.Position;
import org.erlymon.litvak.core.view.MainView;
import org.erlymon.litvak.core.view.MapView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

/**
 * Created by sergey on 10/24/16.
 */

public class MapPresenterImpl implements MapPresenter {
    private static final Logger logger = LoggerFactory.getLogger(MainPresenterImpl.class);
    private Model model;
    private Realm realmdb;

    private MapView view;
    private CompositeSubscription mCompositeSubscription  = new CompositeSubscription();

    public MapPresenterImpl(Context context, MapView view) {
        this.view = view;
        this.model = new ModelImpl(context);
        this.realmdb = Realm.getDefaultInstance();
    }

    @Override
    public void onStart() {
        mCompositeSubscription.add(Observable.interval(5, TimeUnit.SECONDS, Schedulers.io())
                .flatMap(new Func1<Long, Observable<Position[]>>() {
                    @Override
                    public Observable<Position[]> call(Long aLong) {
                        return model.getLatestPositions();
                    }
                })
                .doOnError(err -> logger.error(Log.getStackTraceString(err)))
                .retry()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(positions -> realmdb.executeTransactionAsync(realm -> {
                    realm.delete(Position.class);
                    realm.copyToRealmOrUpdate(Arrays.asList(positions));
                    for (Position position: positions) {
                        realm.copyToRealmOrUpdate(position.getDevice());
                    }
                }))
                .doOnNext(positions -> {
                    logger.debug(positions.toString());
                    view.showLastPositions(positions);
                })
                .subscribe()
        );

        mCompositeSubscription.add(realmdb.where(Position.class).findAll().asObservable()
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<RealmResults<Position>, Observable<Position[]>>() {
                    @Override
                    public Observable<Position[]> call(RealmResults<Position> results) {
                        return Observable.just(results.toArray(new Position[results.size()]));
                    }
                })
                .doOnNext(positions -> {
                    logger.debug(positions.toString());
                    view.showLastPositions(positions);
                })
                .doOnError(err -> logger.error(Log.getStackTraceString(err)))
                .subscribe()
        );
    }

    @Override
    public void onStop() {
        if (!mCompositeSubscription.isUnsubscribed()) {
            mCompositeSubscription.unsubscribe();
        }

        if (!realmdb.isClosed()) {
            realmdb.close();
        }
    }
}
