import java.util.Scanner;

public class CLI {
    int listLength;
    String path;
    Scanner scan;

    public CLI() {
        listLength = 10;
        path = "./data/";
        scan = new Scanner(System.in);
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

    }

    public void rebuild() {

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

    public static void main(String[] args) {
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