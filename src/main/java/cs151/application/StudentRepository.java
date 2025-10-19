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
                List<String> cols = parseCsv(line);
                // Expected 10 columns
                while (cols.size() < 10) cols.add("");
                Student s = new Student(
                        cols.get(0), cols.get(1), cols.get(2), cols.get(3),
                        cols.get(4), cols.get(5), cols.get(6), cols.get(7),
                        "true".equalsIgnoreCase(cols.get(8)), "true".equalsIgnoreCase(cols.get(9))
                );
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
                    w.write(csv(
                            s.getFull_Name(),
                            s.getAcademic_Status(),
                            s.getCurrent_Job_Status(),
                            s.getJob_Details(),
                            s.getProgramming_Languages(),
                            s.getDatabases(),
                            s.getPreferred_Role(),
                            s.getComments(),
                            String.valueOf(s.getWhitelist()),
                            String.valueOf(s.getBlacklist())
                    ));
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

    public static Optional<Student> findByName(String name) {
        String norm = Student.normalizeName(name);
        return cache.stream().filter(s ->
                Student.normalizeName(s.getFull_Name()).equalsIgnoreCase(norm)).findFirst();
    }

    public static void add(Student s) {
        cache.add(s);
        sortByName();
        saveAll();
    }

    public static void delete(Student s) {
        cache.remove(s);
        saveAll();
    }

    public static List<Student> getAll() {
        return cache;
    }

    public static List<Student> search(String nameQ, String statusQ,
                                       List<String> languageAny, List<String> dbAny, String roleQ) {
        String nq = nameQ == null ? "" : nameQ.trim().toLowerCase();
        String rq = roleQ == null ? "" : roleQ.trim().toLowerCase();
        String sq = statusQ == null ? "" : statusQ.trim().toLowerCase();

        return cache.stream().filter(s -> {
            boolean ok = true;
            if (!nq.isEmpty()) ok &= Student.normalizeName(s.getFull_Name()).toLowerCase().contains(nq);
            if (!sq.isEmpty() && !"any".equals(sq)) ok &= s.getAcademic_Status().toLowerCase().equals(sq);
            if (!rq.isEmpty() && !"any".equals(rq)) ok &= s.getPreferred_Role().toLowerCase().equals(rq);

            if (languageAny != null && !languageAny.isEmpty()) {
                String have = (s.getProgramming_Languages() == null ? "" : s.getProgramming_Languages()).toLowerCase();
                ok &= languageAny.stream().anyMatch(t -> have.contains(t.toLowerCase()));
            }
            if (dbAny != null && !dbAny.isEmpty()) {
                String have = (s.getDatabases() == null ? "" : s.getDatabases()).toLowerCase();
                ok &= dbAny.stream().anyMatch(t -> have.contains(t.toLowerCase()));
            }
            return ok;
        }).sorted().collect(Collectors.toList());
    }

    public static void sortByName() {
        cache.sort(Comparator.naturalOrder());
    }

    // CSV helpers (quote-safe)
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
