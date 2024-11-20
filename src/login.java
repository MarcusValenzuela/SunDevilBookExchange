import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class login {

    @FXML
    private TextField asuid;
    @FXML
    private TextField password;
    @FXML
    private Label success;

    private static final String DB_URL = "jdbc:sqlite:./sun_devil_book_exchange.db";

    public void userLogin() {
        String id = asuid.getText();
        String pass = password.getText();
        String role = checkCredentials(id, pass);

        if (role != null) {
            success.setText("Login Successful!");
            success.setStyle("-fx-text-fill: #11ff00");
            // TESTING AREA
            try {
                String pagetoRouteTo = "";
                if ("admin".equals(role)) {
                    pagetoRouteTo = "ManageUserAccounts.fxml";
                } else if ("seller".equals(role)) {
                    pagetoRouteTo = "Seller.fxml";
                } else if ("buyer".equals(role)) {
                    pagetoRouteTo = "Buyer.fxml";
                } else {
                    throw new IllegalStateException("ERROR! Unexpected role: " + role + "\n");
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource(pagetoRouteTo));
                Parent buyerPage = loader.load();
                Scene buyerScene = new Scene(buyerPage);
                Stage currentStage = (Stage) ((Node) success).getScene().getWindow();
                currentStage.setScene(buyerScene);
                currentStage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // TESTING AREA
        } else {
            success.setText("Invalid ID or Password.");
            success.setStyle("-fx-text-fill: #ff0000");
        }
    }

    // Function checks user credentials and returns user's role if credentials are
    // correct and NULL otherwise.
    private String checkCredentials(String id, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (conn != null) {
                String sql = "SELECT password, role FROM Users WHERE asu_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, id);

                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            String storedPassword = rs.getString("password");
                            if (password.equals(storedPassword)) {
                                return rs.getString("role");
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}