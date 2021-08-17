package com.cactuscoffee.magic.block;

import com.cactuscoffee.magic.BlockRegister;
import com.cactuscoffee.magic.MagicMod;
import com.cactuscoffee.magic.data.Element;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMagite extends BlockBreakable implements IRegistrableBlock {

    private final String name;

    public BlockMagite(Element element) {
        super(Material.GLASS, false);

        this.name = "magite_block_" + element.getName();

        setUnlocalizedName(name);
        setRegistryName(name);
        setCreativeTab(MagicMod.creativeTab);

        setSoundType(SoundType.GLASS);
        setHardness(1.5F);
        setHarvestLevel("pickaxe", 0);
        setLightOpacity(5);

        BlockRegister.addBlockToList(this);
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public void registerItemModel(Item itemBlock) {
        MagicMod.proxy.registerItemRenderer(itemBlock, 0, name);
    }

    @Override
    public Item getItemBlock() {
        return new ItemBlock(this).setRegistryName(getRegistryName());
    }
}
