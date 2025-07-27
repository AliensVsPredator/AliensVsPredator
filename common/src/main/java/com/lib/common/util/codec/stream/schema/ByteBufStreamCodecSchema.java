package com.lib.common.util.codec.stream.schema;

import com.bvanseg.just.serialization.codec.stream.schema.StreamCodecSchema;
import io.netty.buffer.ByteBuf;

public class ByteBufStreamCodecSchema implements StreamCodecSchema<ByteBuf> {

    @Override
    public byte readByte(ByteBuf input) {
        return input.readByte();
    }

    @Override
    public byte[] readBytes(ByteBuf input, int length) {
        var bytes = new byte[length];
        // Reads 'length' bytes and advances the position.
        input.readBytes(bytes);
        return bytes;
    }

    @Override
    public char readChar(ByteBuf input) {
        return input.readChar();
    }

    @Override
    public short readShort(ByteBuf input) {
        return input.readShort();
    }

    @Override
    public int readInt(ByteBuf input) {
        return input.readInt();
    }

    @Override
    public float readFloat(ByteBuf input) {
        return input.readFloat();
    }

    @Override
    public long readLong(ByteBuf input) {
        return input.readLong();
    }

    @Override
    public double readDouble(ByteBuf input) {
        return input.readDouble();
    }

    @Override
    public void writeByte(ByteBuf input, byte value) {
        input.writeByte(value);
    }

    @Override
    public void writeBytes(ByteBuf input, byte[] value) {
        input.writeBytes(value);
    }

    @Override
    public void writeChar(ByteBuf input, char value) {
        input.writeChar(value);
    }

    @Override
    public void writeShort(ByteBuf input, short value) {
        input.writeShort(value);
    }

    @Override
    public void writeInt(ByteBuf input, int value) {
        input.writeInt(value);
    }

    @Override
    public void writeFloat(ByteBuf input, float value) {
        input.writeFloat(value);
    }

    @Override
    public void writeLong(ByteBuf input, long value) {
        input.writeLong(value);
    }

    @Override
    public void writeDouble(ByteBuf input, double value) {
        input.writeDouble(value);
    }
}
