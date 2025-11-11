package cs151.application;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StudentRepository {

    // Match StudentListController's path exactly
    private static final Path FILE = Paths.get("data", "student_data_test.csv");

    private static void ensureDir() throws IOException {
        Path parent = FILE.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
    }

    /** Check duplicate by trimmed, case-insensitive full name. */
    public static boolean existsByName(String fullName) {
        String needle = normalize(fullName);
        try {
            if (!Files.exists(FILE)) return false;
            try (BufferedReader r = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
                String line;
                while ((line = r.readLine()) != null) {
                    String[] f = parseCsvLine(line, 10);
                    if (normalize(f[0]).equals(needle)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void deleteStudent(String studentName) {
        try {
            Path path = Paths.get("data", "student_data_test.csv");
            BufferedReader br = Files.newBufferedReader(path);
            List<String> lines = br.lines().toList();
            br.close();

            BufferedWriter wr = Files.newBufferedWriter(path);
            boolean fline = true;
            for(String line : lines) {
                String name = line.substring(0, line.indexOf(','));
                if(!name.equals(studentName)) {
                    if(!fline) {
                        wr.newLine();
                    }
                    wr.write(line);
                    fline = false;
                }
            }

            wr.close();
            br.close();
        } catch (IOException e) {
            // unable to read from file
        }
    }

    /** Append a full 10-column row (Sections 2.1 - 2.4). */
    public static void appendRow(String fullName,
                                 String academicStatus,
                                 String currentJobStatus,
                                 String jobDetails,
                                 String programmingLanguages,
                                 String databases,
                                 String preferredRole,
                                 boolean whitelist,
                                 boolean blacklist,
                                 String comment) {
        try {
            ensureDir();
            try (BufferedWriter w = Files.newBufferedWriter(FILE, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                String row = toCsv(
                        nvl(fullName),
                        nvl(academicStatus),
                        nvl(currentJobStatus),
                        nvl(jobDetails),
                        nvl(programmingLanguages),
                        nvl(databases),
                        nvl(preferredRole),
                        String.valueOf(whitelist),
                        String.valueOf(blacklist),
                        nvl(comment)
                );
                boolean empty = false;
                try (BufferedReader r = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
                    String line = r.readLine();
                    if(line==null) empty = true;
                }

                if(!empty) w.newLine();
                w.write(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ------------- Helpers -------------

    private static String nvl(String s) { return s == null ? "" : s; }
    private static String normalize(String s) { return s == null ? "" : s.trim().toLowerCase(Locale.ROOT); }

    private static String toCsv(String... cols) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cols.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(quoteCsv(cols[i]));
        }
        return sb.toString();
    }

    private static String quoteCsv(String v) {
        if (v == null) return "";
        boolean needs = v.contains(",") || v.contains("\"") || v.contains("\n") || v.contains("\r");
        if (!needs) return v;
        return '"' + v.replace("\"", "\"\"") + '"';
    }

    /** Simple CSV parser into at least minCols columns. */
    private static String[] parseCsvLine(String line, int minCols) {
        List<String> out = new ArrayList<>();
        boolean inQ = false;
        StringBuilder cur = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (inQ) {
                if (ch == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') { cur.append('"'); i++; }
                    else inQ = false;
                } else {
                    cur.append(ch);
                }
            } else {
                if (ch == ',') { out.add(cur.toString()); cur.setLength(0); }
                else if (ch == '"') inQ = true;
                else cur.append(ch);
            }
        }
        out.add(cur.toString());
        while (out.size() < minCols) out.add("");
        return out.toArray(new String[0]);
    }
}