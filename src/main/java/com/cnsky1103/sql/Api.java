package com.cnsky1103.sql;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.cnsky1103.Config;
import com.cnsky1103.sql.exception.SQLException;
import com.cnsky1103.sql.exception.TableExistException;
import com.cnsky1103.sql.exception.TableNotFoundException;
import com.cnsky1103.sql.model.Instruction;
import com.cnsky1103.sql.model.Table;
import com.cnsky1103.sql.model.Syntax.Operator;

public final class Api {
    // cannot create an instance of Api
    private Api() {
    }

    private static Map<String, Table> tables;
    private static Map<String, ReentrantReadWriteLock> tableLock;

    static {
        tables = new ConcurrentHashMap<>();
        tableLock = new ConcurrentHashMap<>();
    }

    public static void execute(String command) {
        execute(Parser.parse(command));
    }

    private static void execute(Instruction ins) {
        try {
            if (ins.getOp() == Operator.SELECT) {

            } else if (ins.getOp() == Operator.CREATE) {
                createTable(ins);
            } else if (ins.getOp() == Operator.UPDATE) {

            } else if (ins.getOp() == Operator.DELETE) {

            } else if (ins.getOp() == Operator.INSERT) {
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Table getTable(String name) throws TableNotFoundException {
        if (!tables.containsKey(name)) {
            throw new TableNotFoundException("Undefined table " + name);
        }
        return tables.get(name);
    }

    private static void createTable(Instruction ins) throws TableExistException, IOException {
        if (tables.containsKey(ins.getTableName())) {
            throw new TableExistException("Table " + ins.getTableName() + " already exists");
        }
        Table table = new Table(ins.getTableName(), ins.getColumns());
        tableLock.put(table.getName(), new ReentrantReadWriteLock());
        try {
            FileOutputStream fos = new FileOutputStream(Config.tablesDataPath);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(table);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Failed in local file IO when creating table " + ins.getTableName());
        }
    }
}
