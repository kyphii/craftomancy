package com.cactuscoffee.magic.control;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public class KeyBinds {
    public static String CATEGORY = "key.tfmagic2.category";

    public static KeyBinding keyCharge;

    public static void register() {
        keyCharge = new KeyBinding("key.charge.desc", Keyboard.KEY_C, CATEGORY);
        ClientRegistry.registerKeyBinding(keyCharge);
    }
}
