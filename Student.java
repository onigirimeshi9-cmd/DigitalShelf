

public class Student {
    private int id;
    private String studentId;
    private String name;
    private String email;
    private String yearLevel;

    public Student(int id, String studentId, String name, String email, String yearLevel) {
        this.id = id;
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.yearLevel = yearLevel;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getYearLevel() { return yearLevel; }
    public void setYearLevel(String yearLevel) { this.yearLevel = yearLevel; }
}