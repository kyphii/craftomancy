package com.cactuscoffee.magic.network;

import com.cactuscoffee.magic.item.IChargeableItem;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ModPacketHandler implements IMessageHandler<ModPacket, IMessage> {
    @Override
    public IMessage onMessage(ModPacket message, MessageContext ctx) {
        if (ModPacket.PACKET_CHARGE_NAME.equals(message.getName())) {
            IChargeableItem.chargeInHand(ctx.getServerHandler().player);
        }

        return null;
    }
}