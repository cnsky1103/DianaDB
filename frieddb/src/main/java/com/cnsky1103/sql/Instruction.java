package com.cnsky1103.sql;

import java.util.List;

public class Instruction {
    public Syntax.Operator op;
    public String tableName;
    public List<Column> columns; //in CREATE or in SELECT
    public List<Condition> conditions; //in where clause
}
