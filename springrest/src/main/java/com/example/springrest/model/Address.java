package com.example.springrest.model;


public class Address {
	
	public Address(String street, String suite, String city, String zipcode, Geo geo) {
		super();
		this.street = street;
		this.suite = suite;
		this.city = city;
		this.zipcode = zipcode;
		this.geo = geo;
	}
	public Address() {};
	String street = "default street";
	String suite = "default suite";
	String city = "default city";
	String zipcode = "default zipcode";
	Geo geo;
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getSuite() {
		return suite;
	}
	public void setSuite(String suite) {
		this.suite = suite;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	
	public Geo getGeo() {
		return this.geo;
	}

	public void setGeo(Geo geo) {
		this.geo = geo;
	}

}
