package com.mjzh.clause;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 特约Demo,动态生成特约
 * @author zhou
 * @date 2020/5/1 23:14
 */
public class SpecialClause {
    private static final Log logger = LogFactory.getLog(SpecialClause.class);
    private static final Map<String, String> wellKnownSimplePrefixes = new HashMap<>(4);

    static {
        wellKnownSimplePrefixes.put("}", "{");
        wellKnownSimplePrefixes.put("]", "[");
        wellKnownSimplePrefixes.put(")", "(");
    }

    private final String placeholderPrefix;
    private final String placeholderSuffix;
    private final String simplePrefix;

    public SpecialClause(String placeholderPrefix, String placeholderSuffix) {
        Assert.notNull(placeholderPrefix, "'placeholderPrefix' must not be null");
        Assert.notNull(placeholderSuffix, "'placeholderSuffix' must not be null");
        this.placeholderPrefix = placeholderPrefix;
        this.placeholderSuffix = placeholderSuffix;
        String simplePrefixForSuffix = wellKnownSimplePrefixes.get(this.placeholderSuffix);
        if (simplePrefixForSuffix != null && this.placeholderPrefix.endsWith(simplePrefixForSuffix)) {
            this.simplePrefix = simplePrefixForSuffix;
        }
        else {
            this.simplePrefix = this.placeholderPrefix;
        }
    }

    public String clause(String value, @Nullable Set<String> visitedPlaceholders, Map<String, String> valueMap) {
        int startIndex = value.indexOf(this.placeholderPrefix);
        if (startIndex == -1) {
            return value;
        }

        StringBuilder result = new StringBuilder(value);
        while (startIndex != -1) {
            int endIndex = findPlaceholderEndIndex(result, startIndex);
            if (endIndex != -1) {
                String placeholder = result.substring(startIndex + this.placeholderPrefix.length(), endIndex);
                String originalPlaceholder = placeholder;
                if (visitedPlaceholders == null) {
                    visitedPlaceholders = new HashSet<>(4);
                }
                if (!visitedPlaceholders.add(originalPlaceholder)) {
                    throw new IllegalArgumentException(
                            "Circular placeholder reference '" + originalPlaceholder + "' in property definitions");
                }
                placeholder = clause(placeholder, visitedPlaceholders, valueMap);
                String propVal = valueMap.get(originalPlaceholder);
                if (propVal != null) {
                    result.replace(startIndex, endIndex + this.placeholderSuffix.length(), propVal);
                    if (logger.isTraceEnabled()) {
                        logger.trace("Resolved placeholder '" + placeholder + "'");
                    }
                    startIndex = result.indexOf(this.placeholderPrefix, startIndex + propVal.length());
                }
                else {
                    throw new IllegalArgumentException("Could not resolve placeholder '" +
                            placeholder + "'" + " in value \"" + value + "\"");
                }
                visitedPlaceholders.remove(originalPlaceholder);
            }
            else {
                startIndex = -1;
            }
        }
        return result.toString();
    }

    private int findPlaceholderEndIndex(CharSequence buf, int startIndex) {
        int index = startIndex + this.placeholderPrefix.length();
        int withinNestedPlaceholder = 0;
        while (index < buf.length()) {
            if (StringUtils.substringMatch(buf, index, this.placeholderSuffix)) {
                if (withinNestedPlaceholder > 0) {
                    withinNestedPlaceholder--;
                    index = index + this.placeholderSuffix.length();
                }
                else {
                    return index;
                }
            }
            else if (StringUtils.substringMatch(buf, index, this.simplePrefix)) {
                withinNestedPlaceholder++;
                index = index + this.simplePrefix.length();
            }
            else {
                index++;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        SpecialClause specialClause = new SpecialClause("{", "}");
        String value = "特约{related}的,{age}";
        Map<String, String> valueMap = new HashMap<>(4);
        valueMap.put("related", "多多");
        valueMap.put("age", "20");
        String result = specialClause.clause(value, null, valueMap);
        System.out.println(result);
    }
}
