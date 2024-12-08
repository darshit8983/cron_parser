package com.cp.cronparser.parser;

import java.util.*;

public interface Parser {

    void validate(String val);

    Set<Integer> parse(String value);
}
