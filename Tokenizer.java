import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
        // also needs to rewrite to a new file called persistent.txt
        // needs a persist() method that loads everything from this persistent.txt for fresh compiles
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
