package ru.mkedonsky.lesson5;

import android.os.Bundle;

import java.util.Date;
import java.util.List;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Module
public class DaggerSugar {
    List<RetrofitModel> models;

    public DaggerSugar(List<RetrofitModel> models) {
        this.models = models;
    }

    public Bundle initModel() {
        String curLogin = "";
        String curUserID = "";
        String curAvatarUrl = "";
        Date first = new Date();
        for (RetrofitModel curItem : models) {
            curLogin = curItem.getLogin();
            curUserID = curItem.getId();
            curAvatarUrl = curItem.getAvatarUrl();
            SugarModel sugarModel = new SugarModel(curLogin, curUserID, curAvatarUrl);
            sugarModel.save();
        }
        Date second = new Date();
        List<SugarModel> tempList = SugarModel.listAll(SugarModel.class);
        Bundle bundle = new Bundle();
        bundle.putInt("count", tempList.size());
        bundle.putLong("msek", second.getTime() - first.getTime());
        return bundle;
    }

    @Provides
    Single<Bundle> saveAll(){
        return Single.create((SingleOnSubscribe<Bundle>) emitter -> {
            try {
                emitter.onSuccess(initModel());
            } catch (Exception e) {
                emitter.onError(e);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Provides
    @Named("getAll")
    Single<Bundle> getAll() {
        return Single.create((SingleOnSubscribe<Bundle>) emitter -> {
            try {
                Date first = new Date();
                List<SugarModel> tempList = SugarModel.listAll(SugarModel.class);
                Date second = new Date();
                Bundle bundle = new Bundle();
                bundle.putInt("count", tempList.size());
                bundle.putLong("msek", second.getTime() - first.getTime());
                emitter.onSuccess(bundle);
            } catch (Exception e) {
                emitter.onError(e);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Provides
    @Named("deleteAll")
    Single<Bundle> deleteAll() {
        return Single.create((SingleOnSubscribe<Bundle>) emitter -> {
            try {
                Date first = new Date();
                List<SugarModel> tempList = SugarModel.listAll(SugarModel.class);
                Date second = new Date();
                Bundle bundle = new Bundle();
                bundle.putInt("count", tempList.size());
                bundle.putLong("msek", second.getTime() - first.getTime());
                emitter.onSuccess(bundle);
            } catch (Exception e) {
                emitter.onError(e);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
