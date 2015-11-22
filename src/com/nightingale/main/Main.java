package com.nightingale.main;

import java.io.PrintStream;
import java.util.HashMap;

public class Main {
	
	static HashMap<String, Double[]> runTimes;

    public static void main(String[] args) throws Exception {

        // create an array to hold the run times
        runTimes = new HashMap<String, Double[]>();
        
        for (int i=0; i<Integer.parseInt(args[2]); i++) {
	        // Load Problem
	        VRProblem problem = new VRProblem(args[0]);
	
	        // Create blank solution
	        VRSolution solution = new VRSolution(problem);
	        
	        double startTime = System.currentTimeMillis();
	        double startTimeNano = System.nanoTime();
	        
	        // Use the existing problem solver to build a solution
	        solution.clarkWrightSolution();
	
	        double endTime = System.currentTimeMillis();
	        double endTimeNano = System.nanoTime();
	        System.out.println("The time taken was " + (endTime - startTime));
	        
	        Double[] values = {(endTime - startTime), (endTimeNano - startTimeNano)/1000, solution.solnCost()};
	        
	        runTimes.put("Run "+ i, values);
	
	        // Print out the cost of the solution
	        System.out.println("Cost = " + solution.solnCost());
	
	        // create the csv and svg with the first run
	        if (i == 0) {
		        // Save the solution file for verification
		        solution.writeOut(args[1]);
		        //Create pictures of the problem and the solution
		        solution.writeSVG(args[0], "one_route.svg");
	        }
        }
        
        writeOut("report.csv");

    }
    
    public static void writeOut(String filename) throws Exception{
		PrintStream ps = new PrintStream(filename);
		ps.printf("%s,%s,%s,%s", "Run Number", "Time(ms)","Time(microseconds)","Cost");
		ps.print(",");
		ps.println();
		for(String run : runTimes.keySet()){
			ps.printf("%s,%.2f,%.2f,%.5f",run,Double.valueOf(runTimes.get(run)[0]),Double.valueOf(runTimes.get(run)[1]),Double.valueOf(runTimes.get(run)[2]));
			ps.print(",");
			ps.println();
		}
		ps.close();
	}
}
