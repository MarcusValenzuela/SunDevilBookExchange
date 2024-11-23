package application;

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

public class Login {

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
		
        FXMLLoader loader = new FXMLLoader();
		
		if (role != null) {
			success.setText("Login Successful!");
			success.setStyle("-fx-text-fill: #11ff00");
			//TESTING AREA
			try {
				String pagetoRouteTo = "";
				if ("admin".equals(role)) {
					pagetoRouteTo = "AdminDash.fxml";
//				} else if ("seller".equals(role)) {
//					pagetoRouteTo = "Seller.fxml";
				} else if ("seller".equals(role)) {
				    pagetoRouteTo = "Seller.fxml";
				    loader.setControllerFactory(param -> {
				        Seller seller = new Seller();
				        seller.setAsuId(id);
				        return seller;
				    });
				} else if ("buyer".equals(role)) {
					pagetoRouteTo = "Buyer.fxml";

					// Custom controller to be able to pass the buyer id to Buyer page. Need this because we are using FXMl.
	                loader.setControllerFactory(param -> {
	                    Buyer buyer = new Buyer();
	                    buyer.setAsuId(id);
	                    return buyer;
	                });
					
				} else {
				    throw new IllegalStateException("ERROR! Unexpected role: " + role + "\n");
				}
                
				
                loader.setLocation(getClass().getResource(pagetoRouteTo));
				Parent page = loader.load();
				Scene scene = new Scene(page);
				Stage currentStage = (Stage) ((Node) success).getScene().getWindow();
				currentStage.setScene(scene);
				currentStage.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
			//TESTING AREA
		} else {
			success.setText("Invalid ID or Password.");
			success.setStyle("-fx-text-fill: #ff0000");
		}	
	}

	// Function checks user credentials and returns user's role if credentials are correct and NULL otherwise.
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


















