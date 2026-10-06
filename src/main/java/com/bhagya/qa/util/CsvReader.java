package com.bhagya.qa.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CsvReader {
    private CsvReader() {}

    public static List<String[]> read(Path path) throws IOException {
        List<String[]> rows = new ArrayList<>();
        List<String> lines = Files.readAllLines(path);
        for (int i = 1; i < lines.size(); i++) {
            if (!lines.get(i).isBlank()) {
                rows.add(parse(lines.get(i)));
            }
        }
        return rows;
    }

    private static String[] parse(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;
        for (char c : line.toCharArray()) {
            if (c == '"') {
                quoted = !quoted;
            } else if (c == ',' && !quoted) {
                values.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        values.add(current.toString().trim());
        return values.toArray(new String[0]);
    }

    public static String csv(Object... values) {
        return Arrays.stream(values).map(String::valueOf)
                .map(v -> v.contains(",") ? "\"" + v + "\"" : v)
                .reduce((a,b) -> a + "," + b).orElse("");
    }
}
