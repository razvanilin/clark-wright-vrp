package com.nightingale.main;

import java.io.PrintStream;
import java.util.ArrayList;

public class VRSolution {
	public VRProblem prob;
	public ArrayList<Route> soln = new ArrayList<Route>();
	public VRSolution(VRProblem problem){
		this.prob = problem;
	}

	//The dumb solver adds one route per customer
	/*public void oneRoutePerCustomerSolution(){
		this.soln = new ArrayList<List<Customer>>();
		for(Customer c:prob.customers){
			ArrayList<Customer> route = new ArrayList<Customer>();
			route.add(c);
			soln.add(route);
		}
	}*/

	public void clarkWrightSolution() {
		// go through all the nodes
		for (SavingsNode node : prob.savingsNodes) {
			// neither customers are in the routes
			if (!isInRoutes(node.getFrom()) && !isInRoutes(node.getTo())) {
				// make sure the requirements of the two customers don't go over the van's capacity
				if ((node.getFrom().c + node.getTo().c) <= prob.depot.c) {
					
					// create a new route and add it to the solution
					Route route = new Route();
					route.add(node.getFrom());
					route.add(node.getTo());
					
					if (!soln.contains(route)) {
						soln.add(route);
					}
					
				}
			// else find a route that ends at 'from'	
			} else if (!isInRoutes(node.getTo())) {
				// go through all the routes in the solution
				for (Route route : soln) {
					// if the last customer is the 'from' part of the node
					if (route.getLastCustomer() == node.getFrom()) {
						// make sure to check the new requirement
						if (route.hasCapacity(node.getTo().c, prob.depot.c)) {
							route.add(0, node.getTo());
							break;
						}
					}
				}
			// else find a route that ends at 'to'
			} else if (!isInRoutes(node.getFrom())) {
				for(Route route : soln) {
					// if the last customer is the 'to' part of the node
					if (route.getLastCustomer() == node.getTo()) {
						// make sure to check the new requirement
						if (route.hasCapacity(node.getFrom().c, prob.depot.c)) {
							route.add(node.getFrom());
							break;
						}
					}
				}
			}
			
			// check for route merging possibilities
			Route merged = null;
			for (Route routeX : soln) {
				// if a route has been marked for merge, exit the loop
				if (merged!=null) break;
				
				// if the last customer in the route coincides with the 'from' from the current node, then check to see if the 'to' node can be found in other routes
				if (routeX.getLastCustomer() == node.getFrom()) {
					for (Route routeY : soln) {
						if (routeY.getFirstCustomer() == node.getTo()) {
							if (routeX != routeY) {
								if ((routeX.getCurrentCapacity() + routeY.getCurrentCapacity()) <= prob.depot.c) {
									routeX.addAll(routeY);
									merged = routeY;
									break;
								}
							}
						}
					}
				}
			}
			
			if (merged != null) {
				soln.remove(merged);
			}
		}
		
	}
	
	private boolean isInRoutes(Customer customer) {
		for (Route route : soln) {
			for(Customer c : route.getList()) {
				if (customer == c) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	
	//Students should implement another solution
	
	//Calculate the total journey
	/*public double solnCost(){
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
	}*/
	public void writeOut(String filename) throws Exception{
		PrintStream ps = new PrintStream(filename);
		for(Route route:this.soln){
			boolean firstOne = true;
			for(Customer c:route.getList()){
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
