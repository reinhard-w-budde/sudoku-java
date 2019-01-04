package de.budde.sudoku;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iais.dbc.DBCException;

public class RuleMachine {
    private static final Logger RULE_ONE_VAL_LEFT = LoggerFactory.getLogger("RULE_ONE_VAL_LEFT");
    private static final Logger RESULT_ONE_VAL_LEFT = LoggerFactory.getLogger("RESULT_ONE_VAL_LEFT");
    private static final Logger RULE_EXCLUDED_VAL = LoggerFactory.getLogger("RULE_RULE_EXCLUDED_VAL");
    private static final Logger RESULT_EXCLUDED_VAL = LoggerFactory.getLogger("RESULT_EXCLUDED_VAL");
    private static final Logger RULE_BACKTRACK = LoggerFactory.getLogger("RULE_BACKTRACK");
    private static final Logger RESULT_BACKTRACK = LoggerFactory.getLogger("RESULT_BACKTRACK");

    private RuleMachine() {
    }

    /**
     * for for every cell C<br>
     * if: only one value is possible for C<br>
     * then: propagate this to C' neighborhood<br>
     * if: some cell could be finalized,<br>
     * then: call the rule recursively
     *
     * @param recDepth depth of recursion, used for debug indentation
     * @param state actual state of the cells
     * @return the updated state, after the rule has finished
     */
    static State ruleOneValLeft(int recDepth, State state) {
        final char ruleOneValLeftId = 'O';
        Do.logStartRule(RULE_ONE_VAL_LEFT, recDepth, ruleOneValLeftId, state);
        int finalizedCellsBefore = state.getNumberFinalized();
        state = ruleOneValLeftSingleStep(recDepth, state);
        int finalizedCellsAfter = state.getNumberFinalized();
        if ( finalizedCellsBefore != finalizedCellsAfter && finalizedCellsAfter < 81 ) {
            state = ruleOneValLeft(recDepth + 1, state);
        }
        Do.logEndRule(RULE_ONE_VAL_LEFT, recDepth, ruleOneValLeftId, null, state);
        return state;
    }

    /**
     * for for every cell C<br>
     * if: only one value is possible for C<br>
     * then: propagate this to C' neighborhood
     *
     * @param recDepth depth of recursion, used for debug indentation
     * @param state actual state of the cells
     * @return the updated state, after the rule has finished
     */
    static State ruleOneValLeftSingleStep(int recDepth, State state) {
        final char ruleOneValLeftId = 'O';
        for ( Cell cell : state.getCells() ) {
            if ( !cell.isFinalValueSet() && cell.isOnlyOneValLeft() ) {
                Val val = cell.getTheFinalVal();
                state.setFinalCellVal(cell, val, ruleOneValLeftId);
                Do.logI(RESULT_ONE_VAL_LEFT, recDepth, ruleOneValLeftId + ": cell " + cell.toXY() + " = " + val);
            }
        }
        return state;
    }

    /**
     * check for every cell C<br>
     * if: C's neighbarhood cannot hold a value V, which is possible for C<br>
     * then: V must be the correct value for C. Propagate this to C's neighbarhood if: some cell could be finalized,<br>
     * then: call the rule recursively
     *
     * @param recDepth depth of recursion, used for debug indentation
     * @param state actual state of the cells
     * @return the updated state, after the rule has finished
     */
    static State ruleExcludedVal(int recDepth, State state) {
        final char ruleExcludedValId = 'E';
        Do.logStartRule(RULE_EXCLUDED_VAL, recDepth, ruleExcludedValId, state);
        boolean atLeastOneSuccess = false;
        for ( Cell cell : state.getCells() ) {
            if ( !cell.isFinalValueSet() ) {
                for ( Val val : cell.getPossibleVals() ) {
                    boolean success = state.valImpossibleInAtLeastOneNeighborhood(val, cell);
                    if ( success ) {
                        atLeastOneSuccess = true;
                        state.setFinalCellVal(cell, val, ruleExcludedValId);
                        Do.logI(RESULT_EXCLUDED_VAL, recDepth, ruleExcludedValId + ": cell " + cell.toXY() + " = " + val);
                        break;
                    }
                }
            }
        }
        state.valid();
        if ( atLeastOneSuccess && state.getNumberFinalized() < 81 ) {
            state = ruleOneValLeft(recDepth + 1, state);
            state = ruleExcludedVal(recDepth + 1, state);
        }
        Do.logEndRule(RULE_EXCLUDED_VAL, recDepth, ruleExcludedValId, null, state);
        return state;
    }

    /**
     * check for every cell C, by stepping through all possible values V<br>
     * try: to solve the sudoko assumg that C's value is V<br>
     * success: done<br>
     * fail: backtrack to try the next possible value of C
     *
     * @param recDepth depth of recursion, used for debug indentation
     * @param state actual state of the cells
     * @param visitedCells item[i]==true, if the cell has been tried
     * @return the updated state, after the rule has finished
     */
    static State ruleBacktracker(int recDepth, State state, boolean[] visitedCells) {
        final char ruleBacktrackerId = 'B';
        Do.logStartRule(RULE_BACKTRACK, recDepth, ruleBacktrackerId, state);
        Cell cell = null;
        while ( (cell = pickCell(state, visitedCells)) != null ) {
            int idx = cell.getIdx();
            for ( Val val : cell.getPossibleVals() ) {
                State stateForTrial = state.clone();
                Cell test = stateForTrial.getCells()[idx];
                try {
                    Do.logI(RESULT_BACKTRACK, recDepth, ruleBacktrackerId + ": TRY  cell " + test + " = " + val);
                    stateForTrial.setFinalCellVal(test, val, ruleBacktrackerId);
                    stateForTrial = ruleOneValLeft(recDepth + 1, stateForTrial);
                    stateForTrial = ruleExcludedVal(recDepth + 1, stateForTrial);
                    Do.logI(RESULT_BACKTRACK, recDepth, ruleBacktrackerId + ": SUCC cell " + test.toXY() + " = " + val);
                    stateForTrial.valid();
                    if ( stateForTrial.getNumberFinalized() < 81 ) {
                        stateForTrial = ruleBacktracker(recDepth + 1, stateForTrial, visitedCells);
                    }
                    Do.logEndRule(RULE_BACKTRACK, recDepth, ruleBacktrackerId, "FINAL SUCCESS", stateForTrial);
                    return stateForTrial;
                } catch ( DBCException e ) {
                    Do.logI(RESULT_BACKTRACK, recDepth, ruleBacktrackerId + ": FAIL cell " + state.getCells()[idx] + " = " + val);
                    state.incrSteps(stateForTrial.getSteps());
                }
            }
            Do.logEndRule(RULE_BACKTRACK, recDepth, ruleBacktrackerId, "NO SOLUTION for " + cell.toXY(), state);
            throw new DBCException(ruleBacktrackerId + ": no solution (1)");
        }
        Do.logEndRule(RULE_BACKTRACK, recDepth, ruleBacktrackerId, "NO SOLUTION AT ALL", state);
        throw new DBCException(ruleBacktrackerId + ": no solution (2)");
    }

    /**
     * pick the cell with the least number of possible values, but only, if not already visited :-) and if not finalized
     *
     * @param state of the sudoku
     * @param visitedCells array remembering which cells have been visited
     * @return
     */
    private static Cell pickCell(State state, boolean[] visitedCells) {
        Cell[] cells = state.getCells();
        Cell minValsCell = null;
        for ( int i = 0; i < visitedCells.length; i++ ) {
            if ( !visitedCells[i] ) {
                Cell pickCandidate = cells[i];
                if ( !pickCandidate.isFinalValueSet() ) {
                    minValsCell = pickCandidate;
                }
            }
        }
        // all have been visited. Unsolvable sudoku (???)
        if ( minValsCell == null ) {
            return null;
        }
        // find the cell with the least number of possible values
        int minValsSize = minValsCell.getPossibleVals().size();
        if ( minValsSize > 2 ) {
            for ( int i = 0; i < cells.length; i++ ) {
                if ( !visitedCells[i] ) {
                    Cell pickCandidate = cells[i];
                    if ( !pickCandidate.isFinalValueSet() ) {
                        int size = pickCandidate.getPossibleVals().size();
                        if ( size < minValsSize ) {
                            minValsSize = size;
                            minValsCell = pickCandidate;
                            if ( minValsSize <= 2 ) {
                                break;
                            }
                        }
                    }
                }
            }
        }
        // mark it visited and return it
        visitedCells[minValsCell.getIdx()] = true;
        return minValsCell;
    }
}