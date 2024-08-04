package com.example.examplemod.hud;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.sun.jna.platform.unix.X11;
import jdk.nashorn.internal.parser.JSONParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.gui.Gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.server.console.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import scala.Array;
import scala.tools.nsc.typechecker.ContextErrors;
import scala.util.parsing.json.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import com.example.examplemod.gamecore.serverAuth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.examplemod.player.playerData;

public class bwHUD {

    ArrayList<playerData> serverPlayers = new ArrayList<playerData>();
    boolean isInBWGame = false;
    ArrayList<String> playerNames = new ArrayList<String>();

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) throws IOException {
        // this method is called multiple times per frame, you want to filter it
        // by checking the event type to only render your HUD once per frame
        if (event.type == RenderGameOverlayEvent.ElementType.TEXT) {
            this.drawHUD(event.resolution);
        }
    }

    @SubscribeEvent
    public void onPlayerChatRecieved(ClientChatReceivedEvent event)
    {
        String message = event.message.getUnformattedText();
        System.out.println(message);

        JsonParser parser = new JsonParser();
        JsonObject locraw = parser.parse(String.valueOf(message)).getAsJsonObject();
        String gameType = locraw.get("gametype").toString();

        serverAuth gameAuthenticator = new serverAuth();

        if(gameType.equals("BEDWARS")) {
            if(locraw.has("mode")) {
                String BWGameMode = locraw.get("mode").toString();
                if(gameAuthenticator.isValidGameType(gameType)) {
                    isInBWGame = true;
                }
            }
        }
    }

    @SubscribeEvent
    public void entityJoinWorld(EntityJoinWorldEvent e) throws IOException
    {
        if(!e.world.isRemote) {
            playerNames.add(e.entity.getName());

            for (String playerName : playerNames) {
                if (e.entity != null && e.entity instanceof EntityPlayer) {

                    if (Minecraft.getMinecraft().thePlayer != null && e.entity == Minecraft.getMinecraft().thePlayer) {
                        Minecraft.getMinecraft().thePlayer.sendChatMessage("/locraw");
                    }

                    BufferedReader JSONScanner;
                    String JSONScannerLine;
                    StringBuffer playerJSON = new StringBuffer();

                    String playerURL = "https://api.hypixel.net/player?key=f8fd15bb-9838-4859-849d-bc7272f788ed&name=" + playerName;
                    boolean uniqueRequest = true;

                    for(playerData serverPlayer: serverPlayers) {
                        if (serverPlayer.getName().equals(playerName)) {
                            uniqueRequest = false;
                            break;
                        }
                    }

                    if(uniqueRequest) {

                        try {
                            URL req = new URL(playerURL);
                            HttpURLConnection connection = (HttpURLConnection) req.openConnection();
                            connection.setRequestMethod("GET");
                            connection.setConnectTimeout(5000);
                            connection.setReadTimeout(5000);
                            int status = connection.getResponseCode();
                            System.out.print(status + " ");

                            JSONScanner = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                            while ((JSONScannerLine = JSONScanner.readLine()) != null) {
                                playerJSON.append(JSONScannerLine);
                            }

                            JSONScanner.close();
                            connection.disconnect();

                            JsonParser parser = new JsonParser();
                            JsonObject playerJSONConverted = parser.parse(String.valueOf(playerJSON)).getAsJsonObject();

                            JsonObject playerJSONplayer = playerJSONConverted.getAsJsonObject("player");
                            playerData currentPlayer;

                            if (playerJSONplayer == null) {
                                currentPlayer = new playerData("nickedplayer100", 0, "\"UNRANKED\"", true, 0);
                            } else {
                                JsonObject playerJSONachievements = playerJSONplayer.getAsJsonObject("achievements");
                                JsonObject playerStats = playerJSONplayer.getAsJsonObject("stats");
                                JsonObject playerBedwars = playerStats.getAsJsonObject("Bedwars");

                                double playerFinalKills = playerBedwars.get("final_kills_bedwars").getAsDouble();
                                double playerFinalDeaths = playerBedwars.get("final_deaths_bedwars").getAsDouble();
                                double playerFKDR;
                                if (playerFinalDeaths == 0)
                                    playerFKDR = playerFinalKills;
                                playerFKDR = Math.round(playerFinalKills / playerFinalDeaths * 100.0) / 100.0;

                                String playerBWLevel = playerJSONachievements.get("bedwars_level").toString();
                                String playerRank = "\"UNRANKED\"";

                                if (playerJSONplayer.has("newPackageRank")) {
                                    playerRank = playerJSONplayer.get("newPackageRank").toString();
                                    if (playerJSONplayer.has("monthlyPackageRank")) {
                                        if (playerJSONplayer.get("monthlyPackageRank").toString().equals("\"NONE\""))
                                            playerRank = playerJSONplayer.get("newPackageRank").toString();
                                        else
                                            playerRank = "\"MVP_PLUS_PLUS\"";
                                    }
                                }

                                currentPlayer = new playerData(playerName, Integer.parseInt(playerBWLevel), playerRank, false, playerFKDR);
                                System.out.println(playerRank);
                            }

                            serverPlayers.add(currentPlayer);
                        } catch (IOException ex) {
                            System.out.println("BAD REQUEST");
                        }
                    }
                }
            }
        }

    }

    private void drawHUD(ScaledResolution resolution) throws IOException {

        // when drawing a HUD, the coordinates (x, y) represent a point on your screen
        // coordinates (0, 0) is top left of your screen,
        // coordinates (screenWidth, screenHeight) is bottom right of your screen

        final FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        GlStateManager.pushMatrix();

        float scale = 0.7f;
        GlStateManager.scale(scale, scale, scale);

        final int top = 0;
        final int left = 0;
        final int bottom = resolution.getScaledHeight();
        final int right = resolution.getScaledWidth();

        int HUDHeight = 40 + serverPlayers.size() * 10;
        Gui.drawRect(10, 20, 210, HUDHeight, 0x66797c80);

        for(int i = 0; i < serverPlayers.size(); i++)
        {
            String playerDataRender = serverPlayers.get(i).getName();
            String starDataRender = "[" + serverPlayers.get(i).getStarsAsString() + (char) 0x272B + "]";
            String FKDRDataRender = "FKDR: " + serverPlayers.get(i).getFDKRAsString();

            fr.drawStringWithShadow(starDataRender.toString(), 20, 30 + i * 10, serverPlayers.get(i).getStarHex());
            fr.drawStringWithShadow(playerDataRender.toString(), 65, 30 + i * 10, serverPlayers.get(i).getHex());
            fr.drawStringWithShadow(FKDRDataRender.toString(), 150, 30 + i * 10, serverPlayers.get(i).getFKDRHex());

        }

        GlStateManager.popMatrix();

    }

}