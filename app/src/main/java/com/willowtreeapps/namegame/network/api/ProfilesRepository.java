package com.willowtreeapps.namegame.network.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.willowtreeapps.namegame.network.api.model.Metadata;
import com.willowtreeapps.namegame.network.api.model.Person;
import com.willowtreeapps.namegame.network.api.model.Profiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfilesRepository {

    @NonNull
    private final NameGameApi api;
    @NonNull
    private List<Listener> listeners = new ArrayList<>(1);
    @Nullable
    private Profiles profiles;

    public ProfilesRepository(@NonNull NameGameApi api, Listener... listeners) {
        this.api = api;
        if (listeners != null) {
            this.listeners = new ArrayList<>(Arrays.asList(listeners));
        }
        load();
    }

    private void load() {
        this.api.getProfiles().enqueue(new Callback<List<Person>>() {
            @Override
            public void onResponse(Call<List<Person>> call, Response<List<Person>> response) {
                List<Person> people = response.body();
                profiles = new Profiles(people,new Metadata(1,5, people.size()));
                for (Listener listener : listeners) {
                    listener.onLoadFinished(profiles);
                }
            }

            @Override
            public void onFailure(Call<List<Person>> call, Throwable t) {
                for (Listener listener : listeners) {
                    listener.onError(t);
                }
            }
        });
    }

    public void register(@NonNull Listener listener) {
        if (listeners.contains(listener)) throw new IllegalStateException("Listener is already registered.");
        listeners.add(listener);
        if (profiles != null) {
            listener.onLoadFinished(profiles);
        }
    }

    public void unregister(@NonNull Listener listener) {
        listeners.remove(listener);
    }

    public interface Listener {
        void onLoadFinished(@NonNull Profiles people);
        void onError(@NonNull Throwable error);
    }

}
