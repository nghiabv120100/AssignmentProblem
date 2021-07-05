package com.nghbui;
import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearExpr;

public class TeamOfWorkersSAT {
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

        IntVar[][] x = new IntVar[numWorkers][numTasks];
        IntVar[] xFlat = new IntVar[numWorkers*numTasks];
        int[] costFlat = new int[numTasks*numWorkers];

        CpModel model = new CpModel();
        for (int i=0; i< numWorkers; i++) {
            for (int j=0;j<numTasks;j++) {
                int k=i*numTasks+j;
                x[i][j] = model.newIntVar(0,1,"");
                xFlat[k] = x[i][j];
                costFlat[k]=cost[i][j];
            }
        }

        //Declare Constraint
        //Each worker is assigned to at most one task
        for (int i=0;i<numWorkers;i++) {
            IntVar[] vars = new IntVar[numTasks];
            for (int j=0;j<numTasks;j++) {
                vars[j] = x[i][j];
            }
            model.addLessOrEqual(LinearExpr.sum(vars),1);
        }
        //Each task is assigned to exactly one worker
        for (int j =0; j<numTasks;j++) {
            IntVar[] vars = new IntVar[numWorkers];
            for (int i=0;i<numWorkers;i++) {
                vars[i] = x[i][j];
            }
            model.addEquality(LinearExpr.sum(vars),1);
        }
        //Each team can perform at most two task
        //Team 1
        IntVar[] expressionTeam1 = new IntVar[numTasks*team1.length];
        int index = 0;
        for (int i=0;i<team1.length;i++) {
            for (int j =0 ;j< numTasks;j++) {
                expressionTeam1[index] = x[team1[i]][j];
                index++;
            }
        }
        model.addLessOrEqual(LinearExpr.sum(expressionTeam1),2);
        //Team 2
        IntVar[] expressionTeam2 = new IntVar[numTasks*team2.length];
        index = 0;
        for (int i=0;i<team2.length;i++) {
            for (int j =0 ;j< numTasks;j++) {
                expressionTeam2[index] = x[team2[i]][j];
                index++;
            }
        }
        model.addLessOrEqual(LinearExpr.sum(expressionTeam2),2);

        model.minimize(LinearExpr.scalProd(xFlat,costFlat));

        CpSolver solver = new CpSolver();

        CpSolverStatus status = solver.solve(model);

        // Print solution.
        // Check that the problem has a feasible solution.
        if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
            System.out.println("Total cost: " + solver.objectiveValue() + "\n");
            for (int i = 0; i < numWorkers; i++) {
                for (int j = 0; j< numTasks;j++)
                if (solver.value(x[i][j])==1) {
                    System.out.println("Worker " + i + " assigned to task " + i%numTasks + ".  Cost: " + cost[i][j]);
                }
            }
        } else {
            System.err.println("No solution found.");
        }
    }
}
