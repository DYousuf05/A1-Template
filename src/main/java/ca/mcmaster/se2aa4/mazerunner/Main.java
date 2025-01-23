package ca.mcmaster.se2aa4.mazerunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.cli.*;

class Maze {
    private char[][] maze;

    // public Maze(char[][] maze) {
    //     this.maze = maze;
    // }

    public boolean isBlocked(int row, int col) {
        return maze[row][col] == '#';
    }
}

class Player {
    private String dir;
    private int[] pos;

    public String getDir() {
        return dir;
    }

    public void setDir(String newDir) {
        dir = newDir;
    }

    public int[] getPos() {
        return pos;
    }

    public void setPos(int row, int col) {
        pos[0] = row;
        pos[1] = col;
    }
}

public class Main {

    private static final Logger logger = LogManager.getLogger();
    private static final Option mazeArg = new Option("i", "input", true, "Enter a maze");
    Maze maze = new Maze();
    Player player = new Player();
    public static void main(String[] args) {
        logger.info("** Starting Maze Runner");

        CommandLineParser clParser = new DefaultParser();
        Options options = new Options();
        options.addOption(mazeArg);

        try {
            CommandLine cl = clParser.parse(options, args);
            if (cl.hasOption(mazeArg.getLongOpt())) {
                String fileArg = cl.getOptionValue(mazeArg.getLongOpt());
                logger.trace("**** Reading the maze from file " + fileArg);
                BufferedReader reader = new BufferedReader(new FileReader(fileArg));
                String line;
                while ((line = reader.readLine()) != null) {
                    for (int idx = 0; idx < line.length(); idx++) {
                        if (line.charAt(idx) == '#') {
                            System.out.print("WALL ");
                        } else if (line.charAt(idx) == ' ') {
                            System.out.print("PASS ");
                        }
                    }
                    System.out.print(System.lineSeparator());
                }
            } 
        } catch(Exception e) {
            logger.error("/!\\ An error has occured /!\\");
        }
        logger.trace("**** Computing path");
        logger.trace("PATH NOT COMPUTED");
        logger.info("** End of MazeRunner");
    }
}
