package com.mod.hypixelmegahud.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.mod.hypixelmegahud.hud.bwHUD;
import com.mod.hypixelmegahud.player.playerData;

import java.io.IOException;

public class sessionStatsHUD {

    private static int finalKills = 0;
    private static int finalDeaths = 0;
    private static int kills = 0;
    private static int deaths = 0;
    private static int bedsBroken = 0;
    private static int bedsLost = 0;
    private static int wins = 0;
    private static int losses = 0;
    private final static String[] teamColors = {"White", "Gray", "Pink", "Aqua", "Red", "Blue", "Yellow", "Green"};

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type == RenderGameOverlayEvent.ElementType.TEXT) {
            this.drawHUD(event.resolution);
        }
    }


    @SubscribeEvent
    public void onPlayerChatReceived(ClientChatReceivedEvent event) throws IOException {
        String unformattedMessage = event.message.getUnformattedText();
        String formattedMessaage = event.message.getFormattedText();

        if(bwHUD.getIsInBWGame() && bwHUD.getIsGameStarted()) {
            String[] playerTokenFilter = {"§", "[SHOUT]", "[RED]", "[BLUE]", "[YELLOW]", "[GREEN]", "[WHITE]", "[GRAY]", "[PINK]", "[AQUA]"};
            for(String token : playerTokenFilter)
                if(unformattedMessage.contains(token)) {
                    return;
                }

            //Kill tracking
            if(unformattedMessage.contains(Minecraft.getMinecraft().thePlayer.getName())) {
                int playerIndex = unformattedMessage.indexOf(Minecraft.getMinecraft().thePlayer.getName());
                int opponentIndex = 0;
                for (playerData player : bwHUD.getServerPlayers()) {
                    if (unformattedMessage.contains(player.getName())) { opponentIndex = unformattedMessage.indexOf(player.getName()); }
                }

                if(unformattedMessage.contains("FINAL KILL!")) {
                    if (playerIndex > opponentIndex) { finalKills++; }
                    else if (playerIndex < opponentIndex) { finalDeaths++; }
                }
                else {
                    if (playerIndex > opponentIndex) { kills++; }
                    else if (playerIndex < opponentIndex) { deaths++; }
                }
            }

            //Bed tracking
            if (unformattedMessage.contains("BED DESTRUCTION > Your Bed")) { bedsLost++; }
            if (unformattedMessage.contains("BED DESTRUCTION >") && unformattedMessage.contains(Minecraft.getMinecraft().thePlayer.getName())) { bedsBroken++; }

            //Win tracking
            for(String color : teamColors) {
                if (unformattedMessage.contains(color + " - ") && unformattedMessage.contains(Minecraft.getMinecraft().thePlayer.getName())) { wins++; }
            }
        }
    }


    private void drawHUD(ScaledResolution resolution) {
        final FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        GlStateManager.pushMatrix();

        float scale = 0.7f;
        GlStateManager.scale(scale, scale, scale);

        int yShift = -125;
        int xShift = resolution.getScaledWidth() + 110;

        Gui.drawRect(10 + xShift, resolution.getScaledHeight() - 10 - yShift, 120 + xShift, resolution.getScaledHeight() - 150  - yShift, 0x66797c80);
        double FKDR = finalDeaths == 0 ? finalKills : Math.round( (double) finalKills / finalDeaths * 100.0) / 100.0;
        double KDR = deaths == 0 ? kills : Math.round( (double) kills / deaths * 100.0) / 100.0;
        double BBLR = bedsLost == 0 ? bedsBroken : Math.round( (double) bedsBroken / bedsLost * 100.0) / 100.0;

        fr.drawStringWithShadow("FKDR: " + FKDR, 15 + xShift, resolution.getScaledHeight() - 140 - yShift, 0xf5c400);
        fr.drawStringWithShadow("Final Kills: ", 15 + xShift, resolution.getScaledHeight() - 130 - yShift, 0xffffff);
        fr.drawStringWithShadow(String.valueOf(finalKills), 67 + xShift, resolution.getScaledHeight() - 130 - yShift, 0x00ff62);
        fr.drawStringWithShadow("Final Deaths: ", 15 + xShift, resolution.getScaledHeight() - 120 - yShift, 0xffffff);
        fr.drawStringWithShadow(String.valueOf(finalDeaths), 81 + xShift, resolution.getScaledHeight() - 120 - yShift, 0xed665f);

        fr.drawStringWithShadow("KDR: " + KDR, 15 + xShift, resolution.getScaledHeight() - 105 - yShift, 0xf5c400);
        fr.drawStringWithShadow("Kills: ", 15 + xShift, resolution.getScaledHeight() - 95 - yShift, 0xffffff);
        fr.drawStringWithShadow(String.valueOf(kills), 40 + xShift, resolution.getScaledHeight() - 95 - yShift, 0x00ff62);
        fr.drawStringWithShadow("Deaths: ", 15 + xShift, resolution.getScaledHeight() - 85 - yShift, 0xffffff);
        fr.drawStringWithShadow(String.valueOf(deaths), 54 + xShift, resolution.getScaledHeight() - 85 - yShift, 0xed665f);

        fr.drawStringWithShadow("BBLR: " + BBLR, 15 + xShift, resolution.getScaledHeight() - 70 - yShift, 0xf5c400);
        fr.drawStringWithShadow("Beds Broken: ", 15 + xShift, resolution.getScaledHeight() - 60 - yShift, 0xffffff);
        fr.drawStringWithShadow(String.valueOf(bedsBroken), 83 + xShift, resolution.getScaledHeight() - 60 - yShift, 0x00ff62);
        fr.drawStringWithShadow("Beds Lost: ", 15 + xShift, resolution.getScaledHeight() - 50 - yShift, 0xffffff);
        fr.drawStringWithShadow(String.valueOf(bedsLost), 70 + xShift, resolution.getScaledHeight() - 50 - yShift, 0xed665f);

        fr.drawStringWithShadow("Session Games: " + bwHUD.getSessionGames(), 15 + xShift, resolution.getScaledHeight() - 35 - yShift, 0xc7c2c1);
        fr.drawStringWithShadow("Wins: " + wins, 15 + xShift, resolution.getScaledHeight() - 25 - yShift, 0xc7c2c1);

        GlStateManager.popMatrix();
    }

}