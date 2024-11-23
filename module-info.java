module Group2 {
	requires javafx.controls;
	requires javafx.fxml;
	
	requires java.sql;
	requires javafx.graphics;
	requires javafx.base;

	
	opens application to javafx.graphics, javafx.fxml, javafx.base;
}





//module Group2 {
//	requires javafx.controls;
//	requires javafx.fxml;
//	
//	requires java.sql;
//	requires javafx.graphics;
//
//	
//	opens application to javafx.graphics, javafx.fxml;
//}
