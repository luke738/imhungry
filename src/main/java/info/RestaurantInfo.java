package info;

public class RestaurantInfo extends Info {
	private String address;
	private int price;
	private int driveTime;
	private String phone;
	private String url;
	
	
	public RestaurantInfo(String name, int rating, String address, int price, int driveTime, String phone,
			String url) {
		this.name = name;
		this.rating = rating;
		this.address = address;
		this.price = price;
		this.driveTime = driveTime;
		this.phone = phone;
		this.url = url;
	}
	
	public String getAddress() { return address; }
	
	public int getPrice() { return price; }
	
	public int getDriveTime() { return driveTime; }
	
	public String getPhone() { return phone; }
	
	public String getURL() {return url; }
	
	public boolean equals(RestaurantInfo other) {
		return this.name == other.name && this.address == other.address;
	}
}