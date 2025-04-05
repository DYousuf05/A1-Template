package ca.mcmaster.se2aa4.mazerunner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.cli.*;

enum Direction { // The various directions for the maze
    NORTH,
    EAST,
    SOUTH,
    WEST
}

enum MazeValidity {
    VALID_MAZE, // Has an entry and exit
    INVALID_MAZE // No entry or exit
}

enum Result { // Boolean representation of valid and invalid paths by the user
    CORRECT,
    INCORRECT
}

interface Command {
    void execute();
}

class forwardCommand implements Command {
    private Player player;

    public forwardCommand(Player player) {
        this.player = player;
    }

    public void execute() {
        player.move();
    }
}

class turnLeftCommand implements Command {
    private Player player;

    public turnLeftCommand(Player player) {
        this.player = player;
    }

    public void execute() {
        player.turnLeft();
    }
}

class turnRightCommand implements Command {
    private Player player;

    public turnRightCommand(Player player) {
        this.player = player;
    }

    public void execute() {
        player.turnRight();
    }
}

class PlayerControl {
    private Command command;

    public void setCommand(Command command) {
        this.command = command;
    }

    public void mazeNextStep() {
        command.execute();
    }
}

abstract class MazeReaderTemplate {
    
    protected int rows;
    protected int cols;
    protected String line;
    protected String fileArg;
    private Maze maze;

    public MazeReaderTemplate(String fileArg) {
        this.fileArg = fileArg;
    }

    public final Maze performMazeRead() throws IOException {
        maze = readDimensions();
        return executeMazeReadAction(maze);
    } 

    private Maze readDimensions() throws IOException {
        rows = 0;
        cols = 0;
        BufferedReader reader = new BufferedReader(new FileReader(fileArg));
        while ((line = reader.readLine()) != null) {
            rows++;
            cols = Math.max(cols, line.length());
        }
        return new Maze(rows, cols);
    }

    protected abstract Maze executeMazeReadAction(Maze maze);
}

class SizeReader extends MazeReaderTemplate {

    public SizeReader(String fileArg) {
        super(fileArg);
    }

    protected Maze executeMazeReadAction(Maze maze) {
        return maze;
    }
}

class BuildReader extends MazeReaderTemplate {

    public BuildReader(String fileArg) {
        super(fileArg);
    }

    protected Maze executeMazeReadAction(Maze maze) {
        try {
            int lineCount = 0;
            BufferedReader reader = new BufferedReader(new FileReader(fileArg));
            while ((line = reader.readLine()) != null) {
                for (int idx = 0; idx < line.length(); idx++) {
                    if (line.charAt(idx) == '#') {
                        maze.setMazeUnit(lineCount, idx, true);
                    } else if (line.charAt(idx) == ' ') {
                        maze.setMazeUnit(lineCount, idx, false);
                    }
                }
                lineCount++;
            }
        }
        catch (Exception e) {
            System.err.println("/!\\ An error has occured /!\\");
        }
        return maze;
    }
}

class Maze {
    private boolean[][] maze;
    private Player player;

    private StringBuffer path = new StringBuffer();
    int repeats = 0;

    public Maze(int rows, int cols) { // Creates array from maze file
        maze = new boolean[rows][cols];
        player = new Player();
    }

    public MazeValidity locateEntry() { // Finds the entry in the maze
        for (int entryRow = 0; entryRow < maze.length; entryRow++) {
            if (!maze[entryRow][0]) {
                
                for (int exitRow = 0; exitRow < maze.length; exitRow++) {
                    
                    if (!maze[exitRow][maze[0].length - 1]) {
                        player.setPos(entryRow, 0);
                        player.setDir(Direction.EAST);
                        return MazeValidity.VALID_MAZE;
                    }
                }
                return MazeValidity.INVALID_MAZE;
            }
        }
        return MazeValidity.INVALID_MAZE;
    }

    public Result userPathCheck(StringBuffer userPath) { // If a path is given, check if the path is valid or not
        Command moveForward = new forwardCommand(player);
        Command turnLeft = new turnLeftCommand(player);
        Command turnRight = new turnRightCommand(player);

        PlayerControl controller = new PlayerControl();

        repeats = 1;
        for (int i = 0; i < userPath.length(); i++) {
            char instruction = userPath.charAt(i);
            
            if (Character.isDigit(instruction)) {
                repeats = instruction - '0'; // Turn the number into an int
            }
            else {
                for (int j = 0; j < repeats; j++) {
                    if (instruction == 'F') {
                        if (player.isAtExit(maze) || player.forwardSide(maze)) {
                            return Result.INCORRECT;
                        }
                        controller.setCommand(moveForward);
                    }
                    else if (instruction == 'R') {
                        controller.setCommand(turnRight);
                    }
                    else if (instruction == 'L') {
                        controller.setCommand(turnLeft);
                    }
                    else {
                        return Result.INCORRECT;
                    }
                    controller.mazeNextStep();
                }
                repeats = 1;
            }
        }
        if (!player.isAtExit(maze)) {
            return Result.INCORRECT;
        }
        return Result.CORRECT;
    }

    public void traverse() { // Traverse the maze given by the file
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

    public void emptyRepeats() { // For F's, if multiple, store the count and append all at once
        if (repeats == 1) {
            path.append("F");
        }
        else {
            path.append(repeats + "F");
        }
        repeats = 0;
    }

    public void setMazeUnit(int row, int col, boolean val) { // Map out the maze in the array
        maze[row][col] = val;
    }

    public String getPath() { // Send a copy of the path
        String pathCopy = path.toString();
        return pathCopy;
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

    public boolean rightSide(boolean[][] maze) { // Checks right side of player
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

    public boolean leftSide(boolean[][] maze) { // Checks left side of player
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

    public boolean forwardSide(boolean[][] maze) { // Checks in front of player
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

    private static final Option mazeArg = new Option("i", "input", true, "Enter a maze");
    private static final Option pathArg = new Option("p", "path", true, "Enter a path to see if it can traverse the maze");
       public static void main(String[] args) {
        CommandLineParser clParser = new DefaultParser();
        Options options = new Options();
        options.addOption(mazeArg);
        options.addOption(pathArg);

        try {
            CommandLine cl = clParser.parse(options, args);
            if (cl.hasOption(mazeArg.getLongOpt())) {
                String fileArg = cl.getOptionValue(mazeArg.getLongOpt());
                SizeReader getMazeSize = new SizeReader(fileArg);
                BuildReader fillMazeArray = new BuildReader(fileArg);

                Maze maze = getMazeSize.performMazeRead();
                maze = fillMazeArray.performMazeRead();
                
                MazeValidity validity = maze.locateEntry();
                if (validity == MazeValidity.VALID_MAZE) {
                    if (cl.hasOption(pathArg.getOpt())) {
                        String userArg = cl.getOptionValue(pathArg.getLongOpt());
                        StringBuffer userPath = new StringBuffer(userArg);
                        Result pathResult = maze.userPathCheck(userPath);
                        if (pathResult == Result.CORRECT) {
                            System.out.println("correct Path");
                        }
                        else {
                            System.out.println("incorrect Path");
                        }
                    }
                    else {
                        maze.traverse();
                        String mazeResult = maze.getPath();
                        System.out.println(mazeResult);
                    }
                }
                else {
                    System.out.println("Maze cannot be traversed");
                }
            } 
            
        } catch (Exception exception) {
            String errorMessage;
            if (exception instanceof ArrayIndexOutOfBoundsException) {
                errorMessage = "/!\\ Error: Maze is empty /!\\";
            }
            else if (exception instanceof FileNotFoundException) {
                errorMessage = "/!\\ Error: Maze file could not be found /!\\";
            }
            else {
                errorMessage = "/!\\ An error has occured /!\\";
                
            }
            System.err.println(errorMessage);
        }
    }
}
