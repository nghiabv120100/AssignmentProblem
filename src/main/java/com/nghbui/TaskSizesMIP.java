package com.nghbui;

// Import libraries
import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.Constraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPVariable;

public class TaskSizesMIP {
    public static void main(String[] args) {
        Loader.loadNativeLibraries();
        int[][] cost = {{90, 76, 75, 70, 50, 74, 12, 68},
                      {35, 85, 55, 65, 48, 101, 70, 83},
                      {125, 95, 90, 105, 59, 120, 36, 73},
                      {45, 110, 95, 115, 104, 83, 37, 71},
                      {60, 105, 80, 75, 59, 62, 93, 88},
                      {45, 65, 110, 95, 47, 31, 81, 34},
                      {38, 51, 107, 41, 69, 99, 115, 48},
                      {47, 85, 57, 71, 92, 77, 109, 36},
                      {39, 63, 97, 49, 118, 56, 92, 61},
                      {47, 101, 71, 60, 88, 109, 52, 90}};

        int[] sizes = {10, 7, 3, 12, 15, 4, 11, 5};
        int totalSizeMax = 15;
        int numWorkers = cost.length;
        int numTasks = cost[0].length;

        MPSolver solver = MPSolver.createSolver("SCIP"); //SCIP and GLOP
        MPVariable[][] x = new MPVariable[numWorkers][numTasks];
        for (int i=0; i<numWorkers;i++) {
            for (int j=0;j<numTasks;j++) {
                x[i][j]= solver.makeIntVar(0,1,"");
            }
        }

        //Declare constraint
        //Each task is assigned to at exactly one worker
        for (int j=0;j<numTasks;j++) {
            MPConstraint c = solver.makeConstraint(1.0,1.0,"");
            for (int i=0;i<numWorkers;i++) {
                c.setCoefficient(x[i][j],1);
            }
        }
        //One worker can perform at most total size
        for (int i=0;i<numWorkers;i++) {
            MPConstraint c = solver.makeConstraint(0.0,totalSizeMax,"");
            for (int j=0;j<numTasks;j++) {
                c.setCoefficient(x[i][j],sizes[j]);
            }
        }

        //Objective
        MPObjective objective = solver.objective();
        for (int i = 0;i<numWorkers;i++) {
            for (int j=0; j<numTasks;j++) {
                objective.setCoefficient(x[i][j],cost[i][j]);
            }
        }
        objective.setMinimization();

        MPSolver.ResultStatus resultStatus=  solver.solve();


        if (resultStatus == MPSolver.ResultStatus.OPTIMAL || resultStatus == MPSolver.ResultStatus.FEASIBLE) {
            for (int i = 0; i < numWorkers; i++) {
                for (int j = 0; j < numTasks; j++) {
                    if (x[i][j].solutionValue() >0.5) {
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
