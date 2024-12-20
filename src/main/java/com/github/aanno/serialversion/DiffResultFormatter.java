package com.github.aanno.serialversion;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class DiffResultFormatter {

    private final DiffResultFormatterConfig config;

    public DiffResultFormatter(DiffResultFormatterConfig config) {
        this.config = config;
    }

    public StringBuilder appendTo(StringBuilder stringBuilder, DiffResult diffResult) {
        stringBuilder.append("DiffResult:\n");

        stringBuilder.append("\n").append("\tsvuDiff:\n");
        formatSvuDiffMap(stringBuilder, diffResult.getSvuDiff());
        stringBuilder.append("\n");

        stringBuilder.append("\n").append("\tonlyInA:\n");
        formatIn(stringBuilder, diffResult.getOnlyInA());
        stringBuilder.append("\n");

        stringBuilder.append("\n").append("\tonlyInB:\n");
        formatIn(stringBuilder, diffResult.getOnlyInB());
        stringBuilder.append("\n");

        if (config.isSameClasses()) {
            stringBuilder.append("\n").append("\tsame:\n");
            formatIn(stringBuilder, diffResult.getSameInAandB());
            stringBuilder.append("\n");
        }

        return stringBuilder;
    }

    public StringBuilder formatIn(StringBuilder stringBuilder, Set<String> in) {
        Set<String> set = in;
        if (config.isSort()) {
            set = new TreeSet<>(set);
        }
        for (String s : set) {
            stringBuilder.append("\t").append(s).append("\n");
        }
        return stringBuilder;
    }

    public StringBuilder formatSvuDiffMap(StringBuilder stringBuilder, Map<String, DiffSvu> svuDiffMap) {
        Set<String> set = svuDiffMap.keySet();
        if (config.isSort()) {
            set = new TreeSet<>(set);
        }
        for (String key : set) {
            stringBuilder.append("\t").append(key).append("->");
            formatSvuDiff(stringBuilder, svuDiffMap.get(key));
            stringBuilder.append("\n");
        }
        return stringBuilder;
    }

    public StringBuilder formatSvuDiff(StringBuilder stringBuilder, DiffSvu svuDiff) {
        stringBuilder.append("(").append(Long.toHexString(svuDiff.getSuv1()))
                .append(", ").append(Long.toHexString(svuDiff.getSuv2())).append(")\n");
        return stringBuilder;
    }

    public String format(DiffResult diffResult) {
        StringBuilder stringBuilder = new StringBuilder();
        appendTo(stringBuilder, diffResult);
        return stringBuilder.toString();
    }
}
