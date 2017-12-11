package com.willowtreeapps.namegame.util;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by adamrowe on 12/5/17.
 */
@Singleton
@Component
public interface PlayModeUtilities {

    int PLAY_MODE_BASIC = 0;
    int PLAY_MODE_MATT = 1;
    int PLAY_MODE_REVERSE = 2;
}
