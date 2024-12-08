package com.cp.cronparser;

import com.cp.cronparser.dto.ParserType;
import com.cp.cronparser.exception.ParserException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class ParserUtil {

    public static String printer(ParserType parser, Set<Integer> values) {
        StringBuffer b = new StringBuffer();
        values = values.stream().sorted().collect(Collectors.toCollection(LinkedHashSet::new));
        b.append(String.format("%-14s%s\n", parser.toString().replaceAll("_", " ").toLowerCase(),
                values.toString().substring(1, values.toString().length() - 1)));
        return b.toString();
    }

    /**
     * Util validator for all cron components.
     *
     * @param val         Input value of Cron component.
     * @param allowedChar Character allowed in cron.
     */
    public static void charValidator(String val, Set<Character> allowedChar) {
        char[] values = val.toCharArray();
        for (char v : values) {
            if (!Character.isAlphabetic(v) && !Character.isDigit(v) && !allowedChar.contains(v)) {
                throw new ParserException(String.format("unrecognized character %s in input", Character.toString(v)));
            }
        }
    }

    public static void validateRange(String val, List<Integer> range) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(val);
        List<Integer> extractedNumbers = new ArrayList<>();
        while (matcher.find()) {
            extractedNumbers.add(Integer.parseInt(matcher.group()));
        }
        if (!extractedNumbers.isEmpty()) {
            extractedNumbers.sort(Integer::compare);
            if (extractedNumbers.get(0) < range.get(0) || extractedNumbers.get(extractedNumbers.size() - 1) > range.get(1)) {
                throw new ParserException(String.format("Invalid range for field, value: %s, must be in range of: %s.", val, range));
            }
        }
    }

    public static Set<Integer> parseRangeWithDash(String val) {
        String[] range = val.split("-");
        if (range.length == 2) {
            int start = Integer.parseInt(range[0]);
            int end = Integer.parseInt(range[1]);
            return populateSet(start, end, 1);
        } else {
            throw new ParserException(String.format("Invalid range input %s, must be of example type '0-6'", val));
        }
    }

    public static Set<Integer> parseRangeWithSlash(String val, List<Integer> range) {
        String[] intervals = val.split("/");
        int min = range.get(0);
        int max = range.get(1);
        int interval = 1;
        if (intervals.length == 2) {
            if (!"*".equals(intervals[0])) {
                min = Integer.parseInt(intervals[0]);
            }
            interval = Integer.parseInt(intervals[1]);
        } else {
            throw new ParserException(String.format("Invalid input %s, must be of example type '*/2'", val));
        }
        return populateSet(min, max, interval);
    }

    public static Set<Integer> parsePureWildCard(String val, List<Integer> range) {
        if (val.toCharArray().length == 1) {
            return populateSet(range.get(0), range.get(1), 1);
        } else {
            throw new ParserException(String.format("Invalid input %s, must be of example type '*'", val));
        }
    }

    public static Set<Integer> populateSet(int start, int end, int increment) {
        Set<Integer> range = new HashSet<>();
        if (start > end) {
            throw new ParserException(String.format("Invalid input provided! please check the input"));
        }
        for (int i = start; i <= end; i += increment) {
            range.add(i);
        }
        return range;
    }

    public static boolean isWithSpecialChar(String val, Set<Character> allowedChar) {
        char[] values = val.toCharArray();
        for (char v : values) {
            if (allowedChar.contains(v)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Base parser used to parse all components of cron.
     * All the parsers common between cron components are written here.
     *
     * @param val         cron component value
     * @param allowedChar Character allowed in cron.
     * @param range       Allowed range for Cron component.
     * @return set {@link Set} values for which the Cron will run.
     */
    public static Set<Integer> baseParser(String val, Set<Character> allowedChar, List<Integer> range) {
        Set<Integer> out = new HashSet<>();
        String[] values = val.split(",");
        for (String value : values) {
            if (isWithSpecialChar(value, allowedChar) && values.length == 1) {
                if (value.contains("-") && allowedChar.contains('-')) {
                    out.addAll(parseRangeWithDash(value));
                } else if (value.contains("/") && allowedChar.contains('/')) {
                    out.addAll(parseRangeWithSlash(value, range));
                } else if (value.contains("*") && allowedChar.contains('*')) {
                    out.addAll(parsePureWildCard(value, range));
                }
            } else if(!isWithSpecialChar(value, allowedChar)) {
                out.add(Integer.parseInt(value));
            } else {
                throw new ParserException(String.format("Incorrect Input %s provided", val ));
            }
        }
        return out;
    }
}