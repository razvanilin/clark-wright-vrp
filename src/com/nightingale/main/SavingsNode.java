package com.nightingale.main;

public class SavingsNode {
	private Customer from;
	private Customer to;
	private double savings;
	
	public SavingsNode(Customer c1, Customer c2, double savings) {
		this.from = c1;
		this.to = c2;
		this.savings = savings;
	}
	
	public Customer getFrom() {
		return this.from;
	}
	
	public Customer getTo() {
		return this.to;
	}
	
	public double getSavings() {
		return this.savings;
	}
}
