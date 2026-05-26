import java.util.Date;

public class Book {
    private int id;
    private String isbn;
    private String title;
    private String author;
    private String coverImagePath;
    private String shelfLocation;
    private String genre;
    private Date publicationDate;
    private int totalCopies;
    private int availableCopies;
    private Date addedDate;
    private boolean isArchived;

    // Constructor with all fields
    public Book(int id, String isbn, String title, String author,
                String coverImagePath, String shelfLocation, String genre,
                Date publicationDate, int totalCopies, int availableCopies,
                boolean isArchived) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.coverImagePath = coverImagePath;
        this.shelfLocation = shelfLocation;
        this.genre = genre;
        this.publicationDate = publicationDate;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
        this.addedDate = new Date();
        this.isArchived = isArchived;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getCoverImagePath() { return coverImagePath; }
    public void setCoverImagePath(String coverImagePath) { this.coverImagePath = coverImagePath; }

    public String getShelfLocation() { return shelfLocation; }
    public void setShelfLocation(String shelfLocation) { this.shelfLocation = shelfLocation; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public Date getPublicationDate() { return publicationDate; }
    public void setPublicationDate(Date publicationDate) { this.publicationDate = publicationDate; }

    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }

    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }

    public boolean isAvailable() { return availableCopies > 0 && !isArchived; }

    public Date getAddedDate() { return addedDate; }

    public boolean isArchived() { return isArchived; }
    public void setArchived(boolean archived) { isArchived = archived; }
}