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

	public void oneRouteForAllCustomersSolution() {
		this.soln = new ArrayList<List<Customer>>();
		ArrayList<Customer> route = new ArrayList<Customer>();
		for (Customer c:prob.customers){

			route.add(c);
		}

		soln.add(route);
	}

	public void clarkWrightSolution() {

		HashMap<HashMap<Integer, Integer>, Double> pairs = prob.makePairs();

//		for (Double d : prob.sortedDistances) {
//			System.out.println(d);
//			System.out.println(getKeyByValue(prob.pairsMap, d));
//		}
		
		pathFinder(prob.customers.get(0), 0);
		//System.out.println(prob.customers.get(0).x + " --- " + prob.depot.x);

	}
	
	
	ArrayList<Customer> recRoute = new ArrayList<Customer>();
	int capacity = 0;
	int recursion = 0;
	int gIndex = 0;
	
	private void pathFinder(Customer curPos, int index) {
		System.out.println(curPos.c);
		HashMap<Integer, Integer> curRoute = prototypeMethod(prob.pairsMap, gIndex, curPos);
		
		if (curRoute == null) {
			//recursion++;
			gIndex = 0;
			System.out.println("null");
			pathFinder(prob.customers.get(0), 0);
			return;
		} else {
			// get the current customer requirements
			int requirement = prob.customers.get((int)curRoute.values().toArray()[0]).c;
			
			// if the new customer's requirements exceeds the van capacity, search for another customer 
			if ((capacity + requirement) > prob.depot.c) {
				
				// if there are other customers to be checked, make a recursive call with the next index
				if ((gIndex + 1) != prob.sortedDistances.size()) {
					recursion++;
					gIndex++;
					System.out.println("capacity reached");
					pathFinder(curPos, gIndex);
					return;
				}
				
				System.out.println("route added");
				
				// otherwise add the route to the solution
				soln.add(recRoute);
				System.out.println(recRoute.toString());
				
				// reset the capacity and route
				capacity = 0;
				recRoute = new ArrayList<Customer>();
				gIndex=0;
				// recursive call to start creating another route
				
				pathFinder(prob.customers.get(0), 0);
				return;
			// else keep looking for possible customers
			} else {
				System.out.println("customer added");
				recRoute.add(prob.customers.get((int)curRoute.values().toArray()[0]));
				prob.sortedDistances.remove(gIndex);
				gIndex=0;
				capacity += requirement;
				System.out.println(capacity);
				// make a recursive call with the next customer
				pathFinder(curPos, 0);
				return;
			}
		}
	
	}
	
	private HashMap<Integer, Integer> prototypeMethod(HashMap<HashMap<Integer, Integer>, Double> map, int value, Customer curPos) {
		for (int i=value; i<prob.sortedDistances.size(); i++) {
			for (Entry<HashMap<Integer, Integer>, Double> entry : map.entrySet()) {
				if (Objects.equals(prob.sortedDistances.get(i), entry.getValue())) {
					if (Objects.equals(prob.customers.get((int)entry.getKey().keySet().toArray()[0]), curPos)) {
						gIndex = i;
						return entry.getKey();
					}
				}
			}
		}
		return null;
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
        PrintStream ppw = new PrintStream(new FileOutputStream(probFilename));
        PrintStream spw = new PrintStream(new FileOutputStream(solnFilename));
        ppw.append(psb);
        //spw.append(ssb);
    	ppw.close();
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
