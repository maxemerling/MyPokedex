package com.example.mypokedex;

import android.accessibilityservice.FingerprintGestureController;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import static com.example.mypokedex.Pokemon.allTypes;
import static com.example.mypokedex.Pokemon.init;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private Button spinButton, menuButton, searchButton, resetButton;
    private View menuCard, recyclerCard;
    private SearchView searchView;
    private EditText attackNumber, defenseNumber, healthNumber;

    public static volatile boolean[] selected;

    private ObjectAnimator up, down;
    private Animation fadeIn, fadeOut;

    private boolean showingMenu = true;

    private volatile Pokemon[] liveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //do this so that keyboard doesn't shift layout
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        //set up pokemon data
        Pokemon.init(getResources());

        //type selector
        initSelected();

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
                        staticSearchAndDisplay();
                    }
                });

                builder.setNeutralButton("Cancel", null);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        //searcher
        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                staticSearchAndDisplay();
                //dynamicSearchAndDisplay(searchView.getQuery().toString());
                return false;
            }
        });

        //search button

        searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                staticSearchAndDisplay();
            }
        });

        //minimum attack, defense, health selectors

        attackNumber = findViewById(R.id.attackNumber);
        defenseNumber = findViewById(R.id.defenseNumber);
        healthNumber = findViewById(R.id.healthNumber);

        //reset button
        resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
            }
        });

        //recycler view
        recyclerView = findViewById(R.id.recycler);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        liveData = Pokemon.pokeSet.toArray(new Pokemon[0]);

        adapter = new PokeAdapter(liveData, this);
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
                    menuButton.setText("O");
                } else {
                    showMenu();
                    menuButton.setText("X");
                }
                showingMenu = !showingMenu;
            }
        });
    }

    private synchronized void hideMenu() {
        menuCard.startAnimation(fadeOut);
        up.start();
    }

    private synchronized void showMenu() {
        down.start();
        menuCard.startAnimation(fadeIn);
    }

    private synchronized void staticSearchAndDisplay() {
        String searchStr = searchView.getQuery().toString();
        int minAttack, minDefense, minHealth;

        String attack = attackNumber.getText().toString();
        if (attack.isEmpty()) {
            minAttack = 0;
        } else {
            minAttack = Integer.parseInt(attack);
        }

        String defense = defenseNumber.getText().toString();
        if (defense.isEmpty()) {
            minDefense = 0;
        } else {
            minDefense = Integer.parseInt(defense);
        }

        String health = healthNumber.getText().toString();
        if (health.isEmpty()) {
            minHealth = 0;
        } else {
            minHealth = Integer.parseInt(health);
        }

        liveData = Searcher.Companion.search(searchStr, minAttack, minDefense, minHealth, Searcher.Companion.getCategories());

        updateData(liveData);
    }

    private synchronized void updateData(Pokemon[] data) {
        ((PokeAdapter) adapter).updateData(data);
    }

    private synchronized void reset() {
        initSelected();
        attackNumber.setText("");
        defenseNumber.setText("");
        healthNumber.setText("");
        staticSearchAndDisplay();
    }

    private synchronized void initSelected() {
        selected = new boolean[allTypes.length];
        for (int i = 0; i < selected.length; i++) {
            selected[i] = true;
        }
    }
}
