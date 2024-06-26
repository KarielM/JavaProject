import java.sql.*;
import java.util.*;
import java.lang.*;

public class dbClass {
    public Connection connectToDatabase(String dbname, String user, String pass) {
        Connection conn = null;
        try{
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+dbname, user, pass);
            if (conn != null) {
                System.out.println("Connection Established");
            } else {
                System.out.println("Connection Failed");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return conn;
    }

    public HashMap<String, Boolean> readAll(Connection conn, String tableName){
        Statement statement;
        ResultSet rs=null;

        HashMap<String, Boolean> booksHashMap=new HashMap<String, Boolean>();

        try{
            String query = String.format(" SELECT * FROM %s", tableName);
            statement = conn.createStatement();
            rs=statement.executeQuery(query);
            while(rs.next()){
//                System.out.print(rs.getString("isbn")+ " ");
                String key = rs.getString("title");
                boolean value = rs.getBoolean("checked_out");
                booksHashMap.put(key, value);
            }
//            System.out.println(booksHashMap);
        }
        catch (Exception e){
            System.out.println(e);
        }
        return booksHashMap;
    }

    public void checkoutBook(Connection conn, String tableName, String title, int member_id){
        PreparedStatement selectStatement = null;
        PreparedStatement updateStatement = null;
        ResultSet rs=null;
        try{
            String selectQuery = String.format("SELECT * FROM %s WHERE title ILIKE ? AND checked_out = false", tableName);
            selectStatement = conn.prepareStatement(selectQuery);
            selectStatement.setString(1, title);
            rs = selectStatement.executeQuery();

            if (rs.next()) {
                String updateQuery = String.format("UPDATE %s SET checked_out = true WHERE title ILIKE ?", tableName);
                updateStatement = conn.prepareStatement(updateQuery);
                updateStatement.setString(1, title);
                updateStatement.executeUpdate();
                System.out.println("Book checked out successfully.");


                String newQuery = "INSERT INTO borrowed_book(member_id, book_id) VALUES (?,?)";
                PreparedStatement nextStatement = conn.prepareStatement(newQuery);
                nextStatement.setInt(1, member_id);
                nextStatement.setInt(2, rs.getInt("isbn"));
                nextStatement.executeUpdate();

            }  else {
                System.out.println("Book not available or already checked out.");
            }
        }
        catch(Exception e){System.out.println(e);}
    }

    public void returnBook(Connection conn, String tableName, String book, String secondTable){
        PreparedStatement selectStatement = null;
        PreparedStatement updateStatement = null;
        ResultSet rs=null;
        try {
            String selectQuery = String.format("SELECT * FROM %s WHERE title ILIKE ? AND checked_out = true", tableName);
            selectStatement = conn.prepareStatement(selectQuery);
            selectStatement.setString(1, book);
            rs = selectStatement.executeQuery();
            if (rs.next()) {
                int bookId = rs.getInt("isbn");
                String updateQuery = String.format("UPDATE %s SET checked_out = false WHERE title ILIKE ?", tableName);
                updateStatement = conn.prepareStatement(updateQuery);
                updateStatement.setString(1, book);
                updateStatement.executeUpdate();
                System.out.println("Book returned successfully.");

                String newQuery = String.format("DELETE FROM %s WHERE book_id = ?", secondTable);
                PreparedStatement statement = conn.prepareStatement(newQuery);
                statement.setInt(1, bookId);
                statement.executeUpdate();


            }  else {
                System.out.println("Invalid.");
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }
    public int getMemberCreateMember(Connection conn, String tableName, String[] memberName){
        ResultSet rs=null;
        Statement statement = null;
        PreparedStatement preparedStatement = null;

        try{
            String query = String.format("SELECT * FROM %s WHERE last_name ILIKE ? and first_name ILIKE ?", tableName);
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, memberName[1]);
            preparedStatement.setString(2, memberName[0]);
            rs=preparedStatement.executeQuery();

            if (rs.next()){
                System.out.println("Query successfully executed");
//                System.out.print(rs.getInt("member_id"));
                return rs.getInt("member_id");


            } else {
                System.out.println("Member not found. Creating member.");
                String creationQuery = String.format("INSERT INTO %s (first_name, last_name) VALUES (?,?)", tableName);
                preparedStatement = conn.prepareStatement(creationQuery, PreparedStatement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, memberName[0]);
                preparedStatement.setString(2, memberName[1]);
                int newEntry = preparedStatement.executeUpdate();
                if (newEntry > 0) {
                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        return generatedId;
//                        System.out.println("New member created with ID: " + generatedId);
                    }
                }
            }
        }
        catch(Exception e){
            System.out.print(e);
        }
        return -1;
    }

    public List<String> viewAllBooksIHaveCheckedOut(Connection conn, int member_id, String tableName){
        ResultSet rs = null;
        PreparedStatement preparedStatement = null;
        List<String> bookList = new ArrayList<String>();

        try{
//            String query = String.format("SELECT b.title FROM %s bb JOIN %s b ON bb.book_id = bb.book_id WHERE bb.member_id = ?",
//                    tableName, "books");
            String query = String.format("SELECT b.title FROM %s bb JOIN books b ON bb.book_id = b.isbn WHERE bb.member_id = ?",
                    tableName);
            preparedStatement =  conn.prepareStatement(query);
            preparedStatement.setInt(1, member_id);
            rs = preparedStatement.executeQuery();

            while(rs.next()){
                String title = rs.getString("title");
                bookList.add(title);
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
        return bookList;
    }

    public void deleteMember(Connection conn, String tableName, String[] memberName){
        PreparedStatement psmt = null;

        try{
            String query = String.format("DELETE FROM %s WHERE first_name ILIKE ? and last_name ILIKE ?", tableName);
            psmt = conn.prepareStatement(query);
            psmt.setString(1, memberName[0]);
            psmt.setString(2, memberName[1]);
            int rowsAffected = psmt.executeUpdate();
            if (rowsAffected == 0){
                System.out.println("Member doesn't exist.");
            }else{
                String status = String.format("%s's membership has been successfully deleted.", memberName[0]);
                System.out.println(status);
            }
        }
        catch(Exception e){
            System.out.print(e);
        }
    }
    public void removeBook(Connection conn, String tableName, String[] bookInfo){
        ResultSet rs = null;
        PreparedStatement psmt = null;
        PreparedStatement psmt2 = null;

        try{
            String query1 = String.format("SELECT * FROM %s WHERE title ILIKE ? and author ILIKE ?", tableName);
            psmt2 = conn.prepareStatement(query1);
            psmt2.setString(1, bookInfo[1]);
            psmt2.setString(2, bookInfo[0]);
            rs = psmt2.executeQuery();

            if (rs.next()){
                boolean checked_outFalse = rs.getBoolean("checked_out");

                if (checked_outFalse){
                    System.out.println("Please wait for the book to be returned to remove it from the library!");
                }else{
                    String query = String.format("DELETE FROM %s WHERE author ILIKE ? and title ILIKE ?", tableName);
                    psmt = conn.prepareStatement(query);
                    psmt.setString(1, bookInfo[0]);
                    psmt.setString(2, bookInfo[1]);
                    int rowsAffected = psmt.executeUpdate();
                    String book = String.format("%s successfully removed from library.", bookInfo[1]);
                    System.out.println(book);
                }
            } else if (!rs.next()) {
                System.out.println("Book not found in library.");
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    public void addBookToDatabase(Connection conn, String tableName, String[] bookInfo){
        ResultSet rs = null;
        PreparedStatement psmt = null;

        try{
            String query = String.format("INSERT INTO %s(title, author) VALUES (?,?)", tableName);
            psmt = conn.prepareStatement(query);
            psmt.setString(1,bookInfo[1]);
            psmt.setString(2,bookInfo[0]);
            psmt.executeUpdate();
            System.out.println("Book successfully added to library!");
        }
        catch(SQLException e){
            if (e.getSQLState().equals("23505")) {
                System.out.println("This book already exists in your library!");
            }else{System.out.println(e);}
            }
    }
    public List<String> filterByAuthor(Connection conn, String tableName, String author){
        ResultSet rs = null;
        PreparedStatement psmt = null;
        List<String> titlesMatchingAuthor = new ArrayList<String>();

        try{
            String query = String.format("SELECT * FROM %s WHERE author ILIKE ?", tableName);
            psmt = conn.prepareStatement(query);
            psmt.setString(1, author);
            rs = psmt.executeQuery();


            while(rs.next()){
                titlesMatchingAuthor.add(rs.getString("title"));
            } if (!rs.next() && titlesMatchingAuthor.size() == 0){System.out.println("No books in this library match your provided author.");}
        }
        catch(Exception e){System.out.println(e);}
        return titlesMatchingAuthor;
    }
    public List<String> filterByGenre(Connection conn, String tableName, String genre){
        ResultSet rs = null;
        PreparedStatement psmt = null;
        List<String> titlesMatchingGenre = new ArrayList<String>();

        try{
            String query = String.format("SELECT * FROM %s WHERE genre ILIKE ?", tableName);
            psmt = conn.prepareStatement(query);
            psmt.setString(1, genre);
            rs = psmt.executeQuery();


            while (rs.next()) {
                titlesMatchingGenre.add(rs.getString("title"));
            }if (!rs.next() && titlesMatchingGenre.size() == 0) {System.out.println("No books in this library match your provided genre.");}
        }
        catch(Exception e){System.out.println(e);}
        return titlesMatchingGenre;
    }

}
