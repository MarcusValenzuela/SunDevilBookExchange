package application;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class AdminTransactions {
    
    @FXML private Button MonitorTransactionsButton;
    @FXML private Button BuyingSalesRecordButton;
    @FXML private Button ManageUserAccountButton;
    @FXML private Button SystemConfigurationButton;
    @FXML private Button LogoutButton;
    
    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, Integer> indexColumn;
    @FXML private TableColumn<Transaction, String> typeColumn;
    @FXML private TableColumn<Transaction, Integer> idColumn;
    @FXML private TableColumn<Transaction, String> dateColumn;
    @FXML private TableColumn<Transaction, String> titleColumn;
    @FXML private TableColumn<Transaction, String> categoryColumn;
    @FXML private TableColumn<Transaction, String> conditionColumn;
    @FXML private TableColumn<Transaction, Double> unitPriceColumn;
    @FXML private TableColumn<Transaction, Integer> userColumn;

    private final ObservableList<Transaction> transactions = FXCollections.observableArrayList();
    private static final String DB_URL = "jdbc:sqlite:./sun_devil_book_exchange.db";

    @FXML
    public void initialize() {
        // Configure Index Column
        indexColumn.setCellValueFactory(cellData ->
            new ReadOnlyObjectWrapper<>(transactionTable.getItems().indexOf(cellData.getValue()) + 1)
        );

        // Configure Table Columns
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        conditionColumn.setCellValueFactory(new PropertyValueFactory<>("condition"));
        unitPriceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        userColumn.setCellValueFactory(new PropertyValueFactory<>("user"));

        loadTransactionsFromDatabase();
    }

    private void loadTransactionsFromDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            Statement stmt = conn.createStatement();
            String query = """
                SELECT ListedBooks.book_id, ListedBooks.title, ListedBooks.listed_price,
                       Conditions.condition, Categories.category_name,
                       'available' AS type, NULL AS transaction_date,
                       ListedBooks.fk_users_seller_id AS user_id
                FROM ListedBooks
                JOIN Conditions ON ListedBooks.fk_conditions_condition = Conditions.condition
                JOIN Categories ON ListedBooks.fk_categories_category_id = Categories.category_id
                WHERE ListedBooks.sale_status = 'available'

                UNION

                SELECT BuyingRecords.transaction_id AS book_id, ListedBooks.title,
                       ListedBooks.listed_price, Conditions.condition,
                       Categories.category_name, 'sold' AS type,
                       BuyingRecords.transaction_date, BuyingRecords.fk_users_buyer_id AS user_id
                FROM BuyingRecords
                JOIN ListedBooks ON BuyingRecords.fk_books_book_id = ListedBooks.book_id
                JOIN Conditions ON ListedBooks.fk_conditions_condition = Conditions.condition
                JOIN Categories ON ListedBooks.fk_categories_category_id = Categories.category_id;
            """;

            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                transactions.add(new Transaction(
                    rs.getString("type"),
                    rs.getInt("book_id"),
                    rs.getString("transaction_date"),
                    rs.getString("title"),
                    rs.getString("category_name"),
                    rs.getString("condition"),
                    rs.getDouble("listed_price"),
                    rs.getInt("user_id")
                ));
            }

            transactionTable.setItems(transactions);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Navigation Methods
    @FXML
    private void BuyingSalesRecordButtonClick() throws IOException {
        navigateToPage("AdminDash.fxml");
    }
    
    @FXML
    private void ManageUserAccountButtonClick() throws IOException {
        navigateToPage("AdminAccts.fxml");
    }
    
    @FXML
    private void SystemConfigurationButtonClick() throws IOException {
        navigateToPage("AdminConfig.fxml");
    }
    
    @FXML
    private void LogoutButtonClick() throws IOException {
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
}

