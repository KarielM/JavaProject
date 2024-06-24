import java.sql.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner inputObj = new Scanner(System.in);
        dbClass db = new dbClass();
        libraryEdits edit = new libraryEdits();
        Connection conn = db.connectToDatabase("postgres", "postgres", "vulpix20");
        String tableName = "books";
        boolean running;

        String[] fullName = edit.getOrCreateMember();
        int pk = db.getMemberCreateMember(conn, "library_members", fullName);
        running = (pk != -1) ? true : false;
        System.out.println("Hello, " + fullName[0] + "!");


        while (running){

            System.out.print("[V]iew all books, view [A]ll available books, [C]heckout book, [R]eturn book, [Q]uit> ");
            String action = inputObj.nextLine().toLowerCase();
            HashMap<String, Boolean> booksDict = db.readAll(conn, tableName);

            switch(action){
                case "v":
                    edit.printBooks(booksDict);
                    break;
                case "a":
                    edit.viewAvailable(booksDict);
                    break;
                case "c":
                    System.out.print("Which book> ");
                    String title = inputObj.nextLine();
                    db.checkoutBook(conn, tableName, title, pk);
                    break;
                case "r":
                    List<String> bookList = db.viewAllBooksIHaveCheckedOut(conn, pk, "borrowed_book");
                    System.out.println(edit.showMyBooksFormatted(bookList));
                    System.out.print("Which book would you like to return> ");
                    String title2 = inputObj.nextLine();
                    db.returnBook(conn, tableName, title2, "borrowed_book");
                    break;
                case "q":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid input provided.");
            }
        }
    }
}
