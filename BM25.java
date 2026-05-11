import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class BM25 {
    Tokenizer tokenizer;
    CLI cli;
    String query;
    double k1 = 1.5;
    double b = 0.75;

    public BM25(CLI cli, Tokenizer tokenizer, String query) {
        this.cli = cli;
        this.tokenizer = tokenizer;
        this.query = query.toLowerCase();
    }

    public List<ScoredDoc> topKDocs() {
        PriorityQueue<ScoredDoc> heap = new PriorityQueue<>((x, y) -> Double.compare(y.score, x.score));
        File[] files = new File(cli.path).listFiles();
        for (File file : files) {
            if (!file.isFile()) continue;
            heap.offer(new ScoredDoc(file.getName(), scoreDocument(file)));
        }
        List<ScoredDoc> top = new ArrayList<>();
        while (!heap.isEmpty() && top.size() < cli.listLength) {
            top.add(heap.poll());
        }
        return top;
    }

    private double scoreDocument(File doc) {
        double score = 0.0;
        String[] queryTokens = query.split("\\s+");
        for (String token : queryTokens) {
            token = token.replaceAll("[^a-z0-9]", "");
            double idf = 0.0;
            int tf = 0;
            if (tokenizer.getIndex().get(token) != null) {
                idf = Math.log(((double)(tokenizer.getDocCount() - tokenizer.getIndex().get(token).size()) + 0.5) / ((double)tokenizer.getIndex().get(token).size() + 0.5) + 1);
                tf = tokenizer.getIndex().get(token).getOrDefault(tokenizer.getDocNames().indexOf(doc.getName()), 0);
            }
            int docLength = tokenizer.getDocLengths().get(tokenizer.getDocNames().indexOf(doc.getName()));
            score += idf * ((tf*(k1+1))/(tf+(k1*(1-b+((b*docLength)/tokenizer.getAvgDocLength())))));
        }
        return score;
    }

    public static class ScoredDoc {
        final String name;
        final double score;
        ScoredDoc(String name, double score) {
            this.name = name;
            this.score = score;
        }
    }
}