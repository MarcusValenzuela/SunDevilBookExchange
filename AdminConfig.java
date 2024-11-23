package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class AdminConfig {
    
    @FXML
    private TableView<CategoryRecord> categoryTable;
    @FXML
    private TableColumn<CategoryRecord, String> categoryColumn;
    @FXML
    private TableView<ConditionRecord> conditionTable;
    @FXML
    private TableColumn<ConditionRecord, String> conditionColumn;
    @FXML
    private TableColumn<ConditionRecord, Double> markdownColumn;
    @FXML
    private TextField newCategoryField;
    @FXML
    private Button addCategoryButton;
    @FXML
    private TextField conditionField;
    @FXML
    private TextField markdownField;
    @FXML
    private Button editMarkdownButton;
    
    
    // Database URL
    private static final String DB_URL = "jdbc:sqlite:./sun_devil_book_exchange.db";

    
    @FXML
    public void initialize() {
        loadCategoryData();
        loadConditionData();
    }

    public void loadCategoryData() {
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        ObservableList<CategoryRecord> categories = loadCategoriesFromDatabase();
        categoryTable.setItems(categories);
    }
    
    private ObservableList<CategoryRecord> loadCategoriesFromDatabase() {
        ObservableList<CategoryRecord> categories = FXCollections.observableArrayList();
        
        String query = "SELECT category_name FROM categories";

        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(query)) {
            
            while (result.next()) {
                String category = result.getString("category_name");
                categories.add(new CategoryRecord(category));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
          
        return categories;
    }
    
    public static class CategoryRecord {
        private final SimpleStringProperty category;

        public CategoryRecord(String category) {
            this.category = new SimpleStringProperty(category);
        }

        public String getCategory() {
            return category.get();
        }
        
        public SimpleStringProperty categoryProperty() {
            return category;
        }
    }
	
	// NAVIGATION BUTTONS
	@FXML
	private Button MonitorTransactionsButton;
	@FXML
	private void MonitorTransactionsButtonClick() throws IOException {
		navigateToPage("AdminTransactions.fxml");
	}
	
	@FXML
	private Button ManageUserAccountButton;
	@FXML
	private void ManageUserAccountButtonClick() throws IOException {
	    navigateToPage("AdminAccts.fxml");
	}
	
	@FXML
	private Button BuyingSalesRecordButton;
	@FXML
	private void BuyingSalesRecordButtonClick() throws IOException {
		navigateToPage("AdminDash.fxml");
	}
	
	@FXML
	private Button LogoutButton;
	@FXML
	private void LogoutButtonClick() throws IOException  {
		navigateToPage("Login.fxml");
	}
	    
	private void navigateToPage(String pageFXMLName) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(pageFXMLName));
	    Parent page = loader.load();
	    Scene scene = new Scene(page);
	    Stage currentStage = (Stage) MonitorTransactionsButton.getScene().getWindow(); 
	    currentStage.setScene(scene);
	    currentStage.show();
	}
    
	 public void loadConditionData() {
	        conditionColumn.setCellValueFactory(new PropertyValueFactory<>("condition"));
	        markdownColumn.setCellValueFactory(new PropertyValueFactory<>("markdown"));
	        ObservableList<ConditionRecord> conditions = loadConditionsFromDatabase();
	        conditionTable.setItems(conditions);
	    }
	    
	    private ObservableList<ConditionRecord> loadConditionsFromDatabase() {
	        ObservableList<ConditionRecord> conditions = FXCollections.observableArrayList();
	        
	        String query = "SELECT * FROM conditions";

	        try (Connection connection = DriverManager.getConnection(DB_URL);
	             Statement statement = connection.createStatement();
	             ResultSet result = statement.executeQuery(query)) {
	            
	            while (result.next()) {
	                String condition = result.getString("condition");
	                double markdown = result.getDouble("markdown");
	                conditions.add(new ConditionRecord(condition, markdown));
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	          
	        return conditions;
	    }
	    
	    public static class ConditionRecord {
	        private final SimpleStringProperty condition;
	        private final SimpleDoubleProperty markdown;

	        public ConditionRecord(String condition, double markdown) {
	            this.condition = new SimpleStringProperty(condition);
	            this.markdown = new SimpleDoubleProperty(markdown);
	        }

	        public String getCondition() {
	            return condition.get();
	        }
	        
	        public Double getMarkdown() {
	            return markdown.get();
	        }
	        
	        public SimpleStringProperty conditionProperty() {
	            return condition;
	        }
	        
	        public SimpleDoubleProperty markdownProperty() {
	            return markdown;
	        }
	    }
	    
	    @FXML
	    private void handleAddCategory() {
	        String newCategory = newCategoryField.getText().trim();
	        
	        if (newCategory.isEmpty()) {
	            return;
	        }
	        
	        if (categoryExists(newCategory)) {
	            newCategoryField.clear();
	            return;
	        }
	        
	        if (addCategoryToDatabase(newCategory)) {
	            loadCategoryData();
	            newCategoryField.clear();
	        }
	    }
	    
	    private boolean categoryExists(String category) {
	        String query = "SELECT COUNT(*) FROM categories WHERE LOWER(category_name) = LOWER(?)";
	        
	        try (Connection connection = DriverManager.getConnection(DB_URL);
	             PreparedStatement pstmt = connection.prepareStatement(query)) {
	            
	            pstmt.setString(1, category);
	            ResultSet rs = pstmt.executeQuery();
	            
	            if (rs.next()) {
	                return rs.getInt(1) > 0;
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return false;
	    }
	    
	    private boolean addCategoryToDatabase(String category) {
	        String query = "INSERT INTO categories (category_name) VALUES (?)";
	        
	        try (Connection connection = DriverManager.getConnection(DB_URL);
	             PreparedStatement pstmt = connection.prepareStatement(query)) {
	            
	            pstmt.setString(1, category);
	            int rowsAffected = pstmt.executeUpdate();
	            return rowsAffected > 0;
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	            return false;
	        }
	    }

	    @FXML
	    private void handleEditMarkdown() {
	        String condition = conditionField.getText().trim();
	        String markdownStr = markdownField.getText().trim();
	        
	        if (condition.isEmpty() || markdownStr.isEmpty()) {
	            return;
	        }
	        
	        if (!conditionExists(condition)) {
	            return;
	        }
	        
	        double markdown;
	        try {
	            markdown = Double.parseDouble(markdownStr);
	            if (markdown < 0 || markdown > 1) {
	                return;
	            }
	        } catch (NumberFormatException e) {
	            return;
	        }
	        
	        if (updateMarkdown(condition, markdown)) {
	            loadConditionData();
	            conditionField.clear();
	            markdownField.clear();
	        }
	    }

	    private boolean conditionExists(String condition) {
	        String query = "SELECT COUNT(*) FROM conditions WHERE LOWER(condition) = LOWER(?)";
	        
	        try (Connection connection = DriverManager.getConnection(DB_URL);
	             PreparedStatement pstmt = connection.prepareStatement(query)) {
	            
	            pstmt.setString(1, condition);
	            ResultSet rs = pstmt.executeQuery();
	            
	            if (rs.next()) {
	                return rs.getInt(1) > 0;
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return false;
	    }

	    private boolean updateMarkdown(String condition, double markdown) {
	        String query = "UPDATE conditions SET markdown = ? WHERE LOWER(condition) = LOWER(?)";
	        
	        try (Connection connection = DriverManager.getConnection(DB_URL);
	             PreparedStatement pstmt = connection.prepareStatement(query)) {
	            
	            pstmt.setDouble(1, markdown);
	            pstmt.setString(2, condition);
	            
	            int rowsAffected = pstmt.executeUpdate();
	            return rowsAffected > 0;
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	            return false;
	        }
	    }
 
}


