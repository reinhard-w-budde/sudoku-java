package de.budde.sudoku;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        new Main().run(args[0]);
    }

    private void run(String challengFileNameInFolderChallenges) {
        System.out.println("solving SUDOKU " + challengFileNameInFolderChallenges);
        Path path;
        try {
            path = Paths.get(challengFileNameInFolderChallenges);
            String toSolve = Files.readAllLines(path).stream().collect(Collectors.joining());
            RunSudoku.run(toSolve);
        } catch ( IOException e ) {
            System.out.println("The challenge file " + challengFileNameInFolderChallenges + " could not be read");
        }
    }
}
