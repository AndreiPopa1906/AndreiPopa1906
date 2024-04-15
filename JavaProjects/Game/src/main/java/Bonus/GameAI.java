package Bonus;

import Compusory.CoordPair;
import Compusory.GridGame;
import org.jgrapht.Graph;
import org.jgrapht.alg.matching.HopcroftKarpMaximumCardinalityBipartiteMatching;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.awt.*;
import java.util.*;
import java.util.List;

public class GameAI {
    private final GridGame gridGame;
    /**
     * Constructs a GameAI object with a reference to an existing GridGame instance.
     *
     * @param gridGame The GridGame instance this AI will operate on.
     */
    public GameAI(GridGame gridGame) {
        this.gridGame = gridGame;
    }

    /**
     * Determines if the current game state has a perfect matching using the Hopcroft-Karp algorithm.
     * This method is critical for the AI to decide on strategic moves.
     *
     * @return true if a perfect matching exists in the graph representation of the game's state, false otherwise.
     */
    public boolean hasPerfectMatching() {
        int[][] nodeStates = gridGame.getNodeStates();
        List<CoordPair> sticks = gridGame.getLoadedSticks();

        Graph<Integer, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);

        Set<Integer> partition1 = new HashSet<>();
        Set<Integer> partition2 = new HashSet<>();

        for (CoordPair stick : sticks) {
            int node1 = stick.getX1() * nodeStates[0].length + stick.getY1();
            int node2 = stick.getX2() * nodeStates[0].length + stick.getY2();

            graph.addVertex(node1);
            graph.addVertex(node2);

            if ((stick.getX1() + stick.getY1()) % 2 == 0) {
                partition1.add(node1);
                partition2.add(node2);
            } else {
                partition1.add(node2);
                partition2.add(node1);
            }

            graph.addEdge(node1, node2);
        }

        HopcroftKarpMaximumCardinalityBipartiteMatching<Integer, DefaultEdge> matchingAlgorithm =
                new HopcroftKarpMaximumCardinalityBipartiteMatching<>(graph, partition1, partition2);

        return matchingAlgorithm.getMatching().isPerfect();
    }

    /**
     * Executes the AI's turn by choosing and performing a move based on the game's current state.
     * The AI prioritizes winning moves, defensive moves to block the opponent, and, if neither are applicable,
     * the longest path move or a random valid move. The method updates the game state accordingly.
     */
    public void aiTurn() {
        boolean perfectMatchingExists = hasPerfectMatching();
        Point move = null;

        if (gridGame.getLastPlacedStone() != null) {
            move = findAdjacentMove(gridGame.getLastPlacedStone());
        }

        if (move == null) {
            if (!perfectMatchingExists && gridGame.getCurrentPlayer() == gridGame.getPlayer1()) {
                move = findWinningMove();
            } else if (perfectMatchingExists && gridGame.getCurrentPlayer() == gridGame.getPlayer2()) {
                move = findBestDefensiveMove();
            } else {
                move = findLongestPathMove();
                if (move == null) {
                    move = getRandomValidMove();
                }
            }
        }

        if (move != null) {
            gridGame.setNodeState(move.x, move.y, getCurrentPlayerStone());
            gridGame.setLastPlacedStone(move);
        }
        gridGame.switchPlayer();
        gridGame.refreshDrawingPanelWithCurrentState();
    }

    /**
     * Searches for a adjacent move for the AI.
     *
     * @param lastHumanMove the last move the human opponent did
     * @return a Point object that is near lastHumanMove
     */

    public Point findAdjacentMove(Point lastHumanMove) {
        List<CoordPair> sticks = gridGame.getLoadedSticks();
        List<Point> validMoves = new ArrayList<>();

        for (CoordPair stick : sticks) {
            if (stick.connects(lastHumanMove)) {
                Point otherEnd = stick.getOtherEnd(lastHumanMove.x, lastHumanMove.y);
                if (otherEnd != null && gridGame.getNodeState(otherEnd.x, otherEnd.y) == 0) {
                    validMoves.add(otherEnd);
                }
            }
        }

        if (!validMoves.isEmpty()) {
            return validMoves.get(0);
        }

        return null;
    }

    /**
     * Determines the stone (piece type) the current player is using in the game.
     *
     * @return An integer representing the current player's stone type.
     */
    private int getCurrentPlayerStone() {
        return gridGame.getCurrentPlayer() == gridGame.getPlayer1() ? 3 : 4;
    }

    /**
     * Searches for a winning move for the AI. A winning move is defined as a move that immediately concludes
     * the game in favor of the AI by fulfilling the winning condition without violating the game rules.
     *
     * @return The Point representing the winning move's coordinates, or null if no winning move is found.
     */
    public Point findWinningMove() {
        List<CoordPair> sticks = gridGame.getLoadedSticks();
        for (CoordPair stick : sticks) {
            if (stick.connects(gridGame.getLastPlacedStone())) {
                Point potentialWinningMove = stick.getOtherEnd(gridGame.getLastPlacedStone().x, gridGame.getLastPlacedStone().y);
                if (potentialWinningMove != null && gridGame.getNodeState(potentialWinningMove.x, potentialWinningMove.y) == 0 &&
                        isWinningMove(potentialWinningMove.x, potentialWinningMove.y, getCurrentPlayerStone(), sticks)) {
                    return potentialWinningMove;
                }
            }
        }
        return null;
    }


    /**
     * Finds a random valid move for the AI to make. This method is typically used when the AI has no
     * strategic moves available.
     *
     * @return A Point object representing the coordinates of the chosen valid move, or null if no valid moves are available.
     */

    public Point getRandomValidMove() {
        List<CoordPair> sticks = gridGame.getLoadedSticks();
        List<Point> validMoves = new ArrayList<>();
        for (CoordPair stick : sticks) {
            if (stick.connects(gridGame.getLastPlacedStone())) {
                Point otherEnd = stick.getOtherEnd(gridGame.getLastPlacedStone().x, gridGame.getLastPlacedStone().y);
                if (otherEnd != null && gridGame.getNodeState(otherEnd.x, otherEnd.y) == 0) {
                    validMoves.add(otherEnd);
                }
            }
        }

        if (!validMoves.isEmpty()) {
            Random rand = new Random();
            return validMoves.get(rand.nextInt(validMoves.size()));
        }

        return null;
    }


    /**
     * Identifies the best defensive move to prevent the opponent from winning. The method simulates the opponent's
     * winning move and chooses a move to block it.
     *
     * @return A Point representing the defensive move's coordinates, or null if it cannot find a specific defensive move.
     */
    public Point findBestDefensiveMove() {
        List<CoordPair> sticks = gridGame.getLoadedSticks();
        for (CoordPair stick : sticks) {
            if (stick.connects(gridGame.getLastPlacedStone())) {
                Point potentialDefensiveMove = stick.getOtherEnd(gridGame.getLastPlacedStone().x, gridGame.getLastPlacedStone().y);
                if (potentialDefensiveMove != null && gridGame.getNodeState(potentialDefensiveMove.x, potentialDefensiveMove.y) == 0) {
                    gridGame.setNodeState(potentialDefensiveMove.x, potentialDefensiveMove.y, getCurrentPlayerStone());
                    if (!isWinningMove(potentialDefensiveMove.x, potentialDefensiveMove.y, getCurrentPlayerStone(), sticks)) {
                        gridGame.setNodeState(potentialDefensiveMove.x, potentialDefensiveMove.y, 0);
                        return potentialDefensiveMove;
                    }
                    gridGame.setNodeState(potentialDefensiveMove.x, potentialDefensiveMove.y, 0);
                }
            }
        }
        return null;
    }

    /**
     * Determines if placing a stone at a specified location would result in a winning move for the player.
     *
     * @param x           The x-coordinate of the move.
     * @param y           The y-coordinate of the move.
     * @param playerStone The stone type of the player making the move.
     * @param sticks      The list of CoordPairs representing the game's sticks.
     * @return true if the move is a winning move, false otherwise.
     */
    private boolean isWinningMove(int x, int y, int playerStone, List<CoordPair> sticks) {
        gridGame.setNodeState(x, y, playerStone);

        boolean isWinning = true;
        for (CoordPair stick : sticks) {
            if (stick.isConnectedTo(x, y) && gridGame.getNodeState(stick.getOtherEnd(x, y).x, stick.getOtherEnd(x, y).y) == 0) {
                isWinning = false;
                break;
            }
        }

        gridGame.setNodeState(x, y, 0);

        return isWinning;
    }

    /**
     * Finds the longest path starting from a specific point or the last placed stone. This method is used
     * when the AI decides to make a strategic move based on the length of the path connected to the move.
     *
     * @return The Point at the beginning of the longest path found, or null if no path is found.
     */

    private Point findLongestPathMove() {
        int[][] nodeStates = gridGame.getNodeStates();
        Point lastPlacedStone = gridGame.getLastPlacedStone();
        List<CoordPair> sticks = gridGame.getLoadedSticks();

        Map<Point, List<Point>> movesMap = new HashMap<>();

        for (CoordPair stick : sticks) {
            Point p1 = new Point(stick.getX1(), stick.getY1());
            Point p2 = new Point(stick.getX2(), stick.getY2());
            if (nodeStates[p1.x][p1.y] == 0 && nodeStates[p2.x][p2.y] == 0) {
                movesMap.computeIfAbsent(p1, k -> new ArrayList<>()).add(p2);
                movesMap.computeIfAbsent(p2, k -> new ArrayList<>()).add(p1);
            }
        }

        Set<Point> visited = new HashSet<>();
        List<Point> longestPath = new ArrayList<>();
        if (lastPlacedStone != null && movesMap.containsKey(lastPlacedStone)) {
            dfsFindLongestPath(lastPlacedStone, movesMap, visited, new ArrayList<>(), longestPath);
        } else {
            for (Point start : movesMap.keySet()) {
                visited.clear();
                dfsFindLongestPath(start, movesMap, visited, new ArrayList<>(), longestPath);
                if (!longestPath.isEmpty()) {
                    break;
                }
            }
        }

        return longestPath.isEmpty() ? null : longestPath.get(0);
    }

    /**
     * Helper method for findLongestPathMove. Performs a depth-first search (DFS) to find the longest path
     * from a starting point in the moves map. Updates the longestPath list with the longest path found during
     * the search.
     *
     * @param current      The current point in the DFS.
     * @param movesMap     A map where each point is a key to a list of points to which it is connected.
     * @param visited      A set containing points that have been visited in the current path.
     * @param currentPath  The current path being explored in the DFS.
     * @param longestPath  The longest path found so far. This list is updated if a longer path is found.
     */

    private void dfsFindLongestPath(Point current, Map<Point, List<Point>> movesMap, Set<Point> visited, List<Point> currentPath, List<Point> longestPath) {
        visited.add(current);
        currentPath.add(current);

        if (currentPath.size() > longestPath.size()) {
            longestPath.clear();
            longestPath.addAll(currentPath);
        }

        for (Point neighbor : movesMap.getOrDefault(current, new ArrayList<>())) {
            if (!visited.contains(neighbor)) {
                dfsFindLongestPath(neighbor, movesMap, visited, currentPath, longestPath);
            }
        }

        visited.remove(current);
        currentPath.remove(currentPath.size() - 1);
    }
}

