package ca.mcmaster.se2aa4.mazerunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.junit.jupiter.api.Assertions;


public class MazeTest {
    
    Maze maze;
    Maze emptyMaze;
    Maze singleMaze;

    @BeforeEach
    @DisplayName("Set up Maze and Player classes for testing")
    void setUpTest() {
        String line;
        int lineCount = 0;
        try {
            BufferedReader mazeBuildReader = new BufferedReader(new FileReader("./examples/direct.maz.txt"));
            maze = new Maze(8, 8);
            while ((line = mazeBuildReader.readLine()) != null) {
                for (int idx = 0; idx < line.length(); idx++) {
                    if (line.charAt(idx) == '#') {
                        maze.setMazeUnit(lineCount, idx, true);
                    } else if (line.charAt(idx) == ' ') {
                        maze.setMazeUnit(lineCount, idx, false);
                    }
                }
                lineCount++;
            }
            MazeValidity validity = maze.locateEntry();
        }
        catch (Exception e) {
            System.err.println(e);
        }
    }

    @Test
    void testInvalidFile() { // Maze files that do not exist in the directory
        Exception exception = Assertions.assertThrows(FileNotFoundException.class, () -> {
            BufferedReader testReader = new BufferedReader(new FileReader("./examples/doesnotexist.maz.txt"));
        });
        Assertions.assertEquals(".\\examples\\doesnotexist.maz.txt (The system cannot find the file specified)", exception.getMessage());
    }

    @Test
    void testSingleCellMazeWithSpace() { // ' ' in one line only
        singleMaze = new Maze(1,1);
        singleMaze.setMazeUnit(0, 0, false);
        MazeValidity singleCellEmptyValidity = singleMaze.locateEntry();
        Assertions.assertEquals(MazeValidity.VALID_MAZE, singleCellEmptyValidity);
    }

    @Test
    void testSingleCellMazeWithWall() { // '#' in one line only
        singleMaze = new Maze(1,1);
        singleMaze.setMazeUnit(0, 0, true);
        MazeValidity singleCellFilledValidity = singleMaze.locateEntry();
        Assertions.assertEquals(MazeValidity.INVALID_MAZE, singleCellFilledValidity);
    }   

    @Test
    void testEmptyMaze() {
        emptyMaze = new Maze(0,0);
        MazeValidity emptyValidity = emptyMaze.locateEntry();
        Assertions.assertEquals(MazeValidity.INVALID_MAZE, emptyValidity);
    }

    @Test
    void testMazeAutoTraverse() { // Simulates program with -i flag
        maze.traverse();
        String result = maze.getPath();
        Assertions.assertEquals("FR2FL3FRFLFRFL2F", result);
    }

    @Test
    void testMazeCorrectPath() { // Simulates program with -i and -p flag (correct path)
        StringBuffer path = new StringBuffer("FR2FL3FRFLFRFL2F");
        Result pathResult = maze.userPathCheck(path);
        Assertions.assertEquals(Result.CORRECT, pathResult);
    }

    @Test
    void testMazeIncorrectPath() { // Simulates program with -i and -p flag (incorrect path)
        StringBuffer path = new StringBuffer("FR2FL3FRFLFRFLF");
        Result pathResult = maze.userPathCheck(path);
        Assertions.assertEquals(Result.INCORRECT, pathResult);
    }

    @Test
    void testUserPathBeyondExit() { // Simulates program with -i and -p flag (ends up at exit, but tries to move beyond maze/array boundaries)
        StringBuffer path = new StringBuffer("FR2FL3FRFLFRFL8F");
        Result pathResult = maze.userPathCheck(path);
        Assertions.assertEquals(Result.INCORRECT, pathResult);
    }

    @Test
    void testLargeUserInput() {
        StringBuffer path = new StringBuffer("4444444444444444RFR2FL3FRFLFRFL2F"); // Simulates program with -i and -p flag (large numerical values before directions)
        Result pathResult = maze.userPathCheck(path);
        Assertions.assertEquals(Result.CORRECT, pathResult);
    }

    @Test
    void testInvalidUserInput() { // Simulates program with -i and -p flag (characters not recognized as valid directions)
        StringBuffer path = new StringBuffer("FR2FCQY");
        Result pathResult = maze.userPathCheck(path);
        Assertions.assertEquals(Result.INCORRECT, pathResult);
    }
}
