package com.nightingale.main;

import java.awt.geom.Point2D;
import java.security.KeyPair;
import java.util.*;
import java.io.*;
public class VRProblem {
	public String id;
	public Customer depot;
	ArrayList<Customer> customers;
	HashMap<HashMap<Integer, Integer>, Double> pairsMap;
    ArrayList<Double> sortedDistances;


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
		pairsMap = new HashMap<HashMap<Integer, Integer>, Double>();
        sortedDistances = new ArrayList<Double>();
		//Every customer is stored on a comma separated line
		while ((s=br.readLine())!=null){
			String wrd [] = s.split(",");
			customers.add(new Customer(
					Integer.parseInt(wrd[0]),
					Integer.parseInt(wrd[1]),
					Integer.parseInt(wrd[2])));
		}
		br.close();
	}
	public int size(){
		return this.customers.size();				
	}

	public HashMap<HashMap<Integer, Integer>, Double> makePairs() {
        // make sure the map is empty before making the pairs
        pairsMap.clear();

        // calculate the cost between pairs of customers
        for (int i=0; i<customers.size()-1; i++) {
            for (int j=i+1; j<customers.size(); j++) {
                HashMap<Integer, Integer> coordMap = new HashMap<Integer, Integer>();
                coordMap.put(i, j);

                Double distance = customers.get(i).distance(customers.get(j));

                pairsMap.put(coordMap, distance);
            }
        }

        // update the distances array
        sortedDistances = new ArrayList<Double>(pairsMap.values());
        System.out.println(sortedDistances.size());
        // sort the distances in descending order
        Comparator<Double> comparator = Collections.reverseOrder();
        Collections.sort(sortedDistances, comparator);

        return pairsMap;
	}

}
