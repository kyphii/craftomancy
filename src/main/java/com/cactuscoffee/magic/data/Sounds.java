package com.cactuscoffee.magic.data;

import com.cactuscoffee.magic.MagicMod;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class Sounds {
    public static List<SoundEvent> soundEventList = new ArrayList<>();

    public static final SoundEvent FLUTEWEED = makeSoundEvent("fluteweed");

    public static final SoundEvent CAST_PROJECTILE = makeSoundEvent("cast_projectile");
    public static final SoundEvent CAST_MEGAVOLT = makeSoundEvent("cast_megavolt");
    public static final SoundEvent CAST_SIPHON = makeSoundEvent("cast_siphon");
    public static final SoundEvent CAST_EXCAVATE = makeSoundEvent("cast_excavate");
    public static final SoundEvent CAST_AVALANCHE = makeSoundEvent("cast_avalanche");
    public static final SoundEvent CAST_CURSE = makeSoundEvent("cast_curse");
    public static final SoundEvent CAST_DELIRIUM = makeSoundEvent("cast_delirium");
    public static final SoundEvent CAST_RADIANCE = makeSoundEvent("cast_radiance");
    public static final SoundEvent CAST_CORONA = makeSoundEvent("cast_corona");
    public static final SoundEvent CAST_GUARDIAN_STAR = makeSoundEvent("cast_guardian_star");

    public static final SoundEvent SEALED_ARCANA_AMBIENT = makeSoundEvent("sealed_arcana");
    public static final SoundEvent SEALED_ARCANA_OPEN = makeSoundEvent("sealed_arcana_open");

    public static final SoundEvent MUSIC_RECORD_RED = makeSoundEvent("record_red");
    public static final SoundEvent MUSIC_RECORD_YELLOW = makeSoundEvent("record_yellow");
    public static final SoundEvent MUSIC_RECORD_GREEN = makeSoundEvent("record_green");
    public static final SoundEvent MUSIC_RECORD_BLUE = makeSoundEvent("record_blue");
    public static final SoundEvent MUSIC_RECORD_BLACK = makeSoundEvent("record_black");
    public static final SoundEvent MUSIC_RECORD_WHITE = makeSoundEvent("record_white");


    private static SoundEvent makeSoundEvent(String resourcePathIn) {
        SoundEvent s = new SoundEvent(new ResourceLocation(MagicMod.MODID, resourcePathIn));
        s.setRegistryName(resourcePathIn);
        soundEventList.add(s);
        return s;
    }
}
