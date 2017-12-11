package com.willowtreeapps.namegame.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.willowtreeapps.namegame.R;
import com.willowtreeapps.namegame.core.NameGameApplication;

import java.util.List;

public class NameGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.name_game_activity);
        NameGameApplication.get(this).component().inject(this);

        if (savedInstanceState != null) {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            if(fragments != null && !fragments.isEmpty()) {
                if(fragments.get(0) instanceof NameGameFragment) {
                    NameGameFragment nameGameFragment = (NameGameFragment) fragments.get(0);
                    nameGameFragment.onResume();

                    /*FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container, nameGameFragment, getString(R.string.name_game_frag_tag));
                    fragmentTransaction.commit();*/
                }
            }
            else showGameModeChooser();
        }
        else showGameModeChooser();
    }

    private void showGameModeChooser() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        PlayModeFragment fragment = new PlayModeFragment();
        fragmentTransaction.replace(R.id.container, fragment, getString(R.string.play_mode_frag_tag));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                if(getSupportFragmentManager().getBackStackEntryCount() > 0)
                    getSupportFragmentManager().popBackStack();
                else
                    super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
