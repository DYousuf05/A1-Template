package ca.mcmaster.se2aa4.mazerunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.cli.*;

enum Direction {
    NORTH,
    EAST,
    SOUTH,
    WEST,
}

class Maze {
    public boolean[][] maze;

    public Maze(int rows, int cols) {
        maze = new boolean[rows][cols];
    }
    
    public void setMazeUnit(int row, int col, boolean val) {
        maze[row][col] = val;
    }

    public boolean isBlocked(int row, int col) {
        return maze[row][col] == true;
    }
}

class Player {
    private Direction dir;
    private Position pos;

    public Player() {

    }

    public void setDir(Direction newDir) {
        dir = newDir;
    }

}

class Position extends Player {
    int row;
    int col;

    public void setPos(int newRow, int newCol) {
        row = newRow;
        col = newCol;
    }
}

public class Main {

    private static final Logger logger = LogManager.getLogger();
    private static final Option mazeArg = new Option("i", "input", true, "Enter a maze");
       public static void main(String[] args) {
        Player player = new Player();
        
    
        logger.info("** Starting Maze Runner");

        CommandLineParser clParser = new DefaultParser();
        Options options = new Options();
        options.addOption(mazeArg);

        try {
            CommandLine cl = clParser.parse(options, args);
            if (cl.hasOption(mazeArg.getLongOpt())) {
                String fileArg = cl.getOptionValue(mazeArg.getLongOpt());
                logger.trace("**** Reading the maze from file " + fileArg);
                BufferedReader tempReader = new BufferedReader(new FileReader(fileArg));
                BufferedReader reader = new BufferedReader(new FileReader(fileArg));
                String tempLine;
                String line;
                int rows = 0;
                int cols = 0;
                int lineCount = 0;
                while ((tempLine = tempReader.readLine()) != null) {
                    rows++;
                    cols = Math.max(cols, tempLine.length());
                }

                Maze mazeo = new Maze(rows, cols);

                while ((line = reader.readLine()) != null) {
                    for (int idx = 0; idx < line.length(); idx++) {
                        if (line.charAt(idx) == '#') {
                            mazeo.setMazeUnit(lineCount, idx, true);
                        } else if (line.charAt(idx) == ' ') {
                            mazeo.setMazeUnit(lineCount, idx, false);
                        }
                    }
                    System.out.print(System.lineSeparator());
                    lineCount++;
                }
                for (int i = 0; i < mazeo.maze.length; i++) {
                    System.out.println();
                    for (int j = 0; j < mazeo.maze[i].length; j++) {
                        System.out.print(mazeo.maze[i][j]);
                    }
                }
            } 
        } catch(Exception e) {
            logger.error("/!\\ An error has occured /!\\");
            logger.error(e);
        }
        logger.trace("**** Computing path");
        logger.trace("PATH NOT COMPUTED");
        logger.info("** End of MazeRunner");
    }
}
