package com.nightingale.main;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

public class VRSolution {
	public VRProblem prob;
	public List<List<Customer>>soln;
	public VRSolution(VRProblem problem){
		this.prob = problem;
	}

	//The dumb solver adds one route per customer
	public void oneRoutePerCustomerSolution(){
		this.soln = new ArrayList<List<Customer>>();
		for(Customer c:prob.customers){
			ArrayList<Customer> route = new ArrayList<Customer>();
			route.add(c);
			soln.add(route);
		}
	}
	
	
	ArrayList<Customer> recRoute = new ArrayList<Customer>();
	ArrayList<Double> ignoredRoutes = new ArrayList<Double>();
	ArrayList<Customer> visitedCustomers = new ArrayList<Customer>();
	int capacity = 0;
	int recursion = 0;
	int gIndex = 0;

	public void clarkWrightSolution() {
		soln = new ArrayList<List<Customer>>();
		HashMap<HashMap<Customer, Customer>, Double> pairs = prob.makePairs();

//		for (Double d : prob.sortedDistances) {
//			System.out.println(d);
//			System.out.println(getKeyByValue(prob.pairsMap, d));
//		}
		
		// get the customer from where we should begin the route
		double firstDistance = prob.sortedDistances.get(0);
		Customer firstCustomer = (Customer) getKeyByValue(pairs, firstDistance).keySet().toArray()[0];
		// update the capacity with the first customer's requirements and add the customer to the route
		//recRoute.add(firstCustomer);
		System.out.println(getKeyByValue(pairs, prob.sortedDistances.get(0)));
		pathFinder(firstCustomer);
		//System.out.println(prob.customers.get(0).x + " --- " + prob.depot.x);
		for (HashMap<Customer, Customer> pair : prob.pairsMap.keySet()) {
			System.out.println(pair.keySet().toArray()[0].toString() + " <---> " + pair.values().toArray()[0].toString());
		}
		
		System.out.println();

	}
	
	// recursive method that populates the solution
	private void pathFinder(Customer curPos) {
		// add the customer requirement to the capacity
		capacity += curPos.c;
		HashMap<Customer, Customer> curPair = getPairTwo(prob.pairsMap, curPos);
		
		Customer nextCustomer = new Customer(0,0,0);
		
		// if the pair is null, it means there are no other possible connections that can be made for the route solution
		if (curPair == null || curPair.size() < 1) {
			System.out.println("adding route to solution - null");
			// add the current route to the solution
			soln.add(recRoute);
			// reset the global route list, capacity and the ignored distances list
			recRoute = new ArrayList<Customer>();
			capacity = 0;
			ignoredRoutes.clear();
			
			// check to see if there are any customers left to be visited and start a new route
			if (visitedCustomers.size() < prob.customers.size()) {
				// make a recursive call with a customer that needs to be visited
				// also make sure the he's part of the longest distance pair available
				for (double d : prob.sortedDistances) {
					Customer fromCustomer = (Customer)getKeyByValue(prob.pairsMap, d).keySet().toArray()[0];
					Customer toCustomer = (Customer)getKeyByValue(prob.pairsMap, d).values().toArray()[0];
					// check to see if the customers between which the van is traveling were visited or not
					if (!isCustomerVisited(fromCustomer) && !isCustomerVisited(toCustomer)) {
						pathFinder(fromCustomer);
						return;
					}
				}
			} else {
				System.out.println("All customers were visited");
				return;
			}
		} else {
			// get the next customer and the new requirement
			//System.out.println(curPair);
			nextCustomer = (Customer) curPair.values().toArray()[0];
		}
		
		if ((capacity + nextCustomer.c) > prob.depot.c) {
			System.out.println("capacity is reached with the next customer");
			// if the capacity will go over maximum with the new customer, add the distance in the ignored list
			ignoredRoutes.add(curPos.distance(nextCustomer));
			System.out.println(ignoredRoutes.size() + " / " + prob.sortedDistances.size());
			// and make a recursive call to find other possibilities
			pathFinder(curPos);
			return;
		} else {		
			System.out.println("adding customer to the route. Capacity = " + capacity);
			// add the customer to the route
			recRoute.add(curPos);
			// mark customer as visited
			visitedCustomers.add(curPos);
			// delete the distance from the sorted array list
			prob.sortedDistances.remove(gIndex);
			// clear the ignored routes list
			ignoredRoutes.clear();
			// recursive call with the next customer
			pathFinder(nextCustomer);
			return;
		}
	}
	
	private HashMap<Customer, Customer> getPair(HashMap<HashMap<Customer, Customer>, Double> map, Customer curPos) {
		for (int i=0; i<prob.sortedDistances.size(); i++) {
			// check if the route is ignored so we don't go into an infinite loop of recursive calls
			if (!isRouteIgnored(prob.sortedDistances.get(i))) {
				// go through the entry sets of the pairs map
				for (Entry<HashMap<Customer, Customer>, Double> entry : map.entrySet()) {
					// check if the distance and the customers match
					if (Objects.equals(prob.sortedDistances.get(i), entry.getValue())) {
						// check to see if the initial customer matches the hashmap's key
						if (Objects.equals(entry.getKey().keySet().toArray()[0], curPos)) {
							// check if the next customer was visited
							if(!isCustomerVisited(entry.getKey().values().toArray()[0])) {
								// save the distance index for future references
								gIndex = i;
								System.out.println(entry.getKey());
								return entry.getKey();
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	private HashMap<Customer, Customer> getPairTwo(HashMap<HashMap<Customer, Customer>, Double> map, Customer curPos) {
		for (int i=0; i<prob.sortedDistances.size(); i++) {
			if (!isRouteIgnored(prob.sortedDistances.get(i))) {
				HashMap<Customer, Customer> customersPair = getKeyByValue(map, prob.sortedDistances.get(i));
				if (customersPair.keySet().toArray()[0] == curPos) {

					if (!isCustomerVisited(customersPair.values().toArray()[0])) {
						//System.out.println("yoyo");
						gIndex = i;
						return customersPair;
					}
				}
			}
		}		
		return null;
	}
	
	// helper method that returns true if the parameter is found in the ignored list (that gets populated during the recursive calls)
	private boolean isRouteIgnored(double distance) {
		for (int i=0; i<ignoredRoutes.size(); i++) {
			if (ignoredRoutes.get(i) == distance) {
				return true;
			}
		}
		
		return false;
	}
	// helper method to check if the customer was visited or not
	private boolean isCustomerVisited(Object object) {
		for (int i=0; i<visitedCustomers.size(); i++) {
			if (Objects.equals(visitedCustomers.get(i), object)) {
				return true;
			}
		}
		
		return false;
	}
	
	private static <T, E> T getKeyByValue(Map<T, E> map, E value) {
	    for (Entry<T, E> entry : map.entrySet()) {
	        if (Objects.equals(value, entry.getValue())) {
	            return entry.getKey();
	        }
	    }
	    return null;
	}
	
	//Students should implement another solution
	
	//Calculate the total journey
	public double solnCost(){
		double cost = 0;
		for(List<Customer>route:soln){
			Customer prev = this.prob.depot;
			for (Customer c:route){
				cost += prev.distance(c);
				prev = c;
			}
			//Add the cost of returning to the depot
			cost += prev.distance(this.prob.depot);
		}
		return cost;
	}
	public Boolean verify(){
		//Check that no route exceeds capacity
		Boolean okSoFar = true;
		for(List<Customer> route : soln){
			//Start the spare capacity at
			int total = 0;
			for(Customer c:route)
				total += c.c;
			if (total>prob.depot.c){
				System.out.printf("********FAIL Route starting %s is over capacity %d\n",
						route.get(0),
						total
						);
				okSoFar = false;
			}
		}
		//Check that we keep the customer satisfied
		//Check that every customer is visited and the correct amount is picked up
		Map<String,Integer> reqd = new HashMap<String,Integer>();
		for(Customer c:this.prob.customers){
			String address = String.format("%fx%f", c.x,c.y);
			reqd.put(address, c.c);
		}
		for(List<Customer> route:this.soln){
			for(Customer c:route){
				String address = String.format("%fx%f", c.x,c.y);
				if (reqd.containsKey(address))
					reqd.put(address, reqd.get(address)-c.c);
				else
					System.out.printf("********FAIL no customer at %s\n",address);
			}
		}
		for(String address:reqd.keySet())
			if (reqd.get(address)!=0){
				System.out.printf("********FAIL Customer at %s has %d left over\n",address,reqd.get(address));
				okSoFar = false;
			}
		return okSoFar;
	}
	
	public void readIn(String filename) throws Exception{
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String s;
		this.soln = new ArrayList<List<Customer>>();
		while((s=br.readLine())!=null){
			ArrayList<Customer> route = new ArrayList<Customer>();
			String [] xycTriple = s.split(",");
			for(int i=0;i<xycTriple.length;i+=3)
				route.add(new Customer(
						(int)Double.parseDouble(xycTriple[i]),
						(int)Double.parseDouble(xycTriple[i+1]),
						(int)Double.parseDouble(xycTriple[i+2])));
			soln.add(route);
		}
		br.close();
	}
	
	public void writeSVG(String probFilename,String solnFilename) throws Exception{
		String[] colors = "chocolate cornflowerblue crimson cyan darkblue darkcyan darkgoldenrod".split(" ");
		int colIndex = 0;
		String hdr = 
				"<?xml version='1.0'?>\n"+
				"<!DOCTYPE svg PUBLIC '-//W3C//DTD SVG 1.1//EN' '../../svg11-flat.dtd'>\n"+
				"<svg width='8cm' height='8cm' viewBox='0 0 500 500' xmlns='http://www.w3.org/2000/svg' version='1.1'>\n";
		String ftr = "</svg>";
        StringBuffer psb = new StringBuffer();
        StringBuffer ssb = new StringBuffer();
        psb.append(hdr);
        ssb.append(hdr);
        for(List<Customer> route:this.soln){
        	ssb.append(String.format("<path d='M%s %s ",this.prob.depot.x,this.prob.depot.y));
        	for(Customer c:route)
        		ssb.append(String.format("L%s %s",c.x,c.y));
        	ssb.append(String.format("z' stroke='%s' fill='none' stroke-width='2'/>\n",
        			colors[colIndex++ % colors.length]));
        }
        for(Customer c:this.prob.customers){
        	String disk = String.format(
        			"<g transform='translate(%.0f,%.0f)'>"+
        	    	"<circle cx='0' cy='0' r='%d' fill='pink' stroke='black' stroke-width='1'/>" +
        	    	"<text text-anchor='middle' y='5'>%d</text>"+
        	    	"</g>\n", 
        			c.x,c.y,10,c.c);
        	psb.append(disk);
        	ssb.append(disk);
        }
        String disk = String.format("<g transform='translate(%.0f,%.0f)'>"+
    			"<circle cx='0' cy='0' r='%d' fill='pink' stroke='black' stroke-width='1'/>" +
    			"<text text-anchor='middle' y='5'>%s</text>"+
    			"</g>\n", this.prob.depot.x,this.prob.depot.y,20,"D");
    	psb.append(disk);
    	ssb.append(disk);
        psb.append(ftr);
        ssb.append(ftr);
        //PrintStream ppw = new PrintStream(new FileOutputStream(probFilename));
        PrintStream spw = new PrintStream(new FileOutputStream(solnFilename));
        //ppw.append(psb);
        spw.append(ssb);
    	//ppw.close();
    	spw.close();
	}
	public void writeOut(String filename) throws Exception{
		PrintStream ps = new PrintStream(filename);
		for(List<Customer> route:this.soln){
			boolean firstOne = true;
			for(Customer c:route){
				if (!firstOne)
					ps.print(",");
				firstOne = false;
				ps.printf("%f,%f,%d",c.x,c.y,c.c);
			}
			ps.println();
		}
		ps.close();
	}
}
