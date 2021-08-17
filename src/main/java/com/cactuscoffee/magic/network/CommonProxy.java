package com.cactuscoffee.magic.network;

import com.cactuscoffee.magic.BlockRegister;
import com.cactuscoffee.magic.ItemRegister;
import com.cactuscoffee.magic.data.Sounds;
import com.cactuscoffee.magic.entity.EntityGuardStar;
import com.cactuscoffee.magic.entity.EntityMagicOrb;
import com.cactuscoffee.magic.entity.EntityStealthMine;
import com.cactuscoffee.magic.entity.EntityVexFriendly;
import com.cactuscoffee.magic.gui.GuiHandler;
import com.cactuscoffee.magic.MagicMod;
import com.cactuscoffee.magic.recipe.RecipeArcaneTome;
import com.cactuscoffee.magic.recipe.RecipeMagicRobes;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

@Mod.EventBusSubscriber
public class CommonProxy {
    public void preInit(FMLPreInitializationEvent e) {
    }

    public void init(FMLInitializationEvent e) {
        NetworkRegistry.INSTANCE.registerGuiHandler(MagicMod.instance, new GuiHandler());
    }

    public void postInit(FMLPostInitializationEvent e) {
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        event.getRegistry().register(new RecipeArcaneTome().setRegistryName("recipe_arcane_tome"));
        event.getRegistry().register(new RecipeMagicRobes().setRegistryName("recipe_magic_robes"));
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        BlockRegister.register(event.getRegistry());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        ItemRegister.register(event.getRegistry());
        BlockRegister.registerItemBlocks(event.getRegistry());
    }

    @SubscribeEvent
    public static void registerItemModels(ModelRegistryEvent event) {
        ItemRegister.registerModels();
        BlockRegister.registerModels();
    }

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        EntityEntry entry = EntityEntryBuilder.create()
                .entity(EntityMagicOrb.class)
                .id(new ResourceLocation(MagicMod.MODID, "magic_orb"), 0)
                .name(MagicMod.MODID + ":magic_orb")
                .tracker(64, 1, true)
                .build();
        event.getRegistry().register(entry);

        EntityEntry stealthMine = EntityEntryBuilder.create()
                .entity(EntityStealthMine.class)
                .id(new ResourceLocation(MagicMod.MODID, "stealth_mine"), 1)
                .name(MagicMod.MODID + ":stealth_mine")
                .tracker(64, 10, false)
                .build();
        event.getRegistry().register(stealthMine);

        EntityEntry guardStar = EntityEntryBuilder.create()
                .entity(EntityGuardStar.class)
                .id(new ResourceLocation(MagicMod.MODID, "guard_star"), 2)
                .name(MagicMod.MODID + ":guard_star")
                .tracker(64, 1, true)
                .build();
        event.getRegistry().register(guardStar);

        EntityEntry vexFriendly = EntityEntryBuilder.create()
                .entity(EntityVexFriendly.class)
                .id(new ResourceLocation(MagicMod.MODID, "vex_friendly"), 3)
                .name(MagicMod.MODID + ":vex_friendly")
                .tracker(64, 1, true)
                .build();
        event.getRegistry().register(vexFriendly);
    }

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        for (SoundEvent s : Sounds.soundEventList) {
            event.getRegistry().register(s);
        }
    }

    public void registerItemRenderer(Item item, int meta, String id) {}
}