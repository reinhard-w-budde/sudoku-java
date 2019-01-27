package de.budde.sudoku;

import java.math.BigInteger;
import java.util.EnumSet;

import de.fraunhofer.iais.dbc.DBC;
import de.fraunhofer.iais.dbc.DBCException;

public class Cell {
    static final BigInteger MINUS1 = BigInteger.valueOf(-1);
    static final BigInteger ZERO = BigInteger.valueOf(0);
    static final BigInteger ONE = BigInteger.valueOf(1);

    private final int idx;
    private EnumSet<Val> possibleVals;
    private BigInteger step;
    private char ruleId;

    /**
     * create a new cell with a set of possible values
     *
     * @param idx the index of the cell from 1...81
     * @param possibleVals set of possible values. Values are from 1...9
     */
    public Cell(int idx, EnumSet<Val> possibleVals) {
        this.idx = idx;
        this.possibleVals = possibleVals;
        step = MINUS1;
        ruleId = '?';
    }

    /**
     * create a new cell as a deep clone of another cell
     *
     * @param toClone the cell to clone
     */
    private Cell(Cell toClone) {
        idx = toClone.idx;
        possibleVals = toClone.possibleVals.clone();
        step = toClone.step;
        ruleId = toClone.ruleId;
    }

    /**
     * deep clone of a this cell
     */
    @Override
    public Cell clone() {
        return new Cell(this);
    }

    /**
     * @return the index of the cell, starting from 1 left to right, top to down
     */
    public int getIdx() {
        return idx;
    }

    /**
     * @return the X index of the cell (for pretty printing), starting from 1. Value is from 1...9
     */
    public int getX() {
        return idx % 9 + 1;
    }

    /**
     * @return the Y index of the cell (for pretty printing), starting from 1. Value is from 1...9
     */
    public int getY() {
        return idx / 9 + 1;
    }

    /**
     * set the initial value. This is a value known from the beginning of the sudoku.
     *
     * @param val the initial value
     */
    public void setInitVal(Val val) {
        DBC.notNull(val);
        possibleVals = EnumSet.of(val);
        step = ZERO;
        ruleId = 'I';
    }

    /**
     * set the final value for this cell. If the value is NOT possible, an exception is thrown. This method should only be called by -
     * {@link State#setFinalCellVal(Cell, Val, char)}, which is responsible for propagating the finalization to the neighborhoods<br>
     * <br>
     * <b>Note:</b> see {@link #isFinalValueSet()} and {@link #isOnlyOneValLeft()}.
     *
     * @param val the final value
     * @param step the step in which the final value was decided (helps to explain how the sudoku was solved)
     * @param ruleId the id of the rule that detected the final value (helps to explain how the sudoku was solved)
     */
    public void setFinalVal(Val val, BigInteger step, char ruleId) {
        DBC.notNull(val);
        if ( this.step.compareTo(ZERO) == 1 ) {
            throw new DBCException("cell " + this + " got a final value for the second time - logical error of a rule");
        } else if ( isValPossible(val) ) {
            possibleVals = EnumSet.of(val);
            this.step = step;
            this.ruleId = ruleId;
        } else {
            throw new DBCException(this + " should be set to " + val + ", but that is impossible");
        }
    }

    /**
     * remove a value from the set of possible values. If the value was already removed, this is NO error.
     *
     * @param val the value to be removed from the set of possible values
     */
    public void removeFromSetOfPossibleValues(Val val) {
        possibleVals.remove(val);
    }

    /**
     * @return the set of possible values
     */
    public EnumSet<Val> getPossibleVals() {
        return possibleVals;
    }

    /**
     * is the given value possible?
     *
     * @param val the value to check
     * @return true, if possible; false otherwise
     */
    public boolean isValPossible(Val val) {
        return possibleVals.contains(val);
    }

    /**
     * check if only one value is possible (i.e. the final value is known). This may have been propagated to the neighborhood or may not.<br>
     * <b>Note:</b> this method behaves similar to {@link #isFinalValueSet()}.
     *
     * @return true, if the set of possible values has one element; false otherwise
     */
    public boolean isOnlyOneValLeft() {
        int size = possibleVals.size();
        return size == 1;
    }

    /**
     * check if the final value has been set (explicitly) by calling {@link #setFinalVal(Val, int, char)}.<br>
     * <b>Note:</b> if this method returns true, {@link #isOnlyOneValLeft()} will return true, too.
     *
     * @return true, if final value has been set explicitly; false otherwise
     */
    public boolean isFinalValueSet() {
        return step.compareTo(ZERO) >= 0;
    }

    /**
     * return the final value of this cell. If the final value is unknown, throw an exception.
     *
     * @return the final value
     */
    public Val getTheFinalVal() {
        int size = possibleVals.size();
        DBC.isTrue(size == 1);
        return possibleVals.iterator().next();
    }

    /**
     * @return the step, in which the final value was discovered. If the final value is unknown, return -1
     */
    public BigInteger getStep() {
        return step;
    }

    /**
     * @return the ruleId, that discovered the final value. If the final value is unknown, return '?'
     */
    public char getRuleId() {
        return ruleId;
    }

    /**
     * @return the x-y-coordinates of this cell. X from left to right, starting with 1. Y from top to bottom, starting at 1.
     */
    public String toXY() {
        return "{" + getX() + "," + getY() + "}";
    }

    @Override
    public String toString() {
        return "{" + getX() + "," + getY() + ";" + possibleVals.toString() + "}";
    }

    /**
     * convert a x-y-coordinate to the index of a cell. Used for testing only.
     *
     * @param x coordinate
     * @param y coordinate
     * @return the index of a cell
     */
    public static int xy2idx(int x, int y) {
        return x + 9 * y - 10;
    }

    /**
     * convert an index to a x-y-coordinate of a cell.
     *
     * @param idx the index of a cell
     * @return the array of 2 elements with the x- and y-coordinate
     */
    public static int[] idx2xy(int idx) {
        return new int[] {
            idx % 9 + 1,
            idx / 9 + 1
        };
    }
}