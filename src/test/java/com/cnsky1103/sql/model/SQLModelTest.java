package com.cnsky1103.sql.model;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;

import com.cnsky1103.Config;
import com.cnsky1103.sql.exception.SQLModelException;
import com.cnsky1103.sql.memory.MemoryManager;

import org.junit.Test;

public class SQLModelTest {
    

    @Test
    public void test() {
        assertTrue(true);
    }

    @Test
    public void testRecord() {
        Table table = new Table("test", new ArrayList<Column>() {
            {
                add(new Column("c1", Syntax.Type.INT, true));
                add(new Column("c1", Syntax.Type.DOUBLE, false));
                add(new Column("c1", Syntax.Type.CHAR, false, 10));
            }
        });
        Record record = new Record(table) {
            {
                set(0, new Value(114));
                set(1, new Value(514.1919));
                set(2, new Value("yjsp"));
            }
        };
        byte[] bytes = record.toBytes(0);
        assertTrue(bytes.length == 27);
    }

    @Test
    public void testConvert() throws IOException, SQLModelException {
        Table table = new Table("test", new ArrayList<Column>() {
            {
                add(new Column("c1", Syntax.Type.INT, true));
                add(new Column("c2", Syntax.Type.DOUBLE, false));
                add(new Column("c3", Syntax.Type.CHAR, false, 10));
            }
        });
        byte[] b = MemoryManager.readARecord(table, 0);
        Record record = table.convertToRecord(b);
        assertTrue(record.getValues()[0].getVINT() == 114);
        assertTrue(BigDecimal.valueOf(record.getValues()[1].getVDOUBLE()).equals(BigDecimal.valueOf(514.1919)));
        assertTrue(record.getValues()[2].getVString(), record.getValues()[2].getVString().equals("yjsp"));
    }

    @Test
    public void testSerialize() {
        try {
            FileOutputStream fos = new FileOutputStream(Config.dataPath + "/test/test.data");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(new Column("test", Syntax.Type.INT, true));
            ArrayList<Column> columns = new ArrayList<>();
            columns.add(new Column("c1", Syntax.Type.INT, true));
            columns.add(new Column("c2", Syntax.Type.DOUBLE, false));
            columns.add(new Column("c3", Syntax.Type.CHAR, false));
            Table table = new Table("test", columns);
            oos.writeObject(columns.toArray());
            oos.writeObject(table);
            oos.close();
            fos.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    public void testCompare() {
        Value v1 = new Value(10);
        Value v2 = new Value(20);
        Syntax.CompareOp op = Syntax.CompareOp.eq;
        assertTrue(!op.compare(v1, v2));
    }
}
