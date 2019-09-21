package com.example.mypokedex;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.mypokedex.Pokemon.allTypes;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager, gridLayoutManager;

    private Button spinButton, menuButton, switchButton, resetButton;
    private View menuCard, recyclerCard;
    private SearchView searchView;
    private EditText attackNumber, defenseNumber, healthNumber;

    public static volatile boolean[] selected;

    private ObjectAnimator up, down;
    private Animation fadeIn, fadeOut;

    private boolean showingMenu = true, grid = false;

    private volatile Pokemon[] liveData;

    public static volatile MainActivity currInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currInstance = this;

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
                        searchAndDisplay();
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
                searchAndDisplay();
                //dynamicSearchAndDisplay(searchView.getQuery().toString());
                return false;
            }
        });

        //minimum attack, defense, health selectors

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchAndDisplay();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        attackNumber = findViewById(R.id.attackNumber);
        attackNumber.addTextChangedListener(textWatcher);
        defenseNumber = findViewById(R.id.defenseNumber);
        defenseNumber.addTextChangedListener(textWatcher);
        healthNumber = findViewById(R.id.healthNumber);
        healthNumber.addTextChangedListener(textWatcher);

        //reset button
        resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
            }
        });

        //switch button

        gridLayoutManager = new GridLayoutManager(this, 2);

        switchButton = findViewById(R.id.switchButton);
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (grid) {
                    switchButton.setText("GRID");
                    recyclerView.setLayoutManager(layoutManager);

                } else {
                    switchButton.setText("LIST");
                    recyclerView.setLayoutManager(gridLayoutManager);
                }
                grid = !grid;
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

    private synchronized void searchAndDisplay() {
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
        searchAndDisplay();
    }

    private synchronized void initSelected() {
        selected = new boolean[allTypes.length];
        for (int i = 0; i < selected.length; i++) {
            selected[i] = true;
        }
    }

    public void openPokePage(String pokemon) {
        Intent intent = new Intent(this, PokePage.class);
        intent.putExtra(PokePage.DATA, Pokemon.pokeMap.get(pokemon).getData());
        startActivity(intent);
    }
}