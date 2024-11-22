package application;

// Import Necessary JavaFX And SQL Libraries
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class AdminTransactions {

    // Define FXML Labels For Navigation
    @FXML private Label dashboardLabel;
    @FXML private Label monitorTransactionsLabel;
    @FXML private Label buyingSalesRecordLabel;
    @FXML private Label manageUserAccountsLabel;
    @FXML private Label systemConfigLabel;

    // Define FXML TableView And TableColumn Elements
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

    // ObservableList To Store Transaction Data
    private final ObservableList<Transaction> transactions = FXCollections.observableArrayList();

    // Database URL For SQLite Connection
    private static final String DB_URL = "jdbc:sqlite:./sun_devil_book_exchange.db";

    @FXML
    public void initialize() {
        // Configure Index Column For Row Numbers
        indexColumn.setPrefWidth(50.0); // Set Column Width
        indexColumn.setResizable(false); // Disable Column Resizing
        indexColumn.setCellValueFactory(cellData ->
            new ReadOnlyObjectWrapper<>(transactionTable.getItems().indexOf(cellData.getValue()) + 1)
        );

        // Map Table Columns To Transaction Properties
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        conditionColumn.setCellValueFactory(new PropertyValueFactory<>("condition"));
        unitPriceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        userColumn.setCellValueFactory(new PropertyValueFactory<>("user"));

        // Load Transactions Data From Database
        loadTransactionsFromDatabase();
    }

    private void loadTransactionsFromDatabase() {
        // Establish Connection To SQLite Database
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            System.out.println("Connecting to database: " + DB_URL);

            Statement stmt = conn.createStatement();

            // SQL Query To Fetch Available And Sold Transactions
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

            System.out.println("Executing query...");
            ResultSet rs = stmt.executeQuery(query);

            // Process Result Set And Populate ObservableList
            while (rs.next()) {
                System.out.println(
                    "Row: " + rs.getString("type") + ", " +
                    rs.getInt("book_id") + ", " +
                    rs.getString("transaction_date") + ", " +
                    rs.getString("title") + ", " +
                    rs.getString("category_name") + ", " +
                    rs.getString("condition") + ", " +
                    rs.getDouble("listed_price") + ", " +
                    rs.getInt("user_id")
                );

                // Add Each Transaction To The Table
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

            // Set TableView Data Source
            transactionTable.setItems(transactions);
            System.out.println("Data loaded into table successfully.");

        } catch (Exception e) {
            // Handle Errors During Data Load
            System.err.println("Error Received:");
            e.printStackTrace();
        }
    }

    @FXML
    public void handleNavigation(MouseEvent event) {
        // Handle Navigation Based On Clicked Label
        try {
            String fxmlFile = "";

            // Determine Target FXML File Based On Source Label
            if (event.getSource() == dashboardLabel) fxmlFile = "AdminDash.fxml";
            else if (event.getSource() == monitorTransactionsLabel) fxmlFile = "AdminTransactions.fxml";
            else if (event.getSource() == buyingSalesRecordLabel) fxmlFile = "AdminDash.fxml";
            else if (event.getSource() == manageUserAccountsLabel) fxmlFile = "AdminAccts.fxml";
            else if (event.getSource() == systemConfigLabel) fxmlFile = "AdminConfig.fxml";

            if (!fxmlFile.isEmpty()) {
                // Load And Display Target FXML Scene
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            }
        } catch (Exception e) {
            // Handle Errors During Navigation
            System.err.println("Navigation Error:");
            e.printStackTrace();
        }
    }
}