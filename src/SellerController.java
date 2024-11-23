package application;

import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class SellerController {

    @FXML
    private TableView<Book> bookTable;

    @FXML
    private TableColumn<Book, String> titleColumn, authorColumn, categoryColumn, conditionColumn;
    @FXML
    private TableColumn<Book, Integer> yearColumn, stockColumn;
    @FXML
    private TableColumn<Book, Double> priceColumn;

    @FXML
    private TextField titleField, authorField, yearField, originalPriceField;

    @FXML
    private ComboBox<String> categoryBox, conditionBox;

    @FXML
    private Label generatedPriceLabel;

    private final ObservableList<Book> books = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Initialize TableView Columns
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        conditionColumn.setCellValueFactory(new PropertyValueFactory<>("condition"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceColumn.setCellFactory(tc -> new TableCell<>() {
          @Override
          protected void updateItem(Double price, boolean empty) {
              super.updateItem(price, empty);
              if (empty || price == null) {
                  setText(null); // Clear the cell if it's empty
              } else {
                  setText(String.format("$%.2f", price)); // Format as currency
              }
          }
        });
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));

        // Add sample data
        /*books.addAll(
            new Book("Cosmos", "Carl Sagan", 1980, "Natural Science", "Used like new", 12.90, 1),
            new Book("Entangled Life", "Merlin Sheldrake", 2020, "Natural Science", "Moderately used", 15.90, 4)
        );*/
        bookTable.setItems(books);

        // Configure ComboBoxes
        populateCategories();
        // categoryBox.setItems(FXCollections.observableArrayList("Natural Science", "Math", "Computer", "English Language", "Others"));
        conditionBox.setItems(FXCollections.observableArrayList("Used like new", "Moderately used", "Heavily used"));
    }

    private void populateCategories() {
      ObservableList<String> categories = FXCollections.observableArrayList();
      String url = "jdbc:sqlite:./sun_devil_book_exchange.db"; // Adjust this path if needed
  
      try (Connection conn = DriverManager.getConnection(url)) {
          System.out.println("Database connected successfully!");
  
          String query = "SELECT category_name FROM Categories";
          Statement stmt = conn.createStatement();
          ResultSet rs = stmt.executeQuery(query);
  
          while (rs.next()) {
              System.out.println("Category fetched: " + rs.getString("category_name"));
              categories.add(rs.getString("category_name"));
          }
  
          categoryBox.setItems(categories);
      } catch (Exception e) {
          e.printStackTrace();
      }
  }

    @FXML
    private void handleListBook() {
        try {
            String title = titleField.getText();
            String author = authorField.getText();
            int year = Integer.parseInt(yearField.getText());
            String category = categoryBox.getValue();
            String condition = conditionBox.getValue();
            double originalPrice = Double.parseDouble(originalPriceField.getText());
            double price = calculateGeneratedPrice(originalPrice, condition);

            books.add(new Book(title, author, year, category, condition, price, 1));
            clearFormFields();
        } catch (Exception e) {
            System.out.println("Invalid input!");
        }
    }

    private double calculateGeneratedPrice(double originalPrice, String condition) {
        if ("Moderately used".equals(condition)) {
            return originalPrice * 0.8;
        } else if ("Heavily used".equals(condition)) {
            return originalPrice * 0.6;
        }
        return originalPrice;
    }

    private void clearFormFields() {
        titleField.clear();
        authorField.clear();
        yearField.clear();
        originalPriceField.clear();
        generatedPriceLabel.setText("$0.00");
    }
}
