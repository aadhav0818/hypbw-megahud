package com.mod.hypixelmegahud.cache;

import com.mod.hypixelmegahud.player.playerData;
import java.util.ArrayList;

public class playerCache {

    private static ArrayList<playerData> playerDataCache = new ArrayList<playerData>();

    public playerCache() { }

    public void addPlayerToCache(playerData player) { playerDataCache.add(player); }

    public void clearCache() {
        playerDataCache.clear();
    }

    public ArrayList<playerData> getPlayerDataCache() {
        return playerDataCache;
    }

    public boolean isPlayerCached(String playerName) {
        if(playerDataCache == null) { return false; }
        for(playerData player : playerDataCache) {
            if(player.getName().equals(playerName)) {
                return true;
            }
        }
        return false;
    }

    public playerData getPlayerFromName(String playerName) {
        for(playerData player : playerDataCache) {
            if (player.getName().equals(playerName)) {
                return player;
            }
        }
        return new playerData("0", 0, "0", false, 0.0);
    }
}
