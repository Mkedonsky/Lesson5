package ru.mkedonsky.lesson5;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RestAPI {
        @GET("users")
        Call<List<RetrofitModel>> loadUsers();
}
