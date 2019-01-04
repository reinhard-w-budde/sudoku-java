package de.budde.sudoku;

import java.util.Arrays;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunSudoku {
    static final Logger RUN = LoggerFactory.getLogger("RUN");

    private RunSudoku() {
    }

    /**
     * main entry: solve a sudoku given as a String. Log the result.
     *
     * @param aS the sudoku given as a String
     */
    public static State run(String aS) {
        long start = new Date().getTime();
        State state = new State(Do.string2cells(aS));
        RUN.info("start with " + state.getNumberFinalized() + " known values");
        showState(state, false);
        state = RuleMachine.ruleOneValLeftSingleStep(0, state);
        if ( state.getNumberFinalized() < 81 ) {
            state = RuleMachine.ruleExcludedVal(0, state);
            if ( state.getNumberFinalized() < 81 ) {
                state = RuleMachine.ruleBacktracker(0, state, mkNothingVisited());
            }
        }
        state.valid();
        long delta = new Date().getTime() - start; // runtime native code generation has great effect on delta!
        RUN.info("final result after " + state.getSteps() + " steps  in " + delta + " msec");
        showState(state, false); // false: compact solution, true: solution with rule names and step number
        return state;
    }

    private static void showState(State state, boolean showDetails) {
        if ( RUN.isInfoEnabled() ) {
            RUN.info(state.toString(showDetails));
        }
    }

    private static boolean[] mkNothingVisited() {
        boolean[] visitedCells = new boolean[81];
        Arrays.fill(visitedCells, false);
        return visitedCells;
    }
}