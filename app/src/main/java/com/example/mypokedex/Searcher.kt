package com.example.mypokedex

class Searcher {
    fun search(minAttack: Int, minDefense: Int, minHealth: Int, categories: Array<String>): Set<Pokemon> {
        val pokeSet: MutableSet<Pokemon> = mutableSetOf()
        for (category in categories) {
            for (pokemon in Pokemon.typeMap.get(category)!!) {

            }
        }

        return pokeSet;
    }
}