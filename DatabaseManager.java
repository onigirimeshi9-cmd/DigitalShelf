import java.sql.*;
import java.util.*;

public class DatabaseManager {

    //Change these to match your MySQL setup
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "digitalshelf";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";
    //

    private static final String DB_URL =
            "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME
                    + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Manila";

    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        connect();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) instance = new DatabaseManager();
        return instance;
    }

    private void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("Connected to MySQL database.");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Add mysql-connector-j to your classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Failed to connect to MySQL. Check your credentials and that MySQL is running.");
            e.printStackTrace();
        }
    }

    private Connection getConn() throws SQLException {
        if (connection == null || connection.isClosed()) connect();
        return connection;
    }

    // ==================== AUTH ====================

    public boolean adminLogin(String username, String password) {
        try {
            PreparedStatement ps = getConn().prepareStatement(
                    "SELECT id FROM admin WHERE username = ? AND password = ?");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            boolean ok = rs.next();
            rs.close(); ps.close();
            return ok;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public Student studentLogin(String studentId, String password) {
        try {
            PreparedStatement ps = getConn().prepareStatement(
                    "SELECT * FROM students WHERE student_id = ? AND password = ?");
            ps.setString(1, studentId);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            Student s = null;
            if (rs.next()) {
                s = new Student(
                        rs.getInt("id"),
                        rs.getString("student_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("year_level"));
            }
            rs.close(); ps.close();
            return s;
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    // ==================== CATEGORY METHODS ====================

    public ArrayList<Category> getAllCategories() {
        ArrayList<Category> list = new ArrayList<>();
        try {
            Statement stmt = getConn().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM categories ORDER BY name");
            while (rs.next()) {
                list.add(new Category(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description")));
            }
            rs.close(); stmt.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean addCategory(String name, String description) {
        try {
            PreparedStatement ps = getConn().prepareStatement(
                    "INSERT INTO categories (name, description) VALUES (?,?)");
            ps.setString(1, name);
            ps.setString(2, description);
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateCategory(int id, String name, String description) {
        try {
            PreparedStatement ps = getConn().prepareStatement(
                    "UPDATE categories SET name=?, description=? WHERE id=?");
            ps.setString(1, name);
            ps.setString(2, description);
            ps.setInt(3, id);
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteCategory(int id) {
        try {
            PreparedStatement check = getConn().prepareStatement(
                    "SELECT COUNT(*) FROM books WHERE category=?");
            check.setInt(1, id);
            ResultSet rs = check.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            rs.close();
            check.close();

            if (count > 0) {
                return false;
            }

            PreparedStatement ps = getConn().prepareStatement("DELETE FROM categories WHERE id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public Category getCategoryById(int id) {
        try {
            PreparedStatement ps = getConn().prepareStatement(
                    "SELECT * FROM categories WHERE id=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Category c = new Category(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"));
                rs.close();
                ps.close();
                return c;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // ==================== BOOK METHODS ====================

    public ArrayList<Book> getAllBooks(boolean includeArchived) {
        ArrayList<Book> list = new ArrayList<>();
        try {
            String sql = "SELECT id, isbn, title, author, cover_image_path, " +
                    "shelf_location, genre, publication_date, total_copies, " +
                    "available_copies, is_archived FROM books ";
            if (!includeArchived) {
                sql += "WHERE is_archived = 0 ";
            }
            sql += "ORDER BY id";

            Statement stmt = getConn().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Book b = new Book(
                        rs.getInt("id"),
                        rs.getString("isbn"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("cover_image_path"),
                        rs.getString("shelf_location"),
                        rs.getString("genre"),
                        rs.getDate("publication_date"),
                        rs.getInt("total_copies"),
                        rs.getInt("available_copies"),
                        rs.getBoolean("is_archived")
                );
                list.add(b);
            }
            rs.close(); stmt.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public ArrayList<Book> getAllBooks() {
        return getAllBooks(false);
    }

    // Advanced search for books with multiple criteria
    public ArrayList<Book> searchBooks(String searchTerm, String searchType, boolean includeArchived) {
        ArrayList<Book> list = new ArrayList<>();
        try {
            String sql;
            PreparedStatement ps;
            
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return getAllBooks(includeArchived);
            }
            
            String term = "%" + searchTerm.toLowerCase() + "%";
            
            switch (searchType.toLowerCase()) {
                case "title":
                    sql = "SELECT id, isbn, title, author, cover_image_path, shelf_location, " +
                          "genre, publication_date, total_copies, available_copies, is_archived " +
                          "FROM books WHERE LOWER(title) LIKE ? ";
                    if (!includeArchived) sql += "AND is_archived = 0 ";
                    sql += "ORDER BY title";
                    ps = getConn().prepareStatement(sql);
                    ps.setString(1, term);
                    break;
                    
                case "author":
                    sql = "SELECT id, isbn, title, author, cover_image_path, shelf_location, " +
                          "genre, publication_date, total_copies, available_copies, is_archived " +
                          "FROM books WHERE LOWER(author) LIKE ? ";
                    if (!includeArchived) sql += "AND is_archived = 0 ";
                    sql += "ORDER BY author, title";
                    ps = getConn().prepareStatement(sql);
                    ps.setString(1, term);
                    break;
                    
                case "genre":
                    sql = "SELECT id, isbn, title, author, cover_image_path, shelf_location, " +
                          "genre, publication_date, total_copies, available_copies, is_archived " +
                          "FROM books WHERE LOWER(genre) LIKE ? ";
                    if (!includeArchived) sql += "AND is_archived = 0 ";
                    sql += "ORDER BY genre, title";
                    ps = getConn().prepareStatement(sql);
                    ps.setString(1, term);
                    break;
                    
                default: // "all" - search across multiple fields
                    sql = "SELECT id, isbn, title, author, cover_image_path, shelf_location, " +
                          "genre, publication_date, total_copies, available_copies, is_archived " +
                          "FROM books WHERE (LOWER(title) LIKE ? OR LOWER(author) LIKE ? OR LOWER(genre) LIKE ?) ";
                    if (!includeArchived) sql += "AND is_archived = 0 ";
                    sql += "ORDER BY title";
                    ps = getConn().prepareStatement(sql);
                    ps.setString(1, term);
                    ps.setString(2, term);
                    ps.setString(3, term);
                    break;
            }
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Book b = new Book(
                        rs.getInt("id"),
                        rs.getString("isbn"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("cover_image_path"),
                        rs.getString("shelf_location"),
                        rs.getString("genre"),
                        rs.getDate("publication_date"),
                        rs.getInt("total_copies"),
                        rs.getInt("available_copies"),
                        rs.getBoolean("is_archived")
                );
                list.add(b);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return list;
    }

    // Get all unique genres/categories from books
    public ArrayList<String> getAllGenres() {
        ArrayList<String> genres = new ArrayList<>();
        try {
            Statement stmt = getConn().createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT DISTINCT genre FROM books WHERE genre IS NOT NULL AND genre != '' ORDER BY genre");
            while (rs.next()) {
                genres.add(rs.getString("genre"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return genres;
    }

    // Get all unique authors
    public ArrayList<String> getAllAuthors() {
        ArrayList<String> authors = new ArrayList<>();
        try {
            Statement stmt = getConn().createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT DISTINCT author FROM books ORDER BY author");
            while (rs.next()) {
                authors.add(rs.getString("author"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return authors;
    }

    public boolean addBook(String isbn, String title, String author,
                           String coverImagePath, String shelfLocation,
                           String genre, java.util.Date publicationDate, int totalCopies) {
        try {
            PreparedStatement ps = getConn().prepareStatement(
                    "INSERT INTO books (isbn, title, author, cover_image_path, " +
                            "shelf_location, genre, publication_date, total_copies, " +
                            "available_copies, is_archived) VALUES (?,?,?,?,?,?,?,?,?,0)");
            ps.setString(1, isbn);
            ps.setString(2, title);
            ps.setString(3, author);
            ps.setString(4, coverImagePath);
            ps.setString(5, shelfLocation);
            ps.setString(6, genre);
            if (publicationDate != null) {
                ps.setDate(7, new java.sql.Date(publicationDate.getTime()));
            } else {
                ps.setNull(7, Types.DATE);
            }
            ps.setInt(8, totalCopies);
            ps.setInt(9, totalCopies);
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateBook(int id, String isbn, String title, String author,
                              String coverImagePath, String shelfLocation,
                              String genre, java.util.Date publicationDate,
                              int totalCopies, int availableCopies) {
        try {
            PreparedStatement ps = getConn().prepareStatement(
                    "UPDATE books SET isbn=?, title=?, author=?, cover_image_path=?, " +
                            "shelf_location=?, genre=?, publication_date=?, total_copies=?, " +
                            "available_copies=? WHERE id=?");
            ps.setString(1, isbn);
            ps.setString(2, title);
            ps.setString(3, author);
            ps.setString(4, coverImagePath);
            ps.setString(5, shelfLocation);
            ps.setString(6, genre);
            if (publicationDate != null) {
                ps.setDate(7, new java.sql.Date(publicationDate.getTime()));
            } else {
                ps.setNull(7, Types.DATE);
            }
            ps.setInt(8, totalCopies);
            ps.setInt(9, availableCopies);
            ps.setInt(10, id);
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean archiveBook(int id) {
        try {
            PreparedStatement check = getConn().prepareStatement(
                    "SELECT COUNT(*) FROM borrow_records WHERE book_id=? AND status='BORROWED'");
            check.setInt(1, id);
            ResultSet rs = check.executeQuery();
            rs.next();
            int borrowed = rs.getInt(1);
            rs.close();
            check.close();

            if (borrowed > 0) {
                return false;
            }

            PreparedStatement ps = getConn().prepareStatement(
                    "UPDATE books SET is_archived=1, archived_at=NOW() WHERE id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean unarchiveBook(int id) {
        try {
            PreparedStatement ps = getConn().prepareStatement(
                    "UPDATE books SET is_archived=0, archived_at=NULL WHERE id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Deprecated
    public boolean deleteBook(int id) {
        try {
            PreparedStatement ps = getConn().prepareStatement("DELETE FROM books WHERE id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public void updateBookAvailableCopies(int bookId, int availableCopies) {
        try {
            PreparedStatement ps = getConn().prepareStatement(
                    "UPDATE books SET available_copies=? WHERE id=?");
            ps.setInt(1, availableCopies);
            ps.setInt(2, bookId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ==================== STUDENT METHODS ====================

    public ArrayList<Student> getAllStudents() {
        ArrayList<Student> list = new ArrayList<>();
        try {
            Statement stmt = getConn().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM students ORDER BY id");
            while (rs.next()) {
                list.add(new Student(
                        rs.getInt("id"),
                        rs.getString("student_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("year_level")));
            }
            rs.close(); stmt.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean addStudent(String studentId, String name, String email,
                              String yearLevel, String password) {
        try {
            PreparedStatement ps = getConn().prepareStatement(
                    "INSERT INTO students (student_id, name, email, year_level, password) " +
                            "VALUES (?,?,?,?,?)");
            ps.setString(1, studentId);
            ps.setString(2, name);
            ps.setString(3, email);
            ps.setString(4, yearLevel);
            ps.setString(5, password);
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteStudent(int id) {
        try {
            PreparedStatement ps = getConn().prepareStatement("DELETE FROM students WHERE id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ==================== BORROW RECORDS METHODS ====================

    public ArrayList<BorrowRecord> getAllBorrowRecords() {
        ArrayList<BorrowRecord> list = new ArrayList<>();
        try {
            Statement stmt = getConn().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM borrow_records ORDER BY record_id");
            while (rs.next()) {
                BorrowRecord r = new BorrowRecord(
                        rs.getInt("record_id"),
                        rs.getInt("book_id"),
                        rs.getInt("student_id"),
                        rs.getTimestamp("borrow_date"),
                        rs.getTimestamp("due_date"));
                r.setStatus(rs.getString("status"));
                r.setBookTitle(rs.getString("book_title"));
                r.setStudentName(rs.getString("student_name"));
                Timestamp ret = rs.getTimestamp("return_date");
                if (ret != null) r.setReturnDate(ret);
                list.add(r);
            }
            rs.close(); stmt.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public int addBorrowRecord(int bookId, int studentId, java.util.Date borrowDate,
                               java.util.Date dueDate, String bookTitle, String studentName) {
        try {
            PreparedStatement ps = getConn().prepareStatement(
                    "INSERT INTO borrow_records " +
                            "(book_id, student_id, borrow_date, due_date, status, book_title, student_name) " +
                            "VALUES (?,?,?,?,'BORROWED',?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, bookId);
            ps.setInt(2, studentId);
            ps.setTimestamp(3, new Timestamp(borrowDate.getTime()));
            ps.setTimestamp(4, new Timestamp(dueDate.getTime()));
            ps.setString(5, bookTitle);
            ps.setString(6, studentName);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            int id = keys.next() ? keys.getInt(1) : -1;
            keys.close();
            ps.close();
            return id;
        } catch (SQLException e) { e.printStackTrace(); return -1; }
    }

    public void returnBorrowRecord(int recordId) {
        try {
            PreparedStatement ps = getConn().prepareStatement(
                    "UPDATE borrow_records SET status='RETURNED', return_date=? WHERE record_id=?");
            ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            ps.setInt(2, recordId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ==================== UTILITY ====================

    public void close() {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}