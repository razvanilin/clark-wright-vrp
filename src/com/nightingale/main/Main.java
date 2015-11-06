package com.nightingale.main;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws Exception {
        // Load Problem
        VRProblem problem = new VRProblem("data/rand01000prob.csv");

        // Create blank solution
        VRSolution solution = new VRSolution(problem);

        double startTime = System.currentTimeMillis();

        // Use the existing problem solver to build a solution
        //solution.oneRouteForAllCustomersSolution();
        //solution.oneRoutePerCustomerSolution();
        solution.clarkWrightSolution();

        double endTime = System.currentTimeMillis();
        System.out.println("The time taken was " + (endTime - startTime));

        // Print out the cost of the solution
        //System.out.println("Cost = " + solution.solnCost());

        // Save the solution file for verification
        solution.writeOut("one_route.csv");
        //Create pictures of the problem and the solution
        solution.writeSVG("data/rand01000prob.csv", "one_route.svg");

    }
}
