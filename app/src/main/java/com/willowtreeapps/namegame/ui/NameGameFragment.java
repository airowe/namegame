package com.willowtreeapps.namegame.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.willowtreeapps.namegame.R;
import com.willowtreeapps.namegame.core.ListRandomizer;
import com.willowtreeapps.namegame.core.NameGameApplication;
import com.willowtreeapps.namegame.network.api.ProfilesRepository;
import com.willowtreeapps.namegame.network.api.model.Person;
import com.willowtreeapps.namegame.network.api.model.Profiles;
import com.willowtreeapps.namegame.ui.adapters.PeopleAdapter;
import com.willowtreeapps.namegame.util.OnPersonClickListener;
import com.willowtreeapps.namegame.util.Ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import static com.willowtreeapps.namegame.util.PlayModeUtilities.PLAY_MODE_BASIC;
import static com.willowtreeapps.namegame.util.PlayModeUtilities.PLAY_MODE_MATT;
import static com.willowtreeapps.namegame.util.PlayModeUtilities.PLAY_MODE_REVERSE;

public class NameGameFragment extends Fragment implements OnPersonClickListener, View.OnClickListener {

    @Inject
    Picasso picasso;
    @Inject
    ListRandomizer listRandomizer;
    @Inject
    ProfilesRepository profilesRepository;

    ProfilesRepository.Listener profilesListener;

    RecyclerView namesListView;
    StaggeredGridLayoutManager namesListViewLayoutManager;
    PeopleAdapter peopleAdapter;
    PeopleAdapter namesListAdapter;

    TextView nameView;
    TextView answerStatusView;
    Button playAgainButton;

    RecyclerView gridview;
    GridLayoutManager facesLayoutManager;

    private List<Person> people = new ArrayList<>();
    private List<Person> selectedPeople = new ArrayList<>();
    private Person personToGuess;

    private int playMode;
    private int numberOfPeopleToDisplay = 6;

    private static final String incorrectMessage = "Incorrect! That's ";
    private static final String correctMessage = "Correct! That's ";
    private static final String whoIsMessage = "Who is ";

    private int incorrectGuesses = 0;
    private int totalGuesses = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NameGameApplication.get(getActivity()).component().inject(this);
        setRetainInstance(true);

        Bundle bundle = getArguments();
        if(bundle != null)
            playMode = bundle.getInt("play_mode", PLAY_MODE_BASIC);

        namesListAdapter = new PeopleAdapter(R.layout.person_row, selectedPeople, picasso, this);
        peopleAdapter = new PeopleAdapter(R.layout.face_row, selectedPeople, picasso, this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.name_game_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        gridview = view.findViewById(R.id.face_container);
        nameView = view.findViewById(R.id.name_view);
        answerStatusView = view.findViewById(R.id.answer_status);

        playAgainButton = view.findViewById(R.id.play_again_button);
        playAgainButton.setOnClickListener(this);

        namesListView = view.findViewById(R.id.names_list);

        gridview.setAdapter(peopleAdapter);
        gridview.setHasFixedSize(true);

        if(savedInstanceState != null) {
            facesLayoutManager.onRestoreInstanceState(savedInstanceState.getParcelable("gridViewLayoutManagerState"));
            if(namesListViewLayoutManager != null) {
                namesListViewLayoutManager.onRestoreInstanceState(savedInstanceState.getParcelable("namesViewLayoutManagerState"));
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable("gridViewLayoutManagerState", facesLayoutManager.onSaveInstanceState());
        if(namesListView != null && namesListView.getLayoutManager() != null)
            savedInstanceState.putParcelable("namesViewLayoutManagerState", namesListViewLayoutManager.onSaveInstanceState());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        setUpViews();

        if(people.isEmpty()) {
            getProfiles();
        }
        else {
            if(selectedPeople.isEmpty() || personToGuess == null) {
                getSelectedPeople();
            }
            setImages();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(profilesRepository != null && profilesListener != null) {
            profilesRepository.unregister(profilesListener);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //Since RecyclerView number of columns is changed, all previous selections must be reset
        resetData();
        int imageSizeDp = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 75 : 100;
        int imageSize = (int) Ui.convertDpToPixel(imageSizeDp, getContext());
    }

    /**
     * Initialize recycler views and their corresponding adapters
     */
    private void setUpViews() {
        int numberOfColumns = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 3 : 2;

        if(playMode == PLAY_MODE_REVERSE) {
            namesListViewLayoutManager = new StaggeredGridLayoutManager(numberOfColumns, LinearLayoutManager.VERTICAL);
            numberOfPeopleToDisplay = 5;
            namesListView.setLayoutManager(namesListViewLayoutManager);
            namesListView.setHasFixedSize(true);
            namesListView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
            namesListView.setItemAnimator(new DefaultItemAnimator());
            namesListView.setAdapter(namesListAdapter);
            namesListView.setVisibility(View.VISIBLE);

            nameView.setVisibility(View.GONE);

            numberOfColumns = 1;
        }
        facesLayoutManager = new GridLayoutManager(getContext(), numberOfColumns);

        gridview.setLayoutManager(facesLayoutManager);
    }

    /**
     * Create ProfilesRepository Listener and register it to call the server retrieve people
     */
    private void getProfiles() {

        profilesListener = new ProfilesRepository.Listener() {
            @Override
            public void onLoadFinished(@NonNull Profiles profiles) {
                people = profiles.getPeople();
                getSelectedPeople();
                setImages();
            }

            @Override
            public void onError(@NonNull Throwable error) {
                Log.e("Profiles",error.getMessage());
                Snackbar.make(gridview,"Error getting profiles",Snackbar.LENGTH_LONG);
            }
        };

        profilesRepository.register(profilesListener);
    }

    /**
     * This methods retrieves the people to be displayed in the recycler views from the list of people
     * downloaded from the server
     */
    private void getSelectedPeople() {
        if(playMode != PLAY_MODE_MATT) {
            selectedPeople = listRandomizer.ensureValidHeadshots(people,numberOfPeopleToDisplay);
        }
        else // if(playMode == PLAY_MODE_MATT)
        {
            selectedPeople = listRandomizer.pickCertainNamedPeople(people, numberOfPeopleToDisplay, "Matt", false);
        }
        personToGuess = listRandomizer.pickOne(selectedPeople);
    }

    /**
     * A method for setting the images of people into the imageviews
     */
    private void setImages() {
        int imageSizeDp = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 80 : 100;
        int imageSize;
        if(playMode == PLAY_MODE_REVERSE) {
            peopleAdapter.setPeopleList(Collections.singletonList(personToGuess));
            imageSizeDp = 250;
            imageSize = (int) Ui.convertDpToPixel(imageSizeDp, getContext());
        }
        else {
            peopleAdapter.setPeopleList(selectedPeople);
            imageSize = (int) Ui.convertDpToPixel(imageSizeDp, getContext());
        }
        peopleAdapter.setImageSize(imageSize);
        peopleAdapter.notifyDataSetChanged();

        setNames();
    }

    /**
     * A method for setting the names of people into the recyclerview/text view depending on mode
     */
    private void setNames() {
        if(playMode == PLAY_MODE_REVERSE) {
            namesListAdapter.setPeopleList(selectedPeople);
            namesListAdapter.notifyDataSetChanged();
        }
        else //everything else
        {
            String nameViewText = whoIsMessage + personToGuess.getFullName() + "?";
            nameView.setEnabled(false);
            nameView.setText(nameViewText);
        }

    }


    /**
     * A method to handle when a person is selected
     *
     * @param person The person that was selected
     */
    @Override
    public boolean onPersonClick(Person person) {
        answerStatusView.setVisibility(View.VISIBLE);
        boolean correctPerson = person.equals(personToGuess);

        if(correctPerson) {
            totalGuesses = ++totalGuesses + incorrectGuesses;
            Snackbar.make(nameView, totalGuesses + " guesses to get that one right", Snackbar.LENGTH_LONG).show();
        }
        else {
            incorrectGuesses++;
        }

        decorateResponse(person, correctPerson);

        return correctPerson;
    }

    /**
     * Decorate views based on whether the person selected was the personToGuess
     * @param person The person clicked on in the PeopleAdapter
     * @param correctPerson Value representing whether the person selected is the personToGuess
     */
    private void decorateResponse(Person person, boolean correctPerson) {
        String message = person.getFullName();
        if(correctPerson) {
            message = correctMessage + message;
            //gridview.setClickable(false);
            //gridview.setEnabled(false);
        }
        else {
            message = incorrectMessage + message;
        }

        answerStatusView.setBackgroundColor(getResources().getColor(getColorResource(correctPerson)));
        answerStatusView.setText(message);
    }

    /**
     * This method determines the correct color resource to apply to selected RecyclerView cell
     * @param correctPerson Value representing whether correct person was selected
     * @return colorResource The applicable color resource based on selection
     */
    private int getColorResource(boolean correctPerson) {
        int colorResource = R.color.incorrectRed;
        if(correctPerson) {
            colorResource = R.color.alphaGreen;
        }
        return colorResource;
    }

    /**
     * This method resets all variables and views associated with game play
     */
    private void resetGame() {
        resetData();
        personToGuess = null;
        selectedPeople.clear();
        peopleAdapter.setPeopleList(selectedPeople);
        peopleAdapter.notifyDataSetChanged();
        gridview.removeAllViewsInLayout();
        answerStatusView.setVisibility(View.GONE);

        if(playMode == PLAY_MODE_REVERSE) {
            namesListAdapter.setPeopleList(selectedPeople);
            namesListAdapter.setClickable(true);
            namesListAdapter.notifyDataSetChanged();
            namesListView.removeAllViewsInLayout();
        }
    }

    private void resetData() {
        peopleAdapter.setClickable(true);
        totalGuesses = 0;
        incorrectGuesses = 0;
    }

    /**
     * This method gets the game play UI ready for a new round of the same mode
     * @param view The view clicked on by the user
     */
    @Override
    public void onClick(View view) {
        if(view.getId() == playAgainButton.getId()) {
            resetGame();
            getSelectedPeople();
            setImages();
        }
    }
}
