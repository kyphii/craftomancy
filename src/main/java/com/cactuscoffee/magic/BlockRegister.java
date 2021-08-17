package com.cactuscoffee.magic;

import com.cactuscoffee.magic.block.*;
import com.cactuscoffee.magic.data.Element;
import com.cactuscoffee.magic.tileentity.TileEntityEssenceExtractor;
import com.cactuscoffee.magic.tileentity.TileEntityManaCollector;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

public class BlockRegister {

    private static List<IRegistrableBlock> blockList = new ArrayList<>();

    /*============== ADD BLOCKS HERE ==============*/

    public static ModBlock manaCrystalOre = new BlockManaCrystalOre();

    public static ModBlock magiteOreRed = new BlockMagiteOre(Element.RED);
    public static ModBlock magiteOreYellow = new BlockMagiteOre(Element.YELLOW);
    public static ModBlock magiteOreGreen = new BlockMagiteOre(Element.GREEN);
    public static ModBlock magiteOreBlue = new BlockMagiteOre(Element.BLUE);
    public static ModBlock magiteOreBlack = new BlockMagiteOre(Element.BLACK);
    public static ModBlock magiteOreWhite = new BlockMagiteOre(Element.WHITE);

    public static BlockMagicFlower flowerScorchbloom = new BlockMagicFlower("flower_scorchbloom", Element.RED);
    public static BlockMagicFlower flowerFluteweed = new BlockMagicFlower("flower_fluteweed", Element.YELLOW);
    public static BlockMagicFlower flowerRockgrass = new BlockMagicFlower("flower_rockgrass", Element.GREEN);
    public static BlockMagicFlower flowerFrostbell = new BlockMagicFlower("flower_frostbell", Element.BLUE);
    public static BlockMagicFlower flowerGloomthorn = new BlockMagicFlower("flower_gloomthorn", Element.BLACK);
    public static BlockMagicFlower flowerBrightroot = new BlockMagicFlower("flower_brightroot", Element.WHITE);

    public static BlockMagite magiteBlockRed = new BlockMagite(Element.RED);
    public static BlockMagite magiteBlockYellow = new BlockMagite(Element.YELLOW);
    public static BlockMagite magiteBlockGreen = new BlockMagite(Element.GREEN);
    public static BlockMagite magiteBlockBlue = new BlockMagite(Element.BLUE);
    public static BlockMagite magiteBlockBlack = new BlockMagite(Element.BLACK);
    public static BlockMagite magiteBlockWhite = new BlockMagite(Element.WHITE);

    public static BlockEssenceExtractor essenceExtractor = new BlockEssenceExtractor();
    public static BlockArcaneInfuser arcaneInfuser = new BlockArcaneInfuser();
    public static BlockManaCollector manaCollector = new BlockManaCollector();

    public static BlockSealedArcana sealedArcana = new BlockSealedArcana();

    /*=============================================*/

    public static void register(IForgeRegistry<Block> registry) {
        for (IRegistrableBlock b : blockList) {
            registry.register((Block) b);
        }

        //Tile Entities
        GameRegistry.registerTileEntity(TileEntityEssenceExtractor.class, essenceExtractor.getRegistryName());
        GameRegistry.registerTileEntity(TileEntityManaCollector.class, manaCollector.getRegistryName());
    }

    public static void registerItemBlocks(IForgeRegistry<Item> registry) {
        for (IRegistrableBlock b : blockList) {
            registry.register(b.getItemBlock());
        }
    }

    public static void registerModels() {
        for (IRegistrableBlock b : blockList) {
            b.registerItemModel(Item.getItemFromBlock((Block) b));
        }
    }

    public static void addBlockToList(IRegistrableBlock block) {
        blockList.add(block);
    }
}
