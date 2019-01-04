package de.budde.sudoku;

import de.fraunhofer.iais.dbc.DBCException;

/**
 * the possible values for a cell. They are defined as a enum and not as char or short or int, because this allows to build easily efficient sets of values
 */
public enum Val {
    one( 1 ), two( 2 ), three( 3 ), four( 4 ), five( 5 ), six( 6 ), seven( 7 ), eight( 8 ), nine( 9 );

    private int n;

    private Val(int n) {
        this.n = (short) n;
    }

    /**
     * return the enum Val matching the char '1' ... '9'
     *
     * @param c the char to transform
     * @return
     */
    public static Val of(char c) {
        return of(c - '0');
    }

    /**
     * return the enum Val matching the int 1 ... 9
     *
     * @param n the int to transform
     * @return
     */
    public static Val of(int n) {
        for ( Val val : Val.values() ) {
            if ( val.n == n ) {
                return val;
            }
        }
        throw new DBCException("invalid Val: " + n);
    }

    @Override
    public String toString() {
        return "" + n;
    }
}