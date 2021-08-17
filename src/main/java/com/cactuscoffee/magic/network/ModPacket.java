package com.cactuscoffee.magic.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ModPacket implements IMessage {
    public static final String PACKET_CHARGE_NAME = "charge";

    private String name;
    private int data1, data2, data3;

    public ModPacket() {}

    public ModPacket(String name, int data1, int data2, int data3) {
        this.name = name;
        this.data1 = data1;
        this.data2 = data2;
        this.data3 = data3;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        name = ByteBufUtils.readUTF8String(buf);
        data1 = ByteBufUtils.readVarInt(buf, 5);
        data2 = ByteBufUtils.readVarInt(buf, 5);
        data3 = ByteBufUtils.readVarInt(buf, 5);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, name);
        ByteBufUtils.writeVarInt(buf, data1, 5);
        ByteBufUtils.writeVarInt(buf, data2, 5);
        ByteBufUtils.writeVarInt(buf, data2, 5);
    }

    public String getName() {
        return name;
    }

    public int getData1() {
        return data1;
    }

    public int getData2() {
        return data2;
    }

    public int getData3() {
        return data3;
    }
}
