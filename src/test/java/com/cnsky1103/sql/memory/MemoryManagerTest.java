package com.cnsky1103.sql.memory;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;

import com.cnsky1103.sql.model.Column;
import com.cnsky1103.sql.model.Record;
import com.cnsky1103.sql.model.Syntax;
import com.cnsky1103.sql.model.Table;
import com.cnsky1103.sql.model.Value;

import org.junit.Test;

public class MemoryManagerTest {
    private Table table = new Table("test", new ArrayList<Column>() {
        {
            add(new Column("c1", Syntax.Type.INT, true));
            add(new Column("c1", Syntax.Type.DOUBLE, false));
            add(new Column("c1", Syntax.Type.CHAR, false, 10));
        }
    });
    private Record record = new Record(table) {
        {
            set(0, new Value(114));
            set(1, new Value(514.1919));
            set(2, new Value("yjsp"));
        }
    };

    @Test
    public void testWriteARecord() {
        try {
            MemoryManager.writeARecord(table, record, 0);
            MemoryManager.writeBackAll();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    public void testReadARecord() {
        try{
            byte[] b = MemoryManager.readARecord(table, 0);
            assertTrue(b.length == table.getRecordSize());
            assertTrue(true);
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
