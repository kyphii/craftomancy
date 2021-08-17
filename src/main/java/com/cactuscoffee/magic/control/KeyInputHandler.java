package com.cactuscoffee.magic.control;

import com.cactuscoffee.magic.MagicMod;
import com.cactuscoffee.magic.network.ModPacket;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class KeyInputHandler {
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (KeyBinds.keyCharge.isPressed()) {
            MagicMod.network.sendToServer(new ModPacket(ModPacket.PACKET_CHARGE_NAME, 0, 0, 0));
        }
    }
}
