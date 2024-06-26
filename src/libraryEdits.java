import java.sql.*;
import java.util.*;

public class libraryEdits {
    private Scanner inputObj = new Scanner(System.in);

    public void printBooks(HashMap<String, Boolean> dict) {
        List<String> bookList = new ArrayList<String>();

        for(Map.Entry<String, Boolean> entry : dict.entrySet()) {
            String status = entry.getValue() ? "Unavailable" : "Available";
            String book = String.format("%s: %s", entry.getKey(), status);
            bookList.add(book);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("These are the books we have in our library: ");

        boolean first = true;
        for (int i = 0; i < bookList.size(); i++) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(bookList.get(i));
            first = false;
        }

        String result = sb.toString();
        System.out.println(result);
    }

    public void viewAvailable(HashMap<String, Boolean> dict) {
        StringBuilder sb = new StringBuilder();
        sb.append("The following book(s) are available for checkout: ");

        for (Map.Entry<String, Boolean> entry : dict.entrySet()) {
            String book = entry.getKey();
            boolean available = entry.getValue();

            if (!available){
                sb.append(book);
                sb.append(", ");
            }
        }
        if (sb.toString().equals("The following book(s) are available for checkout: ")){
            System.out.println("There are no books available to check out.");
        }
        else {
            sb.setLength(sb.length() - 2);
            String result = sb.toString();
            System.out.println(result);
        }
    }

    public String[] getOrCreateMember(){
        System.out.print("Enter full name> ");
        String name = inputObj.nextLine().trim();

        String[] fullName = name.split("\\s+");
        if (fullName.length != 2 || !fullName[0].matches("[a-zA-Z]+") || !fullName[1].matches("[a-zA-Z]+")) {
            System.out.println("Invalid name provided. Please enter your first and last name.");
            return getOrCreateMember();
        }
        return new String[] { fullName[0].trim(), fullName[1].trim() };
    }

    public void showMyBooksFormatted(List<String> books) {
        StringBuilder sb = new StringBuilder();

        if (!books.isEmpty()) {
            sb.append("You have checked out the following books: ");

            for (int i = 0; i < books.size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(books.get(i));
            }

            String formattedBooks = sb.toString();
            System.out.println(formattedBooks);
        } else {
            System.out.println("You have not checked out any books.");
        }
    }

    public String[] getBookTitleAndAuthor(){
        System.out.print("Enter book title> ");
        String title = inputObj.nextLine().trim();

        System.out.print("Enter book author> ");
        String author = inputObj.nextLine().trim();
        return new String[] {author, title};
    }

    public void formatList(List<String> books){
        StringBuilder sb = new StringBuilder();

        if (!books.isEmpty()){
            sb.append("Here are the books that match your search: ");

            for (int i = 0; i < books.size(); i++){
                if (i >0){
                    sb.append(", ");
                }sb.append(books.get(i));
            }
            String formattedString = sb.toString();
            System.out.println(formattedString);
        }
        else{System.out.println(" ");}
    }
}
