package com.nghbui;
import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearExpr;

public class SAT {
    public static void main(String[] args) {
        Loader.loadNativeLibraries();
        //Declare variable
        String[] workers = {"A","B","C","D"};
        int[] costs = {18,52,64,39,75,55,19,48,35,57,8,65,27,25,14,16};
        CpModel model = new CpModel();

        int numWorkers = workers.length;
        int numTasks = costs.length/numWorkers;
        int n = numTasks*numTasks;
        IntVar[] xFlat = new IntVar[n];
        for (int i=0;i<n;i++) {
            xFlat[i] = model.newIntVar(0,1,"");
        }

        for(int i=0; i<numWorkers;i++) {
            IntVar[] vars = new IntVar[numTasks];
            for (int j=0;j<numTasks;j++) {
                vars[j]= xFlat[i*numTasks+j];
            }
            model.addEquality(LinearExpr.sum(vars),1);
        }

        for(int j=0; j<numTasks;j++) {
            IntVar[] vars = new IntVar[numWorkers];
            for (int i =0;i<numWorkers;i++) {
                vars[i] = xFlat[i*numWorkers+j];
            }
            model.addEquality(LinearExpr.sum(vars),1);
        }

        model.minimize(LinearExpr.scalProd(xFlat,costs));

        CpSolver solver = new CpSolver();
        CpSolverStatus status = solver.solve(model);

        // Print solution.
        // Check that the problem has a feasible solution.
        if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
            System.out.println("Total cost: " + solver.objectiveValue() + "\n");
            for (int i = 0; i < n; i++) {
                if (solver.value(xFlat[i])==1) {
                    System.out.println("Worker " + i/numWorkers + " assigned to task " + i%numTasks + ".  Cost: " + costs[i]);
                }
            }
        } else {
            System.err.println("No solution found.");
        }


    }
}
