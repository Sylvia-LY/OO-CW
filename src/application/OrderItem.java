package application;

public class OrderItem {
	private Integer product_number;
	private Integer qty;
	private Double subtotal;
	private String product_id;
	private String product_name;
	private String bin;
	
	public OrderItem(Integer product_number, Integer qty, Double subtotal, String product_id, String product_name,
			String bin) {
		super();
		this.product_number = product_number;
		this.qty = qty;
		this.subtotal = subtotal;
		this.product_id = product_id;
		this.product_name = product_name;
		this.bin = bin;
	}

	public Integer getProduct_number() {
		return product_number;
	}

	public void setProduct_number(Integer product_number) {
		this.product_number = product_number;
	}

	public Integer getQty() {
		return qty;
	}

	public void setQty(Integer qty) {
		this.qty = qty;
	}

	public Double getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(Double subtotal) {
		this.subtotal = subtotal;
	}

	public String getProduct_id() {
		return product_id;
	}

	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public String getBin() {
		return bin;
	}

	public void setBin(String bin) {
		this.bin = bin;
	}
	
	
}
