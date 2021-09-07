package com.cnsky1103.sql.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Condition implements SQLModel{
    private Syntax.CompareOp op;
    private Column left;
    private Value right;
}
