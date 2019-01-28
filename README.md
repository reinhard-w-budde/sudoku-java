# SUDOKU

## Overview

There are hundreds of Sudoku solvers on Github alone. Why another one? The sources are written to show good software engineering practices.
Or at least I try to do this :-). Or at least I try to stimulate discussions about that topic :-). So participate!

It is open-source and can be changed and used for private purposes without any restriction.

I will prepare for languages, I use or like, compatible implementations:

* Java (this Git repo, https://github.com/reinhard-w-budde/sudoku-java.git )
* Python ( https://github.com/reinhard-w-budde/sudoku-java.git )
* Go,
* Typescript (implies a Javascript solution).

## Model of the solver

* a soduku (class 'State.java') contains 81 cells (class 'Cell.java') numbered from 1 to 81.
* each cell contains the set of possible values (enum 'Val.java') for this cell. If the set has exactly one item, the cell value is discovered.
* each cell has 3 neighborhoods (defined in class 'Structure.java'), called horizontal, vertical and group neighborhood.
* each of the 27 neighborhoods (each contains 9 cells) doesn't allow that two of its cell have the same value.

* a soduku is read (class 'InOut.java') from a file (stored in directory 'examples' with the name 'sudoku-<two-digit-number>').
* an initial state is constructed (class 'RunSoduku.java').
* this state is transformed to a state, that is closer to the final solution (class 'RunSoduku.java') by applying rules (class 'RuleMachine.java').

* O: rule 'ruleOneValLeft' is the simplest one: it looks, whether a cell has only one possible value. If yes, a solution is found and the rule
  calls itself recursively.

* E: rule 'ruleExcludedVal' is of medium complexity: it takes a possible value of a cell and looks into the cell's 3 neighborhoods to check whether in one
  of the neighborhoods this value is impossible for all (other) cells. If yes, then a solution is found and the first rule 'ruleOneValLeft' is called 
  again and then the rule calls itself recursively.
  
* remark: if a solution is found for a cell, this knowledge is propagated by removing the found value from the set of possible values of all cells of
  its 3 neighborhoods.

* B: rule 'backtrack' is the most complex rule: it takes a cell as a 'test cell', fixes one of its possible values as a temporary solution and tries to solve the sodoku
  by calling 'ruleOneValLeft' and 'ruleExcludedVal'.
  * if no inconsistency is detected ('SUCC', this is checked by method 'valid()' in class 'State.java'), the rule selects
    recursively another cell until the sudoku is solved.
  * if an inconsistency is detected ('FAIL'), the next possible value from the 'test cell' is taken. If all values are exhausted, the sudoku is unsolvable.
  * to improve performance, that cell is selected as the 'test cell', which has the least number (>=2) of possible values left.

* that's all.
* the solver works very fast.
* class 'State.java' contains the method 'toString(boolean showStep)'. When called with parameter 'true', it shows the sudoku and annotates every cell with
  its solving rule and the step, the solution was found. 

## Requirements for Java

* Java >=8.
* Git. It is great, that after installation (on windows) you get a bash shell for free.
* Maven.
* Eclipse Photon. Older versions will do as well. Eclipse integration of Git is good. Eclipse integration of Maven is usable.

Eclipse is not strictly needed. But to see, test and experiment with the code it is a must. Of course, if you have experiences with Intellij, this will work, too. The folder 'resources' contains the formatter 'compactJava'. Using it makes collaboration much easier (same style, change sets are closer to the 'semantic' of a change)
  
## Work with the Java installation

1. create a directory for your git repositories, e.g. "git"
```sh
    mkdir git; cd git
```

2. get the git repository. It contains the branch master.
```sh
    git clone https://github.com/reinhard-w-budde/sudoku-java.git
    cd sudoku-java
```

3. build and run. All sudokus from the '_examples' folder will be solved as tests and compared to the solutions from '_solutions'.
   The test coverage of 'src/main/java' is >97%. Run from the project base directory:
```sh
    mvn clean install
```

4. add sudokus, which you want to solve in the '_challenges' folder (for instance). Run a main class to solve it from the project base directory:
```sh
    java -cp 'target/lib/*' de.budde.sudoku.Main _challenges/YOUR_FILE_NAME
```
   
   There is a lot of logging and there are a lot of loggers configured in 'src/main/resources/logback.xml'. See the comments there.
   The level for a logger to be shown must be 'INFO' or lower. Thus: if you want to reduce logging for some loggers, set their level to 'ERROR' and the logging
   disappears. Leave at least logger 'RUN' at level 'INFO'.

5. For suggestions, discussions, questions, proposals for a better programing style contact me at reinhard.budde at iais.fraunhofer.de
