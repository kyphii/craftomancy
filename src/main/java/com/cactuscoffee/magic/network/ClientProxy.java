package com.cactuscoffee.magic.network;

import com.cactuscoffee.magic.MagicMod;
import com.cactuscoffee.magic.control.KeyBinds;
import com.cactuscoffee.magic.control.KeyInputHandler;
import com.cactuscoffee.magic.entity.*;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderVex;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        RenderingRegistry.registerEntityRenderingHandler(EntityMagicOrb.class, RenderMagicOrb::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityStealthMine.class, RenderInvisible::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityGuardStar.class, RenderGuardStar::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityVexFriendly.class, RenderVex::new);

        KeyBinds.register();
        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
    }

    @Override
    public void registerItemRenderer(Item item, int meta, String id) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(MagicMod.MODID + ":" + id, "inventory"));
    }
}