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
import net.minecraft.client.gui.Gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.server.console.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import scala.tools.nsc.typechecker.ContextErrors;
import scala.util.parsing.json.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) throws IOException {
        // this method is called multiple times per frame, you want to filter it
        // by checking the event type to only render your HUD once per frame
        if (event.type == RenderGameOverlayEvent.ElementType.TEXT) {
            this.drawHUD(event.resolution);
        }
    }

    @SubscribeEvent
    public void entityJoinWorld(EntityJoinWorldEvent e) throws IOException
    {
        if (e.entity != null && e.entity instanceof EntityPlayer) {
            BufferedReader JSONScanner;
            String JSONScannerLine;
            StringBuffer playerJSON = new StringBuffer();

            String playerURL = "https://api.hypixel.net/player?key=f52b3e5e-a03e-46fb-bdf3-07734141ea47&name=thiccbears";

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

                if(playerJSONplayer == null) {
                    currentPlayer = new playerData("nickedplayer100", 0, "\"UNRANKED\"", true);
                }
                else {
                    JsonObject playerJSONachievements = playerJSONplayer.getAsJsonObject("achievements");
                    String playerBWLevel = playerJSONachievements.get("bedwars_level").toString();
                    String playerRank = "\"UNRANKED\"";

                    if(playerJSONplayer.has("newPackageRank")) {
                        playerRank = playerJSONplayer.get("newPackageRank").toString();
                        if(playerJSONplayer.has("monthlyPackageRank"))
                        {
                            if(playerJSONplayer.get("monthlyPackageRank").toString().equals("\"NONE\""))
                                playerRank = playerJSONplayer.get("newPackageRank").toString();
                            else
                                playerRank = "\"MVP_PLUS_PLUS\"";
                        }
                    }

                    currentPlayer = new playerData("thiccbears", Integer.parseInt(playerBWLevel), playerRank, false);
                    System.out.println(playerRank);
                }

                serverPlayers.add(currentPlayer);
            } catch (IOException ex) {
                System.out.println("BAD REQUEST");
            }
        }
    }

    private void drawHUD(ScaledResolution resolution) throws IOException {

        // when drawing a HUD, the coordinates (x, y) represent a point on your screen
        // coordinates (0, 0) is top left of your screen,
        // coordinates (screenWidth, screenHeight) is bottom right of your screen

        final FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        fr.FONT_HEIGHT = 6;


        final int top = 0;
        final int left = 0;
        final int bottom = resolution.getScaledHeight();
        final int right = resolution.getScaledWidth();


        Gui.drawRect(10, 20, 110, 120, 0x66797c80);

        for(int i = 0; i < serverPlayers.size(); i++)
        {
            String playerDataRender = serverPlayers.get(i).getName() + ": [" + serverPlayers.get(i).getStarsAsString() + (char) 0x272B + "]";
            fr.drawStringWithShadow(playerDataRender.toString(), 20, 30, serverPlayers.get(i).getHex());

        }

    }

}