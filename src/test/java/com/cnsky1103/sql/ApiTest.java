package com.cnsky1103.sql;

import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import com.cnsky1103.Config;
import com.cnsky1103.sql.memory.MemoryManager;
import com.cnsky1103.sql.model.Column;
import com.cnsky1103.sql.model.Instruction;
import com.cnsky1103.sql.model.Syntax;
import com.cnsky1103.sql.model.Table;
import com.cnsky1103.sql.model.Value;
import com.cnsky1103.sql.model.Syntax.Operator;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class ApiTest {
    @Test
    public void testCreate() {
        Instruction ins = new Instruction();
        ins.setOp(Operator.CREATE);
        ins.setTableName("table1");
        ArrayList<Column> columns = new ArrayList<>();
        columns.add(new Column("c1", Syntax.Type.INT, true));
        columns.add(new Column("c2", Syntax.Type.DOUBLE, false));
        columns.add(new Column("c3", Syntax.Type.CHAR, false));
        ins.setColumns(columns);
        Api.execute(ins);
        try {
            FileInputStream fis = new FileInputStream(Config.tablesDataPath);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Table table = (Table) ois.readObject();
            assertTrue(table.getName().equals("table1"));
            assertTrue(table.getColumns().get(0).isPrimaryKey);
            ois.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
        assertTrue(true);
    }

    @Test
    public void testInsert() throws IOException {
        try {
            Instruction ins = new Instruction();
            ins.setOp(Operator.CREATE);
            ins.setTableName("table1");
            ArrayList<Column> columns = new ArrayList<>();
            columns.add(new Column("c1", Syntax.Type.INT, true));
            columns.add(new Column("c2", Syntax.Type.DOUBLE, false));
            columns.add(new Column("c3", Syntax.Type.CHAR, false, 10));
            ins.setColumns(columns);
            Api.execute(ins);

            for (int i = 0; i < 10; ++i) {
                Instruction insert = new Instruction();
                insert.setOp(Operator.INSERT);
                insert.setTableName("table1");
                ArrayList<Value> values = new ArrayList<>();
                values.add(new Value(i));
                values.add(new Value(1.2));
                values.add(new Value("abc"));
                insert.setValues(values);
                Api.execute(insert);
            }
            try {
                MemoryManager.writeBackAll();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } finally {
            //FileUtils.deleteDirectory(new File(Config.dataPath));
        }
    }
}
