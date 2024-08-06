package com.mod.hypixelmegahud.hud;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mod.hypixelmegahud.gamecore.serverAuth;
import com.mod.hypixelmegahud.cache.playerCache;
import com.mod.hypixelmegahud.player.playerData;
import com.mod.hypixelmegahud.tokens.apiAuth;
import com.sun.org.apache.xerces.internal.impl.xpath.XPath;
import com.typesafe.config.ConfigException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.Sys;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class bwHUD {

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private ArrayList<playerData> serverPlayers = new ArrayList<playerData>();
    private boolean isInBWGame = false;
    private boolean gameStarted = false;
    private ArrayList<String> playerNames = new ArrayList<String>();
    private playerCache sessionCache = new playerCache();

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) throws IOException {
        if (event.type == RenderGameOverlayEvent.ElementType.TEXT) {
            this.drawHUD(event.resolution);
        }
    }

    @SubscribeEvent
    public void onPlayerChatReceived(ClientChatReceivedEvent event) throws IOException {
        boolean isJSON = true;
        String message = event.message.getUnformattedText();
        System.out.println(message);

        if(message.contains("You are sending commands too fast! Please slow down.")) {
            event.setCanceled(true);
        }
        else if(message.contains("Protect your bed and destroy the enemy beds.") && isInBWGame) {
            gameStarted = true;
        }
        else if(message.contains("has quit!") && isInBWGame) {
            String[] quitMessage = message.split(" ");
            String username = quitMessage[0];
            removeDisconnectedPlayers(username);
        }
        else {
            try {
                JsonParser parser = new JsonParser();
                JsonObject locraw = parser.parse(String.valueOf(message)).getAsJsonObject();
                String gameType = locraw.get("gametype").toString();

                serverAuth gameAuthenticator = new serverAuth();
                isInBWGame = false;

                if (gameType.equals("\"BEDWARS\"")) {
                    if (locraw.has("mode")) {
                        String BWGameMode = locraw.get("mode").toString();
                        if (gameAuthenticator.isValidGameType(BWGameMode)) {
                            isInBWGame = true;
                            loadExistingPlayers();
                        }
                    }
                }

                if(!isInBWGame) {
                    serverPlayers.clear();
                    playerNames.clear();
                }

                event.setCanceled(true);

            } catch (JsonSyntaxException e) {
                isJSON = false;
                System.out.println("ERROR: JSONSYNTAX EXCEPTION");
            } catch (IllegalStateException e) {
                isJSON = false;
                System.out.println("ERROR: STRING PROVIDED IS NOT A JSON");
            } catch (NullPointerException e) {
                isJSON = false;
                System.out.println("ERROR: NULL POINTER");
            }
        }
    }

    public void removeDisconnectedPlayers(String disconnectedPlayerName) throws IOException {
        if(isInBWGame && serverPlayers != null) {
            for(playerData serverPlayer : serverPlayers) {
                if(serverPlayer.getName().equals(disconnectedPlayerName)) {
                    serverPlayers.remove(serverPlayer);
                    playerNames.remove(disconnectedPlayerName);
                }
            }
        }
    }

    public void loadExistingPlayers() {
        List<EntityPlayer> lobbyPlayers = Minecraft.getMinecraft().theWorld.playerEntities;
        for (final EntityPlayer lobbyPlayer : lobbyPlayers) {
            if (!playerNames.contains(lobbyPlayer.getName())) {
                playerNames.add(lobbyPlayer.getName());
            }

            boolean uniqueRequest = true;

            for (playerData serverPlayer : serverPlayers) {
                if (serverPlayer.getName().equals(lobbyPlayer.getName())) {
                    uniqueRequest = false;
                    break;
                }
            }

            if (uniqueRequest) {
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        fetchPlayerDataAsync(lobbyPlayer.getName());
                    }
                });
            }

        }


    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload e) {
        isInBWGame = false;
        serverPlayers.clear();
        playerNames.clear();
        gameStarted = false;
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent e) {
        try {
            if (e.entity instanceof EntityPlayer && e.world.isRemote && !gameStarted) {
                if (e.entity.equals(Minecraft.getMinecraft().thePlayer)) {
                    executorService.schedule(new Runnable() {
                        @Override
                        public void run() {
                            Minecraft.getMinecraft().thePlayer.sendChatMessage("/locraw");
                        }
                    }, 500, TimeUnit.MILLISECONDS);
                }
                if (isInBWGame && !gameStarted) {
                    if (Minecraft.getMinecraft().theWorld != null) {
                        if(!playerNames.contains(e.entity.getName())) { playerNames.add(e.entity.getName()); }
                    }

                    boolean uniqueRequest = true;

                    for (playerData serverPlayer : serverPlayers) {
                        if (serverPlayer.getName().equals(e.entity.getName())) {
                            uniqueRequest = false;
                            break;
                        }
                    }

                    final String playerName = e.entity.getName();

                    if (uniqueRequest) {
                        executorService.execute(new Runnable() {
                            @Override
                            public void run() {
                                fetchPlayerDataAsync(playerName);
                            }
                        });
                    }

                }
            }
        } catch (NullPointerException ex) {
            System.out.println("NULL POINTER");
        }
    }

    public void fetchPlayerDataAsync(String playerName) {
        apiAuth apiAuthenticator = new apiAuth();
        String key = apiAuthenticator.getKey();
        String playerURL = "https://api.hypixel.net/player?key=" + key + "&name=" + playerName;

        for (playerData player : serverPlayers) {
            if(player.getName().equals(playerName)) { return; }
        }

        if(this.sessionCache.isPlayerCached(playerName)) {
            serverPlayers.add(sessionCache.getPlayerFromName(playerName));
            System.out.println("CACHED???");
            return;
        }

        try {
            URL req = new URL(playerURL);
            HttpURLConnection connection = (HttpURLConnection) req.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int status = connection.getResponseCode();
            System.out.println(status + " STATUS");

            playerData currentPlayer;

            if(status >= 400) {
                System.out.println("BAD RESPONSE CODE!");
                currentPlayer = new playerData(playerName, 1, "\"UNRANKED\"", true, 0);
                sessionCache.addPlayerToCache(currentPlayer);
                serverPlayers.add(currentPlayer);
                return;           }

            BufferedReader JSONScanner = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder playerJSON = new StringBuilder();
            String JSONScannerLine;

            while ((JSONScannerLine = JSONScanner.readLine()) != null) {
                playerJSON.append(JSONScannerLine);
            }

            JSONScanner.close();
            connection.disconnect();

            JsonParser parser = new JsonParser();
            JsonObject playerJSONConverted = parser.parse(String.valueOf(playerJSON)).getAsJsonObject();
            JsonObject playerJSONplayer = playerJSONConverted.getAsJsonObject("player");

            JsonObject playerJSONachievements = playerJSONplayer.getAsJsonObject("achievements");
            JsonObject playerStats = playerJSONplayer.getAsJsonObject("stats");
            JsonObject playerBedwars = playerStats.getAsJsonObject("Bedwars");

            double playerFinalKills = playerBedwars.get("final_kills_bedwars").getAsDouble();
            double playerFinalDeaths = playerBedwars.get("final_deaths_bedwars").getAsDouble();
            double playerFKDR = playerFinalDeaths == 0 ? playerFinalKills : Math.round(playerFinalKills / playerFinalDeaths * 100.0) / 100.0;

            String playerBWLevel = playerJSONachievements.get("bedwars_level").toString();
            String playerRank = "\"UNRANKED\"";

            if (playerJSONplayer.has("newPackageRank")) {
                playerRank = playerJSONplayer.get("newPackageRank").toString();
                if(playerRank.equals("NONE")) { playerRank = "UNRANKED"; }
                if (playerJSONplayer.has("monthlyPackageRank")) {
                    if (playerJSONplayer.get("monthlyPackageRank").toString().equals("\"NONE\"")) {
                        playerRank = playerJSONplayer.get("newPackageRank").toString();
                    } else {
                        playerRank = "\"MVP_PLUS_PLUS\"";
                    }
                }
            }

            currentPlayer = new playerData(playerName, Integer.parseInt(playerBWLevel), playerRank, false, playerFKDR);
            serverPlayers.add(currentPlayer);
            sessionCache.addPlayerToCache(currentPlayer);


        } catch (IOException ex) {
            System.out.println("BAD RESPONSE CODE!");
            playerData currentPlayer = new playerData(playerName, 1, "\"UNRANKED\"", true, 0);
            sessionCache.addPlayerToCache(currentPlayer);
            serverPlayers.add(currentPlayer);

        } catch (NullPointerException ex) {
            System.out.println("NULL POINTER");
        }
    }

    private void drawHUD(ScaledResolution resolution) {
        final FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        GlStateManager.pushMatrix();

        float scale = 0.7f;
        GlStateManager.scale(scale, scale, scale);

        int HUDHeight = 20;
        if(serverPlayers.size() != 0)
            HUDHeight = 40 + serverPlayers.size() * 10;

        Gui.drawRect(10, 20, 255, HUDHeight, 0x66797c80);

        for (int i = 0; i < serverPlayers.size(); i++) {
            String playerDataRender = serverPlayers.get(i).getName();
            String starDataRender = "[" + serverPlayers.get(i).getStarsAsString() + (char) 0x272B + "]";
            String FKDRDataRender = "FKDR: " + serverPlayers.get(i).getFDKRAsString();
            String nickDataRender = "[" + (char) 0x2713 + "]";
            if(serverPlayers.get(i).isNicked()) { nickDataRender = "[" + (char) 0x2717 + "]"; }

            fr.drawStringWithShadow(starDataRender, 20, 30 + i * 10, serverPlayers.get(i).getStarHex());
            fr.drawStringWithShadow(playerDataRender, 65, 30 + i * 10, serverPlayers.get(i).getHex());
            fr.drawStringWithShadow(FKDRDataRender, 165, 30 + i * 10, serverPlayers.get(i).getFKDRHex());
            fr.drawStringWithShadow(nickDataRender, 225, 30 + i * 10, serverPlayers.get(i).getNickHex());
        }

        GlStateManager.popMatrix();
    }
}
