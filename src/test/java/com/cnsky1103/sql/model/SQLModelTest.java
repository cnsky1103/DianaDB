package com.cnsky1103.sql.model;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

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
}
