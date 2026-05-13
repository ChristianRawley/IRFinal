import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tokenizer {

    File path;
    Map<String, Map<Integer, Integer>> index;
    List<String> docNames;
    List<Integer> docLengths;
    double avgDocLength;

    public Tokenizer(String path) {
        this.path = new File(path);
        this.index = new HashMap<>();
        this.docNames = new ArrayList<>();
        this.docLengths = new ArrayList<>();
        this.avgDocLength = 0;
    }

    public void setPath(String path) {
        this.path = new File(path);
    }

    public void build() throws IOException {
        index.clear();
        docNames.clear();
        docLengths.clear();
        File[] files = path.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isFile()) {
                int docId = docNames.size();
                docNames.add(file.getName());
                docLengths.add(0);
                tokenize(file, docId);
            }
        }
        long total = 0;
        for (int len : docLengths) {
            total += len;
        }
        if (docLengths.isEmpty()) avgDocLength = 0;
        else avgDocLength = (double) total / docLengths.size();
        persist();
    }

    private void persist() throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("persist.txt"))) {
            bw.write("!-- AVERAGE DOC LENGTH --!");
            bw.newLine();
            bw.write(String.valueOf(avgDocLength));
            bw.newLine();
            bw.write("!-- DOC LENGTHS --!");
            bw.newLine();
            for (int i = 0; i < docNames.size(); i++) {
                bw.write(docNames.get(i) + ":" + docLengths.get(i));
                bw.newLine();
            }
            bw.write("!-- TERMS --!");
            bw.newLine();
            for (Map.Entry<String, Map<Integer, Integer>> term : index.entrySet()) {
                for (Map.Entry<Integer, Integer> posting : term.getValue().entrySet()) {
                    bw.write(term.getKey() + ":" + posting.getKey() + ":" + posting.getValue());
                    bw.newLine();
                }
            }
        }
    }

    public void load() throws IOException {
        index.clear();
        docNames.clear();
        docLengths.clear();
        String section = "";
        try (BufferedReader br = new BufferedReader(new FileReader("persist.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("!--")) {
                    section = line;
                    continue;
                }
                if (section.contains("AVERAGE DOC LENGTH")) {
                    avgDocLength = Double.parseDouble(line);
                } else if (section.contains("DOC LENGTHS")) {
                    String[] parts = line.split(":");
                    docNames.add(parts[0]);
                    docLengths.add(Integer.parseInt(parts[1]));
                } else if (section.contains("TERMS")) {
                    String[] parts = line.split(":");
                    String term = parts[0];
                    int docId = Integer.parseInt(parts[1]);
                    int count = Integer.parseInt(parts[2]);
                    index.computeIfAbsent(term, k -> new HashMap<>()).put(docId, count);
                }
            }
        }
    }

    private void tokenize(File file, int docId) {
        int length = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                for (String raw : line.split("\\s+")) {
                    String token = clean(raw);
                    if (isValid(token)) {
                        Map<Integer, Integer> docCounts = index.get(token);
                        if (docCounts == null) {
                            docCounts = new HashMap<>();
                            index.put(token, docCounts);
                        }
                        if (docCounts.containsKey(docId)) docCounts.put(docId, docCounts.get(docId) + 1);
                        else docCounts.put(docId, 1);
                        length++;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        docLengths.set(docId, length);
    }

    private String clean(String token) {
        return token.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    private boolean isValid(String token) {
        return token.length() >= 2 && token.length() <= 20;
    }

    public Map<String, Map<Integer, Integer>> getIndex() {
        return index;
    }

    public List<String> getDocNames() {
        return docNames;
    }

    public List<Integer> getDocLengths() {
        return docLengths;
    }

    public double getAvgDocLength() {
        return avgDocLength;
    }

    public int getDocCount() {
        return docNames.size();
    }
}
