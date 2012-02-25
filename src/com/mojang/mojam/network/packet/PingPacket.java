package com.mojang.mojam.network.packet;

import java.io.*;

import com.mojang.mojam.network.Packet;

public class PingPacket extends Packet {
    public static final int TYPE_SYN = 1;
    public static final int TYPE_ACK = 2;

    private int type;
    private long sourceSendTime;
    private long sourceReceiveTime;
    private long destinationReceiveTime;
    private long destinationSendTime;

    public PingPacket() {
        this.type = TYPE_SYN;
    }

    public static PingPacket ack(PingPacket syn) {
        if (syn.type == TYPE_ACK) { throw new IllegalArgumentException("Cannot ACK an ACK"); }
        PingPacket ack = new PingPacket();
        ack.type = TYPE_ACK;
        ack.sourceSendTime = syn.sourceSendTime;
        ack.destinationReceiveTime = syn.destinationReceiveTime;
        return ack;
    }

    @Override
    public void read(DataInputStream dis) throws IOException {
        type = dis.readInt();
        sourceSendTime = dis.readLong();
        if (type == TYPE_SYN) {
            destinationReceiveTime = System.currentTimeMillis();
        } else if (type == TYPE_ACK) {
            destinationReceiveTime = dis.readLong();
            destinationSendTime = dis.readLong();
            sourceReceiveTime = System.currentTimeMillis();
        }
    }

    @Override
    public void write(DataOutputStream dos) throws IOException {
        dos.writeInt(type);
        if (type == TYPE_SYN) {
            sourceSendTime = System.currentTimeMillis();
            dos.writeLong(sourceSendTime);
        } else if (type == TYPE_ACK) {
            destinationSendTime = System.currentTimeMillis();
            dos.writeLong(sourceSendTime);
            dos.writeLong(destinationReceiveTime);
            dos.writeLong(destinationSendTime);
        }
    }

    public int getType() { return type; }
    public int getLatency() { return (int)(sourceReceiveTime-sourceSendTime); }
}