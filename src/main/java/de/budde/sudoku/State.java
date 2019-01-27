package de.budde.sudoku;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;

import com.google.common.base.Strings;

import de.budde.sudoku.Structure.NeighborHoodStream;
import de.fraunhofer.iais.dbc.DBC;
import de.fraunhofer.iais.dbc.DBCException;

public class State {
    private static final BigInteger MINUS1 = BigInteger.valueOf(-1);
    private static final BigInteger ZERO = BigInteger.valueOf(0);
    private static final BigInteger ONE = BigInteger.valueOf(1);

    private Cell[] cells;
    private BigInteger steps;

    /**
     * create an initial state from a cell array.
     *
     * @param cells the initial cells
     */
    public State(Cell[] cells) {
        DBC.isTrue(cells != null && cells.length == 81, "81 cells are required for a 9x9 sudoku");
        this.cells = cells;
        propagateInitialValues();
        valid();
        steps = ZERO;
    }

    /**
     * @return a deep clone of this state
     */
    @Override
    public State clone() {
        Cell[] cells = new Cell[81];
        for ( int i = 0; i < this.cells.length; i++ ) {
            cells[i] = this.cells[i].clone();
        }
        State clone = new State(cells);
        clone.steps = steps;
        clone.valid();
        return clone;
    }

    /**
     * @return the array of all cells of this state
     */
    public Cell[] getCells() {
        return cells;
    }

    /**
     * set the final value for a cell. Remove this value from all cells in all neighborhoods
     *
     * @param cell the cell, whose final value is known now
     * @param val the final value
     * @param ruleId the rule identifier, who discovered the final value
     */
    public void setFinalCellVal(Cell cell, Val val, char ruleId) {
        steps = steps.add(ONE);
        cell.setFinalVal(val, steps, ruleId);
        int finalizedCellId = cell.getIdx();
        NeighborHoodStream neighborHoodStream = Structure.getNeighborHood(finalizedCellId);
        neighborHoodStream.get().forEach(g -> removeValueFromNeighborHood(val, finalizedCellId, g));
        valid();
    }

    /**
     * take a value and search a neighborhood, in which this value is impossible for all its cells. If such a neighborhood is found,
     *
     * @param val the test value
     * @param cell the cell, whose neighborhooda are checked
     * @return true, if a neighborhood is found, in which the value is impossible for all its cells; false, otherwise
     */
    public boolean valImpossibleInAtLeastOneNeighborhood(Val val, Cell cell) {
        DBC.notNull(val);
        DBC.isTrue(cell.getPossibleVals().contains(val));
        int idx = cell.getIdx();
        NeighborHoodStream neighborHoodStream = Structure.getNeighborHood(idx);
        Optional<?> found = neighborHoodStream.get().filter(g -> isValImpossibleInNeighborhood(val, idx, g)).findFirst();
        return found.isPresent();
    }

    /**
     * @return the number of cells, whose value is known and this knowledge has been propagated to the cell's neighborhoods
     */
    public int getNumberFinalized() {
        int finalized = 0;
        for ( Cell cell : cells ) {
            if ( cell.isFinalValueSet() ) {
                finalized++;
            }
        }
        return finalized;
    }

    /**
     * check, whether this state is valid.<br>
     * - the state is valid, if all neighborhoods are valid.<br>
     * - a neighborhood is valid, if all finalized cells of it have different values<br>
     * <br>
     * If the state is not valid, throw an exception, otherwise return.
     */
    public void valid() {
        for ( Cell cell : cells ) {
            EnumSet<Val> possibleVals = cell.getPossibleVals();
            DBC.isTrue(possibleVals.size() > 0);
        }
        for ( int[] neighborHood : Structure.getAllNeighborhoods() ) {
            EnumSet<Val> collect = EnumSet.noneOf(Val.class);
            for ( int idx : neighborHood ) {
                Cell cell = cells[idx];
                if ( cell.isOnlyOneValLeft() ) {
                    Val finalVal = cell.getTheFinalVal();
                    if ( collect.contains(finalVal) ) {
                        throw new DBCException("NeighborHood " + Arrays.toString(neighborHood) + " at idx " + idx + " has duplicate value " + finalVal);
                    }
                    collect.add(finalVal);
                }
            }
        }
    }

    /**
     * @return the number of steps, that have been done to solve the sudoku. By calling {@link #setFinalCellVal(Cell, Val, char)}, the number of steps is
     *         incremented.
     */
    public BigInteger getSteps() {
        return steps;
    }

    /**
     * increment the steps by the number of steps, that have been executed for a failing state copy (this occurs inside the 'backtrack' rule, if a temporary
     * solution led to an inconsistent state). Should only be called by the 'backtrack' rule.
     *
     * @param attempts that have been done and failed to be a solution
     */
    public void incrSteps(BigInteger attempts) {
        steps = steps.add(attempts);
    }

    @Override
    public String toString() {
        return toString(false);
    }

    /**
     * create a readable representation, either compact or annotated by step number and rule (when the value was finalized)
     *
     * @param showDetails true: annotated; false: compact
     * @return the readable representation
     */
    public String toString(boolean showDetails) {
        String horizontalSeparator;
        String percentD = null;
        String empty = null;
        if ( showDetails ) {
            final int stepLength = ("" + steps).length();
            final int stepLengthPlus5 = stepLength + 5;
            final String horizontalCellHeader = " " + Strings.repeat("-", stepLengthPlus5);
            final String horizontalLine = "+" + Strings.repeat(horizontalCellHeader, 3) + " ";
            horizontalSeparator = Strings.repeat(horizontalLine, 3) + "+";
            percentD = "%" + stepLength + "d";
            empty = Strings.repeat(" ", stepLengthPlus5);
        } else {
            horizontalSeparator = "+ - - - + - - - + - - - +";
            empty = " ";
        }
        final StringBuilder sb = new StringBuilder();
        boolean first = true;
        int three = 0;
        for ( int i = 0; i < cells.length; i++ ) {
            if ( i % 9 == 0 ) {
                if ( first ) {
                    first = false;
                    sb.append(horizontalSeparator).append("\n| ");
                } else {
                    sb.append("\n");
                    three++;
                    if ( three == 3 ) {
                        three = 0;
                        sb.append(horizontalSeparator).append("\n| ");
                    } else {
                        sb.append("| ");
                    }
                }
            }
            Cell cell = cells[i];
            addCellInfo(sb, cell, showDetails, percentD, empty);
            sb.append((i + 1) % 3 == 0 ? " | " : " ");
        }
        sb.append("\n").append(horizontalSeparator);
        return sb.toString();
    }

    /**
     * finalize the initialization of a state, after the initial values have been stored into the state. Propagate an initial value to the neighborhoods
     *
     * @param state state in initialization
     */
    private void propagateInitialValues() {
        for ( Cell cell : getCells() ) {
            if ( cell.getRuleId() == 'I' ) {
                int initialCellId = cell.getIdx();
                Val val = cell.getTheFinalVal();
                NeighborHoodStream neighborHoodStream = Structure.getNeighborHood(initialCellId);
                neighborHoodStream.get().forEach(g -> removeValueFromNeighborHood(val, initialCellId, g));
            }
        }
    }

    /**
     * for a finalized cell, add the value and optional anotations: ruleId and step-number; otherwise add spaces.
     */
    private static void addCellInfo(StringBuilder sb, Cell cell, boolean showStep, String percentD, String empty) {
        if ( cell.isFinalValueSet() ) {
            sb.append(cell.getTheFinalVal());
            if ( showStep ) {
                sb.append("(").append(cell.getRuleId()).append(':').append(String.format(percentD, cell.getStep())).append(")");
            }
        } else {
            sb.append(empty);
        }
    }

    /**
     * check for a single neighborhood, whether a value is impossible for all cells (except the one, that triggered the check)
     *
     * @param val the value to be checked
     * @param mineIdx the index of the triggering cell; has to be excluded from the check
     * @param neighborHood the cell id's of a neighborhood
     * @return true, if the value is impossible in the neighborhood
     */
    private boolean isValImpossibleInNeighborhood(Val val, int mineIdx, int[] neighborHood) {
        for ( int idx : neighborHood ) {
            if ( idx != mineIdx ) {
                Cell cell = cells[idx];
                if ( cell.isValPossible(val) ) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * remove a value from the set of possible values of all cells of a neighborHood
     *
     * @param finalVal the value to be removed
     * @param finalizedCellId the index of the cell, whose value was finalized; has to be excluded from the removal, of course
     * @param neighborHood the cell id's of a neighborhood
     */
    private void removeValueFromNeighborHood(Val finalVal, int finalizedCellId, int[] neighborHood) {
        DBC.isTrue(neighborHood.length == 9);
        for ( int idx : neighborHood ) {
            if ( idx != finalizedCellId ) {
                cells[idx].removeFromSetOfPossibleValues(finalVal);
            }
        }
    }
}