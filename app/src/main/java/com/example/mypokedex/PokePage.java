package com.example.mypokedex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class PokePage extends AppCompatActivity {

    private ImageView image;
    private TextView name, number, attack, defense, health, spAttack, spDefense, species, speed, total, type;
    public static final int NAME = 0, NUMBER = 1, ATTACK = 2, DEFENSE = 3, HEALTH = 4, SPATTACK = 5, SPDEFENSE = 6, SPECIES = 7, SPEED = 8, TOTAL = 9, TYPE = 10;
    public static final String DATA = "DATA";

    private Button search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poke_page);

        image = findViewById(R.id.profileImage);
        name = findViewById(R.id.profileName);
        number = findViewById(R.id.profileNumber);
        attack = findViewById(R.id.profileAttack);
        defense = findViewById(R.id.profileDefense);
        health = findViewById(R.id.profileHealth);
        spAttack = findViewById(R.id.profileSpAttack);
        spDefense = findViewById(R.id.profileDefense);
        species = findViewById(R.id.profileSpecies);
        speed = findViewById(R.id.profileSpeed);
        total = findViewById(R.id.profileTotal);
        type = findViewById(R.id.profileType);

        String[] data = getIntent().getStringArrayExtra(DATA);
        name.setText(getText(R.string.name) + ": " + data[0]);
        number.setText(getText(R.string.number) + ": " + data[1]);
        attack.setText(getText(R.string.attack) + ": " + data[2]);
        defense.setText(getText(R.string.defense) + ": " + data[3]);
        health.setText(getText(R.string.health) + ": " + data[4]);
        spAttack.setText(getText(R.string.spAttack) + ": " + data[5]);
        spDefense.setText(getText(R.string.spDefense) + ": " + data[6]);
        species.setText(getText(R.string.species) + ": " + data[7]);
        speed.setText(getText(R.string.speed) + ": " + data[8]);
        total.setText(getText(R.string.total) + ": " + data[9]);
        type.setText(getText(R.string.type) + ": " + data[10]);

        image.setImageResource(R.drawable.pokeball);

        search = findViewById(R.id.searchButton);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String escapedQuery = null;
                try {
                    escapedQuery = URLEncoder.encode(name.getText().toString(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Uri uri = Uri.parse("http://www.google.com/#q=" + escapedQuery);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }
}
