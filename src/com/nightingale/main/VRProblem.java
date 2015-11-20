package com.nightingale.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
public class VRProblem {
	public String id;
	public Customer depot;
	private int length = 0;
	ArrayList<Customer> customers;
    ArrayList<Double> sortedDistances;
    ArrayList<SavingsNode> savingsNodes;


	public VRProblem(String filename) throws Exception{
		this.id = filename;
		BufferedReader br = new BufferedReader(new FileReader(filename));
		//Details of the depot and the truck capacity are stored in the first line
		String s = br.readLine();
		String dpt [] = s.split(",");
		depot = new Customer(
				Integer.parseInt(dpt[0]),
				Integer.parseInt(dpt[1]),
				Integer.parseInt(dpt[2]));
		customers = new ArrayList<Customer>();
        sortedDistances = new ArrayList<Double>();
        savingsNodes = new ArrayList<SavingsNode>();
		//Every customer is stored on a comma separated line
		while ((s=br.readLine())!=null){
			String wrd [] = s.split(",");
			customers.add(new Customer(
					Integer.parseInt(wrd[0]),
					Integer.parseInt(wrd[1]),
					Integer.parseInt(wrd[2])));
		}
		br.close();
		
		createSavings();
	}
	
	public int size(){
		return this.customers.size();				
	}
	
	private void createSavings() {
		for (int i=0; i<customers.size(); i++) {
			for (int j=0; j<customers.size(); j++) {
				if (i != j) {
					// calculate the pair saving and add it to the node list
					double saving = (depot.distance(customers.get(i)) + depot.distance(customers.get(j)) - customers.get(i).distance(customers.get(j)));
					savingsNodes.add(new SavingsNode(customers.get(i), customers.get(j), saving));
				}
			}
		}
		length = savingsNodes.size();
		
		// Sort the savings using quicksort
		sort(savingsNodes);

	}
	
	
	public void sort(ArrayList<SavingsNode> inputArr) {
        
        if (inputArr == null || inputArr.size() == 0) {
            return;
        }
        //savingsNodes = inputArr;
        length = inputArr.size();
        quickSort(0, length - 1);
    }
 
    private void quickSort(int lowerIndex, int higherIndex) {
         
        int i = lowerIndex;
        int j = higherIndex;
        // calculate pivot number, I am taking pivot as middle index number
        SavingsNode pivot = savingsNodes.get(lowerIndex+(higherIndex-lowerIndex)/2);
        // Divide into two arrays
        while (i <= j) {
            /**
             * In each iteration, we will identify a number from left side which
             * is greater then the pivot value, and also we will identify a number
             * from right side which is less then the pivot value. Once the search
             * is done, then we exchange both numbers.
             */
            while (savingsNodes.get(i).getSavings() > pivot.getSavings()) {
                i++;
            }
            while (savingsNodes.get(j).getSavings() < pivot.getSavings()) {
                j--;
            }
            if (i <= j) {
                exchangeNumbers(i, j);
                //move index to next position on both sides
                i++;
                j--;
            }
        }
        // call quickSort() method recursively
        if (lowerIndex < j)
            quickSort(lowerIndex, j);
        if (i < higherIndex)
            quickSort(i, higherIndex);
    }
 
    private void exchangeNumbers(int i, int j) {
        SavingsNode temp = savingsNodes.get(i);
        savingsNodes.set(i, savingsNodes.get(j));
        savingsNodes.set(j, temp);
    }

}
