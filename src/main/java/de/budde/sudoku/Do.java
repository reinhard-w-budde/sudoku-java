package de.budde.sudoku;

import java.util.EnumSet;
import java.util.regex.Pattern;

import org.slf4j.Logger;

import de.fraunhofer.iais.dbc.DBCException;

public class Do {
    private static final Pattern VALIDCHARS = Pattern.compile("[123456789 \\.]{81,81}");

    private Do() {
    }

    /**
     * convert a string of length 81 to an Cell[81] array. Chars between '1' and '9'are considered already known cell values.
     * 
     * @param aS the string to convert
     * @return the cell array
     */
    public static Cell[] string2cells(String aS) {
        check(aS);
        Cell[] cells = new Cell[81];
        for ( int i = 0; i < 81; i++ ) {
            cells[i] = new Cell(i, EnumSet.allOf(Val.class));
        }
        for ( int i = 0; i < 81; i++ ) {
            char charAtI = aS.charAt(i);
            if ( charAtI >= '1' && charAtI <= '9' ) {
                cells[i].setInitVal(Val.of(charAtI));
            }
        }
        return cells;
    }

    /**
     * log, that a rule started
     * 
     * @param log logger to use
     * @param indent depth of indentation
     * @param ruleId name of rule to be started
     * @param state of the solution
     */
    public static void logStartRule(Logger log, int indent, char ruleId, State state) {
        logRule(log, indent, "++", ruleId, null, state);
    }

    /**
     * log, that a rule terminated
     * 
     * @param log logger to use
     * @param indent depth of indentation
     * @param ruleId name of rule to be started
     * @param state of the solution
     */
    public static void logEndRule(Logger log, int indent, char ruleId, String msg, State state) {
        logRule(log, indent, "--", ruleId, msg, state);
    }

    /**
     * log a message with a indentation (for better reading)
     * 
     * @param log logger to use
     * @param depth of indentation
     * @param msg to be logged
     */
    public static void logI(Logger log, int depth, String msg) {
        if ( log.isInfoEnabled() ) {
            StringBuilder sb = new StringBuilder();
            addIndentation(sb, depth);
            sb.append(msg);
            log.info(sb.toString());
        }
    }

    /**
     * help to log that a rule started or finished.
     */
    private static void logRule(Logger log, int depth, String prefix, char ruleId, String msg, State state) {
        if ( log.isInfoEnabled() ) {
            StringBuilder sb = new StringBuilder();
            sb.append(prefix).append(ruleId).append(": ");
            if ( msg != null ) {
                sb.append(msg).append(" ");
            }
            sb.append("[").append(state.getNumberFinalized()).append("]");
            addIndentation(sb, depth);
            sb.append(msg);
            log.info(sb.toString());
        }
    }

    /**
     * add indentation to a StringBuilder
     *
     * @param sb StringBuilder which receives the indentation
     * @param indent indentation level
     */
    private static void addIndentation(StringBuilder sb, int indent) {
        for ( int i = 0; i <= indent; i++ ) {
            sb.append(". ");
        }
    }

    /**
     * check whether an input string might be a valid Sudoku definition:<br>
     * - 9 lines<br>
     * - each line 9 chars<br>
     * - each char 1...9 or '.' or ' '
     *
     * @param aS the string to check
     */
    private static void check(String aS) {
        if ( aS == null || aS.length() != 81 ) {
            throw new DBCException("invalid size. Must be 81");
        } else if ( !VALIDCHARS.matcher(aS).matches() ) {
            throw new DBCException("invalid chars. Must be 1..9 ' ' or '.'");
        }
    }
}