package com.mod.hypixelmegahud;


import com.mod.hypixelmegahud.hud.bwHUD;

import com.mod.hypixelmegahud.hud.potionEffectHUD;
import com.mod.hypixelmegahud.hud.sessionStatsHUD;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
        modid = HypixelMegaHUD.MODID,
        name = HypixelMegaHUD.MODNAME,
        version = HypixelMegaHUD.VERSION)

public class HypixelMegaHUD { // select ExampleMod and hit shift+F6 to rename it

    public static final String MODID = "Hypixel MegaHUD";      // the id of your mod, it should never change, it is used by forge and servers to identify your mods
    public static final String MODNAME = "Hypixel MegaHUD";// the name of your mod
    public static final String VERSION = "0.4.2";           // the current version of your mod

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {

    }

    @EventHandler
    public void init(FMLInitializationEvent event) {

        MinecraftForge.EVENT_BUS.register(new bwHUD());
        MinecraftForge.EVENT_BUS.register(new sessionStatsHUD());
        MinecraftForge.EVENT_BUS.register(new potionEffectHUD());

        if (Loader.isModLoaded("patcher")) { }

    }



}