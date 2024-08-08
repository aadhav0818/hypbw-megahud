package com.mod.hypixelmegahud.hud;

import com.mod.hypixelmegahud.core.numberSystems;
import com.mod.hypixelmegahud.core.potionData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class potionEffectHUD {

    private ArrayList<PotionEffect> potionEffects = new ArrayList<PotionEffect>();

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type == RenderGameOverlayEvent.ElementType.TEXT) {
            this.drawHUD(event.resolution);
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if(e.player.worldObj.isRemote) { fetchEffectsData(); }
    }

    public void fetchEffectsData() {
        potionEffects = new ArrayList<PotionEffect>(Minecraft.getMinecraft().thePlayer.getActivePotionEffects());
    }

    public String getPotionDisplayName(String forgeDisplayName) {
        String[] forgeDisplayNameArray = forgeDisplayName.split("\\.");
        for(int i = 0; i < potionData.forgeEffectNames.length; i++) {
            if(forgeDisplayNameArray[1].equals(potionData.forgeEffectNames[i])) { return potionData.potionEffectNamesMinecraft[i]; }
        }
        return "";
    }

    private void drawHUD(ScaledResolution resolution) {
        final FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        GlStateManager.pushMatrix();

        float scale = 0.7f;
        GlStateManager.scale(scale, scale, scale);

        int yShift = 12;
        int xShift = 257;

        if(potionEffects.size() != 0)
            Gui.drawRect(115 + xShift, 25 + yShift, 289 + xShift, 28 + 15 * potionEffects.size() + yShift, 0x66797c80);

        for(int i = 0; i < potionEffects.size(); i++) {
            String formattedPotionName = getPotionDisplayName(potionEffects.get(i).getEffectName());
            String amplifier = numberSystems.base10toRoman(potionEffects.get(i).getAmplifier() + 1);
            int totalSeconds = potionEffects.get(i).getDuration() / 20;
            int durationHours = totalSeconds / 60;
            int durationSeconds = totalSeconds % 60;
            String durationFormatted;
            if(durationSeconds < 10)
                durationFormatted = String.valueOf(durationHours) + ":0" + String.valueOf(durationSeconds);
            else
                durationFormatted = String.valueOf(durationHours) + ":" + String.valueOf(durationSeconds);

            if(totalSeconds <= 5) { fr.drawStringWithShadow(formattedPotionName + " " + amplifier + " : " + durationFormatted, 120 + xShift, 30 + yShift + 15 * i, 0xfb4141); }
            else { fr.drawStringWithShadow(formattedPotionName + " " + amplifier + " : " + durationFormatted, 120 + xShift, 30 + yShift + 15 * i, 0xFFFFFF); }

            if(totalSeconds == 5) { fr.drawStringWithShadow("" + (char) 0x25A0 + (char) 0x25A0 + (char) 0x25A0  + (char) 0x25A0  + (char) 0x25A0, 245 + xShift, 30 + yShift + 15 * i, 0xfb4141); }
            else if(totalSeconds == 4) { fr.drawStringWithShadow("" + (char) 0x25A0 + (char) 0x25A0 + (char) 0x25A0  + (char) 0x25A0, 245 + xShift, 30 + yShift + 15 * i, 0xfb4141); }
            else if(totalSeconds == 3) { fr.drawStringWithShadow("" + (char) 0x25A0 + (char) 0x25A0 + (char) 0x25A0, 245 + xShift, 30 + yShift + 15 * i, 0xfb4141); }
            else if(totalSeconds == 2) { fr.drawStringWithShadow("" + (char) 0x25A0 + (char) 0x25A0, 245 + xShift, 30 + yShift + 15 * i, 0xfb4141); }
            else if(totalSeconds == 1) { fr.drawStringWithShadow("" + (char) 0x25A0, 245 + xShift, 30 + yShift + 15 * i, 0xfb4141); }
            else if(totalSeconds > 3) { fr.drawStringWithShadow("" + (char) 0x25A0 + "" + (char) 0x25A0 + ""  + (char) 0x25A0 + "" + (char) 0x25A0 + "" + (char) 0x25A0 , 245 + xShift, 30 + yShift + 15 * i, 0x50C878); }
        }

        GlStateManager.popMatrix();

    }

}
