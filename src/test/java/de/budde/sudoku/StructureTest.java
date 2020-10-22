package de.budde.sudoku;

import org.junit.Assert;
import org.junit.Test;

public class StructureTest {
    @Test
    public void testNeighbarHood() {
        Structure structure = new Structure();
        check(0, structure, ia(0, 1, 2, 3, 4, 5, 6, 7, 8), ia(0, 9, 18, 27, 36, 45, 54, 63, 72), ia(0, 1, 2, 9, 10, 11, 18, 19, 20));
        check(40, structure, ia(36, 37, 38, 39, 40, 41, 42, 43, 44), ia(4, 13, 22, 31, 40, 49, 58, 67, 76), ia(30, 31, 32, 39, 40, 41, 48, 49, 50));
        check(Cell.xy2idx(4, 1), structure, ia(0, 1, 2, 3, 4, 5, 6, 7, 8), ia(3, 12, 21, 30, 39, 48, 57, 66, 75), ia(3, 4, 5, 12, 13, 14, 21, 22, 23));
    }

    @Test
    public void testIndex() {
        Assert.assertArrayEquals(ia(1, 1), Structure.xy2block(1, 1));
        Assert.assertArrayEquals(ia(1, 1), Structure.xy2block(2, 1));
        Assert.assertArrayEquals(ia(1, 1), Structure.xy2block(3, 1));
        Assert.assertArrayEquals(ia(1, 1), Structure.xy2block(1, 3));
        Assert.assertArrayEquals(ia(1, 1), Structure.xy2block(2, 2));

        Assert.assertArrayEquals(ia(7, 7), Structure.xy2block(7, 7));
        Assert.assertArrayEquals(ia(7, 7), Structure.xy2block(9, 9));
    }

    private void check(int idx, Structure structure, int[] hor, int[] vert, int[] block) {
        int[][] neighbarhoods = Structure.getNeighborHood(idx).get().toArray(int[][]::new);
        Assert.assertArrayEquals(hor, neighbarhoods[0]);
        Assert.assertArrayEquals(vert, neighbarhoods[1]);
        Assert.assertArrayEquals(block, neighbarhoods[2]);
    }

    private static int[] ia(int... ints) {
        return ints;
    }
}