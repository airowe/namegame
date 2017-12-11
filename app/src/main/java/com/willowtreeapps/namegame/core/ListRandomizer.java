package com.willowtreeapps.namegame.core;

import android.support.annotation.NonNull;

import com.willowtreeapps.namegame.network.api.model.Person;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class ListRandomizer {

    @NonNull
    private final Random random;

    public ListRandomizer(@NonNull Random random) {
        this.random = random;
    }

    @NonNull
    public <T> T pickOne(@NonNull List<T> list) {
        return list.get(random.nextInt(list.size()));
    }

    @NonNull
    public <T> List<T> pickN(@NonNull List<T> list, int n) {
        if (list.size() == n) return list;
        if (n == 0) return Collections.emptyList();
        List<T> pickFrom = new ArrayList<>(list);
        List<T> picks = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            picks.add(pickFrom.remove(random.nextInt(pickFrom.size())));
        }
        return picks;
    }

    @NonNull
    public List<Person> ensureValidHeadshots(@NonNull List<Person> people, int n) {
        if (n == 0) return Collections.emptyList();
        List<Person> pickFrom = stripNullAndDefaultHeadshots(people);
        List<Person> picks = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            picks.add(pickFrom.remove(random.nextInt(pickFrom.size())));
        }
        return picks;
    }

    @NonNull
    private List<Person> stripNullAndDefaultHeadshots(List<Person> people) {
        List<Person> cleanPeople = new ArrayList<>(people);
        for (Iterator<Person> it = cleanPeople.iterator(); it.hasNext();) {
            Person person = it.next();
            String url = person.getHeadshot().getUrl();
            if (url == null || url.contains("featured-image-TEST1")) {
                it.remove();
            }
        }
        return cleanPeople;
    }

    @NonNull
    public List<Person> pickCertainNamedPeople(@NonNull List<Person> people, int n, String name, boolean strict) {
        if (n == 0) return Collections.emptyList();
        List<Person> pickFrom = selectBasedOnName(people, name, strict);
        List<Person> picks = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            picks.add(pickFrom.remove(random.nextInt(pickFrom.size())));
        }
        return picks;
    }

    @NonNull
    private List<Person> selectBasedOnName(List<Person> people, String name, boolean strict) {
        List<Person> cleanPeople = new ArrayList<>(people);
        for (Iterator<Person> it = cleanPeople.iterator(); it.hasNext();) {
            Person person = it.next();
            String firstName = person.getFirstName();
            if (firstName == null) it.remove();
            else {
                if(strict) { if (!firstName.equals(name)) it.remove(); }
                else { if (!firstName.startsWith(name)) it.remove(); }
            }
        }
        return cleanPeople;
    }
}
