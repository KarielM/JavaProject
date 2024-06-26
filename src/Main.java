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
//        System.out.println("Hello, " + fullName[0] + "!");


        while (running){
            if (pk == 12 || pk == 10 || pk == 11){
                System.out.print("[Add] book, [Re]move book, [Del]ete library member, [V]iew books, [C]heckout a book, [R]eturn book, or [Q]uit> ");
            } else{System.out.print("[V]iew books, [C]heckout book, [R]eturn book, or [Q]uit> ");}
            String action = inputObj.nextLine().toLowerCase();
            HashMap<String, Boolean> booksDict = db.readAll(conn, tableName);

            switch(action){
                case "v":
                    System.out.print("View [a]ll books, view all [av]ailable books, view by [au]thor, [g]enre, all your [c]hecked-out books or [q]uit> ");
                    String actionView = inputObj.nextLine().toLowerCase();
                    switch(actionView){
                        case "au":
                            System.out.print("Enter author name> ");
                            String authorQuery = inputObj.nextLine();
                            List<String> titlesMatchingAuthor = db.filterByAuthor(conn, tableName, authorQuery);
//                            System.out.println(booksMatchingAuthor);
                            edit.formatList(titlesMatchingAuthor);
                            break;
                        case "g":
                            System.out.print("Enter genre> ");
                            System.out.print("");
                            String genre = inputObj.nextLine();
                            List<String> titlesMatchingGenre = db.filterByGenre(conn, tableName, genre);
//                            System.out.println(titlesMatchingGenre);
                            edit.formatList(titlesMatchingGenre);
                            break;
                        case "c":
                            List<String> bookList = db.viewAllBooksIHaveCheckedOut(conn, pk, "borrowed_book");
                            edit.showMyBooksFormatted(bookList);
                            break;
                        case "a":
                            edit.printBooks(booksDict);
                            break;
                        case "av":
                            edit.viewAvailable(booksDict);
                            break;
                        case "q":
                            break;
                        default:
                            System.out.println("Invalid input provided.");
                        }
                    break;
                case "c":
                    System.out.println("");
                    edit.viewAvailable(booksDict);
                    String[] bookToCheckoutInfo = edit.getBookTitleAndAuthor();
                    db.checkoutBook(conn, tableName, bookToCheckoutInfo, pk);
                    break;
                case "r":
                    List<String> bookList = db.viewAllBooksIHaveCheckedOut(conn, pk, "borrowed_book");
                    System.out.println(" ");
                    edit.showMyBooksFormatted(bookList);

                    if (bookList.size() != 0) {
                        String[] bookToReturnInfo = edit.getBookTitleAndAuthor();
                        db.returnBook(conn, tableName, bookToReturnInfo, "borrowed_book");
                }

                    break;
                case "q":
                    running = false;
                    break;
                default:
                    if (pk == 12 || pk == 10 || pk == 11) {
                        switch (action) {
                            case "add":
                                String[] newBookInfo = edit.getBookTitleAndAuthor();
                                System.out.print("Enter genre or press enter to skip> ");
                                String potentialGenre =inputObj.nextLine().trim();
                                String reviewedGenre = edit.validateStringInput(potentialGenre);

                                db.addBookToDatabase(conn, tableName, newBookInfo, reviewedGenre);
                                break;
                            case "re":
                                String[] bookStuff = edit.getBookTitleAndAuthor();
                                db.removeBook(conn, tableName, bookStuff);
                                break;
                            case "del":
                                String[] fullNameNewMember = edit.getOrCreateMember();
                                db.deleteMember(conn, "library_members", fullNameNewMember);
                                break;
                            default:
                                System.out.println("Invalid input provided.");
                        }
                    }else{System.out.println("Invalid input provided.");}
                }
            }
        }
    }

