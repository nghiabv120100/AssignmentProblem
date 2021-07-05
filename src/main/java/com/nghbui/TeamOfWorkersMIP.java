package com.nghbui;
// Import libraries
import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPVariable;

public class TeamOfWorkersMIP {
    public static void main(String[] args) {
        Loader.loadNativeLibraries();

        int cost[][] = {{90,76,75,70},
                        {35,85,55,65},
                        {125,95,90,105},
                        {45,110,95,115},
                        {60,105,80,75},
                        {45,65,110,95}};
        int team1[] ={0,2,4};
        int team2[] ={1,3,5};

        int numWorkers =cost.length;
        int numTasks = cost[0].length;
        MPSolver solver = MPSolver.createSolver("GLOP");

        MPVariable x[][] = new MPVariable[numWorkers][numTasks];

        for(int i =0 ; i< numWorkers;i++) {
            for (int j = 0; j<numTasks;j++) {
                x[i][j]= solver.makeIntVar(0.0,1.0,"");
            }
        }

        // Declare Constraint
        //Each worker is assigned to at most one task
        for(int i =0 ; i< numWorkers;i++) {
            MPConstraint c = solver.makeConstraint(0.0,1.0,"");
            for (int j = 0; j<numTasks;j++) {
                c.setCoefficient(x[i][j],1);
            }
        }
        //Each task is assigned to exactly one worker
        for(int j=0;j<numTasks;j++) {
            MPConstraint c = solver.makeConstraint(1.0,1.0,"");
            for (int i=0;i<numWorkers;i++) {
                c.setCoefficient(x[i][j],1);
            }
        }
        //Each team can perform at most two task
        MPConstraint c = solver.makeConstraint(0.0,2.0,"");
        for (int i=0;i<team1.length;i++) {

            for (int j=0;j<numTasks;j++) {
                c.setCoefficient(x[team1[i]][j],1);
            }
        }
        c = solver.makeConstraint(0.0,2.0,"");
        for (int i=0;i < team2.length;i++) {
            for (int j=0;j<numTasks;j++) {
                c.setCoefficient(x[team2[i]][j],1);
            }
        }
        //Objective
        MPObjective objective =solver.objective();
        for (int i = 0 ;i <numWorkers;i++) {
            for (int j=0;j<numTasks;j++) {
                objective.setCoefficient(x[i][j],cost[i][j]);
            }
        }
        objective.setMinimization();

        MPSolver.ResultStatus resultStatus=  solver.solve();

        if (resultStatus == MPSolver.ResultStatus.OPTIMAL || resultStatus == MPSolver.ResultStatus.FEASIBLE) {
            for (int i = 0; i < numWorkers; i++) {
                for (int j = 0; j < numTasks; j++) {
                    if (x[i][j].solutionValue() > 0.5) {
                        System.out.println("Worker "+i+" assigned to task "+ j+".    Cost= "+cost[i][j] );
                    }
                }
            }
            System.out.println("Min: "+objective.value());
        } else {
            System.out.println("No solution found.");
        }



    }




}
