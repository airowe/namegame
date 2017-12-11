package com.willowtreeapps.namegame.util;

import java.util.Arrays;
import java.util.List;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by adamrowe on 12/9/17.
 */

@Singleton
@Component
public interface Instructions {
    String PLAY_MODE_BASIC_INSTRUCTIONS = "You will be presented with six faces of WillowTree employees and asked to identify the listed name.";
    String PLAY_MODE_MATT_INSTRUCTIONS = "You will be presented with six faces of WillowTree employees named Matt" +
            " and asked to identify the listed name.";
    String PLAY_MODE_REVERSE_INSTRUCTIONS = "You will be presented with one face of a WillowTree employee named Matt and asked to choose " +
            "the correct name.";

    List<String> playModeInstructions = Arrays.asList(PLAY_MODE_BASIC_INSTRUCTIONS, PLAY_MODE_MATT_INSTRUCTIONS, PLAY_MODE_REVERSE_INSTRUCTIONS);
}
