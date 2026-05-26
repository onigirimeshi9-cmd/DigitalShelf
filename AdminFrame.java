import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;

public class AdminFrame extends JFrame {

    // COLOURS
    private static final Color TEAL        = new Color(61, 141, 122);
    private static final Color TEAL_DARK   = new Color(45, 110, 95);
    private static final Color TEAL_LIGHT  = new Color(80, 170, 150);
    private static final Color LEMON       = new Color(255, 250, 205);
    private static final Color CARD_WHITE  = Color.WHITE;
    private static final Color DANGER      = new Color(200, 70, 60);
    private static final Color TEXT_DARK   = new Color(35, 35, 35);
    private static final Color TEXT_WHITE  = Color.WHITE;

    private final DatabaseManager db = DatabaseManager.getInstance();
    private boolean showArchived = false;

    // Table models
    private DefaultTableModel booksModel, studentsModel, recordsModel;

    public AdminFrame() {
        setTitle("DigitalShelf — Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 750);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(LEMON);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildTabs(),   BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);

        add(root);
        setVisible(true);
    }

    // HEADER
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(TEAL);
        p.setBorder(new EmptyBorder(18, 30, 18, 30));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBackground(TEAL);

        JLabel title = new JLabel("DigitalShelf");
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(TEXT_WHITE);

        JLabel sub = new JLabel("Administrator Dashboard");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sub.setForeground(new Color(255, 255, 255, 210));

        left.add(title);
        left.add(Box.createVerticalStrut(4));
        left.add(sub);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setBackground(TEAL);

        JLabel welcome = new JLabel("Welcome, Admin");
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        welcome.setForeground(TEXT_WHITE);

        JButton logoutBtn = styledButton("Logout", TEAL_DARK, TEXT_WHITE);
        logoutBtn.addActionListener(e -> logout());

        right.add(welcome);
        right.add(Box.createHorizontalStrut(12));
        right.add(logoutBtn);

        p.add(left,  BorderLayout.WEST);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    // TABS
    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabs.setBackground(LEMON);
        tabs.setForeground(TEXT_DARK);

        tabs.addTab("Manage Books",    buildBooksTab());
        tabs.addTab("Manage Students", buildStudentsTab());
        tabs.addTab("Borrow Records",  buildRecordsTab());

        return tabs;
    }

    // ==================== BOOKS TAB ====================
    private JPanel buildBooksTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(LEMON);
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Search Panel
        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setBackground(LEMON);
        searchPanel.setBorder(new EmptyBorder(0, 0, 12, 0));
        
        // Search input row
        JPanel searchRow = new JPanel(new BorderLayout(8, 0));
        searchRow.setBackground(LEMON);
        
        JTextField searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(TEAL, 1, true), new EmptyBorder(8, 12, 8, 12)));
        searchField.setToolTipText("Search by title, author, or genre...");
        
        // Search type combo box
        JComboBox<String> searchTypeCombo = new JComboBox<>(new String[]{"All Fields", "Title", "Author", "Genre"});
        searchTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchTypeCombo.setBackground(Color.WHITE);
        searchTypeCombo.setPreferredSize(new Dimension(120, 38));
        
        JButton searchBtn = styledButton("Search", TEAL, TEXT_WHITE);
        JButton clearSearchBtn = styledButton("Clear", TEAL_LIGHT, TEXT_WHITE);
        
        searchRow.add(searchField, BorderLayout.CENTER);
        
        JPanel searchControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        searchControls.setBackground(LEMON);
        searchControls.add(searchTypeCombo);
        searchControls.add(searchBtn);
        searchControls.add(clearSearchBtn);
        searchRow.add(searchControls, BorderLayout.EAST);
        
        // Quick filter panel for genres and authors
        JPanel quickFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        quickFilterPanel.setBackground(LEMON);
        quickFilterPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
        
        JLabel quickLabel = new JLabel("Quick filters:");
        quickLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        quickLabel.setForeground(TEXT_DARK);
        quickFilterPanel.add(quickLabel);
        
        // Add genre filter buttons
        java.util.ArrayList<String> genres = db.getAllGenres();
        int genreCount = 0;
        for (String genre : genres) {
            if (genreCount >= 10) break; // Limit to 10 genres to prevent layout issues
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
                performBookSearch(searchField, searchTypeCombo);
            });
            quickFilterPanel.add(genreBtn);
            genreCount++;
        }
        
        // Add "More" button if there are many genres
        if (genres.size() > 10) {
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
        java.util.ArrayList<String> authors = db.getAllAuthors();
        for (String author : authors) {
            authorCombo.addItem(author);
        }
        authorCombo.addActionListener(e -> {
            String selectedAuthor = (String) authorCombo.getSelectedItem();
            if (selectedAuthor != null && !selectedAuthor.equals("-- Select Author --")) {
                searchField.setText(selectedAuthor);
                searchTypeCombo.setSelectedIndex(2); // Author
                performBookSearch(searchField, searchTypeCombo);
            }
        });
        quickFilterPanel.add(authorCombo);
        
        searchPanel.add(searchRow, BorderLayout.NORTH);
        searchPanel.add(quickFilterPanel, BorderLayout.CENTER);
        
        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setBackground(LEMON);

        JButton addBtn    = styledButton("Add Book",    TEAL,       TEXT_WHITE);
        JButton editBtn   = styledButton("Edit Book",   TEAL_LIGHT, TEXT_WHITE);
        JButton archiveBtn = styledButton("Archive Book", DANGER,   TEXT_WHITE);
        JButton unarchiveBtn = styledButton("Unarchive", TEAL,      TEXT_WHITE);

        JCheckBox showArchivedCheck = new JCheckBox("Show Archived");
        showArchivedCheck.setBackground(LEMON);
        showArchivedCheck.addActionListener(e -> {
            showArchived = showArchivedCheck.isSelected();
            loadBooksTable();
        });

        toolbar.add(addBtn);
        toolbar.add(editBtn);
        toolbar.add(archiveBtn);
        toolbar.add(unarchiveBtn);
        toolbar.add(Box.createHorizontalStrut(20));
        toolbar.add(showArchivedCheck);

        // Table
        String[] cols = {"ID", "ISBN", "Title", "Author", "Genre", "Shelf", "Total", "Available", "Status"};
        booksModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(booksModel);
        loadBooksTable();

        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(1).setMaxWidth(120);
        table.getColumnModel().getColumn(6).setMaxWidth(70);
        table.getColumnModel().getColumn(7).setMaxWidth(80);
        table.getColumnModel().getColumn(8).setMaxWidth(80);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(new Color(200, 200, 200)));

        // Search actions
        searchBtn.addActionListener(e -> performBookSearch(searchField, searchTypeCombo));
        clearSearchBtn.addActionListener(e -> {
            searchField.setText("");
            authorCombo.setSelectedIndex(0);
            loadBooksTable();
        });
        
        // Add Enter key support
        searchField.addActionListener(e -> performBookSearch(searchField, searchTypeCombo));

        // Actions
        addBtn.addActionListener(e -> showAddBookDialog());
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { warn("Select a book to edit."); return; }
            int id = (int) booksModel.getValueAt(row, 0);
            String status = (String) booksModel.getValueAt(row, 8);
            if ("Archived".equals(status)) {
                warn("Archived books cannot be edited. Unarchive first.");
                return;
            }
            showEditBookDialog(table, row);
        });

        archiveBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { warn("Select a book to archive."); return; }
            int id = (int) booksModel.getValueAt(row, 0);
            String title = (String) booksModel.getValueAt(row, 2);
            String status = (String) booksModel.getValueAt(row, 8);

            if ("Archived".equals(status)) {
                warn("Book is already archived.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Archive book: \"" + title + "\"?\nArchived books will not be visible to students.",
                    "Confirm Archive", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (db.archiveBook(id)) {
                    loadBooksTable();
                    info("Book archived successfully!");
                } else {
                    warn("Cannot archive book. It may be currently borrowed.");
                }
            }
        });

        unarchiveBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { warn("Select a book to unarchive."); return; }
            int id = (int) booksModel.getValueAt(row, 0);
            String title = (String) booksModel.getValueAt(row, 2);
            String status = (String) booksModel.getValueAt(row, 8);

            if (!"Archived".equals(status)) {
                warn("Book is not archived.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Unarchive book: \"" + title + "\"?", "Confirm Unarchive",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (db.unarchiveBook(id)) {
                    loadBooksTable();
                    info("Book unarchived successfully!");
                } else {
                    warn("Failed to unarchive book.");
                }
            }
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(toolbar, BorderLayout.CENTER);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scroll,  BorderLayout.CENTER);
        return panel;
    }

    // Helper method to show all genres in a dialog
    private void showAllGenresDialog(java.util.ArrayList<String> genres, JTextField searchField, JComboBox<String> searchTypeCombo) {
        JDialog dialog = new JDialog(this, "All Genres", true);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);
        
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
                performBookSearch(searchField, searchTypeCombo);
                dialog.dispose();
            });
            panel.add(genreBtn);
        }
        
        dialog.add(scroll);
        dialog.setVisible(true);
    }

    private void performBookSearch(JTextField searchField, JComboBox<String> searchTypeCombo) {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadBooksTable();
            return;
        }
        
        String searchType = ((String) searchTypeCombo.getSelectedItem()).toLowerCase();
        if (searchType.equals("all fields")) searchType = "all";
        else if (searchType.equals("title")) searchType = "title";
        else if (searchType.equals("author")) searchType = "author";
        else if (searchType.equals("genre")) searchType = "genre";
        
        java.util.ArrayList<Book> results = db.searchBooks(searchTerm, searchType, showArchived);
        
        booksModel.setRowCount(0);
        for (Book b : results) {
            String status = b.isArchived() ? "Archived" : "Active";
            booksModel.addRow(new Object[]{
                    b.getId(),
                    b.getIsbn(),
                    b.getTitle(),
                    b.getAuthor(),
                    b.getGenre(),
                    b.getShelfLocation(),
                    b.getTotalCopies(),
                    b.getAvailableCopies(),
                    status
            });
        }
        
        if (results.isEmpty()) {
            warn("No books found matching \"" + searchTerm + "\"");
        }
    }

    private void loadBooksTable() {
        booksModel.setRowCount(0);
        java.util.ArrayList<Book> books = db.getAllBooks(showArchived);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Book b : books) {
            String status = b.isArchived() ? "Archived" : "Active";
            booksModel.addRow(new Object[]{
                    b.getId(),
                    b.getIsbn(),
                    b.getTitle(),
                    b.getAuthor(),
                    b.getGenre(),
                    b.getShelfLocation(),
                    b.getTotalCopies(),
                    b.getAvailableCopies(),
                    status
            });
        }
    }

    private void showAddBookDialog() {
        JTextField isbnF    = new JTextField(20);
        JTextField titleF   = new JTextField(25);
        JTextField authorF  = new JTextField(20);
        JTextField genreF   = new JTextField(15);
        JTextField shelfF   = new JTextField(15);
        JSpinner copiesS    = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        
        // Cover image selection
        JTextField coverPathF = new JTextField(20);
        coverPathF.setEditable(false);
        coverPathF.setBackground(new Color(245, 245, 245));
        JButton browseCoverBtn = styledButton("Browse...", TEAL_LIGHT, TEXT_WHITE);
        browseCoverBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser("covers");
            // Create covers directory if it doesn't exist
            File coversDir = new File("covers");
            if (!coversDir.exists()) {
                coversDir.mkdirs();
            }
            fileChooser.setCurrentDirectory(coversDir);
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "Image Files", "jpg", "jpeg", "png", "gif", "bmp");
            fileChooser.setFileFilter(filter);
            
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                // Store relative path from project root
                String relativePath = "covers/" + selectedFile.getName();
                coverPathF.setText(relativePath);
                
                // Preview the image
                try {
                    ImageIcon previewIcon = new ImageIcon(selectedFile.getAbsolutePath());
                    Image previewImg = previewIcon.getImage().getScaledInstance(80, 100, Image.SCALE_SMOOTH);
                    JOptionPane.showMessageDialog(this, 
                        new JLabel(new ImageIcon(previewImg)), 
                        "Cover Preview", 
                        JOptionPane.PLAIN_MESSAGE);
                } catch (Exception ex) {
                    // Ignore preview errors
                }
            }
        });
        
        JPanel coverPanel = new JPanel(new BorderLayout(5, 0));
        coverPanel.add(coverPathF, BorderLayout.CENTER);
        coverPanel.add(browseCoverBtn, BorderLayout.EAST);
        
        // Date picker for publication date
        JSpinner yearSpinner = new JSpinner(new SpinnerNumberModel(2024, 1900, 2026, 1));
        JSpinner monthSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));
        JSpinner daySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 31, 1));
        
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        datePanel.add(new JLabel("Year:"));
        datePanel.add(yearSpinner);
        datePanel.add(new JLabel("Month:"));
        datePanel.add(monthSpinner);
        datePanel.add(new JLabel("Day:"));
        datePanel.add(daySpinner);
        
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        String[] labels = {"ISBN:", "Title:", "Author:", "Genre:", "Shelf Location:", "Total Copies:", "Cover Image:", "Publication Date:"};
        JComponent[] fields = {isbnF, titleF, authorF, genreF, shelfF, copiesS, coverPanel, datePanel};
        
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.gridwidth = 1; gbc.weightx = 0;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            form.add(lbl, gbc);
            
            gbc.gridx = 1; gbc.weightx = 1;
            form.add(fields[i], gbc);
            gbc.weightx = 0;
        }
        
        int result = JOptionPane.showConfirmDialog(this, form, "Add New Book",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String isbn   = isbnF.getText().trim();
            String title  = titleF.getText().trim();
            String author = authorF.getText().trim();
            String genre  = genreF.getText().trim();
            String shelf  = shelfF.getText().trim();
            int copies    = (int) copiesS.getValue();
            String coverPath = coverPathF.getText().trim();
            if (coverPath.isEmpty()) coverPath = null;
            
            int year = (int) yearSpinner.getValue();
            int month = (int) monthSpinner.getValue();
            int day = (int) daySpinner.getValue();
            
            java.util.Date pubDate = null;
            try {
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.set(year, month - 1, day);
                pubDate = cal.getTime();
            } catch (Exception ex) {
                pubDate = null;
            }
            
            if (title.isEmpty() || author.isEmpty()) {
                warn("Title and Author are required.");
                return;
            }
            
            if (db.addBook(isbn, title, author, coverPath, shelf, genre, pubDate, copies)) {
                loadBooksTable();
                info("Book added successfully!");
            } else {
                warn("Failed to add book.");
            }
        }
    }

    private void showEditBookDialog(JTable table, int row) {
        int id          = (int) booksModel.getValueAt(row, 0);
        String curIsbn  = (String) booksModel.getValueAt(row, 1);
        String curTitle = (String) booksModel.getValueAt(row, 2);
        String curAuthor= (String) booksModel.getValueAt(row, 3);
        String curGenre = (String) booksModel.getValueAt(row, 4);
        String curShelf = (String) booksModel.getValueAt(row, 5);
        int curTotal    = (int) booksModel.getValueAt(row, 6);
        int curAvail    = (int) booksModel.getValueAt(row, 7);
        
        // Get current cover path from database
        String curCoverPath = null;
        for (Book b : db.getAllBooks(true)) {
            if (b.getId() == id) {
                curCoverPath = b.getCoverImagePath();
                break;
            }
        }
        
        JTextField isbnF   = new JTextField(curIsbn, 20);
        JTextField titleF  = new JTextField(curTitle, 25);
        JTextField authorF = new JTextField(curAuthor, 20);
        JTextField genreF  = new JTextField(curGenre, 15);
        JTextField shelfF  = new JTextField(curShelf, 15);
        JSpinner totalS    = new JSpinner(new SpinnerNumberModel(curTotal, 1, 100, 1));
        JSpinner availS    = new JSpinner(new SpinnerNumberModel(curAvail, 0, curTotal, 1));
        
        // Cover image selection
        JTextField coverPathF = new JTextField(curCoverPath != null ? curCoverPath : "", 20);
        coverPathF.setEditable(false);
        coverPathF.setBackground(new Color(245, 245, 245));
        JButton browseCoverBtn = styledButton("Browse...", TEAL_LIGHT, TEXT_WHITE);
        browseCoverBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser("covers");
            File coversDir = new File("covers");
            if (!coversDir.exists()) {
                coversDir.mkdirs();
            }
            fileChooser.setCurrentDirectory(coversDir);
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "Image Files", "jpg", "jpeg", "png", "gif", "bmp");
            fileChooser.setFileFilter(filter);
            
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String relativePath = "covers/" + selectedFile.getName();
                coverPathF.setText(relativePath);
                
                // Preview the image
                try {
                    ImageIcon previewIcon = new ImageIcon(selectedFile.getAbsolutePath());
                    Image previewImg = previewIcon.getImage().getScaledInstance(80, 100, Image.SCALE_SMOOTH);
                    JOptionPane.showMessageDialog(this, 
                        new JLabel(new ImageIcon(previewImg)), 
                        "Cover Preview", 
                        JOptionPane.PLAIN_MESSAGE);
                } catch (Exception ex) {
                    // Ignore preview errors
                }
            }
        });
        
        JButton clearCoverBtn = styledButton("Clear", DANGER, TEXT_WHITE);
        clearCoverBtn.addActionListener(e -> coverPathF.setText(""));
        
        JPanel coverPanel = new JPanel(new BorderLayout(5, 0));
        coverPanel.add(coverPathF, BorderLayout.CENTER);
        JPanel coverBtnPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        coverBtnPanel.add(browseCoverBtn);
        coverBtnPanel.add(clearCoverBtn);
        coverPanel.add(coverBtnPanel, BorderLayout.EAST);
        
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        String[] labels = {"ISBN:", "Title:", "Author:", "Genre:", "Shelf Location:", "Total Copies:", "Available Copies:", "Cover Image:"};
        JComponent[] fields = {isbnF, titleF, authorF, genreF, shelfF, totalS, availS, coverPanel};
        
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.gridwidth = 1; gbc.weightx = 0;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            form.add(lbl, gbc);
            
            gbc.gridx = 1; gbc.weightx = 1;
            form.add(fields[i], gbc);
            gbc.weightx = 0;
        }
        
        int result = JOptionPane.showConfirmDialog(this, form, "Edit Book",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String isbn   = isbnF.getText().trim();
            String title  = titleF.getText().trim();
            String author = authorF.getText().trim();
            String genre  = genreF.getText().trim();
            String shelf  = shelfF.getText().trim();
            int total     = (int) totalS.getValue();
            int avail     = (int) availS.getValue();
            String coverPath = coverPathF.getText().trim();
            if (coverPath.isEmpty()) coverPath = null;
            
            if (title.isEmpty() || author.isEmpty()) {
                warn("Title and Author are required.");
                return;
            }
            if (avail > total) {
                warn("Available copies cannot exceed total copies.");
                return;
            }
            
            if (db.updateBook(id, isbn, title, author, coverPath, shelf, genre, null, total, avail)) {
                loadBooksTable();
                info("Book updated successfully!");
            } else {
                warn("Failed to update book.");
            }
        }
    }

    // ==================== STUDENTS TAB ====================
    private JPanel buildStudentsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(LEMON);
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setBackground(LEMON);

        JButton addBtn = styledButton("Add Student",    TEAL,   TEXT_WHITE);
        JButton delBtn = styledButton("Delete Student", DANGER, TEXT_WHITE);

        toolbar.add(addBtn);
        toolbar.add(delBtn);

        String[] cols = {"ID", "Student ID", "Name", "Email", "Year Level"};
        studentsModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(studentsModel);
        loadStudentsTable();

        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(new Color(200, 200, 200)));

        addBtn.addActionListener(e -> showAddStudentDialog());
        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { warn("Select a student to delete."); return; }
            int id   = (int) studentsModel.getValueAt(row, 0);
            String n = (String) studentsModel.getValueAt(row, 2);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete student: " + n + "?\nThis cannot be undone.", "Confirm Delete",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                db.deleteStudent(id);
                loadStudentsTable();
            }
        });

        JLabel note = new JLabel(
                "  ℹ  Default password for each student is their Student ID number.");
        note.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        note.setForeground(new Color(100, 100, 100));

        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(scroll,  BorderLayout.CENTER);
        panel.add(note,    BorderLayout.SOUTH);
        return panel;
    }

    private void loadStudentsTable() {
        studentsModel.setRowCount(0);
        for (Student s : db.getAllStudents()) {
            studentsModel.addRow(new Object[]{
                    s.getId(), s.getStudentId(), s.getName(), s.getEmail(), s.getYearLevel()
            });
        }
    }

    private void showAddStudentDialog() {
        JTextField sidF    = new JTextField(15);
        JTextField nameF   = new JTextField(20);
        JTextField emailF  = new JTextField(25);
        String[] years     = {"1st Year", "2nd Year", "3rd Year", "4th Year"};
        JComboBox<String> yearCB = new JComboBox<>(years);
        JPasswordField passF = new JPasswordField(15);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        String[] labels = {"Student ID:", "Full Name:", "Email:", "Year Level:", "Password:"};
        JComponent[] fields = {sidF, nameF, emailF, yearCB, passF};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.gridwidth = 1; gbc.weightx = 0;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            form.add(lbl, gbc);

            gbc.gridx = 1; gbc.weightx = 1;
            fields[i].setFont(new Font("Segoe UI", Font.PLAIN, 13));
            form.add(fields[i], gbc);
            gbc.weightx = 0;
        }

        JLabel hint = new JLabel("  If password is left blank, Student ID will be used.");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(Color.GRAY);

        JPanel wrapper = new JPanel(new BorderLayout(0, 8));
        wrapper.add(form, BorderLayout.CENTER);
        wrapper.add(hint, BorderLayout.SOUTH);

        int result = JOptionPane.showConfirmDialog(this, wrapper, "Add New Student",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String sid   = sidF.getText().trim();
            String name  = nameF.getText().trim();
            String email = emailF.getText().trim();
            String year  = (String) yearCB.getSelectedItem();
            String pass  = new String(passF.getPassword()).trim();
            if (pass.isEmpty()) pass = sid;

            if (sid.isEmpty() || name.isEmpty()) {
                warn("Student ID and Name are required.");
                return;
            }

            if (db.addStudent(sid, name, email, year, pass)) {
                loadStudentsTable();
                info("Student added!\nUsername: " + sid + "\nPassword: " + pass);
            } else {
                warn("Failed to add student. Student ID may already exist.");
            }
        }
    }

    // ==================== BORROW RECORDS TAB ====================
    private JPanel buildRecordsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(LEMON);
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setBackground(LEMON);

        JButton refreshBtn = styledButton("Refresh", TEAL, TEXT_WHITE);
        toolbar.add(refreshBtn);

        String[] cols = {"Record ID", "Book Title", "Student Name", "Borrow Date", "Due Date", "Return Date", "Status"};
        recordsModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(recordsModel);
        loadRecordsTable();

        table.getColumnModel().getColumn(0).setMaxWidth(80);
        table.getColumnModel().getColumn(6).setMaxWidth(90);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(new Color(200, 200, 200)));

        refreshBtn.addActionListener(e -> loadRecordsTable());

        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(scroll,  BorderLayout.CENTER);
        return panel;
    }

    private void loadRecordsTable() {
        recordsModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        for (BorrowRecord r : db.getAllBorrowRecords()) {
            recordsModel.addRow(new Object[]{
                    r.getRecordId(), r.getBookTitle(), r.getStudentName(),
                    sdf.format(r.getBorrowDate()), sdf.format(r.getDueDate()),
                    r.getReturnDate() != null ? sdf.format(r.getReturnDate()) : "—",
                    r.getStatus()
            });
        }
    }

    // ==================== FOOTER ====================
    private JPanel buildFooter() {
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

    // ==================== UTILITIES ====================
    private JTable styledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(61, 141, 122, 60));
        table.setSelectionForeground(TEXT_DARK);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(TEAL);
        table.getTableHeader().setForeground(TEXT_WHITE);
        table.setGridColor(new Color(220, 220, 220));
        table.setShowVerticalLines(true);
        return table;
    }

    private JButton styledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        return btn;
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    private void info(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Log out from Admin Dashboard?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new DigitalShelfApp();
        }
    }
}