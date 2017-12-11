package com.willowtreeapps.namegame.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.willowtreeapps.namegame.R;
import com.willowtreeapps.namegame.core.NameGameApplication;
import com.willowtreeapps.namegame.util.Instructions;

import static com.willowtreeapps.namegame.util.PlayModeUtilities.*;

public class PlayModeFragment extends Fragment implements View.OnClickListener {

    Button basicModeButton;
    Button mattModeButton;
    Button reverseModeButton;

    FloatingActionButton playButton;

    TextView instructionsTextView;

    int playMode = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NameGameApplication.get(getActivity()).component().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.game_chooser_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        basicModeButton = view.findViewById(R.id.basic_mode_button);
        mattModeButton = view.findViewById(R.id.matt_mode_button);
        reverseModeButton = view.findViewById(R.id.reverse_mode_button);

        basicModeButton.setOnClickListener(this);
        mattModeButton.setOnClickListener(this);
        reverseModeButton.setOnClickListener(this);

        playButton = view.findViewById(R.id.play_mode_button);
        playButton.setOnClickListener(this);

        instructionsTextView = view.findViewById(R.id.instructionsTextView);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if(viewId == playButton.getId()) {
            if(playMode != -1)
               showFaces();
            else
                Snackbar.make(instructionsTextView, R.string.choose_mode,Snackbar.LENGTH_LONG).show();
        }
        else if(viewId == basicModeButton.getId()) {
            playMode = PLAY_MODE_BASIC;
        }
        else if(viewId == mattModeButton.getId()) {
            playMode = PLAY_MODE_MATT;
        }
        else if(viewId == reverseModeButton.getId()) {
            playMode = PLAY_MODE_REVERSE;
        }

        showInstructions();
    }

    private void showInstructions() {
        if(playMode != -1)
            instructionsTextView.setText(Instructions.playModeInstructions.get(playMode));
    }

    private void showFaces() {
        Bundle bundle = new Bundle();
        bundle.putInt("play_mode",playMode);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        NameGameFragment fragment = new NameGameFragment();
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.container, fragment, getString(R.string.name_game_frag_tag));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
