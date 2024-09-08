package com.tannv.jobhunter.util.excel;

public class StringUtils {
    public static String indentationString(int indent, String value) {
        String indentation = " ".repeat(indent);
        return indentation + value;
    }
}
