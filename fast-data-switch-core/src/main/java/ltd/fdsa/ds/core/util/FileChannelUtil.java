package ltd.fdsa.ds.core.util;

import lombok.var;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

/**
 * file tool
 */
public class FileChannelUtil {

    private final FileChannel fileChannel;

    FileChannelUtil(FileChannel fileChannel) {
        this.fileChannel = fileChannel;
    }

    public static FileChannelUtil getInstance(FileChannel fileChannel) {
        return new FileChannelUtil(fileChannel);
    }

    private void newPosition(long position) {
        if (position >= 0) {
            try {
                this.fileChannel.position(position);
            } catch (IOException e) {
            }
        }
    }

    public long position() {
        try {
            return this.fileChannel.position();
        } catch (IOException e) {
            return -1;
        }
    }

    public byte[] readVarByte() {
        return readVarByte(-1);
    }

    public boolean writeVLen(long length) {
        return this.writeVLen(length, -1);
    }

    public boolean writeVLen(long length, long position) {
        var data = this.vintEncode(length);
        return this.writeByte(data, position);
    }

    public boolean writeVarByte(byte[] data) {
        return this.writeVarByte(data, -1);
    }

    public boolean writeVarByte(byte[] data, long position) {
        this.newPosition(position);
        var length = vintEncode(data.length);
        this.writeByte(length);
        this.writeByte(data);
        return true;
    }

    public boolean writeByte(byte[] data) {
        return writeByte(data, -1);
    }

    public boolean writeByte(byte[] data, long position) {
        this.newPosition(position);
        var byteBuffer = ByteBuffer.allocate(data.length);
        byteBuffer.put(data);
        try {
            this.fileChannel.write(byteBuffer);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public byte[] readVarByte(long position) {
        var length = (int) this.readVLen(position);
        return this.read(length);
    }

    public long readVLen() {
        return this.readVLen(-1);
    }

    public long readVLen(long position) {
        this.newPosition(position);

        byte b = this.read(1)[0];
        if (b >= 0) {
            return b;
        }
        long i = b & 0x7F;
        var c = 1;
        while (c < 10) {
            b = this.read(1)[0];
            i |= (long) (b & 0x7F) << (7 * c);
            if (b >= 0) {
                return i;
            }
            c++;
        }
        return 0;
    }

    byte[] vintEncode(long i) {
        try (ByteArrayOutputStream s = new ByteArrayOutputStream();) {
            while ((i & ~0x7F) != 0) {
                s.write((int) ((i & 0x7F) | 0x80));
                i >>>= 7;
            }
            s.write((int) i);

            return s.toByteArray();
        } catch (Exception ex) {
            return new byte[0];
        }
    }

    public int readInt() {
        return readInt(-1);
    }

    public int readInt(long position) {
        this.newPosition(position);
        try {
            var byteBuffer = ByteBuffer.allocate(4);
            this.fileChannel.read(byteBuffer);
            return byteBuffer.getInt();
        } catch (IOException e) {
            return -1;
        }
    }

    public long readLong() {
        return readLong(-1);
    }

    public long readLong(long position) {
        this.newPosition(position);
        try {
            var byteBuffer = ByteBuffer.allocate(8);
            this.fileChannel.read(byteBuffer);
            return byteBuffer.getLong();
        } catch (IOException e) {
            return -1;
        }
    }

    public byte[] read(int size) {
        return this.read(size, -1);
    }

    public byte[] read(int size, long position) {
        this.newPosition(position);
        try {
            var byteBuffer = ByteBuffer.allocate(size);
            this.fileChannel.read(byteBuffer);
            return byteBuffer.array();
        } catch (IOException e) {
            return new byte[0];
        }
    }
}
