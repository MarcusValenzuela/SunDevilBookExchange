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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleDoubleProperty;


public class AdminDash {

	// QUICK GLANCE
    @FXML
    private Label MonthySalesLabel;
    @FXML
    private Label TotalBooksSoldLabel;
    @FXML
    private Label BooksListedLabel;
    @FXML
    private Label CurrentBestSellerLabel;
    
    // SALES RECORD TABLE
    @FXML
    private TableView<Record> SalesRecordTable;
    @FXML
    private TableColumn<Record, String> SalesTableColTitle;
    @FXML
    private TableColumn<Record, String> SalesTableColCategory;
    @FXML
    private TableColumn<Record, Double> SalesTableColAmount;
    
    // BUYING RECORD TABLE
    @FXML
    private TableView<Record> BuyingRecordTable;
    @FXML
    private TableColumn<Record, String> BuyingTableColTitle;
    @FXML
    private TableColumn<Record, String> BuyingTableColCategory;
    @FXML
    private TableColumn<Record, Double> BuyingTableColAmount;

    // GRRAPH 
    @FXML
    private BarChart<String, Number> BuyingBarChart;
    @FXML
    private BarChart<String, Number> SaleBarChart;
    
    // DATABASE
    private static final String DB_URL = "jdbc:sqlite:./sun_devil_book_exchange.db";
	
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
	private Button SystemConfigurationButton;
	@FXML
	private void SystemConfigurationButtonClick() throws IOException {
		navigateToPage("AdminConfig.fxml");
	}
	
	@FXML
	private Button LogoutButton;
	@FXML
	private void LogoutButtonClick() throws IOException  {
		navigateToPage("Login.fxml");
	}
	
    @FXML
    public void initialize() {
        loadDashboardData();
        loadTableData();
    }
    
	private void navigateToPage(String pageFXMLName) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(pageFXMLName));
	    Parent page = loader.load();
	    Scene scene = new Scene(page);
	    Stage currentStage = (Stage) MonitorTransactionsButton.getScene().getWindow(); 
	    currentStage.setScene(scene);
	    currentStage.show();
	}
    
    public void loadDashboardData() {
    	// Gets monthly sales in the given month. Note: We don't need "WHERE sale_status = 'sold'" because we are using FROM BuyingRecords not ListedBooks.
    	String monthlySalesQuery = 
    		    "SELECT SUM(ListedBooks.listed_price) AS monthly_sales " 
    		    + "FROM BuyingRecords " 
    		    + "JOIN ListedBooks ON BuyingRecords.fk_books_book_id = ListedBooks.book_id " 
    		    + "WHERE strftime('%Y-%m', BuyingRecords.transaction_date) = strftime('%Y-%m', 'now');";
        
    	String totalBooksQuery = "SELECT COUNT(*) AS total_sold FROM BuyingRecords;";
        
    	String booksListedQuery = "SELECT COUNT(*) AS books_listed FROM ListedBooks WHERE sale_status = 'available';";
        
    	// Gets the titles of sold books, groups them and gets the count and sorts descending so that highest count is on top. 
    	// Then returns the top result.
    	String bestSellerQuery = 
    		    "SELECT ListedBooks.title " 
    		    + "FROM BuyingRecords " 
    		    + "JOIN ListedBooks ON BuyingRecords.fk_books_book_id = ListedBooks.book_id " 
    		    + "WHERE ListedBooks.sale_status = 'sold' " 
    		    + "GROUP BY ListedBooks.book_id " 
    		    + "ORDER BY COUNT(BuyingRecords.transaction_id) DESC " 
    		    + "LIMIT 1;";

        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            if (connection != null) {
                try (Statement statement = connection.createStatement()) {

                    ResultSet result = statement.executeQuery(monthlySalesQuery);
                    if (result.next()) {
                        double monthlySales = result.getDouble("monthly_sales");
                        if (monthlySales == 0) {
                            MonthySalesLabel.setText("$0.00");
                        } else {
                            MonthySalesLabel.setText("$" + monthlySales);
                        }
                    } else {
                    	MonthySalesLabel.setText("0");
                    }

                    result = statement.executeQuery(totalBooksQuery);
                    
                    if (result.next()) {
                    	int numberOfBooksSold = result.getInt("total_sold");
                    	TotalBooksSoldLabel.setText(String.valueOf(numberOfBooksSold));
                    } else {
                    	TotalBooksSoldLabel.setText("0");
                    }

                    result = statement.executeQuery(booksListedQuery);
                    if (result.next()) {
                    	int numberOfBooksActivelyListed = result.getInt("books_listed");
                    	BooksListedLabel.setText(String.valueOf(numberOfBooksActivelyListed));
                    } else {
                    	BooksListedLabel.setText("0");
                    }

                    result = statement.executeQuery(bestSellerQuery);
                    if (result.next()) {
                    	String title = result.getString("title");
                    	CurrentBestSellerLabel.setText(title);
                    } else {
                    	CurrentBestSellerLabel.setText("N/A");
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    
    public void loadTableData() {
    	// SALE - Connect to getter methods
    	SalesTableColTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
    	SalesTableColCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
    	SalesTableColAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
    	
    	// BUYING - Connect to getter methods
    	BuyingTableColTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
    	BuyingTableColCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
    	BuyingTableColAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
    	
    	
    	// SALE - Load the data from the database into an observableList so that it looks for changes automatically.
        ObservableList<Record> saleRecords = loadSalesDataFromDatabase();
        SalesRecordTable.setItems(saleRecords);
        
    	// BUYING - Load the data from the database into an observableList so that it looks for changes automatically.
        ObservableList<Record> buyingRecords = loadBuyingDataFromDatabase();
        BuyingRecordTable.setItems(buyingRecords);
    }
    
    // ObservableList allows the table to be dynamically updated when a change is made.
    private ObservableList<Record> loadSalesDataFromDatabase() {
        ObservableList<Record> records = FXCollections.observableArrayList();

        String query = 
        		"SELECT ListedBooks.title, Categories.category_name, ListedBooks.listed_price " 
        		+ "FROM ListedBooks " 
        		+ "JOIN Categories ON ListedBooks.fk_categories_category_id = Categories.category_id;";

        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            if (connection != null) {
                try (Statement statement = connection.createStatement()) {

                	try (ResultSet result = statement.executeQuery(query)) {
                    	// While there still are results
                        while (result.next()) {
                            String title = result.getString("title");
                            String category = result.getString("category_name");
                            double price = result.getDouble("listed_price");

                            // Create a new salesRecord and add it to the list.
                            Record sale = new Record(title, category, price);
                            records.add(sale);
                        }
                	}
                }
            }
        }
        // TODO: Add proper handling if we have the time
        catch (Exception e) {
            e.printStackTrace();
        }
          
        return records;
    }
    
    // ObservableList allows the table to be dynamically updated when a change is made.
    private ObservableList<Record> loadBuyingDataFromDatabase() {
        ObservableList<Record> records = FXCollections.observableArrayList();

        String query = 
        		"SELECT ListedBooks.title, Categories.category_name, ListedBooks.listed_price " 
        		+ "FROM BuyingRecords " 
        		+ "JOIN ListedBooks ON BuyingRecords.fk_books_book_id = ListedBooks.book_id " 
        	    + "JOIN Categories ON ListedBooks.fk_categories_category_id = Categories.category_id;";
        
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            if (connection != null) {
                try (Statement statement = connection.createStatement()) {

                	try (ResultSet result = statement.executeQuery(query)) {
                    	// While there still are results
                        while (result.next()) {
                            String title = result.getString("title");
                            String category = result.getString("category_name");
                            double price = result.getDouble("listed_price");

                            // Create a new salesRecord and add it to the list.
                            Record sale = new Record(title, category, price);
                            records.add(sale);
                        }
                	}
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return records;
    }
    
    // Class to store Sale and Buying records for table.
    public static class Record {
        private final SimpleStringProperty title;
        private final SimpleStringProperty category;
        private final SimpleDoubleProperty amount;

        public Record(String title, String category, double amount) {
            this.title = new SimpleStringProperty(title);
            this.category = new SimpleStringProperty(category);
            this.amount = new SimpleDoubleProperty(amount);
        }

        public SimpleStringProperty titleProperty() {
            return title;
        }
        
        public SimpleStringProperty categoryProperty() {
            return category;
        }
        
        public SimpleDoubleProperty amountProperty() {
            return amount;
        }
    }
    
    @FXML
    public void generateBuyingGraph() {
    	// Clears data/graph so that graph can be cleanly repopulated if user clicks button many times.
    	BuyingBarChart.getData().clear();
    	BuyingBarChart.setAnimated(false);
    	CategoryAxis xAxis = (CategoryAxis) BuyingBarChart.getXAxis();
    	xAxis.setTickLabelRotation(-45);

        String query = 
            "SELECT Categories.category_name, SUM(ListedBooks.listed_price) AS total_sales "
            + "FROM BuyingRecords "
            + "JOIN ListedBooks ON BuyingRecords.fk_books_book_id = ListedBooks.book_id "
            + "JOIN Categories ON ListedBooks.fk_categories_category_id = Categories.category_id "
            + "GROUP BY Categories.category_name;";

        
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            if (connection != null) {
                try (Statement statement = connection.createStatement();
                     ResultSet result = statement.executeQuery(query)) {

                    // Create collection of data
                    XYChart.Series<String, Number> series = new XYChart.Series<>();

                    // Add data points
                    while (result.next()) {
                        String category = result.getString("category_name");
                        double totalSales = result.getDouble("total_sales");
                        series.getData().add(new XYChart.Data<>(category, totalSales));
                    }

                    // Add datapoints to graph.
                    BuyingBarChart.getData().add(series);
                } 
            }
        } 
        // TODO: Add proper handling if we have the time.
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    public void generateSaleGraph() {
    	// Clears data/graph so that graph can be cleanly repopulated if user clicks button many times.
    	SaleBarChart.getData().clear();
    	SaleBarChart.setAnimated(false);
    	CategoryAxis xAxis = (CategoryAxis) SaleBarChart.getXAxis();
    	xAxis.setTickLabelRotation(-45);

        String query = 
            "SELECT Categories.category_name, SUM(ListedBooks.listed_price) AS total_sales "
            + "FROM ListedBooks "
            + "JOIN Categories ON ListedBooks.fk_categories_category_id = Categories.category_id "
            + "GROUP BY Categories.category_name;";

        
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            if (connection != null) {
                try (Statement statement = connection.createStatement();
                     ResultSet result = statement.executeQuery(query)) {

                    // Create collection of data
                    XYChart.Series<String, Number> series = new XYChart.Series<>();

                    // Add data points
                    while (result.next()) {
                        String category = result.getString("category_name");
                        double totalSales = result.getDouble("total_sales");
                        series.getData().add(new XYChart.Data<>(category, totalSales));
                    }

                    // Add datapoints to graph.
                    SaleBarChart.getData().add(series);
                } 
            }
        } 
        // TODO: Add proper handling if we have the time
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
