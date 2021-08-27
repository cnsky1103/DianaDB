package com.cnsky1103.sql.memory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.cnsky1103.Config;
import com.cnsky1103.sql.model.Record;
import com.cnsky1103.sql.model.Table;
import com.cnsky1103.utils.Pair;

public final class MemoryManager {
    /*
     * 4k per block, at most 256MByte memory will be allocated to store table data
     */
    private final static int blockSize = 4096;
    private final static int maxBlock = 1024 * 64;

    /**
     * l:organize blocks in a doubly linked list way
     * m:organize blocks in a hash map
     * use these two data structures to implement an LRU strategy
     * Pair<String, Integer> has
     * @param String tableName
     * @param Integer blockIndex
     */
    private static Deque<Block> q;
    private static Map<Pair<String, Integer>, Block> m;

    static {
        q = new ConcurrentLinkedDeque<>();
        m = new ConcurrentHashMap<>();
    }

    private static int getBlockIndex(int offset) {
        return offset / blockSize;
    }

    private static int getBlockOffset(int offset) {
        return offset % blockSize;
    }

    public static byte[] getARecord(Table table, int offset) throws IOException {
        int idx = getBlockIndex(offset);
        Block b = m.getOrDefault(new Pair<String, Integer>(table.getName(), idx), null);
        if (b != null) {
            q.remove(b);
            q.addLast(b);
        } else {
            b = new Block();
            if (q.size() == maxBlock) {
                m.remove(q.peekFirst().sign);
                writeBack(q.peekFirst());
                q.removeFirst();
            }
            try {
                b.raf = new RandomAccessFile(new File(Config.dataPath + "table/" + table.getName() + ".rec"), "rw");
                b.buffer = new byte[blockSize];
                b.sign = new Pair<String, Integer>(table.getName(), idx);
                b.raf.seek(idx * blockSize);
                b.raf.read(b.buffer);

                q.addLast(b);
                m.put(b.sign, b);

            } catch (IOException e) {
                //TODO: 完善错误处理
                throw e;
            }
        }
        byte[] bytes = new byte[table.getRecordSize()];
        int start = getBlockOffset(offset);
        for (int i = 0; i < table.getRecordSize(); ++i) {
            bytes[i] = b.buffer[start + i];
        }
        return bytes;
    }

    public static void writeARecord(Table table, Record record, int offset) throws IOException {
        int idx = getBlockIndex(offset);
        Block b = m.getOrDefault(new Pair<String, Integer>(table.getName(), idx), null);
        if (b != null) {
            q.remove(b);
            q.addLast(b);

        } else {
            b = new Block();
            if (q.size() == maxBlock) {
                m.remove(q.peekFirst().sign);
                writeBack(q.peekFirst());
                q.removeFirst();
            }
            try {
                b.raf = new RandomAccessFile(new File(Config.dataPath + "table/" + table.getName() + ".rec"), "rw");
                b.buffer = new byte[blockSize];
                b.sign = new Pair<String, Integer>(table.getName(), idx);
                b.raf.seek(idx * blockSize);
                b.raf.read(b.buffer);

                q.addLast(b);
                m.put(b.sign, b);

            } catch (IOException e) {
                //TODO: 完善错误处理
                throw e;
            }
        }
        int start = getBlockOffset(offset);
        byte[] recordBytes = record.toBytes(table);
        for (int i = 0; i < recordBytes.length; ++i) {
            //前5位不用写，是有效位和下一条记录地址
            b.buffer[start + 5 + i] = recordBytes[i];
        }
        b.dirty = true;

        /* 如果当前记录原本是无效的，那他的偏移量就没有意义了，要重新计算 */
        if (b.buffer[start] == 0) {
            b.buffer[0] = (byte) 0b00000001;
            int cnt = 1;
            try {
                while (true) {
                    byte[] next = getARecord(table, offset + cnt * table.getRecordSize());
                    if (next[0] == 1) {
                        for (int i = 0; i < 4; ++i) {
                            b.buffer[start + 1 + i] = next[1 + i];
                        }
                        break;
                    }
                }
            } catch (IOException e) {
                /* 
                 * 如果读一条记录出了问题，说明超过了文件的大小，这条记录之后再也没有有效的记录了
                 * 那就把下一条记录的地址设为0
                 */
                for (int i = 0; i < 4; ++i) {
                    b.buffer[start + 1 + i] = (byte) 0;
                }
            }
        }
    }

    private static void writeBack(Block b) throws IOException {
        if (b.dirty) {
            b.raf.seek(b.sign.r * blockSize);
            b.raf.write(b.buffer);
        }
    }

    public static void writeBackAll() throws IOException {
        for (Block b : m.values()) {
            writeBack(b);
        }
    }
}

class Block {
    byte[] buffer;
    RandomAccessFile raf;
    Pair<String, Integer> sign; //table name and block index
    boolean dirty;

    Block() {
        dirty = false;
    }
}