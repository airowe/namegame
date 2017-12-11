package com.willowtreeapps.namegame.network.api;

import com.willowtreeapps.namegame.network.api.model.Person;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface NameGameApi {
    @GET("/api/v1.0/profiles")
    Call<List<Person>> getProfiles();
}
