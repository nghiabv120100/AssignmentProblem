package com.nghbui;
// Import libraries
import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPVariable;

public class Overview {
    public static void main(String[] args) {
        Loader.loadNativeLibraries();

        MPSolver solver = MPSolver.createSolver("GLOP");
        MPVariable variable = solver.makeIntVar(0.0,1.0,"");

        //2x < 3
        MPConstraint constraint = solver.makeConstraint(0.0,3.0,"");
        constraint.setCoefficient(variable,2);

        //3x
        MPObjective objective = solver.objective();
        objective.setMinimization();

        solver.solve();

        MPSolver newSolver =solver;
        MPObjective newObjective = newSolver.objective();
        System.out.println(newObjective.value());
    }
}
