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

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public final class MemoryManager {
    private MemoryManager(){
    }

    /*
     * 4k per block, at most 256MByte memory will be allocated to store table data
     * no record will be written across blocks
     * which means that the max length of one record is 4096
     */
    private final static int blockSize = 4096;
    private final static int maxBlock = 1024 * 64;

    /**
     * l:organize blocks in a doubly linked list way
     * m:organize blocks in a hash map
     * use these two data structures to implement an LRU strategy;
     * {@code Pair<String, Integer>} has
     * String as tableName and Integer as blockIndex
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

    /**
     * 获取一条记录
     * @param table 表
     * @param offset 这条记录在整个表里的偏移量
     * @return 这条记录的byte数组表示；如果超过了文件大小则返回null
     * @throws IOException
     */
    public static byte[] readARecord(Table table, int offset) throws IOException {
        if (offset < 0) {
            return null;
        }
        int idx = getBlockIndex(offset);
        int start = getBlockOffset(offset);

        // 如果从当前位置开始读，会超出一个block的范围，那就从下一个block读
        if (start + table.getRecordSize() >= blockSize) {
            return readARecord(table, (idx + 1) * blockSize);
        }

        Block b = m.getOrDefault(new ImmutablePair<String, Integer>(table.getName(), idx), null);
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
                File file = new File(Config.dataPath + table.getName() + Config.recordSuffix);
                if (!file.exists()) {
                    file.createNewFile();
                }
                if (file.length() <= offset) {
                    return null;
                }
                b.raf = new RandomAccessFile(file, "rw");
                b.buffer = new byte[blockSize];
                b.sign = new ImmutablePair<String, Integer>(table.getName(), idx);
                b.raf.seek(idx * blockSize);
                b.raf.read(b.buffer);

                q.addLast(b);
                m.put(b.sign, b);

            } catch (IOException e) {
                //TODO: 完善错误处理
                e.printStackTrace();
                throw e;
            }
        }
        byte[] bytes = new byte[table.getRecordSize()];
        for (int i = 0; i < table.getRecordSize(); ++i) {
            bytes[i] = b.buffer[start + i];
        }
        return bytes;
    }

    /**
     * 写一条记录进内存
     * @param table 表
     * @param record 要写的记录
     * @param offset 要写进这个表的位置
     * @throws IOException
     */
    public static void writeARecord(Table table, Record record, int offset) throws IOException {
        int idx = getBlockIndex(offset);
        int start = getBlockOffset(offset);

        // 如果从当前位置开始写，会超出一个block的范围，那就不要了，写进下一个block
        if (start + table.getRecordSize() >= blockSize) {
            writeARecord(table, record, (idx + 1) * blockSize);
            return;
        }

        Block b = m.getOrDefault(new ImmutablePair<String, Integer>(table.getName(), idx), null);
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
                File file = new File(Config.dataPath + table.getName() + Config.recordSuffix);
                if (!file.exists()) {
                    file.createNewFile();
                }
                b.raf = new RandomAccessFile(file, "rw");
                b.buffer = new byte[blockSize];
                b.sign = new ImmutablePair<String, Integer>(table.getName(), idx);
                b.raf.seek(idx * blockSize);
                b.raf.read(b.buffer);

                q.addLast(b);
                m.put(b.sign, b);

            } catch (IOException e) {
                //TODO: 完善错误处理
                e.printStackTrace();
                throw e;
            }
        }

        // offset是要写进表的位置，再加上表的一条记录的大小就是下一条记录的位置
        byte[] recordBytes = record.toBytes(offset + table.getRecordSize());
        for (int i = 0; i < recordBytes.length; ++i) {
            b.buffer[start + i] = recordBytes[i];
        }
        b.dirty = true;

        /* 如果当前记录原本是无效的，那他的偏移量就没有意义了，要重新计算 */
        /* if (b.buffer[start] == 0) {
            b.buffer[0] = Config.ValidByte;
            int cnt = 1;
            try {
                while (true) {
                    byte[] next = readARecord(table, offset + cnt * table.getRecordSize());
                    if (next[0] == 1) {
                        for (int i = 0; i < 4; ++i) {
                            b.buffer[start + 1 + i] = next[1 + i];
                        }
                        break;
                    }
                    ++cnt;
                }
            } catch (IOException e) {
                for (int i = 0; i < 4; ++i) {
                    b.buffer[start + 1 + i] = (byte) 0;
                }
            }
        } */
    }

    public static void writeARecord(Table table, Record record) throws IOException {
        writeARecord(table, record, record.getNext() - table.getRecordSize());
    }

    private static void writeBack(Block b) throws IOException {
        if (b.dirty) {
            b.raf.seek(b.sign.right * blockSize);
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
    ImmutablePair<String, Integer> sign; //table name and block index
    boolean dirty;

    Block() {
        dirty = false;
    }
}