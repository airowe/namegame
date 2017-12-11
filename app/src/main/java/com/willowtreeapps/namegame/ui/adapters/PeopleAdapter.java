package com.willowtreeapps.namegame.ui.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.willowtreeapps.namegame.R;
import com.willowtreeapps.namegame.network.api.model.Person;
import com.willowtreeapps.namegame.util.CircleBorderTransform;
import com.willowtreeapps.namegame.util.OnPersonClickListener;

import java.util.List;

/**
 * Created by adamrowe on 12/5/17.
 */
public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.ViewHolder> {

    private List<Person> peopleList;
    private final OnPersonClickListener listener;
    private int layoutId;
    private Picasso picasso;

    private static final Interpolator OVERSHOOT = new OvershootInterpolator();

    private static final String PROTOCOL = "https:";
    private static int imageSize;

    private static boolean clickable = true;

    public PeopleAdapter(int layoutId, List<Person> peopleList, Picasso picasso, OnPersonClickListener listener) {
        this.layoutId = layoutId;
        this.peopleList = peopleList;
        this.picasso = picasso;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return peopleList == null ? 0 : peopleList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new ViewHolder(layoutId, view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {
        holder.bind(picasso, peopleList.get(listPosition), listener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        int layoutId;
        ImageView imageView;
        TextView textView;
        ViewHolder(int layoutId, View itemView) {
            super(itemView);
            this.layoutId = layoutId;
            if(layoutId == R.layout.person_row)
                textView = itemView.findViewById(R.id.person_row_item);
            else if(layoutId == R.layout.face_row) {
                imageView = itemView.findViewById(R.id.grid_face);
                imageView.setBackgroundColor(Color.TRANSPARENT);
                imageView.setScaleX(0);
                imageView.setScaleY(0);
            }
        }

        void bind(final Picasso picasso, final Person person, final OnPersonClickListener listener) {
            if(layoutId == R.layout.person_row) {
                textView.setText(person.getFullName());

                itemView.setOnClickListener(v -> {
                    if(clickable)
                        toggleBackgroundColor(textView, listener.onPersonClick(person));
                });
            }
            else if(layoutId == R.layout.face_row) {
                picasso.load(PROTOCOL + person.getHeadshot().getUrl())
                        .placeholder(R.drawable.ic_face_white_48dp)
                        .resize(imageSize, imageSize)
                        .transform(new CircleBorderTransform())
                        .into(imageView, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {}

                            @Override
                            public void onError() {
                                Log.e("Picasso Error", person.getFullName());
                            }
                        });

                animateFaceIn(imageView,getLayoutPosition());

                itemView.setOnClickListener(v -> {
                    if(clickable)
                        toggleBackgroundColor(imageView, listener.onPersonClick(person));
                });
            }

        }

        void toggleBackgroundColor(View view, boolean correct) {
            if(correct) {
                clickable = false;
                view.setBackgroundColor(view.getResources().getColor(R.color.alphaGreen));
            }
            else {
                clickable = true;
                view.setBackgroundColor(view.getResources().getColor(R.color.incorrectRed));
            }

        }
    }

    public void setPeopleList(List<Person> peopleList) {
        this.peopleList = peopleList;
    }

    /**
     * @param imgSize
     */
    public void setImageSize(int imgSize) {
        imageSize = imgSize;
    }

    public void setClickable(boolean canClick) {
        clickable = canClick;
    }

    private static void animateFaceIn(ImageView face, int position) {
        face.animate().scaleX(1).scaleY(1).setStartDelay(800 + 120 * position).setInterpolator(OVERSHOOT).start();
    }
}

