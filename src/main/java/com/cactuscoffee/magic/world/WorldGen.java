package com.cactuscoffee.magic.world;

import com.cactuscoffee.magic.BlockRegister;
import com.cactuscoffee.magic.block.BlockMagicFlower;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.*;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;
import java.util.Set;

public class WorldGen implements IWorldGenerator {

    public static final int MAGITE_PER_CHUNK = 5;
    public static final int MAGITE_SIZE_MIN = 4;
    public static final int MAGITE_SIZE_MAX = 10;

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() == 0) {
            generateOverworld(world, chunkX, chunkZ, chunkGenerator, chunkProvider, random);
        }
    }

    private void generateOverworld(World world, int chunkX, int chunkZ, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider, Random random) {
        int x = chunkX * 16;
        int z = chunkZ * 16;

        Biome biome = world.getBiome(new BlockPos(x + 8, 0, z + 8));

        //Mana Crystal Ore
        generateOre(BlockRegister.manaCrystalOre, world, x, z, 8, 16, 12, 1, 127, random);

        //Magite
        generateOre(BlockRegister.magiteOreRed, world, x, z,
                MAGITE_SIZE_MIN, MAGITE_SIZE_MAX, MAGITE_PER_CHUNK, 1, 16, random);
        generateOre(BlockRegister.magiteOreYellow, world, x, z,
                MAGITE_SIZE_MIN, MAGITE_SIZE_MAX, MAGITE_PER_CHUNK, 80, 127, random);
        if (biomeIs(biome, BiomeDictionary.Type.OCEAN, BiomeDictionary.Type.BEACH, BiomeDictionary.Type.RIVER)) {
            generateOre(BlockRegister.magiteOreBlue, world, x, z,
                    MAGITE_SIZE_MIN, MAGITE_SIZE_MAX, MAGITE_PER_CHUNK, 24, 63, random);
        }
        else {
            generateOre(BlockRegister.magiteOreGreen, world, x, z,
                    MAGITE_SIZE_MIN, MAGITE_SIZE_MAX, MAGITE_PER_CHUNK, 24, 80, random);
        }
        if (biomeIs(biome, BiomeDictionary.Type.SNOWY)) {
            generateOre(BlockRegister.magiteOreWhite, world, x, z,
                    MAGITE_SIZE_MIN, MAGITE_SIZE_MAX, MAGITE_PER_CHUNK, 32, 80, random);
        }
        else {
            generateOre(BlockRegister.magiteOreBlack, world, x, z,
                    MAGITE_SIZE_MIN, MAGITE_SIZE_MAX, MAGITE_PER_CHUNK, 1, 63, random);
        }

        //Flowers
        generateMagicFlower(BlockRegister.flowerRockgrass, world, random, x, z, 80);
        if (biomeIs(biome, BiomeDictionary.Type.SNOWY)) {
            generateMagicFlower(BlockRegister.flowerFrostbell, world, random, x, z, 50);
        }
        else if (biomeIs(biome, BiomeDictionary.Type.CONIFEROUS)) {
            generateMagicFlower(BlockRegister.flowerFrostbell, world, random, x, z, 20);
        }
        else if (biomeIs(biome, BiomeDictionary.Type.MOUNTAIN)) {
            generateMagicFlower(BlockRegister.flowerFluteweed, world, random, x, z, 40);
        }
        else if (biomeIs(biome, BiomeDictionary.Type.SANDY)) {
            generateMagicFlower(BlockRegister.flowerScorchbloom, world, random, x, z, 30);
        }
        else if (biomeIs(biome, BiomeDictionary.Type.SWAMP, BiomeDictionary.Type.SPOOKY)) {
            generateMagicFlower(BlockRegister.flowerGloomthorn, world, random, x, z, 80);
        }
        else if (biomeIs(biome, BiomeDictionary.Type.FOREST)) {
            generateMagicFlower(BlockRegister.flowerBrightroot, world, random, x, z, 20);
        }
        else if (biomeIs(biome, BiomeDictionary.Type.JUNGLE)) {
            generateMagicFlower(BlockRegister.flowerBrightroot, world, random, x, z, 90);
        }

        //Sealed Arcana
        if (random.nextInt(16) == 0) {
            BlockPos pos = new BlockPos(
                    x + random.nextInt(16),
                    random.nextInt(96) + 8,
                    z + random.nextInt(16));
            if (world.getBlockState(pos).getBlock() == Blocks.STONE) {
                world.setBlockState(pos, BlockRegister.sealedArcana.getDefaultState());
            }
        }
    }

    private static void generateOre(Block block, World world, int x, int z,
                                    int vSizeMin, int vSizeMax, int vPerChunk, int vMinY, int vMaxY,
                                    Random random) {
        IBlockState iBlockState = block.getDefaultState();

        int deltaY = vMaxY - vMinY;
        for (int i = 0; i < vPerChunk; ++i) {
            BlockPos pos = new BlockPos(
                    x + random.nextInt(16),
                    vMinY + random.nextInt(deltaY),
                    z + random.nextInt(16));

            WorldGenMinable generator = new WorldGenMinable(iBlockState, random.nextInt(vSizeMax - vSizeMin) + vSizeMin);
            generator.generate(world, random, pos);
        }
    }

    private void generateMagicFlower(BlockMagicFlower block, World world, Random random, int x, int z, int percentChance) {
        if (random.nextInt(100) < percentChance) {
            x += random.nextInt(16) + 8;
            z += random.nextInt(16) + 8;
            int y;
            if (block == BlockRegister.flowerRockgrass) {
                y = random.nextInt(24) + 38;
            }
            else {
                y = random.nextInt(world.getHeight(x, z) + 32);
            }

            BlockPos pos = new BlockPos(x, y, z);
            IBlockState state = block.getDefaultState();

            if (world.isAirBlock(pos)) {
                for (int i = 0; i < 40; ++i) {
                    BlockPos blockPos = pos.add(
                            random.nextInt(8) - random.nextInt(8),
                            random.nextInt(4) - random.nextInt(4),
                            random.nextInt(8) - random.nextInt(8));

                    if (blockPos.getY() < 255 && block.canPlaceBlockAt(world, blockPos)) {
                        world.setBlockState(blockPos, state, 2);
                    }
                }
            }
        }
    }

    private static boolean biomeIs(Biome biome, BiomeDictionary.Type... type) {
        Set<BiomeDictionary.Type> typeSet = BiomeDictionary.getTypes(biome);
        for (BiomeDictionary.Type type1 : type) {
            if (typeSet.contains(type1)) {
                return true;
            }
        }
        return false;
    }
}
