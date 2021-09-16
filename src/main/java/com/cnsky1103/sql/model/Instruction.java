package com.cnsky1103.sql.model;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Instruction implements SQLModel {
    private static final long serialVersionUID = 7705927319797699981L;
    private Syntax.Operator op;
    private String tableName;
    private ArrayList<Column> columns; //in CREATE or in SELECT
    private ArrayList<Value> values; //in insert
    private ArrayList<Condition> conditions; //in where clause
}
