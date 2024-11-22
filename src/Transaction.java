package application;

public class Transaction {
    // Transaction Type (e.g., Sold, Available)
    private String type;

    // Unique Identifier For The Transaction
    private int id;

    // Date Of The Transaction
    private String date;

    // Title Of The Book
    private String title;

    // Category Of The Book
    private String category;

    // Condition Of The Book
    private String condition;

    // Price Of The Book
    private double unitPrice;

    // User Associated With The Transaction
    private int user;

    // Constructor To Initialize Transaction Object
    public Transaction(String type, int id, String date, String title, String category, String condition, double unitPrice, int user) {
        this.type = type;
        this.id = id;
        this.date = date;
        this.title = title;
        this.category = category;
        this.condition = condition;
        this.unitPrice = unitPrice;
        this.user = user;
    }

    // Get Transaction Type In Title Case
    public String getType() {
        return capitalizeWords(type);
    }

    // Get Transaction ID
    public int getId() {
        return id;
    }

    // Get Transaction Date
    public String getDate() {
        return date;
    }

    // Get Book Title
    public String getTitle() {
        return title;
    }

    // Get Book Category In Title Case
    public String getCategory() {
        return capitalizeWords(category);
    }

    // Get Book Condition In Title Case
    public String getCondition() {
        return capitalizeWords(condition);
    }

    // Get Formatted Unit Price With Dollar Sign
    public String getUnitPrice() {
        return String.format("$%.2f", unitPrice);
    }

    // Get User Associated With Transaction
    public int getUser() {
        return user;
    }

    // Utility Method To Capitalize Words And Replace Underscores
    private String capitalizeWords(String text) {
        if (text == null || text.isEmpty()) {
            return text; // Return Original If Null Or Empty
        }
        // Split By Underscores Or Spaces
        String[] words = text.split("[_ ]");
        StringBuilder formatted = new StringBuilder();
        for (String word : words) {
            // Capitalize First Letter And Lowercase Rest
            formatted.append(Character.toUpperCase(word.charAt(0)))
                     .append(word.substring(1).toLowerCase())
                     .append(" ");
        }
        // Return Trimmed String
        return formatted.toString().trim();
    }
}