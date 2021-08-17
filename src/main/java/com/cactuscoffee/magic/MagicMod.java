package com.cactuscoffee.magic;

import com.cactuscoffee.magic.network.CommonProxy;
import com.cactuscoffee.magic.network.ModPacket;
import com.cactuscoffee.magic.network.ModPacketHandler;
import com.cactuscoffee.magic.world.WorldGen;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = MagicMod.MODID, name = MagicMod.MODNAME, version = MagicMod.MODVERSION)
public class MagicMod {

    public static final String MODID = "tfmagic2";
    public static final String MODNAME = "Craftomancy";
    public static final String MODVERSION = "1.0.0";

    @Mod.Instance(MODID)
    public static MagicMod instance;

    @SidedProxy(serverSide = "com.cactuscoffee.magic.network.CommonProxy", clientSide = "com.cactuscoffee.magic.network.ClientProxy")
    public static CommonProxy proxy;

    public static SimpleNetworkWrapper network;

    public static final ModCreativeTab creativeTab = new ModCreativeTab();
    public static final ModCreativeTab creativeTabSecret = null;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        GameRegistry.registerWorldGenerator(new WorldGen(), 3);

        proxy.preInit(event);

        network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
        network.registerMessage(ModPacketHandler.class, ModPacket.class, 0, Side.SERVER);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    private static class ModCreativeTab extends CreativeTabs {
        public ModCreativeTab() {
            super(MODID);
        }

        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(ItemRegister.arcaneSoul);
        }
    }
}