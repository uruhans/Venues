package com.rud.uffe.venues;

/*
* A Venue contains : Name, Address and distance from current location
* This class implements Comparable, in order to sort the Venues by distance
*
*/

public class Venue implements Comparable<Venue>{
	private String name;
	private String address;
	private int distance;

	public Venue() {
		this.name = "";
		this.address = "";
		this.setDistance(-1);
	}

	public String getAddress() {
		if (address.length() > 0) {
			return address;
		}
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	@Override
	public int compareTo(Venue another) {
		return (another.getDistance() < this.getDistance() ? 11 : -1);
	}
}
