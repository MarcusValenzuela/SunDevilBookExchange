import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;

public class ManageUserAccountsController {

    @FXML
    private TableView<UserAccount> buyerTableView; // Table for Buyer Accounts
    @FXML
    private TableColumn<UserAccount, String> buyerUserIdColumn;
    @FXML
    private TableColumn<UserAccount, String> buyerDateJoinedColumn;
    @FXML
    private TableColumn<UserAccount, String> buyerActionColumn;

    @FXML
    private TableView<UserAccount> sellerTableView; // Table for Seller Accounts
    @FXML
    private TableColumn<UserAccount, String> sellerUserIdColumn;
    @FXML
    private TableColumn<UserAccount, String> sellerDateJoinedColumn;
    @FXML
    private TableColumn<UserAccount, String> sellerActionColumn;

    @FXML
    private Button logoutButton;

    private ObservableList<UserAccount> buyerData = FXCollections.observableArrayList();
    private ObservableList<UserAccount> sellerData = FXCollections.observableArrayList();

    private static final String DB_URL = "jdbc:sqlite:./sun_devil_book_exchange.db";

    // Initialize method to set up TableView
    @FXML
    public void initialize() {
        // Set up the columns for Buyer Accounts
        buyerUserIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        buyerDateJoinedColumn.setCellValueFactory(new PropertyValueFactory<>("dateJoined"));
        buyerActionColumn.setCellValueFactory(new PropertyValueFactory<>("action"));

        // Set up the columns for Seller Accounts
        sellerUserIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        sellerDateJoinedColumn.setCellValueFactory(new PropertyValueFactory<>("dateJoined"));
        sellerActionColumn.setCellValueFactory(new PropertyValueFactory<>("action"));

        // Load data from database
        loadBuyerData();
        loadSellerData();
    }

    // Method to load Buyer Accounts data from the database
    private void loadBuyerData() {
        String sql = "SELECT * FROM Users WHERE role = 'buyer'";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String userId = rs.getString("asu_id");
                String dateJoined = rs.getString("date_joined");
                buyerData.add(new UserAccount(userId, dateJoined, "Delete"));
            }

            buyerTableView.setItems(buyerData); // Bind the data to the TableView

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to load Seller Accounts data from the database
    private void loadSellerData() {
        String sql = "SELECT * FROM Users WHERE role = 'seller'";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String userId = rs.getString("asu_id");
                String dateJoined = rs.getString("date_joined");
                sellerData.add(new UserAccount(userId, dateJoined, "Delete"));
            }

            sellerTableView.setItems(sellerData); // Bind the data to the TableView

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void logoutButtonClick() {
        // Close the current window (logging out the user)
        Stage currentStage = (Stage) logoutButton.getScene().getWindow();
        currentStage.close();

        // navigate to the login screen (uncomment the lines below for login page)
        // FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
        // Parent root = loader.load();
        // Scene scene = new Scene(root);
        // currentStage.setScene(scene);
        // currentStage.show();
    }

    // Model class for UserAccount
    public static class UserAccount {
        private final SimpleStringProperty userId;
        private final SimpleStringProperty dateJoined;
        private final SimpleStringProperty action;

        public UserAccount(String userId, String dateJoined, String action) {
            this.userId = new SimpleStringProperty(userId);
            this.dateJoined = new SimpleStringProperty(dateJoined);
            this.action = new SimpleStringProperty(action);
        }

        public String getUserId() {
            return userId.get();
        }

        public void setUserId(String userId) {
            this.userId.set(userId);
        }

        public String getDateJoined() {
            return dateJoined.get();
        }

        public void setDateJoined(String dateJoined) {
            this.dateJoined.set(dateJoined);
        }

        public String getAction() {
            return action.get();
        }

        public void setAction(String action) {
            this.action.set(action);
        }
    }
}
