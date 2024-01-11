package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

public class dashboardController implements Initializable {
	
    @FXML
    private AnchorPane dashboardForm;

    @FXML
    private Button dashboardBtn;
    
    @FXML
    private Label dashboardCompleted;

    @FXML
    private AnchorPane dashboardImage;

    @FXML
    private Label dashboardTotalIncome;

    @FXML
    private Label dashboardTotalOrders;

    @FXML
    private Label dashboardWaiting;

    @FXML
    private Label dashboardWarehousePicked;

    @FXML
    private Button inventoryAddBtn;

    @FXML
    private TextField inventoryBin;

    @FXML
    private Button inventoryBtn;
    
    @FXML
    private TextField inventoryStockLevel;

    @FXML
    private Button inventoryClearBtn;

    @FXML
    private TableColumn<Product, String> inventoryColBin;

    @FXML
    private TableColumn<Product, String> inventoryColProdID;

    @FXML
    private TableColumn<Product, String> inventoryColProdName;

    @FXML
    private TableColumn<Product, String> inventoryColUnitPrice;
    
    @FXML
    private TableColumn<Product, String> inventoryColStockLevel;

    @FXML
    private AnchorPane inventoryForm;

    @FXML
    private AnchorPane inventoryImage;

    @FXML
    private TextField inventoryProdID;

    @FXML
    private TextField inventoryProdName;

    @FXML
    private Button inventoryRemoveBtn;

    @FXML
    private TableView<Product> inventoryTableView;

    @FXML
    private TextField inventoryUnitPrice;

    @FXML
    private Button inventoryUploadBtn;
    
    @FXML
    private Button inventoryUpdateBtn;
    
    @FXML
    private TextField inventorySearchBox;

    
    private static Connection con;
    private static PreparedStatement prep;
    private static Statement statement;
    private static ResultSet result;
    
    private ObservableList<Product> allProdList;
    
    public void inventoryAdd() {
    	String insertSql = "insert into products (product_id, product_name, unit_price, bin, stock_level) values (?,?,?,?,?)";
    	
    	con = DBConnection.getDBConnection();
    	try {
    		prep = con.prepareStatement(insertSql);
    		
    		prep.setString(1, inventoryProdID.getText());
    		prep.setString(2, inventoryProdName.getText());
    		prep.setString(3, inventoryUnitPrice.getText());
			prep.setString(4, inventoryBin.getText());
			prep.setString(5, inventoryStockLevel.getText());
			
			Alert alert;
			if (inventoryProdID.getText().isEmpty() 
					|| inventoryProdName.getText().isEmpty()
					|| inventoryUnitPrice.getText().isEmpty() 
					|| inventoryBin.getText().isEmpty()
					|| inventoryStockLevel.getText().isEmpty()) {
				
				alert = new Alert(AlertType.ERROR);
				alert.setContentText("please fill in all blank fields");
				alert.showAndWait();
			}
			else {
                String selectSql = "select product_id from products WHERE product_id = '"
                		+inventoryProdID.getText()
                		+"'";
                con = DBConnection.getDBConnection();
                statement = con.createStatement();
                result = statement.executeQuery(selectSql);

                if (result.next()) {
                    alert = new Alert(AlertType.ERROR);
                    alert.setContentText("product#"+inventoryProdID.getText()+" already exists");
                    alert.showAndWait();
                }
                else {
                    prep.executeUpdate();
                    
                    alert = new Alert(AlertType.INFORMATION);
                    alert.setContentText("product added to inventory successfully");
                    alert.showAndWait();
                    
                    inventoryShowAllProducts();
                    Main.customerC.orderShowAllProducts();
                    inventoryClear();
                    

				}
			}
    		
    	}
    	catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public void inventoryClear() {
    	inventoryProdID.setText("");
    	inventoryProdName.setText("");
    	inventoryUnitPrice.setText("");
    	inventoryBin.setText("");
    	inventoryStockLevel.setText("");
    }
    

    public static ObservableList<Product> inventoryGetAllProducts() {

        ObservableList<Product> allProdList = FXCollections.<Product> observableArrayList();
        String selectSql = "SELECT * from products";
        con = DBConnection.getDBConnection();
        
        try {
            prep = con.prepareStatement(selectSql);
            result = prep.executeQuery();
            while (result.next()) {
                Product prod = new Product(
                		result.getInt("id"),
                		result.getString("product_id"), 
                		result.getString("product_name"), 
                		result.getDouble("unit_price"), 
                		result.getString("bin"),
                		result.getInt("stock_level")
                		);
                allProdList.add(prod);
            }

        }
    	catch (Exception e) {
			e.printStackTrace();
		}
        return allProdList;
    }
    

    public void inventoryShowAllProducts() {
    	

    	allProdList = inventoryGetAllProducts();
    	inventoryColProdID.setCellValueFactory(new PropertyValueFactory<>("product_id"));
    	inventoryColProdName.setCellValueFactory(new PropertyValueFactory<>("product_name"));
        inventoryColUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unit_price"));
        inventoryColBin.setCellValueFactory(new PropertyValueFactory<>("bin"));
        inventoryColStockLevel.setCellValueFactory(new PropertyValueFactory<>("stock_level"));
        
        inventoryTableView.setItems(allProdList);

    }
    
    public void inventorySearch() {
    	FilteredList<Product> filteredProdList = new FilteredList<>(allProdList, p -> true);
    	
    	inventorySearchBox.textProperty().addListener((obs, oldVal, newVal) -> {
    		if(newVal.isEmpty()) {
    			filteredProdList.setPredicate(p -> true);
    		}
    		else {
    			String sub = newVal.trim().toLowerCase();
    			filteredProdList.setPredicate(p 
    					-> p.getProduct_id().toLowerCase().contains(sub)
    					|| p.getProduct_name().toLowerCase().contains(sub));
    		}
    		inventoryTableView.setItems(filteredProdList);
    		
    	});
    }
    
    
    public void inventorySelectTableRow() {
    	if (inventoryTableView.getSelectionModel().getSelectedItem() != null) {
	        Product prod = inventoryTableView.getSelectionModel().getSelectedItem();
	        inventoryProdID.setText(prod.getProduct_id());
	        inventoryProdName.setText(prod.getProduct_name());
	        inventoryUnitPrice.setText(String.valueOf(prod.getUnit_price()));
	        inventoryBin.setText(prod.getBin());
	        inventoryStockLevel.setText(String.valueOf(prod.getStock_level()));
	        
    	}
    }
    

    public void inventoryUpdate() {

        String updateSql = "UPDATE products SET product_name = '"+inventoryProdName.getText()
        	+"', unit_price = '"+inventoryUnitPrice.getText()
        	+"', bin = '"+inventoryBin.getText()
        	+"', stock_level = '"+inventoryStockLevel.getText()
        	+"' where product_id = '"+inventoryProdID.getText()
        	+"'";
        con = DBConnection.getDBConnection();

        try {
			Alert alert;
			if (inventoryProdID.getText().isEmpty() 
					|| inventoryProdName.getText().isEmpty()
					|| inventoryUnitPrice.getText().isEmpty() 
					|| inventoryBin.getText().isEmpty()
					|| inventoryStockLevel.getText().isEmpty()) {
				
				alert = new Alert(AlertType.ERROR);
				alert.setContentText("please fill in all blank fields");
				alert.showAndWait();
			}
			else {
                String selectSql = "select product_id from products WHERE product_id = '"
                		+inventoryProdID.getText()
                		+"'";
                con = DBConnection.getDBConnection();
                statement = con.createStatement();
                result = statement.executeQuery(selectSql);

                if (!result.next()) {
                    alert = new Alert(AlertType.ERROR);
                    alert.setContentText("product#"+inventoryProdID.getText()+" not found");
                    alert.showAndWait();
                }
                else {
	                statement = con.createStatement();
	                statement.executeUpdate(updateSql);
	                
	                alert = new Alert(AlertType.INFORMATION);
	                String alertText = "product#"+inventoryProdID.getText()+" information updated successfully";
	                alert.setContentText(alertText);
	                alert.showAndWait();
	                
	                Main.customerC.orderShowAllProducts();
	                inventoryShowAllProducts();
	                inventoryClear();
                }
            }
        }
    	catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    
    
    public void inventoryRemove() {
        String deleteSql = "DELETE FROM products WHERE product_id = '"
        		+inventoryProdID.getText()
        		+"'";
        con = DBConnection.getDBConnection();
        
        try {
			Alert alert;
			if (inventoryProdID.getText().isEmpty() 
					|| inventoryProdName.getText().isEmpty()
					|| inventoryUnitPrice.getText().isEmpty() 
					|| inventoryBin.getText().isEmpty()
					|| inventoryStockLevel.getText().isEmpty()) {
				
				alert = new Alert(AlertType.ERROR);
				alert.setContentText("please fill in all blank fields");
				alert.showAndWait();
			}
			else {

                alert = new Alert(AlertType.CONFIRMATION,
                		"are you sure you want to permanently remove product#"+inventoryProdID.getText()+"?",
                		ButtonType.YES, 
                		ButtonType.CANCEL);
                alert.showAndWait();

                if (alert.getResult() == ButtonType.YES) {

	                statement = con.createStatement();
	                statement.executeUpdate(deleteSql);
	                
	                alert = new Alert(AlertType.INFORMATION);
	                String alertText = "product#"+inventoryProdID.getText()+" removed successfully";
	                alert.setContentText(alertText);
	                alert.showAndWait();
                    
	                inventoryShowAllProducts();
	                Main.customerC.orderShowAllProducts();
	                inventoryClear();

                }
                else {
                    alert = new Alert(AlertType.INFORMATION);
                    alert.setContentText("cancelled");
                    alert.showAndWait();
                }
            }

        }
    	catch (Exception e) {
			e.printStackTrace();
		}
    }
    

    
    public void dashboardShowTotalIncome() {
    	double totalIncome = 0;
    	String selectSql = "select sum(order_total) from orders where order_status = 'OrderStatus.Completed'";
    	con = DBConnection.getDBConnection();
        try {
            statement = con.createStatement();
            result = statement.executeQuery(selectSql);

            if (result.next()) {
            	totalIncome = result.getDouble("sum(order_total)");
            }
            dashboardTotalIncome.setText("Total Income: £"+String.valueOf(totalIncome));
        }
        
        catch (Exception e) {
            e.printStackTrace();
        }
    	
    }
    
    public void dashboardShowTotalOrders() {
    	
    	int totalOrders = 0;
    	String selectSql = "SELECT COUNT(*) from orders";
    	con = DBConnection.getDBConnection();
        try {
            statement = con.createStatement();
            result = statement.executeQuery(selectSql);

            if (result.next()) {
            	totalOrders = result.getInt("COUNT(*)");
            }
            dashboardTotalOrders.setText("Total Order(s): "+String.valueOf(totalOrders));
        }
        
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public void dashboardShowOrderStatus(Label label, String orderStatus, String text) {
    	int cnt = 0;
    	String selectSql = "select COUNT(*) from orders where order_status = '"
    						+orderStatus
    						+"'";
    	con = DBConnection.getDBConnection();
        try {
            statement = con.createStatement();
            result = statement.executeQuery(selectSql);

            if (result.next()) {
            	cnt = result.getInt("COUNT(*)");
            }
            label.setText(text+String.valueOf(cnt));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public void dashboardSetAllLabel() {
    	dashboardShowTotalIncome();
	    dashboardShowTotalOrders();
    	dashboardShowOrderStatus(dashboardCompleted, "OrderStatus.Completed", "Completed: ");
	    dashboardShowOrderStatus(dashboardWarehousePicked, "OrderStatus.WarehousePicked", "Warehouse Picked: ");
	    dashboardShowOrderStatus(dashboardWaiting, "OrderStatus.Waiting", "Waiting: ");
    	
    }

    
    
	public void switchForm(ActionEvent event) {
		if (event.getSource() == dashboardBtn) {
			dashboardForm.setVisible(true);
			dashboardImage.setVisible(true);
			inventoryForm.setVisible(false);
			inventoryImage.setVisible(false);
			dashboardSetAllLabel();
		}
		else if (event.getSource() == inventoryBtn) {
			dashboardForm.setVisible(false);
			dashboardImage.setVisible(false);
			inventoryForm.setVisible(true);
			inventoryImage.setVisible(true);
			inventoryShowAllProducts();
		}
		
	}
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		Main.dashboardC = this;
		inventoryShowAllProducts();
		inventorySearch();
		
		dashboardSetAllLabel();
	}
	

}
