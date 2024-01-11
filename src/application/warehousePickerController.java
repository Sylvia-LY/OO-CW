package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;


public class warehousePickerController implements Initializable {
	

    @FXML
    private TableColumn<Integer, Integer> orderColOrderNumber;

    @FXML
    private TableColumn<?, ?> orderItemColBin;

    @FXML
    private TableColumn<?, ?> orderItemColProdID;

    @FXML
    private TableColumn<?, ?> orderItemColProdName;

    @FXML
    private TableColumn<?, ?> orderItemColQty;

    @FXML
    private TableColumn<?, ?> orderItemColSubtotal;

    @FXML
    private Button warehouseCompleteBtn;

    @FXML
    private TableView<OrderItem> warehouseOrderItemTableView;

    @FXML
    private TableView<Integer> warehouseOrderTableView;
    
    private static Connection con;
    private static PreparedStatement prep;
    private static Statement statement;
    private static ResultSet result;

    private ObservableList<Integer> allOrderList;
    
    public ObservableList<Integer> warehouseGetAllOrders() {
    	ObservableList<Integer> allOrderList = FXCollections.<Integer> observableArrayList();
    	String selectSql = "select id from orders where order_status = 'OrderStatus.Waiting'";
    	con = DBConnection.getDBConnection();
        
        try {
            prep = con.prepareStatement(selectSql);
            result = prep.executeQuery();
            while (result.next()) {
            	allOrderList.add(result.getInt("id"));
            }
        }
    	catch (Exception e) {
			e.printStackTrace();
		}
        return allOrderList;
    }
    
    
    public void warehouseShowAllOrders() {
    	
    	allOrderList = warehouseGetAllOrders();
        orderColOrderNumber.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue()).asObject());
        warehouseOrderTableView.setItems(allOrderList);

    }

    private int orderNumber;
    
    public void warehouseSelectOrderTableRow() {
    	if (warehouseOrderTableView.getSelectionModel().getSelectedItem() != null) {
    		orderNumber = warehouseOrderTableView.getSelectionModel().getSelectedItem();
    		warehouseShowOrderItems();
    	}
    }
    
    private List<OrderItem> orderItemList;
    
    public List<OrderItem> warehouseGetOrderItems() {
    	List<OrderItem> orderItemList = new ArrayList<>();
    	String selectSql = "select products.id, qty, subtotal, product_id, product_name, bin from order_items "
    			+ "inner join products on order_items.product_number = products.id "
    			+ "where order_number = "
    			+ orderNumber;
    	con = DBConnection.getDBConnection();
        
        try {
            prep = con.prepareStatement(selectSql);
            result = prep.executeQuery();
            while (result.next()) {
                OrderItem i = new OrderItem(
                		
                		result.getInt("products.id"),
                		result.getInt("qty"), 
                		result.getDouble("subtotal"), 
                		result.getString("product_id"), 
                		result.getString("product_name"),
                		result.getString("bin")
                		);
                orderItemList.add(i);
            }

        }
    	catch (Exception e) {
			e.printStackTrace();
		}
        return orderItemList;
    	
    }
    
    public void warehouseShowOrderItems() {
    	
    	
    	orderItemList = warehouseGetOrderItems();
    	ObservableList<OrderItem> orderItemObservableList = FXCollections.observableList(orderItemList);
    	orderItemColProdID.setCellValueFactory(new PropertyValueFactory<>("product_id"));
    	orderItemColProdName.setCellValueFactory(new PropertyValueFactory<>("product_name"));
    	orderItemColQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
    	orderItemColSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
    	orderItemColBin.setCellValueFactory(new PropertyValueFactory<>("bin"));
        
        warehouseOrderItemTableView.setItems(orderItemObservableList);
    	
    }
    
    
    
    public void warehouseComplete() {
    	
        String updateSql = "UPDATE orders SET order_status = 'OrderStatus.WarehousePicked' "
        	+"where id = '"+orderNumber
        	+"'";
        con = DBConnection.getDBConnection();

        try {
			Alert alert;
            statement = con.createStatement();
            statement.executeUpdate(updateSql);
            
            alert = new Alert(AlertType.INFORMATION);
            String alertText = "order#"+orderNumber+" has been fully picked";
            alert.setContentText(alertText);
            alert.showAndWait();
            
            warehouseShowAllOrders();
            warehouseOrderItemTableView.getItems().clear();
            Main.customerC.collectGetOrderNumber();
            Main.dashboardC.dashboardSetAllLabel();
            
        }
    	catch (Exception e) {
			e.printStackTrace();
		}
    	
    }

    

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		Main.warehousePickerC = this;
		warehouseShowAllOrders();

		
	}

}
