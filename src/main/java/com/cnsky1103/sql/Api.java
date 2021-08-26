package com.cnsky1103.sql;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.cnsky1103.sql.Syntax.Operator;
import com.cnsky1103.sql.exception.SQLException;
import com.cnsky1103.sql.exception.TableNotFoundException;

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
            if (ins.op == Operator.SELECT) {

            } else if (ins.op == Operator.CREATE) {

            } else if (ins.op == Operator.UPDATE) {

            } else if (ins.op == Operator.DELETE) {

            } else if (ins.op == Operator.INSERT) {
                Table table = getTable(ins.tableName);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Table getTable(String name) throws TableNotFoundException {
        if (!tables.containsKey(name)) {
            throw new TableNotFoundException("Undefined table " + name);
        }
        return tables.get(name);
    }
}
