package ca.mcmaster.se2aa4.mazerunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.cli.*;
import java.util.*;

enum Direction {
    NORTH,
    EAST,
    SOUTH,
    WEST;
}

class Maze {
    private boolean[][] maze;
    private int entryPoint;
    private Player player;
    public StringBuffer path = new StringBuffer();
    int repeats = 0;

    public Maze(int rows, int cols) {
        maze = new boolean[rows][cols];
        player = new Player();
        player.setDir(Direction.EAST);
    }

    public void locateEntry() {
        for (int row = 0; row < maze.length; row++) {
            if (!maze[row][0]) {
                entryPoint = row;
                player.setPos(row, 0);
                break;
            }
        }
    }



    public void traverse() {     
        while (!player.isAtExit(maze)) {
            if (!player.rightSide(maze)) {
                emptyRepeats();
                player.turnRight();
                path.append("R");
                player.move();
                repeats++;
            }
            else if (!player.forwardSide(maze)) {
                player.move();
                repeats++;
            }
            else if (!player.leftSide(maze)) {
                emptyRepeats();
                player.turnLeft();
                path.append("L");
                player.move();
                repeats++;
            }
            else {
                emptyRepeats();
                player.turnRight();
                player.turnRight();
                path.append("2R");
                player.move();
                repeats++;
            }
        }
        emptyRepeats();
    }

    public void emptyRepeats() {
        if (repeats == 1) {
            path.append("F");
        }
        else {
            path.append(repeats + "F");
        }
        repeats = 0;
    }

    public void setMazeUnit(int row, int col, boolean val) {
        maze[row][col] = val;
    }
}

class Player {
    private Direction dir;
    private int row;
    private int col;

    public boolean isAtExit(boolean[][] maze) {
        return col == maze[row].length - 1;
    }

    public void setPos(int newRow, int newCol) {
        row = newRow;
        col = newCol;
    }

    public void setDir(Direction newDir) {
        dir = newDir;
    }

    public void move() {
        if (dir == Direction.NORTH) {
            row--;
        }
        else if (dir == Direction.EAST) {
            col++;
        }
        else if (dir == Direction.SOUTH) {
            row++;
        }
        else if (dir == Direction.WEST) {
            col--;
        }
    }

    public boolean rightSide(boolean[][] maze) {
        if (dir == Direction.NORTH) {
            return maze[row][col+1];
        }
        else if (dir == Direction.EAST) {
            return maze[row+1][col];
        }
        else if (dir == Direction.SOUTH) {
            return maze[row][col-1];
        }
        else if (dir == Direction.WEST) {
            return maze[row-1][col];
        }
        return true;
    }

    public boolean leftSide(boolean[][] maze) {
        if (dir == Direction.NORTH) {
            return maze[row][col-1];
        }
        else if (dir == Direction.EAST) {
            return maze[row-1][col];
        }
        else if (dir == Direction.SOUTH) {
            return maze[row][col+1];
        }
        else if (dir == Direction.WEST) {
            return maze[row+1][col];
        }
        return true;
    }

    public boolean forwardSide(boolean[][] maze) {
        if (dir == Direction.NORTH) {
            return maze[row-1][col];
        }
        else if (dir == Direction.EAST) {
            return maze[row][col+1];
        }
        else if (dir == Direction.SOUTH) {
            return maze[row+1][col];
        }
        else if (dir == Direction.WEST) {
            return maze[row][col-1];
        }
        return true;
    }

    public void turnRight() {
        if (dir == Direction.NORTH) {
            dir = Direction.EAST;
        }
        else if (dir == Direction.EAST) {
            dir = Direction.SOUTH;
        }
        else if (dir == Direction.SOUTH) {
            dir = Direction.WEST;
        }
        else if (dir == Direction.WEST) {
            dir = Direction.NORTH;
        }
    }

    public void turnLeft() {
        if (dir == Direction.NORTH) {
            dir = Direction.WEST;
        }
        else if (dir == Direction.WEST) {
            dir = Direction.SOUTH;
        }
        else if (dir == Direction.SOUTH) {
            dir = Direction.EAST;
        }
        else if (dir == Direction.EAST) {
            dir = Direction.NORTH;
        }
    }

}

public class Main {

    private static final Logger logger = LogManager.getLogger();
    private static final Option mazeArg = new Option("i", "input", true, "Enter a maze");
    private static final Option pathArg = new Option("p", "input", true, "Enter a path to see if it can traverse the maze");
       public static void main(String[] args) {
        
        
    
        logger.info("** Starting Maze Runner");

        CommandLineParser clParser = new DefaultParser();
        Options options = new Options();
        options.addOption(mazeArg);
        options.addOption(pathArg);

        try {
            CommandLine cl = clParser.parse(options, args);
            if (cl.hasOption(mazeArg.getLongOpt())) {
                String fileArg = cl.getOptionValue(mazeArg.getLongOpt());
                logger.trace("**** Reading the maze from file " + fileArg);
                BufferedReader mazeSizeReader = new BufferedReader(new FileReader(fileArg));
                BufferedReader mazeBuildReader = new BufferedReader(new FileReader(fileArg));
                String tempLine;
                String line;
                int rows = 0;
                int cols = 0;
                int lineCount = 0;
                while ((tempLine = mazeSizeReader.readLine()) != null) {
                    rows++;
                    cols = Math.max(cols, tempLine.length());
                }

                Maze maze = new Maze(rows, cols);

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
                
                maze.locateEntry();
                maze.traverse();
                System.out.println(maze.path);
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
