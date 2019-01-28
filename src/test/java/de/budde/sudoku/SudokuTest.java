package de.budde.sudoku;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import de.fraunhofer.iais.dbc.DBCException;

public class SudokuTest {
    @Test(expected = DBCException.class)
    public void testInitExc1() {
        new State(null);
    }

    @Test(expected = DBCException.class)
    public void testInitExc2() {
        Do.string2cells("...");
    }

    @Test
    public void testInitExc3() {
        State state = new State(Do.string2cells("................................................................................."));
        Assert.assertNotNull(state);
    }

    @Test
    public void testInit() {
        State state = new State(Do.string2cells(example(3)));
        state = RuleMachine.ruleOneValLeft(0, state);

        for ( int i = 0; i < 9; i++ ) {
            Cell cell = state.getCells()[i];
            Assert.assertEquals(Val.of(i + 1), cell.getTheFinalVal());
        }
        for ( int i = 9; i < 27; i++ ) {
            Cell cell = state.getCells()[i];
            Assert.assertEquals(6, cell.getPossibleVals().size());
        }
        for ( int i = 27; i < 81; i++ ) {
            Cell cell = state.getCells()[i];
            Assert.assertEquals(8, cell.getPossibleVals().size());
        }
    }

    @Test
    public void testClone() {
        State state1 = new State(Do.string2cells(example(2)));
        State state2 = state1.clone();
        Assert.assertEquals(state1.toString(), state2.toString());
        Assert.assertEquals(state1.toString(true), state2.toString(true));
    }

    @Ignore
    @Test
    public void testOne() {
        run(10);
    }

    @Test
    public void testComplete() {
        final int lastNumber = 11;
        System.out.println("\nTESTING " + lastNumber + " SUDOKUS");
        for ( int i = 1; i <= lastNumber; i++ ) {
            run(i);
        }
    }

    private static void run(int number) {
        System.out.println("\nNUMBER " + number);
        String toSolve = example(number);
        State solution = RunSudoku.run(toSolve);
        String expected = solution(number);
        if ( expected != null ) {
            Assert.assertEquals(expected, solution.toString());
        }
    }

    private static String example(int number) {
        Path path = null;
        try {
            path = Paths.get("_examples/sudoku-" + String.format("%02d", number));
            return Files.readAllLines(path).stream().collect(Collectors.joining());
        } catch ( IOException e ) {
            throw new DBCException("File " + path + " could not be read", e);
        }
    }

    private static String solution(int number) {
        Path path = null;
        try {
            path = Paths.get("_solutions/sudoku-" + String.format("%02d", number));
            return Files.readAllLines(path).stream().collect(Collectors.joining("\n"));
        } catch ( IOException e ) {
            return null;
        }
    }
}