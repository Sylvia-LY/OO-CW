package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

public class customerController implements Initializable {
	

    @FXML
    private ComboBox<Integer> collectOrderNumber;

    @FXML
    private Button collectPickUpBtn;

    @FXML
    private Label orderTotal;
    
    @FXML
    private Button collectBtn;

    @FXML
    private AnchorPane collectForm;

    @FXML
    private AnchorPane collectImage;

    @FXML
    private Button orderAddBtn;

    @FXML
    private Button orderClearBtn;

    @FXML
    private AnchorPane orderForm;

    @FXML
    private AnchorPane orderImage;

    @FXML
    private Button orderPayBtn;

    @FXML
    private TextField orderProdID;

    @FXML
    private TextField orderProdName;

    @FXML
    private Spinner<Integer> orderQuantitySpin;

    @FXML
    private Button orderRemoveBtn;

    @FXML
    private TextField orderSearchBox;

    @FXML
    private TableView<Product> orderShopTableView;

    @FXML
    private TableView<OrderItem> orderTrolleyTableView;

    @FXML
    private TextField orderUnitPrice;

    @FXML
    private Button shopBtn;

    @FXML
    private TableColumn<?, ?> shopColProdID;

    @FXML
    private TableColumn<?, ?> shopColProdName;

    @FXML
    private TableColumn<?, ?> shopColStockLevel;

    @FXML
    private TableColumn<?, ?> shopColUnitPrice;

    @FXML
    private TableColumn<?, ?> trolleyColProdID;

    @FXML
    private TableColumn<?, ?> trolleyColProdName;

    @FXML
    private TableColumn<?, ?> trolleyColQty;

    @FXML
    private TableColumn<?, ?> trolleyColSubtotal;
    
    private ObservableList<Product> allProdList;
    
    List<OrderItem> orderItemList = new ArrayList<>();
    
    private OrderItem itemToRemove;
    
    private static Connection con;
    private static PreparedStatement prep;
    private static Statement statement;
    private static ResultSet result;
    
    public void orderShowAllProducts() {
    	allProdList = dashboardController.inventoryGetAllProducts();
    	shopColProdID.setCellValueFactory(new PropertyValueFactory<>("product_id"));
    	shopColProdName.setCellValueFactory(new PropertyValueFactory<>("product_name"));
    	shopColUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unit_price"));
    	shopColStockLevel.setCellValueFactory(new PropertyValueFactory<>("stock_level"));
        
    	orderShopTableView.setItems(allProdList);

    }
    
    
    public void orderSearch() {
        FilteredList<Product> filteredProdList = new FilteredList<>(allProdList, p -> true);
        
        orderSearchBox.textProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal.isEmpty()) {
                filteredProdList.setPredicate(p -> true);
            }
            else {
                String sub = newVal.trim().toLowerCase();
                filteredProdList.setPredicate(p 
                        -> p.getProduct_id().toLowerCase().contains(sub)
                        || p.getProduct_name().toLowerCase().contains(sub));
            }
            orderShopTableView.setItems(filteredProdList);
            
        });
    }
    
    
    public void orderSetQuantitySpin(Integer qty) {
        SpinnerValueFactory<Integer> valueFactory = 
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, qty);
        valueFactory.setValue(0);
        orderQuantitySpin.setValueFactory(valueFactory);
        
    }
    
    public void orderClear() {
        orderProdID.setText("");
        orderProdName.setText("");
        orderUnitPrice.setText("");
        orderSetQuantitySpin(0);
    }
    
    public void orderSelectShopTableRow() {
    	
    	if (orderShopTableView.getSelectionModel().getSelectedItem() != null) {
	        Product prod = orderShopTableView.getSelectionModel().getSelectedItem();
	        orderProdID.setText(prod.getProduct_id());
	        orderProdName.setText(prod.getProduct_name());
	        orderUnitPrice.setText(String.valueOf(prod.getUnit_price()));
	        
	        int qty;
	        List<OrderItem> orderItem = orderItemList.stream()
													.filter(i -> i.getProduct_number() == prod.getId())
													.collect(Collectors.toList());
	        if (orderItem.isEmpty()) {
	        	qty = 0;
	        }
	        else {
	        	qty = orderItem.get(0).getQty();
	        }
	        if (prod.getStock_level() - qty > 0) {
	        	orderSetQuantitySpin(prod.getStock_level() - qty);
	        }
	        else {
	        	orderSetQuantitySpin(0);
	        }
	        
    	}
    }
    
        
    public void orderAdd() {
    	
    	if (orderQuantitySpin.getValue()!=null) {
    		int qty = orderQuantitySpin.getValue();
    		if (!(qty==0 || orderProdID.getText().isEmpty())) {
    	        Product prod = orderShopTableView.getSelectionModel().getSelectedItem();
    	        
    	        List<OrderItem> orderItem = orderItemList.stream()
    	        										.filter(i -> i.getProduct_number() == prod.getId())
    	        										.collect(Collectors.toList());
    	        
    	        if (!orderItem.isEmpty()) {
    	        	qty += orderItem.get(0).getQty();
    	        	orderItemList.remove(orderItem.get(0));
    	        }
            	OrderItem newOrderItem = new OrderItem(
            			prod.getId(),
            			qty,
            			prod.getUnit_price()*qty,
            			prod.getProduct_id(),
            			prod.getProduct_name(),
            			prod.getBin()
            			);
            	orderItemList.add(newOrderItem);
                orderShowAllOrderItems();
                orderShowTotal();
                orderClear();
                
    			
    		}
            else {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setContentText("to add to your trolley, make sure you've selected both a product and its quantity");
                alert.showAndWait();
                
                orderClear();
            }
    		
    		orderShowAllProducts();
    		
    	}
        
    }
    
    public void orderShowAllOrderItems() {
        ObservableList<OrderItem> orderItemObservableList = FXCollections.observableList(orderItemList);
        
        trolleyColProdID.setCellValueFactory(new PropertyValueFactory<>("product_id"));
        trolleyColProdName.setCellValueFactory(new PropertyValueFactory<>("product_name"));
        trolleyColSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        trolleyColQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        
        orderTrolleyTableView.setItems(orderItemObservableList);
    }

    public void orderSelectTrolleyTableRow() {
    	if (orderTrolleyTableView.getSelectionModel().getSelectedItem() != null) {
    		itemToRemove = orderTrolleyTableView.getSelectionModel().getSelectedItem();
    	}
        
    }
    
    public void orderRemove() {
        Alert alert;
        if (itemToRemove==null) {
            
            alert = new Alert(AlertType.ERROR);
            alert.setContentText("no item selected for removal");
            alert.showAndWait();
        }
        else {
            alert = new Alert(AlertType.CONFIRMATION,
                    "are you sure you want to remove this item from your trolley?",
                    ButtonType.YES, 
                    ButtonType.CANCEL);             
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {

                orderItemList.remove(itemToRemove);

                
                alert = new Alert(AlertType.INFORMATION);
                String alertText = "the item has been successfully removed from your trolley";
                alert.setContentText(alertText);
                alert.showAndWait();
                
                orderShowAllOrderItems();
                orderShowTotal();
                orderClear();

            }
            else {
                alert = new Alert(AlertType.INFORMATION);
                alert.setContentText("Cancelled");
                alert.showAndWait();
            }
        }
        
    }
    
    private double total = 0;
    public void orderCalculateTotal() {
    	total = orderItemList.stream()
    						.mapToDouble(i -> i.getSubtotal())
    						.sum();
    }
    
    public void orderShowTotal() {
    	orderCalculateTotal();
    	orderTotal.setText("Total: £"+String.valueOf(total));

    }
    
    private int orderNumber;
    
    public void orderPay() {
    	orderShowAllProducts();
    	Alert alert;
    	if (total>0) {
    		Map<Integer, Product> prodMap = allProdList.stream()
    											.collect(Collectors.toMap(Product::getId, p -> p));

    		for (Iterator<OrderItem> it = orderItemList.iterator(); it.hasNext();) {
    			OrderItem i = it.next();
    			Product inTrolleyProd = prodMap.get(i.getProduct_number());
    			if (inTrolleyProd != null) {
        		    if (i.getQty() > inTrolleyProd.getStock_level()) {
        		        it.remove();
        		        
        		        String alertText = "the requested quantity is not available, product#"+i.getProduct_id()
						+" has been removed from your trolley";
        		        alert = new Alert(AlertType.ERROR);
        		        alert.setContentText(alertText);
                        alert.showAndWait();
                        
            			orderShowAllOrderItems();
                        orderShowTotal();
                        
        		    }
    			}
    		}
    		
            alert = new Alert(AlertType.CONFIRMATION,
            		"would you like to proceed with the payment?",
            		ButtonType.YES, 
            		ButtonType.CANCEL);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
            	con = DBConnection.getDBConnection();
            	
            	try {

                    prep = con.prepareStatement("insert into orders (order_total, order_status) values (?, ?)", 
                    		Statement.RETURN_GENERATED_KEYS);
                    
                    prep.setDouble(1, total);
                    prep.setString(2, "OrderStatus.Waiting");

                    prep.executeUpdate();
                    ResultSet rs = prep.getGeneratedKeys();
                    while (rs.next()) {
                        orderNumber = rs.getInt(1);
                        
                    }

                    
            		con.setAutoCommit(false);

        			prep = con.prepareStatement("insert into order_items (product_number, order_number, qty, subtotal) values (?, ?, ?, ?)");
                    
                    for (OrderItem i: orderItemList) {
                        prep.setInt(1, i.getProduct_number());
                        prep.setInt(2, orderNumber);
                        prep.setInt(3, i.getQty());
                        prep.setDouble(4, i.getSubtotal());
                        prep.addBatch();
                    }
                    prep.executeBatch();
            		
            		
            		prep = con.prepareStatement("update products set stock_level = ? where id = ?");
            		for (OrderItem i: orderItemList) {
            			Product inTrolleyProd = prodMap.get(i.getProduct_number());
            			prep.setInt(1, inTrolleyProd.getStock_level()-i.getQty());
            			prep.setInt(2, i.getProduct_number());
            			prep.addBatch();
            		}
            		prep.executeBatch();
            		con.commit();
            		


            	}
                catch (Exception e) {
                    e.printStackTrace();
                }
            	
                alert = new Alert(AlertType.INFORMATION);
                String alertText = "your order#"+orderNumber+" has been placed, thank you for your order";
                alert.setContentText(alertText);
                alert.showAndWait();

                orderItemList.clear();
                orderShowAllProducts();
                Main.dashboardC.inventoryShowAllProducts();
                Main.warehousePickerC.warehouseShowAllOrders();
                Main.dashboardC.dashboardSetAllLabel();

                

            }
            else {
                alert = new Alert(AlertType.INFORMATION);
                alert.setContentText("cancelled");
                alert.showAndWait();
            }
    		
            
			orderShowAllOrderItems();
            orderShowTotal();
    	}
    	else {
    		alert = new Alert(AlertType.ERROR);
            alert.setContentText("your trolley is currently empty");
            alert.showAndWait();
    		
    	}
    	
    	
    }
    
    public void collectGetOrderNumber() {
        con = DBConnection.getDBConnection();
        String selectSql = "select id from orders where order_status = 'OrderStatus.WarehousePicked'";
        try {
            prep = con.prepareStatement(selectSql);
            result = prep.executeQuery();
            ObservableList<Integer> orderNumberList = FXCollections.observableArrayList();
            
            while (result.next()) {
            	orderNumberList.add(result.getInt("id"));
            }
            collectOrderNumber.setItems(orderNumberList);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    public void collectPickUp() {
    	
    	if (collectOrderNumber.getSelectionModel().getSelectedItem() != null) {
    		
    		int orderId = collectOrderNumber.getSelectionModel().getSelectedItem();
            String updateSql = "UPDATE orders SET order_status = 'OrderStatus.Completed' "
                	+"where id = '"+orderId
                	+"'";
            con = DBConnection.getDBConnection();
            try {
    			Alert alert;
                statement = con.createStatement();
                statement.executeUpdate(updateSql);
                
                alert = new Alert(AlertType.INFORMATION);
                alert.setContentText("we hope you enjoy your new items");
                alert.showAndWait();
                
                collectGetOrderNumber();
                Main.dashboardC.dashboardSetAllLabel();
                
            }
        	catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    	
    }
	
    public void switchForm(ActionEvent event) {
        if (event.getSource() == shopBtn) {
            orderForm.setVisible(true);
            orderImage.setVisible(true);
            collectForm.setVisible(false);
            collectImage.setVisible(false);
            orderShowAllProducts();
            orderClear();

        }
        else if (event.getSource() == collectBtn) {
            orderForm.setVisible(false);
            orderImage.setVisible(false);
            collectForm.setVisible(true);
            collectImage.setVisible(true);
            collectGetOrderNumber();
        }
        
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		Main.customerC = this;
		orderShowAllProducts();

		
	}



}
