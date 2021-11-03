package com.cnsky1103.sql;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.cnsky1103.Config;
import com.cnsky1103.sql.exception.SQLException;
import com.cnsky1103.sql.exception.SQLModelException;
import com.cnsky1103.sql.exception.TableExistException;
import com.cnsky1103.sql.exception.TableNotFoundException;
import com.cnsky1103.sql.model.Condition;
import com.cnsky1103.sql.model.Instruction;
import com.cnsky1103.sql.model.Table;
import com.cnsky1103.sql.model.Record;
import com.cnsky1103.sql.model.Value;
import com.cnsky1103.sql.model.Condition.AndClause;
import com.cnsky1103.sql.model.Condition.OrClause;
import com.cnsky1103.sql.model.Condition.WhereClause;
import com.cnsky1103.sql.model.Syntax.Operator;
import com.cnsky1103.sql.model.Syntax.Type;

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

    /**
     * 执行一条SQL指令
     * @param command
     */
    public static void execute(String command) {
        execute(Parser.parse(command));
    }

    public static void execute(Instruction ins) {
        try {
            if (ins.getOp() == Operator.SELECT) {
                checkType(ins);
                select(ins);
            } else if (ins.getOp() == Operator.CREATE) {
                create(ins);
            } else if (ins.getOp() == Operator.UPDATE) {
                checkType(ins);
            } else if (ins.getOp() == Operator.DELETE) {
                checkType(ins);
            } else if (ins.getOp() == Operator.INSERT) {
                insert(ins);
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

    /**
     * 判断指令中的类型和表的类型是否一致
     * 允许int向double的转换
     * @param ins
     * @throws SQLException
     */
    private static void checkType(Instruction ins) throws SQLException {
        for (OrClause orClause : ins.getWhereClause().getOrClauses()) {
            for (AndClause andClause : orClause.getAndClauses()) {
                Condition condition = andClause.getCondition();
                if (condition.getLeft().type != condition.getRight().getType()) {
                    if (!(condition.getLeft().type == Type.DOUBLE && condition.getRight().getType() == Type.INT)) {
                        throw new SQLException("column type of " + condition.getLeft().name
                                + " not match, should receive type " + condition.getLeft().type);
                    }
                }
            }
        }
    }

    /**
     * 判断一条记录是否符合条件
     * @param record
     * @param conditions
     * @return
     */
    private static boolean satisfy(Record record, WhereClause whereClause) {
        for (OrClause orClause : whereClause.getOrClauses()) {
            if (satisfyAnd(record, orClause.getAndClauses())) {
                return true;
            }
        }
        return false;
    }

    private static boolean satisfyAnd(Record record, ArrayList<AndClause> andClauses) {
        Table table = record.getTable();
        Value[] values = record.getValues();
        for (AndClause andClause : andClauses) {
            Condition condition = andClause.getCondition();
            int columnIndex = table.getColumnIndex(condition.getLeft());
            if (!condition.getOp().compare(values[columnIndex], condition.getRight())) {
                return false;
            }
        }
        return true;
    }

    private static void create(Instruction ins) throws TableExistException, IOException {
        if (tables.containsKey(ins.getTableName())) {
            throw new TableExistException("Table " + ins.getTableName() + " already exists");
        }
        Table table = new Table(ins.getTableName(), ins.getColumns());
        tableLock.put(table.getName(), new ReentrantReadWriteLock());
        tables.put(table.getName(), table);
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

    public static ArrayList<Record> select(Instruction ins) throws SQLException, IOException {
        ArrayList<Record> result = new ArrayList<>();
        Table table = getTable(ins.getTableName());
        table.reset();
        Record record;
        while ((record = table.getNext()) != null) {
            if (satisfy(record, ins.getWhereClause())) {
                result.add(record);
            }
        }
        return result;
    }

    private static void insert(Instruction ins) throws TableNotFoundException, SQLModelException, IOException {
        Table table = getTable(ins.getTableName());
        table.insert(ins);
    }

    /* private static void delete(Instruction ins) throws TableNotFoundException, SQLModelException, IOException {
        Table table = getTable(ins.getTableName());
        table.reset();
        Record record;
        while ((record = table.getNext()) != null) {
            if (satisfy(record, ins.getWhereClause())) {
                record.setValid(Config.InvalidByte);
            }
        }
    }

    private static void update(Instruction ins) throws TableNotFoundException, SQLModelException, IOException {
        Table table = getTable(ins.getTableName());
        table.reset();
        Record record;
        while ((record = table.getNext()) != null) {
            if (satisfy(record, ins.getWhereClause())) {
                
            }
        }
    } */
}
