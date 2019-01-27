package de.budde.sudoku;

import java.util.EnumSet;

import org.junit.Assert;
import org.junit.Test;

import de.fraunhofer.iais.dbc.DBCException;

public class CellTest {
    @Test
    public void testIndex() {
        Assert.assertEquals(0, Cell.xy2idx(1, 1));
        Assert.assertEquals(8, Cell.xy2idx(9, 1));
        Assert.assertEquals(9, Cell.xy2idx(1, 2));
        Assert.assertEquals(80, Cell.xy2idx(9, 9));

        Assert.assertArrayEquals(ia(1, 1), Cell.idx2xy(0));
        Assert.assertArrayEquals(ia(9, 1), Cell.idx2xy(8));
        Assert.assertArrayEquals(ia(1, 2), Cell.idx2xy(9));
        Assert.assertArrayEquals(ia(9, 9), Cell.idx2xy(80));
    }

    @Test
    public void testCell() {
        Cell c = new Cell(10, EnumSet.of(Val.one, Val.three, Val.five));
        Assert.assertEquals(2, c.getX());
        Assert.assertEquals(2, c.getY());
        Assert.assertTrue(c.isValPossible(Val.three));
        Assert.assertFalse(c.isValPossible(Val.four));
    }

    @Test
    public void testCellSetVal() {
        Cell c = new Cell(10, EnumSet.of(Val.one, Val.three, Val.five));
        c.setFinalVal(Val.three, Cell.ZERO, '?');
        Assert.assertTrue(c.isValPossible(Val.three));
        Assert.assertFalse(c.isValPossible(Val.four));
    }

    @Test(expected = DBCException.class)
    public void testCellSetExc() {
        Cell c = new Cell(10, EnumSet.of(Val.one, Val.three, Val.five));
        c.setFinalVal(Val.two, Cell.ZERO, '?');
    }

    @Test
    public void testVal() {
        Assert.assertEquals(Val.one, Val.of(1));
        Assert.assertEquals(Val.one, Val.of('1'));
        Assert.assertEquals(Val.one, Val.of((byte) 1));
        Assert.assertEquals(Val.five, Val.of('5'));
        Assert.assertEquals(Val.nine, Val.of((byte) 9));
    }

    @Test(expected = DBCException.class)
    public void testValExc() {
        Assert.assertEquals(Val.one, Val.of(10));
    }

    @Test
    public void testCellVal() {
        Cell fiveCell = new Cell(0, EnumSet.of(Val.five));
        Assert.assertTrue(fiveCell.isOnlyOneValLeft());
        Assert.assertEquals(Val.five, fiveCell.getTheFinalVal());
        Cell fiveSixCell = new Cell(0, EnumSet.of(Val.five, Val.six));
        Assert.assertFalse(fiveSixCell.isOnlyOneValLeft());
    }

    @Test(expected = DBCException.class)
    public void testCellValExc() {
        Cell fiveSixCell = new Cell(0, EnumSet.of(Val.five, Val.six));
        fiveSixCell.getTheFinalVal();
    }

    private static int[] ia(int... ints) {
        return ints;
    }
}