package com.cp.cronparser.parser;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.cp.cronparser.ParserUtil.baseParser;
import static com.cp.cronparser.ParserUtil.charValidator;
import static com.cp.cronparser.ParserUtil.validateRange;

@Component
public class HourParser implements Parser {

    private static final Set<Character> allowedChar = new HashSet<>(Arrays.asList(',', '-', '*', '/'));
    private static final List<Integer> range = Arrays.asList(0, 23);

    /**
     * Validator for all hour cron component.
     * @param val input values of hour component.
     */
    @Override
    public void validate(String val) {
        charValidator(val, allowedChar);
        validateRange(val,range);
    }

    /**
     * parser used to parse hour component.
     * @param val
     * @return set {@link Set} return the value for which the cron will run.
     */
    @Override
    public Set<Integer> parse(String val) {
        return baseParser(val, allowedChar, range);
    }
}
