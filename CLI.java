import java.io.IOException;
import java.util.Scanner;

public class CLI {
    int listLength;
    String path;
    Scanner scan;
    Tokenizer tokenizer;

    public CLI() throws IOException {
        listLength = 10;
        path = "./data/";
        scan = new Scanner(System.in);
        tokenizer = new Tokenizer(path);
        tokenizer.build();
    }

    int readInt(String prompt) {
        while (true) {
            System.out.println(prompt);
            try {
                return Integer.parseInt(scan.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public void runQuery() {
        System.out.println("Please enter your query:");
        String query = scan.nextLine().trim();
        System.out.println("Here is our list of the " + listLength + " most relevant documents about " + query + ":");
        BM25 bm25 = new BM25(this, tokenizer, query);
        for (BM25.ScoredDoc doc : bm25.topKDocs()) {
            System.out.println(doc.name + " with score " + doc.score);
        }
    }

    public void rebuild() throws IOException {
        System.out.println("Rebuilding...");
        tokenizer.build();
        System.out.println("Build complete.");
    }

    public void settings() {
        while (true) {
            System.out.println("\nSettings:\n\t1. Adjust returned list length\n\t2. Select path for data files\n\t0. Return to main menu");
            int option = readInt("What would you like to do? ");
            if (option == 0) return;
            else if (option == 1) listLength = readInt("Enter your desired list length.");
            else if (option == 2) {
                System.out.println("Enter your desired path.");
                path = scan.nextLine().trim();
            } else System.out.println("That's not an option, try again.");
        }
    }

    public static void main(String[] args) throws IOException {
        CLI cli = new CLI();
        while (true) {
            System.out.println("\nWelcome to Totally Modern Retrieval System.");
            System.out.println("\t1. Perform a query\n\t2. Rebuild the database\n\t3. Settings\n\t0. Quit");
            int option = cli.readInt("What would you like to do? ");
            if (option == 0) return;
            else if (option == 1) cli.runQuery();
            else if (option == 2) cli.rebuild();
            else if (option == 3) cli.settings();
            else System.out.println("That's not an option, try again.");
        }
    }
}