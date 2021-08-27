package com.cnsky1103.sql.model;

import java.util.List;

import com.cnsky1103.sql.model.Syntax.Operator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Instruction implements SQLModel {
    private Syntax.Operator op;
    private String tableName;
    private List<Column> columns; //in CREATE or in SELECT
    private List<Value> values; //in insert
    private List<Condition> conditions; //in where clause
}
