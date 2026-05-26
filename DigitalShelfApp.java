import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;

public class DigitalShelfApp extends JFrame {

    //COLOR
    private static final Color TEAL        = new Color(61, 141, 122);
    private static final Color TEAL_DARK   = new Color(45, 110, 95);
    private static final Color TEAL_LIGHT  = new Color(80, 170, 150);
    private static final Color LEMON       = new Color(255, 250, 205);
    private static final Color CARD_WHITE  = Color.WHITE;
    private static final Color DANGER      = new Color(200, 70, 60);
    private static final Color BG          = LEMON;
    private static final Color TEXT_DARK   = new Color(35, 35, 35);
    private static final Color TEXT_WHITE  = Color.WHITE;

    //APP STATE
    private Student currentStudent;
    private ArrayList<Book> booksList        = new ArrayList<>();
    private ArrayList<BorrowRecord> borrowRecordsList = new ArrayList<>();
    private final DatabaseManager db         = DatabaseManager.getInstance();

    //SWING REFS
    private JFrame loginFrame;
    private JFrame mainFrame;
    private JPanel booksPanel, myBooksPanel, historyPanel;
    private JTextField searchField;
    private String currentSearch = "";

    //ENTRY POINT
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(DigitalShelfApp::new);
    }

    public DigitalShelfApp() {
        showLoginScreen();
    }

    //  LOGIN SCREEN
    private void showLoginScreen() {
        loginFrame = new JFrame("DigitalShelf — Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(480, 560);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(TEAL);

        // Header
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(TEAL);
        header.setBorder(new EmptyBorder(40, 30, 30, 30));

        JLabel titleLbl = new JLabel("DigitalShelf");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 34));
        titleLbl.setForeground(TEXT_WHITE);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLbl = new JLabel("School Library Management System");
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subLbl.setForeground(new Color(255, 255, 255, 210));
        subLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(titleLbl);
        header.add(Box.createVerticalStrut(8));
        header.add(subLbl);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(LEMON);
        form.setBorder(new EmptyBorder(35, 45, 35, 45));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.fill   = GridBagConstraints.HORIZONTAL;

        // Username row
        JLabel userLbl = label("Username / Student ID:", Font.BOLD, 14);
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        form.add(userLbl, g);

        JTextField usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(TEAL, 2, true), new EmptyBorder(8, 12, 8, 12)));
        g.gridy = 1;
        form.add(usernameField, g);

        // Password row
        JLabel passLbl = label("Password:", Font.BOLD, 14);
        g.gridy = 2;
        form.add(passLbl, g);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(TEAL, 2, true), new EmptyBorder(8, 12, 8, 12)));
        g.gridy = 3;
        form.add(passwordField, g);

        // Hint
        JLabel hintLbl = label("Students: use your Student ID as username & default password",
                Font.ITALIC, 11);
        hintLbl.setForeground(new Color(100, 100, 100));
        g.gridy = 4;
        form.add(hintLbl, g);

        // Login button
        JButton loginBtn = new JButton("LOGIN");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginBtn.setBackground(TEAL);
        loginBtn.setForeground(TEXT_WHITE);
        loginBtn.setOpaque(true);
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.setBorder(new EmptyBorder(12, 20, 12, 20));
        g.gridy = 5;
        g.insets = new Insets(20, 8, 4, 8);
        form.add(loginBtn, g);

        //Create Account link-style button
        JButton registerBtn = new JButton("Don't have an account? Create one here");
        registerBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        registerBtn.setBackground(LEMON);
        registerBtn.setForeground(TEAL_DARK);
        registerBtn.setOpaque(false);
        registerBtn.setContentAreaFilled(false);
        registerBtn.setBorderPainted(false);
        registerBtn.setFocusPainted(false);
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        g.gridy = 6;
        g.insets = new Insets(0, 8, 8, 8);
        form.add(registerBtn, g);

        // Actions
        Runnable doLogin = () -> performLogin(usernameField, passwordField);
        loginBtn.addActionListener(e -> doLogin.run());
        usernameField.addActionListener(e -> passwordField.requestFocus());
        passwordField.addActionListener(e -> doLogin.run());
        registerBtn.addActionListener(e -> showRegisterDialog());

        root.add(header, BorderLayout.NORTH);
        root.add(form,   BorderLayout.CENTER);

        loginFrame.add(root);
        loginFrame.setVisible(true);
        usernameField.requestFocusInWindow();
    }

    //REGISTER / CREATE ACCOUNT
    private void showRegisterDialog() {
        JDialog dialog = new JDialog(loginFrame, "Create New Account", true);
        dialog.setSize(460, 480);
        dialog.setLocationRelativeTo(loginFrame);
        dialog.setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(LEMON);

        // Dialog header
        JPanel hdr = new JPanel();
        hdr.setBackground(TEAL);
        hdr.setBorder(new EmptyBorder(20, 30, 20, 30));
        hdr.setLayout(new BoxLayout(hdr, BoxLayout.Y_AXIS));

        JLabel ttl = new JLabel("Create Student Account");
        ttl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        ttl.setForeground(TEXT_WHITE);
        ttl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Fill in your details to register");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(255, 255, 255, 200));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        hdr.add(ttl);
        hdr.add(Box.createVerticalStrut(6));
        hdr.add(sub);
        root.add(hdr, BorderLayout.NORTH);

        // Form fields
        JTextField sidField   = new JTextField(15);
        JTextField nameField  = new JTextField(20);
        JTextField emailField = new JTextField(25);
        String[] years = {"1st Year", "2nd Year", "3rd Year", "4th Year"};
        JComboBox<String> yearCB = new JComboBox<>(years);
        JPasswordField passField  = new JPasswordField(15);
        JPasswordField pass2Field = new JPasswordField(15);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(LEMON);
        formPanel.setBorder(new EmptyBorder(20, 40, 10, 40));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.fill   = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.WEST;

        String[] lbls = {"Student ID:", "Full Name:", "Email:", "Year Level:", "Password:", "Confirm Password:"};
        JComponent[] flds = {sidField, nameField, emailField, yearCB, passField, pass2Field};

        for (int i = 0; i < lbls.length; i++) {
            gc.gridx = 0; gc.gridy = i; gc.gridwidth = 1; gc.weightx = 0;
            JLabel l = new JLabel(lbls[i]);
            l.setFont(new Font("Segoe UI", Font.BOLD, 13));
            l.setForeground(TEXT_DARK);
            formPanel.add(l, gc);

            gc.gridx = 1; gc.weightx = 1;
            flds[i].setFont(new Font("Segoe UI", Font.PLAIN, 13));
            formPanel.add(flds[i], gc);
        }

        // Hint label
        JLabel hint = new JLabel("  If password is blank, Student ID will be used as default.");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(Color.GRAY);
        gc.gridx = 0; gc.gridy = lbls.length; gc.gridwidth = 2; gc.weightx = 1;
        gc.insets = new Insets(2, 6, 6, 6);
        formPanel.add(hint, gc);

        root.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        btnRow.setBackground(LEMON);

        JButton createBtn = makeBtn("Create Account", TEAL, TEXT_WHITE);
        JButton cancelBtn = makeBtn("Cancel", new Color(150, 150, 150), TEXT_WHITE);

        btnRow.add(createBtn);
        btnRow.add(cancelBtn);
        root.add(btnRow, BorderLayout.SOUTH);

        // Actions
        createBtn.addActionListener(e -> {
            String sid   = sidField.getText().trim();
            String name  = nameField.getText().trim();
            String email = emailField.getText().trim();
            String year  = (String) yearCB.getSelectedItem();
            String pass  = new String(passField.getPassword()).trim();
            String pass2 = new String(pass2Field.getPassword()).trim();

            if (sid.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Student ID and Full Name are required.", "Missing Fields",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!pass.isEmpty() && !pass.equals(pass2)) {
                JOptionPane.showMessageDialog(dialog,
                        "Passwords do not match. Please try again.", "Password Mismatch",
                        JOptionPane.WARNING_MESSAGE);
                passField.setText("");
                pass2Field.setText("");
                passField.requestFocus();
                return;
            }

            if (pass.isEmpty()) pass = sid;

            if (db.addStudent(sid, name, email, year, pass)) {
                JOptionPane.showMessageDialog(dialog,
                        "✅ Account created successfully!\n\n" +
                                "Student ID: " + sid + "\n" +
                                "Password:   " + pass + "\n\n" +
                                "You can now log in with these credentials.",
                        "Account Created", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Failed to create account.\nStudent ID \"" + sid + "\" may already be registered.",
                        "Registration Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.add(root);
        dialog.setVisible(true);
    }

    private JButton makeBtn(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBackground(bg);
        b.setForeground(fg);
        b.setOpaque(true);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(10, 20, 10, 20));
        return b;
    }

    private void performLogin(JTextField usernameField, JPasswordField passwordField) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(loginFrame,
                    "Please enter both username and password.", "Login Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check admin
        if (db.adminLogin(username, password)) {
            loginFrame.dispose();
            new AdminFrame();
            return;
        }

        // Student check
        Student student = db.studentLogin(username, password);
        if (student != null) {
            currentStudent = student;
            booksList         = db.getAllBooks();
            borrowRecordsList = db.getAllBorrowRecords();
            loginFrame.dispose();
            setupMainFrame();
        } else {
            JOptionPane.showMessageDialog(loginFrame,
                    "Invalid username or password.\nPlease try again.", "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }

    //  MAIN STUDENT UI
    private void setupMainFrame() {
        mainFrame = new JFrame("DigitalShelf — " + currentStudent.getName());
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1200, 800);
        mainFrame.setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        root.add(createHeader(),  BorderLayout.NORTH);
        root.add(createTabs(),    BorderLayout.CENTER);
        root.add(createFooter(),  BorderLayout.SOUTH);

        mainFrame.add(root);
        mainFrame.setVisible(true);
    }

    // Header
    private JPanel createHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(TEAL);
        p.setBorder(new EmptyBorder(18, 30, 18, 30));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBackground(TEAL);

        JLabel title = new JLabel("DigitalShelf");
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(TEXT_WHITE);

        JLabel sub = new JLabel("School Library Management System");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sub.setForeground(new Color(255, 255, 255, 210));

        left.add(title);
        left.add(Box.createVerticalStrut(4));
        left.add(sub);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setBackground(TEAL);

        JLabel welcome = new JLabel("Welcome, " + currentStudent.getName()
                + "  |  " + currentStudent.getStudentId());
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        welcome.setForeground(TEXT_WHITE);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logoutBtn.setBackground(TEAL_DARK);
        logoutBtn.setForeground(TEXT_WHITE);
        logoutBtn.setOpaque(true);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> logout());

        right.add(welcome);
        right.add(Box.createHorizontalStrut(12));
        right.add(logoutBtn);

        p.add(left,  BorderLayout.WEST);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    // Tabs
    private JTabbedPane createTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabs.setBackground(BG);
        tabs.setForeground(TEXT_DARK);

        tabs.addTab("Browse Books",        createBrowsePanel());
        tabs.addTab("My Borrowed Books",   createMyBooksPanel());
        tabs.addTab("Borrow History",      createHistoryPanel());

        return tabs;
    }

    // ==================== HELPER: LOAD IMAGE FROM URL OR FILE ====================
    private ImageIcon loadBookCover(String imagePath, int width, int height) {
        if (imagePath == null || imagePath.isEmpty()) {
            return createPlaceholderIcon(width, height);
        }

        ImageIcon icon = null;

        try {
            // Check if it's a URL (starts with http:// or https://)
            if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
                URL url = new URL(imagePath);
                Image img = ImageIO.read(url);
                if (img != null) {
                    Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                    icon = new ImageIcon(scaledImg);
                }
            } else {
                // Local file path
                java.io.File file = new java.io.File(imagePath);
                if (file.exists()) {
                    Image img = ImageIO.read(file);
                    if (img != null) {
                        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                        icon = new ImageIcon(scaledImg);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Could not load image: " + imagePath + " - " + e.getMessage());
        }

        if (icon == null) {
            return createPlaceholderIcon(width, height);
        }

        return icon;
    }

    private ImageIcon createPlaceholderIcon(int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Book shape
        g2d.setColor(TEAL_LIGHT);
        g2d.fillRoundRect(0, 0, width, height, 10, 10);
        g2d.setColor(TEAL_DARK);
        g2d.fillRoundRect(0, 0, width / 4, height, 5, 5);
        g2d.setColor(new Color(250, 250, 250));
        g2d.fillRect(width / 4 + 2, 5, width - width / 4 - 7, height - 10);
        g2d.setColor(new Color(180, 180, 180));
        for (int i = 0; i < 4; i++) {
            g2d.drawLine(width / 4 + 5, 15 + i * (height - 20) / 4, width - 5, 15 + i * (height - 20) / 4);
        }

        // Text
        g2d.setColor(Color.GRAY);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
        FontMetrics fm = g2d.getFontMetrics();
        String text = "No Cover";
        int textX = (width - fm.stringWidth(text)) / 2;
        int textY = height / 2;
        g2d.drawString(text, textX, textY);

        g2d.dispose();
        return new ImageIcon(img);
    }

    // Browse Books with Enhanced Search
    private JPanel createBrowsePanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(BG);
        container.setBorder(new EmptyBorder(18, 18, 18, 18));

        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setBackground(BG);
        searchPanel.setBorder(new EmptyBorder(0, 0, 16, 0));
        
        // Search input row
        JPanel searchRow = new JPanel(new BorderLayout(8, 0));
        searchRow.setBackground(BG);
        
        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(TEAL, 2, true), new EmptyBorder(9, 14, 9, 14)));
        searchField.setToolTipText("Search by title, author, or genre...");
        
        JComboBox<String> searchTypeCombo = new JComboBox<>(new String[]{"All Fields", "Title", "Author", "Genre"});
        searchTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchTypeCombo.setBackground(Color.WHITE);
        searchTypeCombo.setPreferredSize(new Dimension(120, 38));
        
        JButton searchBtn = iconButton("Search", TEAL);
        JButton clearBtn = iconButton("Clear", new Color(150, 150, 150));
        
        searchRow.add(searchField, BorderLayout.CENTER);
        
        JPanel searchControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        searchControls.setBackground(BG);
        searchControls.add(searchTypeCombo);
        searchControls.add(searchBtn);
        searchControls.add(clearBtn);
        searchRow.add(searchControls, BorderLayout.EAST);
        
        // Quick filter panel
        JPanel quickFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        quickFilterPanel.setBackground(BG);
        quickFilterPanel.setBorder(new EmptyBorder(8, 0, 0, 0));
        
        JLabel quickLabel = new JLabel("Browse by genre:");
        quickLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        quickLabel.setForeground(TEXT_DARK);
        quickFilterPanel.add(quickLabel);
        
        // Add genre filter buttons - limit to first 10 to avoid crowding
        ArrayList<String> genres = db.getAllGenres();
        int genreCount = 0;
        for (String genre : genres) {
            if (genreCount >= 12) break; // Limit to 12 genres to prevent layout issues
            JButton genreBtn = new JButton(genre);
            genreBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            genreBtn.setBackground(new Color(245, 245, 245));
            genreBtn.setForeground(TEAL_DARK);
            genreBtn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(4, 10, 4, 10)));
            genreBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            genreBtn.addActionListener(e -> {
                searchField.setText(genre);
                searchTypeCombo.setSelectedIndex(3); // Genre
                performStudentSearch(searchField, searchTypeCombo);
            });
            quickFilterPanel.add(genreBtn);
            genreCount++;
        }
        
        // Add "More" button if there are many genres
        if (genres.size() > 12) {
            JButton moreBtn = new JButton("+ More");
            moreBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
            moreBtn.setBackground(new Color(220, 220, 220));
            moreBtn.setForeground(TEAL_DARK);
            moreBtn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(4, 10, 4, 10)));
            moreBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            moreBtn.addActionListener(e -> showAllGenresDialog(genres, searchField, searchTypeCombo));
            quickFilterPanel.add(moreBtn);
        }
        
        // Add author filter dropdown
        JLabel authorLabel = new JLabel("  |  Authors:");
        authorLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        authorLabel.setForeground(TEXT_DARK);
        quickFilterPanel.add(authorLabel);
        
        JComboBox<String> authorCombo = new JComboBox<>();
        authorCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        authorCombo.setPreferredSize(new Dimension(200, 28));
        authorCombo.addItem("-- Select Author --");
        ArrayList<String> authors = db.getAllAuthors();
        for (String author : authors) {
            authorCombo.addItem(author);
        }
        authorCombo.addActionListener(e -> {
            String selectedAuthor = (String) authorCombo.getSelectedItem();
            if (selectedAuthor != null && !selectedAuthor.equals("-- Select Author --")) {
                searchField.setText(selectedAuthor);
                searchTypeCombo.setSelectedIndex(2); // Author
                performStudentSearch(searchField, searchTypeCombo);
            }
        });
        quickFilterPanel.add(authorCombo);
        
        searchPanel.add(searchRow, BorderLayout.NORTH);
        searchPanel.add(quickFilterPanel, BorderLayout.CENTER);
        
        // Book grid - use GridLayout with proper spacing
        booksPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        booksPanel.setBackground(BG);
        refreshBrowse();

        JScrollPane scroll = new JScrollPane(booksPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getHorizontalScrollBar().setUnitIncrement(16);

        // Search actions
        searchBtn.addActionListener(e -> performStudentSearch(searchField, searchTypeCombo));
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            searchTypeCombo.setSelectedIndex(0);
            authorCombo.setSelectedIndex(0);
            refreshBrowse();
        });
        searchField.addActionListener(e -> performStudentSearch(searchField, searchTypeCombo));

        container.add(searchPanel, BorderLayout.NORTH);
        container.add(scroll, BorderLayout.CENTER);
        return container;
    }

    // Helper method to show all genres in a dialog
    private void showAllGenresDialog(ArrayList<String> genres, JTextField searchField, JComboBox<String> searchTypeCombo) {
        JDialog dialog = new JDialog(mainFrame, "All Genres", true);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(mainFrame);
        
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBackground(LEMON);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        
        for (String genre : genres) {
            JButton genreBtn = new JButton(genre);
            genreBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            genreBtn.setBackground(new Color(245, 245, 245));
            genreBtn.setForeground(TEAL_DARK);
            genreBtn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(8, 12, 8, 12)));
            genreBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            genreBtn.addActionListener(e -> {
                searchField.setText(genre);
                searchTypeCombo.setSelectedIndex(3);
                performStudentSearch(searchField, searchTypeCombo);
                dialog.dispose();
            });
            panel.add(genreBtn);
        }
        
        dialog.add(scroll);
        dialog.setVisible(true);
    }

    private void performStudentSearch(JTextField searchField, JComboBox<String> searchTypeCombo) {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty() || searchTerm.equals("Search by title or author...")) {
            refreshBrowse();
            return;
        }
        
        String searchType = ((String) searchTypeCombo.getSelectedItem()).toLowerCase();
        if (searchType.equals("all fields")) searchType = "all";
        else if (searchType.equals("title")) searchType = "title";
        else if (searchType.equals("author")) searchType = "author";
        else if (searchType.equals("genre")) searchType = "genre";
        
        ArrayList<Book> results = db.searchBooks(searchTerm, searchType, false);
        
        booksPanel.removeAll();
        if (results.isEmpty()) {
            JLabel none = new JLabel("No books found matching \"" + searchTerm + "\"");
            none.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            none.setForeground(Color.GRAY);
            booksPanel.add(none);
        } else {
            for (Book b : results) {
                if (!b.isArchived()) {
                    booksPanel.add(createCard(b, false, null));
                }
            }
        }
        booksPanel.revalidate();
        booksPanel.repaint();
    }

    private void refreshBrowse() {
        booksPanel.removeAll();
        for (Book b : booksList) {
            if (!b.isArchived()) {
                booksPanel.add(createCard(b, false, null));
            }
        }
        if (booksPanel.getComponentCount() == 0) {
            JLabel none = new JLabel("No books available in the library.");
            none.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            none.setForeground(Color.GRAY);
            booksPanel.add(none);
        }
        booksPanel.revalidate();
        booksPanel.repaint();
    }

    // My Borrowed Books
    private JPanel createMyBooksPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(BG);
        container.setBorder(new EmptyBorder(18, 18, 18, 18));

        myBooksPanel = new JPanel(new GridLayout(0, 3, 18, 18));
        myBooksPanel.setBackground(BG);
        refreshMyBooks();

        JScrollPane scroll = new JScrollPane(myBooksPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        container.add(scroll, BorderLayout.CENTER);
        return container;
    }

    private void refreshMyBooks() {
        myBooksPanel.removeAll();
        for (BorrowRecord r : borrowRecordsList) {
            if (r.getStudentId() == currentStudent.getId() && "BORROWED".equals(r.getStatus())) {
                Book b = getBookById(r.getBookId());
                if (b != null) {
                    myBooksPanel.add(createCard(b, true, r));
                }
            }
        }
        if (myBooksPanel.getComponentCount() == 0) {
            JLabel none = new JLabel("You have no currently borrowed books.");
            none.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            none.setForeground(Color.GRAY);
            myBooksPanel.add(none);
        }
        myBooksPanel.revalidate();
        myBooksPanel.repaint();
    }

    // History
    private JPanel createHistoryPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(BG);
        container.setBorder(new EmptyBorder(18, 18, 18, 18));

        historyPanel = new JPanel();
        historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.Y_AXIS));
        historyPanel.setBackground(BG);
        refreshHistory();

        JScrollPane scroll = new JScrollPane(historyPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        container.add(scroll, BorderLayout.CENTER);
        return container;
    }

    private void refreshHistory() {
        historyPanel.removeAll();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");

        for (BorrowRecord r : borrowRecordsList) {
            if (r.getStudentId() == currentStudent.getId()) {
                Book b = getBookById(r.getBookId());
                if (b != null) {
                    historyPanel.add(createHistoryCard(b, r, sdf));
                    historyPanel.add(Box.createVerticalStrut(10));
                }
            }
        }
        if (historyPanel.getComponentCount() == 0) {
            JLabel none = new JLabel("No borrowing history yet.");
            none.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            none.setForeground(Color.GRAY);
            historyPanel.add(none);
        }
        historyPanel.revalidate();
        historyPanel.repaint();
    }

    private JPanel createHistoryCard(Book book, BorrowRecord r, SimpleDateFormat sdf) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(CARD_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(14, 14, 14, 14)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(CARD_WHITE);

        info.add(bold(book.getTitle(), 15));
        info.add(Box.createVerticalStrut(4));
        info.add(italic("by " + book.getAuthor(), 12));
        info.add(Box.createVerticalStrut(4));

        Color sc = "RETURNED".equals(r.getStatus()) ? TEAL : DANGER;
        JLabel statusLbl = new JLabel("Status: " + r.getStatus());
        statusLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLbl.setForeground(sc);
        info.add(statusLbl);
        info.add(Box.createVerticalStrut(4));

        info.add(small("Borrowed: " + sdf.format(r.getBorrowDate())
                + "   |   Due: " + sdf.format(r.getDueDate())));

        if (r.getReturnDate() != null)
            info.add(small("Returned: " + sdf.format(r.getReturnDate())));

        card.add(info, BorderLayout.CENTER);
        return card;
    }

    // Book Card WITH COVER IMAGE
    private JPanel createCard(Book book, boolean isMyBooks, BorrowRecord record) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(CARD_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(14, 14, 14, 14)));

        // Top panel with cover image and title
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBackground(CARD_WHITE);

        // Load and display cover image
        ImageIcon coverIcon = loadBookCover(book.getCoverImagePath(), 100, 130);
        JLabel coverLabel = new JLabel(coverIcon);
        coverLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        coverLabel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        JLabel titleLbl = centeredBold(book.getTitle(), 14);
        JLabel authLbl  = centeredItalic("by " + book.getAuthor(), 11);

        top.add(coverLabel);
        top.add(Box.createVerticalStrut(8));
        top.add(titleLbl);
        top.add(Box.createVerticalStrut(4));
        top.add(authLbl);

        // Middle - details
        JPanel mid = new JPanel();
        mid.setLayout(new BoxLayout(mid, BoxLayout.Y_AXIS));
        mid.setBackground(CARD_WHITE);
        mid.setBorder(new EmptyBorder(8, 0, 8, 0));

        mid.add(small("Genre: " + (book.getGenre() != null ? book.getGenre() : "N/A")));
        mid.add(Box.createVerticalStrut(3));
        mid.add(small("Shelf: " + (book.getShelfLocation() != null ? book.getShelfLocation() : "N/A")));
        mid.add(Box.createVerticalStrut(3));
        mid.add(small("ID: " + book.getId()));
        mid.add(Box.createVerticalStrut(3));

        JLabel copies = new JLabel("Available: " + book.getAvailableCopies() + "/" + book.getTotalCopies());
        copies.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        copies.setForeground(book.isAvailable() ? TEAL : DANGER);
        mid.add(copies);

        // Bottom - action buttons
        JPanel bot = new JPanel(new BorderLayout(8, 0));
        bot.setBackground(CARD_WHITE);

        JLabel statusBadge = new JLabel(book.isAvailable() ? "Available" : "Borrowed");
        statusBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        statusBadge.setOpaque(true);
        statusBadge.setBorder(new EmptyBorder(4, 8, 4, 8));
        statusBadge.setHorizontalAlignment(SwingConstants.CENTER);
        if (book.isAvailable()) {
            statusBadge.setBackground(new Color(61, 141, 122, 40));
            statusBadge.setForeground(TEAL);
        } else {
            statusBadge.setBackground(new Color(200, 70, 60, 30));
            statusBadge.setForeground(DANGER);
        }

        JButton action;
        if (isMyBooks && record != null) {
            action = colorButton("Return", TEAL);
            action.addActionListener(e -> returnBook(record));
        } else {
            action = colorButton(book.isAvailable() ? "Borrow" : "Unavailable",
                    book.isAvailable() ? TEAL : new Color(189, 195, 199));
            action.setEnabled(book.isAvailable());
            if (book.isAvailable()) action.addActionListener(e -> borrowBook(book));
        }
        action.setFont(new Font("Segoe UI", Font.BOLD, 12));

        bot.add(statusBadge, BorderLayout.WEST);
        bot.add(action,      BorderLayout.EAST);

        card.add(top, BorderLayout.NORTH);
        card.add(mid, BorderLayout.CENTER);
        card.add(bot, BorderLayout.SOUTH);

        // Hover + click for detail
        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(TEAL, 2, true), new EmptyBorder(14, 14, 14, 14)));
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(200, 200, 200), 1, true), new EmptyBorder(14, 14, 14, 14)));
                card.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
            public void mouseClicked(MouseEvent e) {
                if (!(e.getSource() instanceof JButton)) showBookDetailDialog(book, isMyBooks, record);
            }
        });
        return card;
    }

    // BOOK DETAIL DIALOG WITH COVER IMAGE
    private void showBookDetailDialog(Book book, boolean isMyBooks, BorrowRecord record) {
        JDialog dialog = new JDialog(mainFrame, "Book Details", true);
        dialog.setSize(550, 650);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(LEMON);

        // Header with cover image
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(TEAL);
        header.setBorder(new EmptyBorder(20, 20, 20, 20));
        header.setPreferredSize(new Dimension(550, 220));

        // Load cover image
        ImageIcon coverIcon = loadBookCover(book.getCoverImagePath(), 120, 160);
        JLabel coverLabel = new JLabel(coverIcon);
        coverLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));

        // Title and author in header
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(TEAL);

        JLabel titleLbl = new JLabel("<html><div style='text-align:center; width:300px'>" + book.getTitle() + "</div></html>");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel authorLbl = new JLabel("by " + book.getAuthor());
        authorLbl.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        authorLbl.setForeground(new Color(255, 255, 255, 220));
        authorLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        textPanel.add(titleLbl);
        textPanel.add(Box.createVerticalStrut(8));
        textPanel.add(authorLbl);

        header.add(coverLabel, BorderLayout.WEST);
        header.add(textPanel, BorderLayout.CENTER);

        root.add(header, BorderLayout.NORTH);

        // Details panel
        JPanel details = new JPanel();
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        details.setBackground(LEMON);
        details.setBorder(new EmptyBorder(22, 36, 16, 36));

        // Divider
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(200, 200, 200));
        details.add(sep);
        details.add(Box.createVerticalStrut(16));

        // Info rows
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String pubDate = book.getPublicationDate() != null ? sdf.format(book.getPublicationDate()) : "Not specified";

        String[][] rows = {
                {"ISBN",             book.getIsbn() != null ? book.getIsbn() : "Not specified"},
                {"Genre",            book.getGenre() != null ? book.getGenre() : "Not specified"},
                {"Shelf Location",   book.getShelfLocation() != null ? book.getShelfLocation() : "Not specified"},
                {"Publication Date", pubDate},
                {"Book ID",          String.valueOf(book.getId())},
                {"Total Copies",     String.valueOf(book.getTotalCopies())},
                {"Available Copies", book.getAvailableCopies() + " / " + book.getTotalCopies()},
                {"Status",           book.isAvailable() ? "Available" : "Not Available"},
        };

        for (String[] row : rows) {
            JPanel rowPanel = new JPanel(new BorderLayout());
            rowPanel.setBackground(LEMON);
            rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

            JLabel key = new JLabel(row[0]);
            key.setFont(new Font("Segoe UI", Font.BOLD, 13));
            key.setForeground(new Color(100, 100, 100));

            JLabel val = new JLabel(row[1]);
            val.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            if (row[0].equals("Status"))
                val.setForeground(book.isAvailable() ? TEAL : DANGER);
            else
                val.setForeground(TEXT_DARK);

            rowPanel.add(key, BorderLayout.WEST);
            rowPanel.add(val, BorderLayout.EAST);
            details.add(rowPanel);
            details.add(Box.createVerticalStrut(8));
        }

        details.add(Box.createVerticalStrut(10));

        // Action button
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnRow.setBackground(LEMON);

        if (isMyBooks && record != null) {
            JButton retBtn = makeBtn("Return Book", TEAL, TEXT_WHITE);
            retBtn.addActionListener(e -> { dialog.dispose(); returnBook(record); });
            btnRow.add(retBtn);
        } else if (book.isAvailable()) {
            JButton borrowBtn = makeBtn("Borrow This Book", TEAL, TEXT_WHITE);
            borrowBtn.addActionListener(e -> { dialog.dispose(); borrowBook(book); });
            btnRow.add(borrowBtn);
        } else {
            JButton unavailBtn = makeBtn("Not Available", new Color(180, 180, 180), TEXT_WHITE);
            unavailBtn.setEnabled(false);
            btnRow.add(unavailBtn);
        }

        JButton closeBtn = makeBtn("Close", new Color(150, 150, 150), TEXT_WHITE);
        closeBtn.addActionListener(e -> dialog.dispose());
        btnRow.add(closeBtn);

        details.add(btnRow);

        JScrollPane scroll = new JScrollPane(details);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(12);

        root.add(scroll, BorderLayout.CENTER);
        dialog.add(root);
        dialog.setVisible(true);
    }

    // BORROW / RETURN
    private void borrowBook(Book book) {
        // Check if already borrowed
        for (BorrowRecord r : borrowRecordsList) {
            if (r.getStudentId() == currentStudent.getId()
                    && r.getBookId() == book.getId()
                    && "BORROWED".equals(r.getStatus())) {
                JOptionPane.showMessageDialog(mainFrame,
                        "You have already borrowed this book.", "Already Borrowed",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        int confirm = JOptionPane.showConfirmDialog(mainFrame,
                "Borrow \"" + book.getTitle() + "\" by " + book.getAuthor() + "?",
                "Confirm Borrow", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Calendar cal = Calendar.getInstance();
            Date borrowDate = cal.getTime();
            cal.add(Calendar.DAY_OF_MONTH, 14);
            Date dueDate = cal.getTime();

            int recId = db.addBorrowRecord(book.getId(), currentStudent.getId(),
                    borrowDate, dueDate, book.getTitle(), currentStudent.getName());

            book.setAvailableCopies(book.getAvailableCopies() - 1);
            db.updateBookAvailableCopies(book.getId(), book.getAvailableCopies());

            BorrowRecord rec = new BorrowRecord(recId, book.getId(),
                    currentStudent.getId(), borrowDate, dueDate);
            rec.setBookTitle(book.getTitle());
            rec.setStudentName(currentStudent.getName());
            borrowRecordsList.add(rec);

            JOptionPane.showMessageDialog(mainFrame,
                    "Book borrowed successfully!\n\n" +
                            "Title: " + book.getTitle() + "\n" +
                            "Due:   " + new SimpleDateFormat("MMM dd, yyyy").format(dueDate),
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            refreshAll();
        }
    }

    private void returnBook(BorrowRecord record) {
        int confirm = JOptionPane.showConfirmDialog(mainFrame,
                "Return \"" + record.getBookTitle() + "\"?",
                "Confirm Return", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            record.setReturnDate(new Date());
            record.setStatus("RETURNED");
            db.returnBorrowRecord(record.getRecordId());

            Book book = getBookById(record.getBookId());
            if (book != null) {
                book.setAvailableCopies(book.getAvailableCopies() + 1);
                db.updateBookAvailableCopies(book.getId(), book.getAvailableCopies());
            }

            JOptionPane.showMessageDialog(mainFrame,
                    "Book returned successfully!\nThank you for returning: " + record.getBookTitle(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            refreshAll();
        }
    }

    private void refreshAll() {
        booksList = db.getAllBooks();
        borrowRecordsList = db.getAllBorrowRecords();
        refreshBrowse();
        refreshMyBooks();
        refreshHistory();
    }

    // Footer
    private JPanel createFooter() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(TEAL);
        p.setBorder(new EmptyBorder(12, 30, 12, 30));

        JLabel left = new JLabel("© 2026 DigitalShelf | Team 6 BITS | Gordon College");
        left.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        left.setForeground(TEXT_WHITE);

        JLabel right = new JLabel("Razon • Pambid • Alinan • Dela Torre • Reyes • Romero");
        right.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        right.setForeground(new Color(255, 255, 255, 180));

        p.add(left, BorderLayout.WEST);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    // Logout
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(mainFrame,
                "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            mainFrame.dispose();
            currentStudent = null;
            showLoginScreen();
        }
    }

    // Helpers
    private Book getBookById(int id) {
        for (Book b : booksList) if (b.getId() == id) return b;
        return null;
    }

    private JLabel label(String text, int style, int size) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", style, size));
        l.setForeground(TEXT_DARK);
        return l;
    }

    private JLabel bold(String t, int sz) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, sz));
        l.setForeground(TEXT_DARK);
        return l;
    }

    private JLabel italic(String t, int sz) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.ITALIC, sz));
        l.setForeground(Color.GRAY);
        return l;
    }

    private JLabel small(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        l.setForeground(TEXT_DARK);
        return l;
    }

    private JLabel centeredBold(String t, int sz) {
        JLabel l = bold(t, sz);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        return l;
    }

    private JLabel centeredItalic(String t, int sz) {
        JLabel l = italic(t, sz);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        return l;
    }

    private JButton iconButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBackground(bg);
        b.setForeground(TEXT_WHITE);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(100, 38));
        return b;
    }

    private JButton colorButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(bg);
        b.setForeground(TEXT_WHITE);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}