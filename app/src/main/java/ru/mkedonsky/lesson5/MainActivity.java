package ru.mkedonsky.lesson5;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.orm.SugarContext;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener {
    private TextView mInfoTextView;
    private ProgressBar progressBar;
    Button btnLoad;
    Button btnSaveAllSugar;
    Button btnSelectAllSugar;
    Button btnDeleteAllSugar;
    RestAPI restAPI;
    List<RetrofitModel> modelList = new ArrayList<>();
    Retrofit retrofit = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInfoTextView = findViewById(R.id.tvLoad);
        progressBar =  findViewById(R.id.progressBar);
        btnLoad = findViewById(R.id.btnLoad);
        btnSaveAllSugar =  findViewById(R.id.btnSaveAllSugar);
        btnSelectAllSugar =  findViewById(R.id.btnSelectAllSugar);
        btnDeleteAllSugar =  findViewById(R.id.btnDeleteAllSugar);
        btnLoad.setOnClickListener(this);
        btnSaveAllSugar.setOnClickListener(this);
        btnSelectAllSugar.setOnClickListener(this);
        btnDeleteAllSugar.setOnClickListener(this);
        SugarContext.init(getApplicationContext());
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SugarContext.terminate();
    }
    private DisposableSingleObserver<Bundle> CreateObserver() {
        return new DisposableSingleObserver<Bundle>() {
            @Override
            protected void onStart() {
                super.onStart();
                progressBar.setVisibility(View.VISIBLE);
                mInfoTextView.setText("");
            }
            @Override
            public void onSuccess(@NonNull Bundle bundle) {
                progressBar.setVisibility(View.GONE);
                mInfoTextView.append("количество = " + bundle.getInt("count") +
                        "\n милисекунд = " + bundle.getLong("msek"));
            }
            @SuppressLint("SetTextI18n")
            @Override
            public void onError(@NonNull Throwable e) {
                progressBar.setVisibility(View.GONE);
                mInfoTextView.setText("ошибка БД: " + e.getMessage());
            }
        };
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLoad:
                mInfoTextView.setText("");
                try {
                    retrofit = new Retrofit.Builder()
                            .baseUrl("https://api.github.com/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    restAPI = retrofit.create(RestAPI.class);
                } catch (Exception io) {
                    mInfoTextView.setText("no retrofit: " + io.getMessage());
                    return;
                }
                // Подготовили вызов на сервер
                Call<List<RetrofitModel>> call = restAPI.loadUsers();
                ConnectivityManager connectivityManager =
                        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkinfo = connectivityManager.getActiveNetworkInfo();

                if (networkinfo != null && networkinfo.isConnected()) {
                    progressBar.setVisibility(View.VISIBLE);
                    downloadOneUrl(call);
                } else {
                    Toast.makeText(this, "Подключите интернет", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnSaveAllSugar:
                Single<Bundle> singleSaveAll = Single.create((SingleOnSubscribe<Bundle>) emitter -> {
                    try {
                        String curLogin = " ";
                        String curUserID = " ";
                        String curAvatarUrl = " ";

                        Date first = new Date();
                        for (RetrofitModel curItem : modelList) {
                            curLogin = curItem.getLogin();
                            curUserID = curItem.getId();
                            curAvatarUrl = curItem.getAvatarUrl();
                            SugarModel sugarModel = new SugarModel(curLogin, curUserID, curAvatarUrl);
                            sugarModel.save();
                        }
                       Date second = new Date(System.currentTimeMillis());
                        List<SugarModel> tempList = SugarModel.listAll(SugarModel.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("count", tempList.size());
                        bundle.putLong("msek", second.getTime() - first.getTime());
                        emitter.onSuccess(bundle);
                    } catch (Exception e) {
                        emitter.onError(e);
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
                singleSaveAll.subscribeWith(CreateObserver());
                break;
            case R.id.btnSelectAllSugar:
                Single<Bundle> singleSelectAll = Single.create((SingleOnSubscribe<Bundle>) emitter -> {
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
                singleSelectAll.subscribeWith(CreateObserver());
                break;
            case R.id.btnDeleteAllSugar:
                Single<Bundle> singleDeleteAll = Single.create((SingleOnSubscribe<Bundle>) emitter -> {
                    try {
                        List<SugarModel> tempList = SugarModel.listAll(SugarModel.class);
                        Date first = new Date();
                        SugarModel.deleteAll(SugarModel.class);
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
                singleDeleteAll.subscribeWith(CreateObserver());
                break;
            case R.id.btnSaveAllRoom:
                Single<Bundle> singleSaveAllRoom = Single.create((SingleOnSubscribe<Bundle>)
                        emitter -> {
                    String curLogin = "";
                    String curUserID = "";
                    String curAvatarUrl = "";
                    Date first = new Date();
                    List<RoomModel> roomModelList = new ArrayList<>();
                    RoomModel roomModel = new RoomModel();
                    for (RetrofitModel curItem : modelList) {
                        curLogin = curItem.getLogin();
                        curUserID = curItem.getId();
                        curAvatarUrl = curItem.getAvatarUrl();
                        roomModel.setLogin(curLogin);
                        roomModel.setAvatarUrl(curAvatarUrl);
                        roomModel.setUserId(curUserID);
                        roomModelList.add(roomModel);

                        OrmApp.getDB().productDao().insertAll(roomModelList);
                    }
                    Date second = new Date();
                    Bundle bundle = new Bundle();
                    List<RoomModel> tempList = OrmApp.getDB().productDao().getAll();
                    bundle.putInt("count", tempList.size());
                    bundle.putLong("msek", second.getTime() - first.getTime());
                    emitter.onSuccess(bundle);
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
                singleSaveAllRoom.subscribeWith(CreateObserver());
                break;
            case R.id.btnSelectAllRoom:
                Single<Bundle> singleSelectAllRoom = Single.create((SingleOnSubscribe<Bundle>) emitter -> {
                    try {
                        Date first = new Date();
                        List<RoomModel> products = OrmApp.getDB().productDao().getAll();
                        Date second = new Date();
                        Bundle bundle = new Bundle();
                        bundle.putInt("count", products.size());
                        bundle.putLong("msek", second.getTime() - first.getTime());
                        emitter.onSuccess(bundle);
                    } catch (Exception e) {
                        emitter.onError(e);
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
                singleSelectAllRoom.subscribeWith(CreateObserver());
                break;
            case R.id.btnDeleteAllRoom:
                Single<Bundle> singleDeleteAllRoom = Single.create((SingleOnSubscribe<Bundle>)
                        emitter -> {
                    try {
                        List<RoomModel> products = OrmApp.getDB().productDao().getAll();
                        Date first = new Date();
                        OrmApp.getDB().productDao().getAll();
                        Date second = new Date();
                        Bundle bundle = new Bundle();
                        bundle.putInt("count", products.size());
                        bundle.putLong("msek", second.getTime() - first.getTime());
                        emitter.onSuccess(bundle);
                    } catch (Exception e) {
                        emitter.onError(e);
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
                singleDeleteAllRoom.subscribeWith(CreateObserver());
                break;
        }

    }


    private void downloadOneUrl(Call<List<RetrofitModel>> call) {
        call.enqueue(new Callback<List<RetrofitModel>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<List<RetrofitModel>> call, Response<List<RetrofitModel>> response) {
                if (response.isSuccessful()) {
                    RetrofitModel curModel;
                    mInfoTextView.append("\n Size = " + response.body().size()+
                            "\n-----------------");
                    for (int i = 0; i < response.body().size(); i++) {
                        curModel = response.body().get(i);
                        modelList.add(curModel);
                        mInfoTextView.append(
                                "\nLogin = " + curModel.getLogin() +
                                        "\nId = " + curModel.getId() +
                                        "\nURI = " + curModel.getAvatarUrl() +
                                        "\n-----------------");
                    }
                } else {
                    System.out.println("onResponse error: " + response.code());
                    mInfoTextView.setText("onResponse error: " + response.code());
                }
                progressBar.setVisibility(View.GONE);
            }
            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(Call<List<RetrofitModel>> call, Throwable t) {
                System.out.println("onFailure " + t);
                mInfoTextView.setText("onFailure " + t.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}




