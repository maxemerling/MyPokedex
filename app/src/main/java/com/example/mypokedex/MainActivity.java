package com.example.mypokedex;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.mypokedex.Pokemon.allTypes;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private Button spinButton, menuButton;
    private View menuCard, recyclerCard;
    private EditText attackNumber, defenseNumber, healthNumber;

    private volatile int min_attack, min_defense, min_health;
    public static volatile boolean[] selected;

    private ObjectAnimator up, down;
    private Animation fadeIn, fadeOut;

    private boolean showingMenu = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //do this so that keyboard doesn't shift layout
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        //set up pokemon data
        Pokemon.init(getResources());

        //type selector

        selected = new boolean[allTypes.length];
        for (int i = 0; i < selected.length; i++) {
            selected[i] = true;
        }

        spinButton = findViewById(R.id.spinButton);
        spinButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Filter by Type");

                final boolean[] currSelection = selected.clone();

                builder.setMultiChoiceItems(allTypes, currSelection, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which, boolean isChecked) {
                        currSelection[which] = isChecked;
                    }
                });

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selected = currSelection;
                    }
                });

                builder.setNeutralButton("Cancel", null);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        //minimum attack, defense, health selectors

        attackNumber = findViewById(R.id.attackNumber);
        defenseNumber = findViewById(R.id.defenseNumber);
        healthNumber = findViewById(R.id.healthNumber);


        //recycler view
        recyclerView = findViewById(R.id.recycler);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new PokeAdapter(Pokemon.pokeSet.toArray(new Pokemon[0]), this);
        recyclerView.setAdapter(adapter);

        recyclerCard = findViewById(R.id.recyclerCard);
        menuCard = findViewById(R.id.slidingMenu);

        up = ObjectAnimator.ofFloat(recyclerCard, "translationY", -500);
        up.setDuration(200);

        down = ObjectAnimator.ofFloat(recyclerCard, "translationY", 0);
        down.setDuration(200);

        fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(100);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                menuCard.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setDuration(20);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                menuCard.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (showingMenu) {
                    hideMenu();
                } else {
                    showMenu();
                }
                showingMenu = !showingMenu;
            }
        });
    }

    private void hideMenu() {
        menuCard.startAnimation(fadeOut);
        up.start();
    }

    private void showMenu() {
        down.start();
        menuCard.startAnimation(fadeIn);
    }
}
