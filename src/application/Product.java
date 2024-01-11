package application;

public class Product {
	private Integer id;
	private String product_id;
	private String product_name;
	private Double unit_price; 
	private String bin;
	private Integer stock_level;

	public Product(Integer id, String product_id, String product_name, Double unit_price, String bin, Integer stock_level) {
		super();
		this.id = id;
		this.product_id = product_id;
		this.product_name = product_name;
		this.unit_price = unit_price;
		this.bin = bin;
		this.stock_level = stock_level;
	}
	
	public Integer getId() {
		return id;
	}

	public String getProduct_id() {
		return product_id;
	}

	public String getProduct_name() {
		return product_name;
	}

	public Double getUnit_price() {
		return unit_price;
	}

	public String getBin() {
		return bin;
	}
	
	public Integer getStock_level() {
		return stock_level;
	}

}
