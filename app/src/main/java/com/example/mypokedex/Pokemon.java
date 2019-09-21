package com.example.mypokedex;

import android.content.res.Resources;
import android.util.JsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Pokemon {

    public static final Set<Pokemon> pokeSet = new HashSet<>();
    public static final Map<String, Pokemon> pokeMap = new HashMap<>();
    public static final Map<String, Set<Pokemon>> typeMap = new HashMap<>();
    public static String[] allTypes;


    private String name, number, spAttack, spDefense, species, speed, total, type;
    private int attack, defense, health;
    private String[] types;

    private static final String ATTACK = "Attack", DEFENSE = "Defense", HEALTH = "HP", TYPES = "Type", NUMBER = "#", SPATTACK = "Sp. Atk", SPDEFENSE = "Sp. Def", SPECIES = "Species", SPEED = "Speed", TOTAL = "Total", TYPE = "Type";

    public Pokemon(String name) {
        this.name = name;
    }

    public static void init(Resources resources) {
        InputStream is = resources.openRawResource(R.raw.poke_data);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }

            JSONObject jsonObject = new JSONObject(writer.toString());

            Iterator<String> keys = jsonObject.keys();

            while(keys.hasNext()) {
                Pokemon curr = new Pokemon(keys.next());
                JSONObject object = jsonObject.getJSONObject(curr.name);
                curr.setAttack(object.getInt(ATTACK));
                curr.setDefense(object.getInt(DEFENSE));
                curr.setHealth(object.getInt(HEALTH));

                curr.number = object.getString(NUMBER);
                curr.spAttack = object.getString(SPATTACK);
                curr.spDefense = object.getString(SPDEFENSE);
                curr.species = object.getString(SPECIES);
                curr.speed = object.getString(SPEED);
                curr.total = object.getString(TOTAL);
                curr.type = object.getString(TYPE);


                JSONArray jsonArray = object.getJSONArray(TYPES);
                curr.setTypes(new String[jsonArray.length()]);
                for (int i = 0; i < curr.types.length; i++) {
                    curr.types[i] = jsonArray.getString(i);
                }

                pokeSet.add(curr);
                pokeMap.put(curr.name, curr);
                for (String type : curr.types) {
                    if (!typeMap.containsKey(type)) {
                        typeMap.put(type, new HashSet<Pokemon>());
                    }
                    typeMap.get(type).add(curr);
                }
            }

            allTypes = typeMap.keySet().toArray(new String[0]);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    public String getName() {
        return name;
    }

    public String getURL() {
        return "https://img.pokemondb.net/artwork/" + name.toLowerCase() + ".jpg";
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getHealth() {
        return health;
    }

    public String[] getData() {
        String[] data = new String[11];
        data[PokePage.NAME] = name;
        data[PokePage.NUMBER] = number;
        data[PokePage.ATTACK] = attack + "";
        data[PokePage.DEFENSE] = defense + "";
        data[PokePage.HEALTH] = health + "";
        data[PokePage.SPATTACK] = spAttack;
        data[PokePage.SPDEFENSE] = spDefense;
        data[PokePage.SPECIES] = species;
        data[PokePage.SPEED] = speed;
        data[PokePage.TOTAL] = total;
        data[PokePage.TYPE] = type.replaceAll("\\[", "").replaceAll("\\]","").replaceAll("\"", "").replaceAll(",", ", ");

        return data;
    }
}