package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;


public class Buyer {
    
    // BOOKS TABLE
    @FXML
    private TableView<BookCart> BooksTable;
    @FXML
    private TableColumn<BookCart, Integer> IDCol;
    @FXML
    private TableColumn<BookCart, String> TitleCol;
    @FXML
    private TableColumn<BookCart, String> AuthorCol;
    @FXML
    private TableColumn<BookCart, Integer> YearCol;
    @FXML
    private TableColumn<BookCart, String> CategoryCol;
    @FXML
    private TableColumn<BookCart, String> ConditionCol;
    @FXML
    private TableColumn<BookCart, Double> PriceCol;
    // Void because it is not a data type. CartCol will have a button where users can add the book to their cart.
    @FXML
    private TableColumn<BookCart, Void> CartCol;
    
    private String asuId;
    public void setAsuId(String asuId) {
        this.asuId = asuId;
    }
    

    private ObservableList<BookCart> LoadedBooks = FXCollections.observableArrayList();
    private ObservableList<BookCart> BooksToShowInTable = FXCollections.observableArrayList();
    
    // CART 
    @FXML
    private VBox cartScrollPane;
    
    @FXML
    private VBox cartVBox;
    
    @FXML
    private Label totalLabel;
    
    @FXML
    private Label ConfirmCheckoutLabel;
    
    @FXML
    private Button RemoveFromCartButon;
    
    private ObservableList<BookCart> BooksInCart = FXCollections.observableArrayList();
    
    
    
    @FXML
    private Button CheckOutButton;
    
    @FXML
    private void CheckOutButtonClick() {
    	System.out.println("CheckOUT");
    	
    	// Make sure cart is not empty.
    	if (BooksInCart.isEmpty()) {
    	    return;
    	}
    	
		String insertQuery = "INSERT INTO BuyingRecords (fk_books_book_id, fk_users_buyer_id, transaction_date) VALUES (?, ?, ?);";
		String updateQuery = "Update ListedBooks SET sale_status = 'sold' WHERE book_id = ?;";
		
		try (Connection connection = DriverManager.getConnection(DB_URL)) {
			
		    PreparedStatement preparedInsertStatement = connection.prepareStatement(insertQuery);
		    PreparedStatement preparedUpdateStatement = connection.prepareStatement(updateQuery);		    
		    
		    LocalDate date = LocalDate.now();
		    
		    ObservableList<BookCart> BooksInCartCopy = FXCollections.observableArrayList(BooksInCart);
		    
		    for (BookCart book : BooksInCartCopy) {
		    	// Add it to the BuyingBooks table
		    	preparedInsertStatement.setInt(1, book.id.get()); 
		    	preparedInsertStatement.setInt(2, Integer.parseInt(asuId)); 
		    	preparedInsertStatement.setString(3, date.toString()); 
		    	
		    	// Update ListedBooks Table
		    	preparedUpdateStatement.setInt(1, book.id.get()); 
		    	
		    	
		    	preparedInsertStatement.executeUpdate();
		    	preparedUpdateStatement.executeUpdate();
		    	BooksInCart.remove(book);
		    }
		    updateCartText();
		    InitialLoadTable();
		    
		    ConfirmCheckoutLabel.setText("TRANSACTION COMPLETE! Rerouting to third-party app.");
		    
		} catch (Exception e) {
		    e.printStackTrace();
		}	
    }
    
    
	@FXML
	private void RemoveFromCartButonClick() {
		BookCart selectedBook = BooksTable.getSelectionModel().getSelectedItem();
		
	    if (selectedBook == null) {
	        return;
	    }
	    
	    if (BooksInCart.contains(selectedBook)) {
	    	removeFromCartAndUpdateUI(selectedBook);
	    }
	}
    
	private void removeFromCartAndUpdateUI(BookCart selectedBook) {
	    selectedBook.isInCart.set(false);
	    selectedBook.cartText.set("Not In Cart");
		BooksInCart.remove(selectedBook);
		
		updateCartText();
	}

    
    @FXML
    private Button AddToCartButton;
    
	@FXML
	private void AddToCartButtonClick()  {
		BookCart selectedBook = BooksTable.getSelectionModel().getSelectedItem();
		
	    if (selectedBook == null) {
	        return;
	    }
	    
	    if (!BooksInCart.contains(selectedBook)) {
	    	addToCartAndUpdateUI(selectedBook);
	    }

	}
	
	public void addToCartAndUpdateUI(BookCart selectedBook) {
	    selectedBook.isInCart.set(true);
	    selectedBook.cartText.set("Added To Cart");
		BooksInCart.add(selectedBook);
		updateCartText();
	}
	
	
	private void updateCartText() {
		cartVBox.getChildren().clear();
		double total = 0;
		for (BookCart book : BooksInCart) {
			VBox cartCard = new VBox();
			Text title = new Text(book.title.get());
			title.setFont(Font.font("System", FontWeight.BOLD, 16));
			Text author = new Text(book.author.get());
			Text condition = new Text(book.condition.get());
			Text price = new Text("$" + String.valueOf(book.price.get()));
			price.setFont(Font.font("System", FontWeight.BOLD, 14));
			price.setFill(Color.GRAY);
			Text spacer = new Text("");
			cartCard.getChildren().add(title);
			cartCard.getChildren().add(author);
			cartCard.getChildren().add(condition);
			cartCard.getChildren().add(price);
			cartCard.getChildren().add(spacer);
			
			cartVBox.getChildren().add(cartCard);
			total = total + book.price.get();
		}
		
		totalLabel.setText("TOTAL: " + String.format("%.2f", total));
	}
	
    // DATABASE
    private static final String DB_URL = "jdbc:sqlite:./sun_devil_book_exchange.db";
	
    
    @FXML
    private VBox CategoryBanner;
    
	@FXML
	private Button LogoutButton;
	@FXML
	private void LogoutButtonClick() throws IOException  {
		navigateToPage("Login.fxml", (Stage) LogoutButton.getScene().getWindow());
	}
	
	@FXML
	private Button BrowseAllButton;
    @FXML
    public void BrowseAllButtonClick() {
    	LoadTableFilterCategory("all");
    }
	
    @FXML
    public void initialize() {
    	loadCategoryBanner();
    	InitialLoadTable();
    }
    

    
    // Populated left banner of page with category buttons which can be used to filter books.
    private void loadCategoryBanner() {
    	ObservableList<String> categories = getCategories();
    	
    	for (String category : categories) {
    		Button button = new Button(category);
    		button.setStyle("-fx-background-color: white");
            button.setOnMouseEntered(event -> button.setCursor(javafx.scene.Cursor.HAND));
            button.setOnMouseExited(event -> button.setCursor(javafx.scene.Cursor.DEFAULT));
            // Set button to load table with books filtered by category.
    		button.setOnAction(event -> LoadTableFilterCategory(category));
    		CategoryBanner.getChildren().add(button);
    	}
    }
    
    // Gets unique categories from database to populate category filter banner.
    private ObservableList<String> getCategories() {
        ObservableList<String> categories = FXCollections.observableArrayList();
        String categoryQuery = "SELECT DISTINCT category_name FROM Categories ;";
        
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
        	if (connection != null) {
        		try (Statement statement = connection.createStatement()) {
        			ResultSet result = statement.executeQuery(categoryQuery);
        		      while (result.next()) {
        		            categories.add(result.getString("category_name"));
        		        }
        		} catch (Exception e) {
                    e.printStackTrace();
                }
        	}
        } catch (Exception e) {
            e.printStackTrace();
        }
		return categories; 
    }
    
    // Populated the table with books given a category. If no filter is needed, use "all" as argument.
    private void InitialLoadTable()  {
    	IDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
    	TitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
    	AuthorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
    	YearCol.setCellValueFactory(new PropertyValueFactory<>("year"));
    	CategoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
    	ConditionCol.setCellValueFactory(new PropertyValueFactory<>("condition"));
    	PriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
    	CartCol.setCellValueFactory(new PropertyValueFactory<>("inCart"));

    	LoadedBooks = loadBooksFromDatabase();
    	BooksTable.setItems(LoadedBooks);
    }
    
    private void LoadTableFilterCategory(String CategoryToFilter) {    	
    	BooksToShowInTable.clear();
    	
    	if (CategoryToFilter.equals("all")) {
    		BooksTable.setItems(LoadedBooks);
    		return;
    	} 
    	
    	for (BookCart book : LoadedBooks) {
    		if (book.category.get().equals(CategoryToFilter)) {
    			BooksToShowInTable.add(book);
    		}
    	}
    	BooksTable.setItems(BooksToShowInTable);
    }
    
    
    // Gets all books with given category from database.
    ObservableList<BookCart> loadBooksFromDatabase() {
    	ObservableList<BookCart> books = FXCollections.observableArrayList();
        	String query = "SELECT "
        		+ "ListedBooks.book_id, "
        		+ "ListedBooks.title, "
        		+ "ListedBooks.author, "
        		+ "ListedBooks.publication_year AS year, "
        		+ "Categories.category_name AS category, "
        		+ "Conditions.condition, "
        		+ "ListedBooks.listed_price AS price "
        		+ "FROM ListedBooks "
        		+ "JOIN Categories ON ListedBooks.fk_categories_category_id = Categories.category_id "
        		+ "JOIN Conditions ON ListedBooks.fk_conditions_condition = Conditions.condition "
        		+ "WHERE ListedBooks.sale_status = 'available' "
        		+ "ORDER BY Conditions.condition;";
     	
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            if (connection != null) {
                try (Statement statement = connection.createStatement()) {
                	try (ResultSet result = statement.executeQuery(query)) {

                        while (result.next()) {
                        	BookCart book = new BookCart(
                            	result.getInt("book_id"),
                            	result.getString("title"),
                            	result.getString("author"),
                            	result.getInt("year"),
                            	result.getString("category"),
                            	result.getString("condition"),
                            	result.getDouble("price"),
                        			"Not In Cart",
                        			false);
                        	
                        	books.add(book);
                        }
                	}
                }
            }
        }
        // TODO: Add proper handling if we have the time
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return books;
    }
    
    
    // Switches scenes to another page.
    private void navigateToPage(String pageFXMLName, Stage currentStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(pageFXMLName));
        Parent page = loader.load();
        Scene scene = new Scene(page);
        currentStage.setScene(scene);
        currentStage.show();
    }

    
    // Class to store Books
    public static class BookCart {
        private final SimpleIntegerProperty id;
        private final SimpleStringProperty title;
        private final SimpleStringProperty author;
        private final SimpleIntegerProperty year;
        private final SimpleStringProperty category;
        private final SimpleStringProperty condition;
        private final SimpleDoubleProperty price;
        private final SimpleStringProperty cartText;
        private final SimpleBooleanProperty isInCart;

        public BookCart(int id, String title, String author, int year, String category, String condition, double price, String cartText, boolean isInCart) {
            this.id = new SimpleIntegerProperty(id);
            this.title = new SimpleStringProperty(title);
            this.author = new SimpleStringProperty(author);
            this.year = new SimpleIntegerProperty(year);
            this.category = new SimpleStringProperty(category);
            this.condition = new SimpleStringProperty(condition);
            this.price = new SimpleDoubleProperty(price);
            this.cartText = new SimpleStringProperty(cartText);
            this.isInCart = new SimpleBooleanProperty(isInCart);
        }
        
        // needed for Javafx to work. 
        public SimpleIntegerProperty idProperty() {return id;}
        public SimpleStringProperty titleProperty() {return title;}
        public SimpleStringProperty authorProperty() {return author;}
        public SimpleIntegerProperty yearProperty() {return year;}
        public SimpleStringProperty categoryProperty() {return category;}
        public SimpleStringProperty conditionProperty() {return condition;}
        public SimpleDoubleProperty priceProperty() {return price;}
        public SimpleStringProperty inCartProperty() {return cartText;}
        public SimpleBooleanProperty isInCartProperty() {return isInCart;}
    }  
}


