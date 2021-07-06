package com.nghbui;
import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import java.util.ArrayList;
import java.util.List;

public class AllowedGroupsMIP {
    public static void main(String[] args) {
        Loader.loadNativeLibraries();
        int[][] cost ={{90, 76, 75, 70, 50, 74},
                      {35, 85, 55, 65, 48, 101},
                      {125, 95, 90, 105, 59, 120},
                      {45, 110, 95, 115, 104, 83},
                      {60, 105, 80, 75, 59, 62},
                      {45, 65, 110, 95, 47, 31},
                      {38, 51, 107, 41, 69, 99},
                      {47, 85, 57, 71, 92, 77},
                      {39, 63, 97, 49, 118, 56},
                      {47, 101, 71, 60, 88, 109},
                      {17, 39, 103, 64, 61, 92},
                      {101, 45, 83, 59, 92, 27}};
        int numWorkers = cost.length;
        int numTasks = cost[0].length;

        List<Integer> allowedGroup;
        MPSolver minSolver =null;
        MPVariable[][] x;
        MPVariable[][] minX = new MPVariable[numWorkers][numTasks];
        double MIN = Double.POSITIVE_INFINITY;
        int numSubgroups = 12;
        int [][]group1 ={{2, 3},       // Subgroups of workers 0 - 3
                         {1, 3},
                         {1, 2},
                         {0, 1},
                         {0, 2}};

        int [][]group2 = {{6, 7},      // Subgroups of workers 4 - 7
                         {5, 7},
                         {5, 6},
                         {4, 5},
                         {4, 7}};

        int[][] group3 = {{10, 11},     // Subgroups of workers 8 - 11
                          {9, 11},
                          {9, 10},
                          {8, 10},
                          {8, 11}};


        for (int i=0;i< group1.length;i++) {
            for (int j=0;j<group2.length;j++) {
                for (int k=0;k<group3.length;k++) {
                    MPSolver solver= MPSolver.createSolver("GLOP");
                    allowedGroup = new ArrayList<Integer>();
                    allowedGroup.add(group1[i][0]);
                    allowedGroup.add(group1[i][1]);

                    allowedGroup.add(group2[j][0]);
                    allowedGroup.add(group2[j][1]);

                    allowedGroup.add(group3[k][0]);
                    allowedGroup.add(group3[k][1]);

                    x = new MPVariable[numWorkers][numTasks];

                    for (int row=0;row<numWorkers;row++) {
                        for (int col =0; col <numTasks;col++) {
                            x[row][col] = solver.makeIntVar(0.0,1.0,"");
                        }
                    }

                    // Each worker is assigned to at exactly one tasks
                    for (int row=0;row<allowedGroup.size();row++) {
                        MPConstraint c = solver.makeConstraint(1.0,1.0,"");
                        for (int col=0;col <numTasks;col++) {
                            c.setCoefficient(x[allowedGroup.get(row)][col],1);
                        }
                    }

                    // Each task is assigned  to at exactly one worker
                    for (int col =0;col<numTasks;col++) {
                        MPConstraint c = solver.makeConstraint(1.0,1.0,"");
                        for (int row = 0; row <numWorkers;row++) {
                            c.setCoefficient(x[row][col],1);
                        }
                    }
                    //Objective
                    MPObjective objective = solver.objective();
                    for (int row=0;row<numWorkers;row++) {
                        for (int col=0;col <numTasks;col++) {
                            objective.setCoefficient(x[row][col],cost[row][col]);
                        }
                    }

                    objective.setMinimization();

                    MPSolver.ResultStatus resultStatus = solver.solve();
                    if (resultStatus == MPSolver.ResultStatus.OPTIMAL) {
                        if (objective.value() < MIN) {
                            minSolver = solver;
                            minX =x;
                            MIN = objective.value();
                        }
                    }

                }
            }
        }

        if (minSolver != null) {
            System.out.println("---------------------------------------------------");
            for (int row = 0; row < numWorkers; row++) {
                for (int col = 0; col < numTasks; col++) {
                    if (minX[row][col].solutionValue() >0.5) {
                        System.out.println("Worker "+row+" assigned to task "+ col+".    Cost= "+cost[row][col] );
                    }
                }
            }
            MPObjective objective = minSolver.objective();
            System.out.println("Min: "+objective.value());
        }

    }
}
