package com.nghbui;

/*
        1   2   3   4
    A   18  52  64  39
    B   75  55  19  48
    C   35  57  8   65
    D   27  25  14  16
 */

// Import libraries
import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPVariable;


public class MIP {
    public static void main(String[] args) {
        Loader.loadNativeLibraries();
        //Declare variable
        String[] workers = {"A","B","C","D"};
        double[] costs = {18,52,64,39,75,55,19,48,35,57,8,65,27,25,14,16};

        int numWorkers = workers.length;
        int numTasks = costs.length/numWorkers;

        MPSolver solver = MPSolver.createSolver("GLOP");
        MPVariable[][] x = new MPVariable[numWorkers][numTasks];
        for (int i =0; i < numWorkers;i++) {
            for (int j = 0; j < numTasks; j++) {
                x[i][j] = solver.makeIntVar(0.0,1.0,"");
            }
        }

        //Create Constraint

        /*
            1   2   3   4
        A   18  52  64  39
        B   75  55  19  48
        C   35  57  8   65
        D   27  25  14  16
        */
        for (int i =0; i < numWorkers;i++) {
            MPConstraint c1 = solver.makeConstraint(1.0,1.0);// Each worker is assigned to exactly one task.
            MPConstraint c2 = solver.makeConstraint(1.0,1.0); // Each task is assigned to exactly one worker.
            for (int j = 0; j < numTasks; j++) {
                c1.setCoefficient(x[i][j],1);
                c2.setCoefficient(x[j][i],1);
            }
        }

        MPObjective objective = solver.objective();
        for (int i = 0; i < numWorkers; i++) {
            for (int j = 0; j < numTasks; j++) {
                objective.setCoefficient(x[i][j],costs[i*numWorkers+j]);
            }
        }
        objective.setMinimization();

        MPSolver.ResultStatus resultStatus=  solver.solve();

        if (resultStatus == MPSolver.ResultStatus.OPTIMAL || resultStatus == MPSolver.ResultStatus.FEASIBLE) {
            for (int i = 0; i < numWorkers; i++) {
                for (int j = 0; j < numTasks; j++) {
                    if (x[i][j].solutionValue() > 0.5) {
                        System.out.println("Worker "+workers[i]+" assigned to task "+ (j+1)+".    Cost= "+costs[i*numWorkers+j] );
                    }
                }
            }
            System.out.println("Min: "+objective.value());
        } else {
            System.out.println("No solution found.");
        }
    }

}
