package com.mod.hypixelmegahud.gamecore;

public class serverAuth {

    final String[] validTypes = {"\"BEDWARS_FOUR_FOUR\"", "\"BEDWARS_FOUR_THREE\"", "\"BEDWARS_EIGHT_TWO\"", "\"BEDWARS_EIGHT_ONE\"", "\"BEDWARS_TWO_FOUR\""};
    public serverAuth() { }

    public boolean isValidGameType(String gameType) {
        for(String type: validTypes) {
            if(gameType.equals(type)) { return true; }
        }
        return false;
    }
}
