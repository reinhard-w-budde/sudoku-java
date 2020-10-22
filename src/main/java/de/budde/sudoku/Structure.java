package de.budde.sudoku;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Structure {
    // the horizontal neighbarhoods made of the cells from the 9 rows
    private static final int[] H1 = ia(0, 1, 2, 3, 4, 5, 6, 7, 8);
    private static final int[] H2 = inc(H1, 9);
    private static final int[] H3 = inc(H2, 9);
    private static final int[] H4 = inc(H3, 9);
    private static final int[] H5 = inc(H4, 9);
    private static final int[] H6 = inc(H5, 9);
    private static final int[] H7 = inc(H6, 9);
    private static final int[] H8 = inc(H7, 9);
    private static final int[] H9 = inc(H8, 9);
    // the vertical neighbarhoods made of cells from the 9 columns
    private static final int[] V1 = ia(0, 9, 18, 27, 36, 45, 54, 63, 72);
    private static final int[] V2 = inc(V1, 1);
    private static final int[] V3 = inc(V2, 1);
    private static final int[] V4 = inc(V3, 1);
    private static final int[] V5 = inc(V4, 1);
    private static final int[] V6 = inc(V5, 1);
    private static final int[] V7 = inc(V6, 1);
    private static final int[] V8 = inc(V7, 1);
    private static final int[] V9 = inc(V8, 1);
    // the block neighbarhoods made of the cells from the 3x3 "blocks" from left to
    // right, top down
    private static final int[] B1 = ia(0, 1, 2, 9, 10, 11, 18, 19, 20);
    private static final int[] B2 = inc(B1, 3);
    private static final int[] B3 = inc(B2, 3);
    private static final int[] B4 = inc(B1, 27);
    private static final int[] B5 = inc(B4, 3);
    private static final int[] B6 = inc(B5, 3);
    private static final int[] B7 = inc(B1, 54);
    private static final int[] B8 = inc(B7, 3);
    private static final int[] B9 = inc(B8, 3);
    // all horizontal, vertical or block neighbarhoods
    private static final int[][] ALL_H = oa(H1, H2, H3, H4, H5, H6, H7, H8, H9);
    private static final int[][] ALL_V = oa(V1, V2, V3, V4, V5, V6, V7, V8, V9);
    private static final int[][] ALL_B = oa(B1, B2, B3, B4, B5, B6, B7, B8, B9);
    // all neighbarhoods @formatter:off
	public static final int[][] ALL_NEIGHBARHOODS = oa(H1, H2, H3, H4, H5, H6, H7, H8, H9, V1, V2, V3, V4, V5, V6, V7,
			V8, V9, B1, B2, B3, B4, B5, B6, B7, B8, B9);
	// @formatter:on
    private static final Map<Integer, NeighborHoodStream> NEIGHBARHOOD_MAPPING;

    /**
     * create the mapping from a cell index to its 3 neighbarhoods (represented as int[9])
     */
    static {
        NEIGHBARHOOD_MAPPING = new HashMap<>(81);
        for ( int i = 0; i < 81; i++ ) {
            int[] xy = Cell.idx2xy(i);
            int x = xy[0];
            int y = xy[1];
            int[] blockXy = xy2block(x, y);
            int g = (blockXy[0] - 1) / 3 + blockXy[1] - 1;
            NeighborHoodStream neighbarHoodStream = new NeighborHoodStream(ALL_H[y - 1], ALL_V[x - 1], ALL_B[g]);
            NEIGHBARHOOD_MAPPING.put(i, neighbarHoodStream);
        }
    }

    /**
     * return the neighbarhoods of a cell. A neighbarhoods object can generate a stream of the 3 neighbarhoods each cell has. A neighbarhood is represented as
     * an int array (each int is the index of a cell)
     *
     * @param idx of a cell
     * @return the neighbarhoods of a cell
     */
    public static NeighborHoodStream getNeighborHood(int idx) {
        return NEIGHBARHOOD_MAPPING.get(idx);
    }

    /**
     * return all neighbarhoods. Used to validate that all cells from each neighbarhood have different values, for instance.
     *
     * @return all neighbarhoods
     */
    public static int[][] getAllNeighborhoods() {
        return ALL_NEIGHBARHOODS;
    }

    /**
     * convert an index to a the x-y-coordinate of the 3x3 block (the third neighborhood) of the cell. Possible values are x from 1..3 and y from 1..3
     *
     * @param idx the index of a cell
     * @return the array of 2 elements with the x- and y-coordinate of the 3x3 block
     */
    static int[] xy2block(int x, int y) {
        return new int[] {
            ((x - 1) / 3) * 3 + 1,
            ((y - 1) / 3) * 3 + 1
        };
    }

    /**
     * convert a var args int argument list to an int[]
     *
     * @param ints to convert
     * @return int[] of the parameters
     */
    @SafeVarargs
    private static int[] ia(int... ints) {
        return ints;
    }

    /**
     * convert a var args argument list of type T to an
     *
     * @param ts objects of type T to convert
     * @return T[] of the parameters
     */
    @SafeVarargs
    private static <T> T[] oa(T... ts) {
        return ts;
    }

    /**
     * create a copy of an int[]. The values of the copy are the original values incremented by a fixed number
     *
     * @param ia the array to copy
     * @param incr value to increment each array item
     * @return
     */
    private static int[] inc(int[] ia, int incr) {
        int[] iaC = new int[9];
        for ( int i = 0; i < 9; i++ ) {
            iaC[i] = ia[i] + incr;
        }
        return iaC;
    }

    public static class NeighborHoodStream {
        private final int[] h;
        private final int[] v;
        private final int[] b;

        public NeighborHoodStream(int[] h, int[] v, int[] b) {
            this.h = h;
            this.v = v;
            this.b = b;
        }

        public Stream<int[]> get() {
            return Stream.of(this.h, this.v, this.b);
        }

        @Override
        public String toString() {
            return "NeighbarHoods [h=" + Arrays.toString(this.h) + ", v=" + Arrays.toString(this.v) + ", b=" + Arrays.toString(this.b) + "]";
        }
    }
}