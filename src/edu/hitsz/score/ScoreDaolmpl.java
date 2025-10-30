package edu.hitsz.score;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ScoreDaolmpl implements ScoreDao {
private static final Path FILE = Paths.get("scores.txt");
private static final DateTimeFormatter F = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public synchronized void doAdd(Score s) {
        List<Score> all = getAllScores();
        all.add(s);
        all.sort(Comparator.comparingInt(Score::getScore).reversed()
                           .thenComparing(Score::getTime));
        writeAll(all);
    }

    @Override
    public synchronized List<Score> getAllScores() {
        ensureFile();
        List<Score> list = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(FILE)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) continue;
                String[] p = line.split(",", -1);
                if (p.length < 3) continue;
                int sc;
                try { sc = Integer.parseInt(p[0]); } catch (NumberFormatException e) { continue; }
                String name = p[1];
                LocalDateTime t;
                try { t = LocalDateTime.parse(p[2], F); } catch (Exception e) { t = LocalDateTime.now(); }
                list.add(new Score(sc, name, t));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        list.sort(Comparator.comparingInt(Score::getScore).reversed()
                            .thenComparing(Score::getTime));
        return list;
    }

    @Override
    public synchronized void removeScores(List<Score> scores) {
        if (scores == null || scores.isEmpty()) {
            return;
        }
        List<Score> all = getAllScores();
        if (all.isEmpty()) {
            return;
        }
        all.removeIf(existing -> containsRecord(scores, existing));
        all.sort(Comparator.comparingInt(Score::getScore).reversed()
                           .thenComparing(Score::getTime));
        writeAll(all);
    }

    private boolean containsRecord(Collection<Score> targets, Score existing) {
        for (Score target : targets) {
            if (isSameRecord(existing, target)) {
                return true;
            }
        }
        return false;
    }

    private boolean isSameRecord(Score a, Score b) {
        return a.getScore() == b.getScore()
                && Objects.equals(a.getScorename(), b.getScorename())
                && Objects.equals(a.getTime(), b.getTime());
    }

    private void writeAll(List<Score> all) {
        try (BufferedWriter bw = Files.newBufferedWriter(FILE)) {
            for (Score s : all) {
                bw.write(s.getScore() + "," + escape(s.getScorename()) + "," + s.getTime().format(F));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void ensureFile() {
        try {
            if (FILE.getParent() != null) Files.createDirectories(FILE.getParent());
            if (!Files.exists(FILE)) Files.createFile(FILE);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String escape(String s) {
        return s.replace(",", "，").replace("\n", " ");
    }
}