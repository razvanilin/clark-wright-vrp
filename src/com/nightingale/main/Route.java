package com.nightingale.main;

import java.util.ArrayList;

public class Route {
	private ArrayList<Customer> route;
	private int capacity;
	
	
	public Route() {
		route = new ArrayList<Customer>();
		capacity = 0;
	}
	
	public void add(Customer customer) {
		route.add(customer);
		capacity += customer.c;
	}
	public void add(int index, Customer customer) {
		route.add(index, customer);
		capacity += customer.c;
	}
	
	public void addAll(Route route) {
		this.route.addAll(route.getList());
		// also update the capacity
		capacity += route.getCurrentCapacity();
	} 
	
	public int getCurrentCapacity() {
		return capacity;
	}
	
	public ArrayList<Customer> getList() {
		return route;
	}
	
	public Customer getLastCustomer() {
		return route.get(route.size()-1);
	}
	
	public Customer getFirstCustomer() {
		return route.get(0);
	}
	
	public boolean hasCapacity(int requirement, int maxCapacity) {
		if ((capacity + requirement) <= maxCapacity) {
			return true;
		}
		return false;
	}
}
