package com.cnsky1103.sql.model;

import java.util.ArrayList;

import lombok.Data;

@Data
public class Condition implements SQLModel {
    private static final long serialVersionUID = 7683957511257188914L;
    private Syntax.CompareOp op;
    private Column left;
    private Value right;

    @Data
    public static class AndClause implements SQLModel {
        private static final long serialVersionUID = -8581227187128432922L;
        private Condition condition;
    }

    @Data
    public static class OrClause implements SQLModel {
        private static final long serialVersionUID = -2661718091333929043L;
        private ArrayList<AndClause> andClauses;
    }

    @Data
    public static class WhereClause implements SQLModel {
        private static final long serialVersionUID = -2399372042517313683L;
        private ArrayList<OrClause> orClauses;
    }
}
