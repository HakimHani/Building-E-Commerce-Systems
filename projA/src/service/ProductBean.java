package service;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ProductBean {
	private String name;
	private double price;
	private String id;

	public ProductBean() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double d) {
		this.price = d;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
