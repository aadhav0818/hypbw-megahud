package com.mod.hypixelmegahud;


import com.mod.hypixelmegahud.hud.bwHUD;

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

    public static final String MODID = "examplemod";      // the id of your mod, it should never change, it is used by forge and servers to identify your mods
    public static final String MODNAME = "Hypixel MegaHUD";// the name of your mod
    public static final String VERSION = "0.4.1";           // the current version of your mod

    // this method is one entry point of you mod
    // it is called by forge when minecraft is starting
    // it is called before the other methods below
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {

    }

    @EventHandler
    public void init(FMLInitializationEvent event) {

        // the ExampleKeybind has a method with the @SubscribeEvent annotation
        // for that code to run, that class needs to be registered on the MinecraftForge EVENT_BUS
        // register your other EventHandlers here


        MinecraftForge.EVENT_BUS.register(new bwHUD());

        if (Loader.isModLoaded("patcher")) {
            // this code will only run if the mod with id "patcher" is loaded
            // you can use it to load or not while modules of your mod that depends on other mods
        }

    }



}