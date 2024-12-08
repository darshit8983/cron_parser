package com.cp.cronparser.parser;

import com.cp.cronparser.ParserUtil;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.cp.cronparser.ParserUtil.*;

@Component
public class DomParser implements Parser {

    private static final Set<Character> allowedChar = new HashSet<>(Arrays.asList(',', '-', '*', '/'));
    private static final List<Integer> range = Arrays.asList(1, 31);

    /**
     * Validator for all day of month cron component.
     * @param val input values of day of month component.
     */
    @Override
    public void validate(String val) {
        charValidator(val, allowedChar);
        validateRange(val,range);
    }

    /**
     * parser used to parse day of month component.
     * @param val
     * @return set {@link Set} return the value for which the cron will run.
     */
    @Override
    public Set<Integer> parse(String val) {
        return baseParser(val, allowedChar, range);
    }
}
