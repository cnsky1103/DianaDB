package com.cnsky1103.sql;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cnsky1103.sql.model.Instruction;
import com.cnsky1103.sql.model.Syntax.Operator;
import com.cnsky1103.sql.exception.ParseException;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import lombok.AllArgsConstructor;

public final class Parser {
    public static enum Token {
        Keyword(Pattern.compile("select|create|insert|delete|update|table|set|from|where|into|values")),
        Type(Pattern.compile("int|double|char")),
        CompareSign(Pattern.compile("<|>|<=|>=|=|!=")),
        Constraint(Pattern.compile("primarykey|unique")),
        Int(Pattern.compile("\\d+")),
        Double(Pattern.compile("\\d+\\.\\d+")),
        Char(Pattern.compile("\"(.+)\"|\'(.+)\'")),
        Name(Pattern.compile("[a-zA-Z_]\\w*")),
        Bracket(Pattern.compile("^\\((.*)\\)$")),
        Comma(Pattern.compile(",")),
        Empty(Pattern.compile("^$")),
        Unknown(Pattern.compile(".+"));
    
        Pattern pattern;
    
        Token(Pattern p) {
            pattern = p;
        }
    }

    @AllArgsConstructor
    /**
     * String的包装类，用于在不同函数之间修改String的值
     */
    private static class Command {
        String value;
    }

    private static Pair<Token, Matcher> parseToken(Command command) {
        command.value = command.value.trim();
        for (Token token : Token.values()) {
            Matcher matcher = token.pattern.matcher(command.value);
            if (matcher.lookingAt()) {
                command.value = command.value.substring(matcher.end());
                return new ImmutablePair<Parser.Token,Matcher>(token, matcher);
            }
        }
        return new ImmutablePair<Parser.Token,Matcher>(Token.Unknown, null);
    }

    private static void parsePlaceholder(Token token, Command command, String placeholder) throws ParseException {
        Pair<Token, Matcher> pair = parseToken(command);
        if (pair.getKey() != token || !pair.getValue().group(0).equalsIgnoreCase(placeholder)) {
            throw new ParseException("Illegal sql! Need " + placeholder + ".");
        }
    }
    
    public static Instruction parse(String commandString) throws ParseException {
        Instruction ins = new Instruction();
        Command command = new Command(commandString);

        Pair<Token, Matcher> pair = parseToken(command);
        if (pair.getKey() == Token.Keyword) {
            String operation = pair.getValue().group(0);
            if (operation.equalsIgnoreCase("create")) {
                ins.setOp(Operator.CREATE);
            } else if (operation.equalsIgnoreCase("select")) {

            } else if (operation.equalsIgnoreCase("insert")) {
                
            }
        } else {
            throw new ParseException("Unknown token");
        }
        
        return ins;
    }

    public static void main(String[] args) {
        String s = "create table";
        Command c = new Command(s);
        parseToken(c);
        System.out.println(c.value);
    }
}
