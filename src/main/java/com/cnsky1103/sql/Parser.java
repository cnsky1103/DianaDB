package com.cnsky1103.sql;

import java.text.ParseException;
import java.util.regex.Pattern;

import com.cnsky1103.sql.model.Instruction;

public class Parser {
    
    public static Instruction parse(String command) {
        Instruction ins = new Instruction();
        
        return ins;
    }
}

enum Token {
    Keyword(Pattern.compile("select|create|insert|delete|update|set|from|where|into|values")),
    Type(Pattern.compile("int|double|char")), CompareSign(Pattern.compile("<|>|<=|>=|=|!=")),
    Constraint(Pattern.compile("primarykey|unique")), Int(Pattern.compile("\\d+")),
    Double(Pattern.compile("\\d+\\.\\d+")), Char(Pattern.compile("\"(.+)\"|\'(.+)\'")),
    Name(Pattern.compile("[a-zA-Z_]\\w*")), Bracket(Pattern.compile("^\\((.*)\\)$")), Comma(Pattern.compile(",")),
    Empty(Pattern.compile("^$")), Unknown(Pattern.compile(".+"));

    Pattern pattern;

    Token(Pattern p) {
        pattern = p;
    }
}
