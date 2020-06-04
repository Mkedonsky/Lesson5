package ru.mkedonsky.lesson5;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RestAPIforUser {
    @GET("users/{user}")
    Call<List<RetrofitModel>> loadUsers(@Path("user") String user);

}
