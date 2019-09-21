package com.example.mypokedex

class Searcher {
    companion object {
        fun search(searchString: String, minAttack: Int, minDefense: Int, minHealth: Int, categories: Array<String>): Array<Pokemon> {
            val pokeSet: MutableList<Pokemon> = mutableListOf()
            for (category in categories) {
                for (pokemon in Pokemon.typeMap.get(category)!!) {
                    if (!pokeSet.contains(pokemon) && pokemon.attack >= minAttack && pokemon.defense >= minDefense && pokemon.health >= minHealth &&
                            pokemon.name.toLowerCase().indexOf(searchString.toLowerCase().trim()) == 0) {
                        pokeSet.add(pokemon)
                    }
                }
            }

            return pokeSet.toTypedArray()
        }

        fun getCategories(): Array<String> {
            val types: MutableList<String> = mutableListOf()
            for (i in MainActivity.selected.indices) {
                if (MainActivity.selected[i]) {
                    types.add(Pokemon.allTypes[i])
                }
            }
            return types.toTypedArray()
        }
    }
}