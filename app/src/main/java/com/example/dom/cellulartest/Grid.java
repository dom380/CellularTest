package com.example.dom.cellulartest;

import com.google.common.base.Stopwatch;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by Dom on 12/10/2016.
 */

public class Grid {

    private Cell[][] cells;
    private long seed;
    private boolean gridSeeded = false;
    private Random random;
    private int gridHeight;
    private int gridWidth;
    private float seedPercentage;
    private static final Object SYNC_LOCK = new Object();
    private Position startPos = new Position();
    private Position endPos = new Position();
    private static double rootTwo = Math.sqrt(2);
    private List<Cell> path = new ArrayList<>();

    public Grid(int gridHeight, int gridWidth, float seedPercent) {
        seed = System.nanoTime();
        random = new Random(seed);
        cells = new Cell[gridHeight][gridWidth];
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.seedPercentage = seedPercent;
    }

    public Grid(int gridHeight, int gridWidth, float seedPercent, long seed) {
        this.seed = seed;
        random = new Random(seed);
        cells = new Cell[gridHeight][gridWidth];
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.seedPercentage = seedPercent;
    }

    public Cell[][] getCells() {
        return cells;
    }

    public void setCells(Cell[][] cells) {
        this.cells = cells;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public void setGridHeight(int gridHeight) {
        this.gridHeight = gridHeight;
    }

    public int getGridWidth() {
        return gridWidth;
    }

    public void setGridWidth(int gridWidth) {
        this.gridWidth = gridWidth;
    }

    public float getSeedPercentage() {
        return seedPercentage;
    }

    public void setSeedPercentage(float seedPercentage) {
        this.seedPercentage = seedPercentage;
    }

    public void seedGrid() {
        if (gridSeeded) {
            return;
        }
        Multimap<Integer, Integer> map = HashMultimap.create();
        Position randPos = new Position();
        int randNumCount = 0;
        float maxRandCount = gridWidth * gridHeight * seedPercentage;
        while (randNumCount < maxRandCount) {
            randPos.setPos(random.nextInt(gridWidth), random.nextInt(gridHeight));
            Collection<Integer> usedX = map.get(randPos.x);
            //If this xCoord never seen before, just add to the map
            if (usedX == null) {
                map.put(randPos.x, randPos.y);
                randNumCount++;
                //If xCoord has been used before, check if y coord has been as well, if not then add to map
            } else if (!usedX.contains(randPos.y)) {
                map.put(randPos.x, randPos.y);
                randNumCount++;
            } //x and y coord have already been used, repeat the loop
        }
        Cell currentCell;
        for (int yCoOrd = 0; yCoOrd < gridHeight; ++yCoOrd) {
            for (int xCoOrd = 0; xCoOrd < gridWidth; ++xCoOrd) {
                currentCell = new Cell(xCoOrd, yCoOrd, CellType.FLOOR);
                //If position has been flagged in seed, change to rock.
                if (map.get(xCoOrd).contains(yCoOrd)) {
                    currentCell.cellType = CellType.ROCK;
                }
                cells[yCoOrd][xCoOrd] = currentCell;
            }
        }
        gridSeeded = true;
    }

    public void reSeedGrid() {
        synchronized (SYNC_LOCK) {
            gridSeeded = false;
            cells = new Cell[gridHeight][gridWidth];
            seedGrid();
            addStartEndGoals();
        }
    }

    public void runCA(int iterations, int threshold, int neighbourSize) {

        for (int i = 0; i < iterations; ++i) {
            calcNeighbourScore();
            checkNeighbourScore(threshold);
        }
    }

    public void addStartEndGoals() {
        int x = random.nextInt(gridWidth);
        int y = random.nextInt(gridHeight);
        while (cells[y][x].cellType != CellType.FLOOR) {
            x = random.nextInt(gridWidth);
            y = random.nextInt(gridHeight);
        }
        cells[y][x].cellType = CellType.START;
        startPos.setPos(x, y);

        x = random.nextInt(gridWidth);
        y = random.nextInt(gridHeight);
        while (cells[y][x].cellType != CellType.FLOOR) {
            x = random.nextInt(gridWidth);
            y = random.nextInt(gridHeight);
        }
        cells[y][x].cellType = CellType.GOAL;
        endPos.setPos(x, y);
    }

    private void calcNeighbourScore() {
        int rowStart;
        int rowFinish;
        int colStart;
        int colFinish;
        int neighbourScore = 0;
        for (int row = 0; row < gridHeight; row++) {
            for (int col = 0; col < gridWidth; col++) {
                neighbourScore = 0;
                rowStart = Math.max(row - 1, 0);
                rowFinish = Math.min(row + 1, gridHeight - 1);
                colStart = Math.max(col - 1, 0);
                colFinish = Math.min(col + 1, gridWidth - 1);

                for (int curRow = rowStart; curRow <= rowFinish; curRow++) {
                    for (int curCol = colStart; curCol <= colFinish; curCol++) {
                        if (cells[curRow][curCol].cellType == CellType.ROCK) {
                            neighbourScore++;
                        }
                    }
                }
                cells[row][col].setNeighbourValue(neighbourScore);
            }
        }
//        int x = 0;
//        int y = 0;
//        //For every cell
//        for (int i = 0; i < gridHeight; ++i) {
//            for (int j = 0; j < gridWidth; ++j) {
//                int row_limit = cells.length-1;
//                int column_limit;
//                int currentT = 0;
//                if(row_limit > 0){
//                    column_limit = cells[0].length-1;
//                    //Check every neighbour
//                    for(x = Math.max(0, i-1); x <= Math.min(i+1, row_limit); x++){
//                        for(y = Math.max(0, j-1); y <=  Math.min(j+1, column_limit); y++){
//                            if(x != i || y != j){
//                                //If neighbour is a rock, increment count
//                                if(cells[y][x].cellType == CellType.ROCK){
//                                    currentT++;
//                                }
//                            }
//                        }
//                    }
//                }
//                cells[i][j].setNeighbourValue(currentT);
//            }
//        }

    }

    private void checkNeighbourScore(int threshold) {
        Cell currentCell;
        for (int i = 0; i < gridHeight; ++i) {
            for (int j = 0; j < gridWidth; ++j) {
                currentCell = cells[i][j];
                if (currentCell.cellType == CellType.START || currentCell.cellType == CellType.GOAL)
                    continue;
                if (currentCell.getNeighbourValue() >= threshold) {
                    currentCell.cellType = CellType.ROCK;
                } else {
                    currentCell.cellType = CellType.FLOOR;
                }
            }
        }
    }

    private ArrayList<Position> eightDir = new ArrayList<>(Arrays.asList(
            new Position(-1, 0),
            new Position(-1, 1),
            new Position(0, 1),
            new Position(1, 1),
            new Position(1, 0),
            new Position(1, -1),
            new Position(0, -1),
            new Position(-1, -1)
    ));

    private ArrayList<Position> fourDir = new ArrayList<>(Arrays.asList(
            new Position(-1, 0),
            new Position(0, 1),
            new Position(1, 0),
            new Position(0, -1)
    ));

    private boolean walkable(int x, int y) {
        if ((x < 0) || (x >= gridWidth)) {
            int check = 1;
            return false;
        } else if ((y < 0) || (y >= gridHeight)) {
            int check = 1;
            return false;
        }
        return !(cells[y][x].cellType == CellType.ROCK || cells[y][x].cellType == CellType.WALL);
    }

//old neighbour checks
//    private List<Cell> getNeighbours(Cell cell, boolean moveDiag) {
//        List<Cell> neighbours = new ArrayList<>();
//        if (moveDiag) {
//            for (Position dir : eightDir) {
//                int x = cell.position.add(dir).x;
//                int y = cell.position.add(dir).y;
//                if (walkable(x, y))
//                    neighbours.add(cells[y][x]);
//            }
//        } else {
//            for (Position dir : fourDir) {
//                //for (Position dir : eightDir) {
//                int x = cell.position.add(dir).x;
//                int y = cell.position.add(dir).y;
//                if (walkable(x, y))
//                    neighbours.add(cells[y][x]);
//            }
//        }
//        return neighbours;
//    }
//
//    public List<Cell> getNeighboursPrune(Cell node, boolean moveDiag) {
//        if (node.parent == null) {
//            return getNeighbours(node, moveDiag);
//        } else {
//            List<Cell> neighbors = new ArrayList<>();
//            int px, py, dx, dy, x, y;
//            boolean walkX = false, walkY = false;
//            x = node.position.x;
//            y = node.position.y;
//            px = node.parent.position.x;
//            py = node.parent.position.y;
//            //get the normalized direction of travel
//            dx = (x - px) / Math.max(Math.abs(x - px), 1);
//            dy = (y - py) / Math.max(Math.abs(y - py), 1);
//            //search diagonally
//            if (dx != 0 && dy != 0 && moveDiag) {
//                if (walkable(x, y + dy)) {
//                    neighbors.add(cells[(y + dy)][x]);
//                    walkY = true;
//                }
//                if (walkable(x + dx, y)) {
//                    neighbors.add(cells[y][x + dx]);
//                    walkX = true;
//                }
//                if (walkX || walkY) {
//                    if (walkable(x + dx, y + dy))
//                        neighbors.add(cells[y + dy][x + dx]);
//                }
//                if (!walkable(x - dx, y) && walkY) {
//                    neighbors.add(cells[y + dy][x - dx]);
//                }
//                if (!walkable(x, y - dy) && walkX) {
//                    neighbors.add(cells[y - dy][x + dx]);
//                }
//            } else {
//                if (dx == 0) {
//                    if (walkable(x, y + dy)) {
//                        neighbors.add(cells[y + dy][x]);
//                        if (!walkable(x + 1, y)) {
//                            if (walkable(x + 1, y + dy))
//                                neighbors.add(cells[y + dy][x + 1]);
//                        }
//                        if (!walkable(x - 1, y)) {
//                            if (walkable(x - 1, y + dy))
//                                neighbors.add(cells[y + dy][x - 1]);
//                        }
//                    }
//                    if (!moveDiag) {
//                        if (walkable(x + 1, y)) {
//                            neighbors.add(cells[y][x + 1]);
//                        }
//                        if (walkable(x - 1, y)) {
//                            neighbors.add(cells[y][x - 1]);
//                        }
//                    }
//                } else {
//                    if (walkable(x + dx, y)) {
//                        neighbors.add(cells[y][x + dx]);
//                        if (!walkable(x, y + 1)) {
//                            if (walkable(x + dx, y + 1))
//                                neighbors.add(cells[y + 1][x + dx]);
//                        }
//                        if (!walkable(x, y - 1)) {
//                            if (walkable(x + dx, y - 1))
//                                neighbors.add(cells[y - 1][x + dx]);
//                        }
//                    }
//                    if (!moveDiag) {
//                        if (walkable(x, y + 1)) {
//                            neighbors.add(cells[y + 1][x]);
//                        }
//                        if (walkable(x, y - 1)) {
//                            neighbors.add(cells[y - 1][x]);
//                        }
//                    }
//                }
//            }
//            return neighbors;
//        }
//    }

    private List<Position> getNeighbours(Cell cell, boolean moveDiag) {
        List<Position> neighbours = new ArrayList<>();
        if (moveDiag) {
            for (Position dir : eightDir) {
                int x = cell.position.add(dir).x;
                int y = cell.position.add(dir).y;
                if (walkable(x, y))
                    neighbours.add(new Position(x, y));
            }
        } else {
            for (Position dir : fourDir) {
                //for (Position dir : eightDir) {
                int x = cell.position.add(dir).x;
                int y = cell.position.add(dir).y;
                if (walkable(x, y))
                    neighbours.add(new Position(x, y));
            }
        }
        return neighbours;
    }

    public List<Position> getNeighboursPrune(Cell node, boolean moveDiag) {
        if (node.parent == null) {
            return getNeighbours(node, moveDiag);
        } else {
            List<Position> neighbors = new ArrayList<>();
            int px, py, dx, dy, x, y;
            x = node.position.x;
            y = node.position.y;
            px = node.parent.position.x;
            py = node.parent.position.y;
            //get the normalized direction of travel
            dx = (x - px) / Math.max(Math.abs(x - px), 1);
            dy = (y - py) / Math.max(Math.abs(y - py), 1);
            //search diagonally
            if (dx != 0 && dy != 0 && moveDiag) {
                if (walkable(x, y + dy)) {
                    neighbors.add(new Position(x, y + dy));
                }
                if (walkable(x + dx, y)) {
                    neighbors.add(new Position(x + dx, y));
                }
                if (walkable(x + dx, y + dy)) {
                    neighbors.add(new Position(x + dx, y + dy));
                }
                if (!walkable(x - dx, y)) {
                    neighbors.add(new Position(x - dx, y + dy));
                }
                if (!walkable(x, y - dy)) {
                    neighbors.add(new Position(x + dx, y - dy));
                }
            } else {
                if (dx == 0) {
                    if (walkable(x, y + dy)) {
                        neighbors.add(new Position(x, y + dy));
                    }
                    if (!walkable(x + 1, y)) {
                        neighbors.add(new Position(x + 1, y + dy));
                    }
                    if (!walkable(x - 1, y)) {
                        neighbors.add(new Position(x - 1, y + dy));
                    }
//                    }
//                    if (!moveDiag) {
//                        if (walkable(x + 1, y)) {
//                            neighbors.add(cells[y][x + 1]);
//                        }
//                        if (walkable(x - 1, y)) {
//                            neighbors.add(cells[y][x - 1]);
//                        }
//                    }
                } else {
                    if (walkable(x + dx, y)) {
                        neighbors.add(new Position(x + dx, y));
                    }
                    if (!walkable(x, y + 1)) {
                        neighbors.add(new Position(x + dx, y + 1));
                    }
                    if (!walkable(x, y - 1)) {
                        neighbors.add(new Position(x + dx, y - 1));
                    }
//                    }
//                    if (!moveDiag) {
//                        if (walkable(x, y + 1)) {
//                            neighbors.add(cells[y + 1][x]);
//                        }
//                        if (walkable(x, y - 1)) {
//                            neighbors.add(cells[y - 1][x]);
//                        }
//                    }
                }
            }
            return neighbors;
        }
    }

    private double realDist(int[] pos1, int[] pos2) {
        int dX = Math.abs(pos2[0] - pos1[0]);
        int dY = Math.abs(pos2[1] - pos1[1]);
        return Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));
    }

    private double realDist(Position pos1, Position pos2) {
        int dX = Math.abs(pos2.x - pos1.x);
        int dY = Math.abs(pos2.y - pos1.y);
        return Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));
    }

    private double diagonalDist(int[] pos1, int[] pos2) {
        int dX = Math.abs(pos2[0] - pos1[0]);
        int dY = Math.abs(pos2[1] - pos1[1]);
        return (dX) + (rootTwo - 1) * Math.min(dX, dY);
    }

    private double diagonalDist(Position pos1, Position pos2) {
        int dX = Math.abs(pos2.x - pos1.x);
        int dY = Math.abs(pos2.y - pos1.y);
//        return (dX) + (rootTwo - 1) * Math.min(dX, dY);
        double F = rootTwo - 1;
        return (dX < dY) ? F * dX + dY : F * dY + dX;
    }

    private double manhatDist(Position pos1, Position pos2) {
        int dX = Math.abs(pos1.x - pos2.x);
        int dY = Math.abs(pos1.y - pos2.y);
        return (dX + dY);
    }

//    private double heuristic(Position curr, Position start, Position goal){
//        double heuristic = manhatDist(curr, goal);
//        double dx1 = curr.x - goal.x;
//        double dy1 = curr.y - goal.y;
//        double dx2 = start.x - goal.x;
//        double dy2 = start.y - goal.y;
//        double cross = Math.abs(dx1*dy2 - dx2*dy1);
//        heuristic += cross*0.001;
//        return heuristic;
//    }

    private List<Cell> rebuildPath(Map<Cell, Cell> cameFrom, Cell current) {
        path.clear();
        Cell startCell = cells[startPos.y][startPos.x];
        path.add(current);

        while (!cameFrom.get(current).equals(startCell)) {
            current = cameFrom.get(current);
            current.cellType = CellType.PATH;
            path.add(current);
        }
        return path;
    }

    private List<Cell> rebuildJPSPath(Cell current, Cell start) {
        List<Cell> path = new ArrayList<>();
        Position position = start.position;
        while (true) {
            if (current.parent != null && !current.parent.position.equals(position)) {
                current = current.parent;
                current.cellType = CellType.PATH;
                path.add(current);
            } else {
                path.add(start);
                return path;
            }
        }
    }

    public void resetPath() {
        for (Cell cell : path) {
            cell.cellType = CellType.FLOOR;
        }
        path.clear();
        for (int row = 0; row < gridHeight; row++) {
            for (int col = 0; col < gridWidth; col++) {
                cells[row][col].reset();
                if (cells[row][col].cellType == CellType.VISITED)
                    cells[row][col].cellType = CellType.FLOOR;
            }
        }
    }

    public List<Cell> checkPath() {
        Stopwatch timer = Stopwatch.createStarted();
        LIFOEntry.resetCount();
        final Map<Cell, Cell> cameFrom = new HashMap<>();
        final Queue<LIFOEntry<Cell>> frontier = new PriorityBlockingQueue<>();
        final Set<Cell> closedSet = new HashSet<>();
        Cell startNode = cells[startPos.y][startPos.x];
        frontier.add(new LIFOEntry(startNode));
        startNode.fScore = manhatDist(startPos, endPos);
        while (!frontier.isEmpty()) {
            LIFOEntry entry = frontier.poll();
            Cell current = (Cell) entry.getEntry();
            if (current.position.equals(endPos)) {
                long elapsed = timer.elapsed(TimeUnit.MILLISECONDS);
                timer.stop();
                return rebuildPath(cameFrom, current);
            }
            closedSet.add(current);
            List<Position> neighbours = getNeighbours(current, false);
            for (Position neighbourPos : neighbours) {
                Cell neighbour = cells[neighbourPos.y][neighbourPos.x];
                if (closedSet.contains(neighbour)) {
                    continue;
                }
                double gScore = current.gScore != 999999 ? (current.gScore + realDist(current.position, neighbour.position)) : (realDist(current.position, neighbour.position));
                if (gScore >= neighbour.gScore)
                    continue; //This isn't a better path than one found before.
                //neighbour.updateScore(gScore, diagonalDist(neighbour.position, endPos), current);
                neighbour.updateScore(gScore, manhatDist(neighbour.position, endPos), current);
                if (!frontier.contains(neighbour)) { //Not encountered this node before.
                    neighbour.cellType = CellType.VISITED;
                    frontier.add(new LIFOEntry<>(neighbour));
                }
                cameFrom.put(neighbour, current);


            }
        }
        long elapsed = timer.elapsed(TimeUnit.MILLISECONDS);
        timer.stop();
        return null;
    }

    public List<Cell> checkPathJPS(boolean moveDiag) {
        Stopwatch timer = Stopwatch.createStarted();
        LIFOEntry.resetCount();
        final Map<Cell, Cell> cameFrom = new HashMap<>();
        final Queue<LIFOEntry<Cell>> frontier = new PriorityBlockingQueue<>();
        final Set<Cell> closedSet = new HashSet<>();
        Cell startNode = cells[startPos.y][startPos.x];
        Cell goalNode = cells[endPos.y][endPos.x];
        frontier.add(new LIFOEntry(startNode));
        startNode.fScore = manhatDist(startPos, endPos);
        while (!frontier.isEmpty()) {
            LIFOEntry entry = frontier.poll();
            Cell current = (Cell) entry.getEntry();
            if (current.position.equals(endPos)) {
                long elapsed = timer.elapsed(TimeUnit.MILLISECONDS);
                timer.stop();
                return !moveDiag ? rebuildPath(cameFrom, current) : rebuildJPSPath(current, startNode); //rebuildPath(cameFrom, current);
            }
            closedSet.add(current);
            List<Position> neighbours = getNeighboursPrune(current, moveDiag);
            for (Position neighbourPos : neighbours) {
                Cell jumpNode = jumpNoObs(neighbourPos, current, goalNode);
                if (jumpNode != null && !closedSet.contains(jumpNode)) {
                    Cell neighbour = cells[neighbourPos.y][neighbourPos.x];
                    double gScore = current.gScore != 999999 ? (current.gScore + realDist(jumpNode.position, current.position)) : (realDist(jumpNode.position, current.position));
                    if (gScore >= neighbour.gScore)
                        continue; //This isn't a better path than one found before.
                    if (!moveDiag) {
                        jumpNode.updateScore(gScore, manhatDist(jumpNode.position, endPos), current);
                    } else {
//                        jumpNode.updateScore(gScore, manhatDist(jumpNode.position, endPos), null);
                        jumpNode.updateScore(gScore, diagonalDist(jumpNode.position, endPos), null);
                    }
                    LIFOEntry<Cell> jumpEntry =  new LIFOEntry<Cell>(jumpNode);
                    if (!frontier.contains(jumpEntry)) {
                        neighbour.cellType = CellType.VISITED;
                        frontier.add(jumpEntry);
                    }
                    cameFrom.put(jumpNode, current);
                }

            }
        }
        long elapsed = timer.elapsed(TimeUnit.MILLISECONDS);
        timer.stop();
        return null;
    }

    private Cell jumpNoObs(Position nodePos, Cell parent, Cell goal) {
        if (!walkable(nodePos.x, nodePos.y)) { //If space isn't walkable return null
            return null;
        }
        Cell node = cells[nodePos.y][nodePos.x];
        node.parent = parent;
        if (nodePos.equals(goal.position)) { //If end point, return it. Search over.
            return node;
        }
        //get the normalized direction of travel
        int x = node.position.x, y = node.position.y;
        int dx = (node.position.x - parent.position.x) / Math.max(Math.abs(node.position.x - parent.position.x), 1);
        int dy = (node.position.y - parent.position.y) / Math.max(Math.abs(node.position.y - parent.position.y), 1);
        if (dx != 0 && dy != 0) { //If x and y have changed we're moving diagonally. Check for forced neighbours
            if ((walkable(x - dx, y + dy) && !walkable(x - dx, y)) ||
                    (walkable(x + dx, y - dy) && !walkable(x, y - dy))) {  //if we find a forced neighbor here, we are on a jump point, and we return the current position
                return node;
            }
            if (jumpNoObs(new Position(x + dx, y), node, goal) != null || jumpAlwaysDiag(new Position(x, y + dy), node, goal) != null) {
                return node;
            }
        } else { //Check horizontal and vertical
            if (dx != 0) { //Moving in X
                if ((walkable(x + dx, y + 1) && !walkable(x, y + 1)) ||
                        (walkable(x + dx, y - 1) && !walkable(x, y - 1))) {
                    return node;
                }
            } else {
                if ((walkable(x + 1, y + dy) && !walkable(x + 1, y)) ||
                        (walkable(x - 1, y + dy) && !walkable(x - 1, y))) {
                    return node;
                }
            }
        }
        if (walkable(x + dx, y) || walkable(x, y + dy)) {
            return jumpNoObs(new Position(x + dx, y + dy), node, goal);
        } else {
            return null;
        }
//        return jumpNoObs(new Position(x + dx, y + dy), node, goal);
    }

    private Cell jumpAlwaysDiag(Position nodePos, Cell parent, Cell goal) {
        if (!walkable(nodePos.x, nodePos.y)) { //If space isn't walkable return null
            return null;
        }
        Cell node = cells[nodePos.y][nodePos.x];
        node.parent = parent;
        if (nodePos.equals(goal.position)) { //If end point, return it. Search over.
            return node;
        }
        //get the normalized direction of travel
        int x = node.position.x, y = node.position.y;
        int dx = (node.position.x - parent.position.x) / Math.max(Math.abs(node.position.x - parent.position.x), 1);
        int dy = (node.position.y - parent.position.y) / Math.max(Math.abs(node.position.y - parent.position.y), 1);

        if (dx != 0 && dy != 0) { //If x and y have changed we're moving diagonally. Check for forced neighbours
            if ((walkable(x - dx, y + dy) && !walkable(x - dx, y)) ||
                    (walkable(x + dx, y - dy) && !walkable(x, y - dy))) {  //if we find a forced neighbor here, we are on a jump point, and we return the current position
                return node;
            }
            if (jumpAlwaysDiag(new Position(x + dx, y), node, goal) != null || jumpAlwaysDiag(new Position(x, y + dy), node, goal) != null) {
                return node;
            }
        } else { //Check horizontal and vertical
            if (dx != 0) { //Moving in X
                if ((walkable(x + dx, y + 1) && !walkable(x, y + 1)) ||
                        (walkable(x + dx, y - 1) && !walkable(x, y - 1))) {
                    return node;
                }
            } else {
                if ((walkable(x + 1, y + dy) && !walkable(x + 1, y)) ||
                        (walkable(x - 1, y + dy) && !walkable(x - 1, y))) {
                    return node;
                }
            }
        }
        return jumpAlwaysDiag(new Position(x + dx, y + dy), node, goal);
    }
//Old jump impl
//    private Cell jump(Position nodePos, Cell parent, Cell goal, boolean moveDiag) {
//        if (!walkable(nodePos.x, nodePos.y)) { //If space isn't walkable return null
//            return null;
//        }
//        Cell node = cells[nodePos.y][nodePos.x];
//        node.parent = parent;
//        if (nodePos.equals(goal.position)) { //If end point, return it. Search over.
//            return node;
//        }
//        //get the normalized direction of travel
//        int x = node.position.x, y = node.position.y;
//        int dx = (node.position.x - parent.position.x) / Math.max(Math.abs(node.position.x - parent.position.x), 1);
//        int dy = (node.position.y - parent.position.y) / Math.max(Math.abs(node.position.y - parent.position.y), 1);
//
//        if (dx != 0 && dy != 0) { //If x and y have changed we're moving diagonally. Check for forced neighbours
//            if ((walkable(x - dx, y + dy) && !walkable(x - dx, y)) || //we are moving diagonally, don't check the parent, or our next diagonal step, but the other diagonals
//                    (walkable(x + dx, y - dy) && !walkable(x, y - dy))) {  //if we find a forced neighbor here, we are on a jump point, and we return the current position
//                return node;
//            }
//        } else { //Check horizontal and vertical
//            if (dx != 0) { //Moving in X
//                if (moveDiag) {
//                    if ((walkable(x + dx, y + 1) && !walkable(x, y + 1)) ||
//                            (walkable(x + dx, y - 1) && !walkable(x, y - 1))) {
//                        return node;
//                    }
//                } else {
//                    if (walkable(x + 1, y) || walkable(x - 1, y)) {
//                        return node;
//                    }
//                }
//            } else {
//                if (moveDiag) {
//                    if ((walkable(x + 1, y + dy) && !walkable(x + 1, y)) ||
//                            (walkable(x - 1, y + dy) && !walkable(x - 1, y))) {
//                        return node;
//                    }
//                } else {
//                    if (walkable(x, y + 1) || walkable(x, y - 1)) {
//                        return node;
//                    }
//                }
//            }
//        }
//        if (dx != 0 && dy != 0) { //Recursive horizontal/vertical search
//            Cell jumpHoz = jump(new Position(x + dx, y), node, goal, moveDiag);
//            if (jumpHoz != null) {
//                return node;
//            }
//            Cell jumpVert = jump(new Position(x, y + dy), node, goal, moveDiag);
//            if (jumpVert != null) {
//                return node;
//            }
//        }
//        if (moveDiag) {
//            if (walkable(x + dx, y) || walkable(x, y + dy)) {
//                return jump(new Position(x + dx, y + dy), node, goal, true);
//            } else
//                return null;
//        }
//        return null;
//    }
}
