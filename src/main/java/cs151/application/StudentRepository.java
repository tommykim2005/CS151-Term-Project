package cs151.application;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class StudentRepository {
    private static final Path FILE = Paths.get("data", "student_profiles.csv");
    private static final List<Student> cache = new ArrayList<>();

    public static List<Student> loadAll() {
        cache.clear();
        if (!Files.exists(FILE)) return cache;
        try (BufferedReader r = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
            String line;
            while ((line = r.readLine()) != null) {
                List<String> c = parseCsv(line);
                while (c.size() < 4) c.add("");
                Student s = new Student(c.get(0), c.get(1), c.get(2), c.get(3));
                cache.add(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cache;
    }

    public static void saveAll() {
        try {
            if (!Files.exists(FILE.getParent())) Files.createDirectories(FILE.getParent());
            try (BufferedWriter w = Files.newBufferedWriter(FILE, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                for (Student s : cache) {
                    w.write(csv(s.getFull_Name(), s.getAcademic_Status(),
                                s.getCurrent_Job_Status(), s.getJob_Details()));
                    w.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean existsByName(String name) {
        String norm = Student.normalizeName(name);
        return cache.stream().anyMatch(s ->
                Student.normalizeName(s.getFull_Name()).equalsIgnoreCase(norm));
    }

    public static void add(Student s) {
        cache.add(s);
        sortByName();
        saveAll();
    }

    public static List<Student> getAll() { return cache; }

    public static void sortByName() { cache.sort(Comparator.naturalOrder()); }

    // Helpers
    private static String csv(String... cols) {
        return Arrays.stream(cols).map(c -> {
            String v = c == null ? "" : c;
            if (v.contains(",") || v.contains("\"")) {
                v = v.replace("\"", "\"\"");
                v = "\"" + v + "\"";
            }
            return v;
        }).collect(Collectors.joining(","));
    }
    private static List<String> parseCsv(String line) {
        List<String> out = new ArrayList<>();
        boolean inQ = false;
        StringBuilder cur = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (inQ) {
                if (ch == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') { cur.append('"'); i++; }
                    else inQ = false;
                } else cur.append(ch);
            } else {
                if (ch == ',') { out.add(cur.toString()); cur.setLength(0); }
                else if (ch == '"') inQ = true;
                else cur.append(ch);
            }
        }
        out.add(cur.toString());
        return out;
    }
}
