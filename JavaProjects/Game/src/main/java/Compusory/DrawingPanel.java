package Compusory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public class DrawingPanel extends JPanel {
    private final int gridWidth;
    private final int gridHeight;
    private List<CoordPair> sticks;
    private final int padding = 20;
    private final GridGame gridGame;

    /**
     * Constructs a DrawingPanel object with specified grid dimensions and initializes the game with random sticks.(without sticks)
     *
     * @param gridGame  The instance of GridGame this panel is associated with.
     * @param gridWidth The width of the grid.
     * @param gridHeight The height of the grid.
     */

    public DrawingPanel(GridGame gridGame, int gridWidth, int gridHeight) {
        this(gridGame, gridWidth, gridHeight, initializeGameWithRandomSticks(gridWidth, gridHeight));
    }


    /**
     * Constructs a DrawingPanel object with specified grid dimensions and a list of sticks.(with pewdifined sticks)
     *
     * @param gridGame  The instance of {@code GridGame} this panel is associated with.
     * @param gridWidth The width of the grid.
     * @param gridHeight The height of the grid.
     * @param sticks    The list of CoordPair objects representing the sticks' positions and types.
     */
    public DrawingPanel(GridGame gridGame, int gridWidth, int gridHeight, List<CoordPair> sticks) {
        this.gridGame = gridGame;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.sticks = sticks != null ? sticks : new ArrayList<>();
        this.setPreferredSize(new Dimension(400, 400));
        this.setBackground(Color.WHITE);
        setupMouseListener();
    }


    /**
     * Initializes the game with random sticks based on the grid dimensions and a specified probability.
     *
     * @param gridWidth  The width of the grid.
     * @param gridHeight The height of the grid.
     * @return A list of CoordPair objects representing the sticks' positions.
     */
    private static List<CoordPair> initializeGameWithRandomSticks(int gridWidth, int gridHeight) {
        List<CoordPair> sticks = new ArrayList<>();
        Random rand = new Random();
        double probabilityToPlaceStick = 0.5;

        for (int y = 0; y < gridHeight; y++) {
            for (int x = 0; x < gridWidth - 1; x++) {
                if (rand.nextDouble() < probabilityToPlaceStick) {
                    sticks.add(new CoordPair(x, y, x + 1, y, 1));
                }
            }
        }

        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight - 1; y++) {
                if (rand.nextDouble() < probabilityToPlaceStick) {
                    sticks.add(new CoordPair(x, y, x, y + 1, 2));
                }
            }
        }

        return sticks;
    }

    /**
     * Sets up a mouse listener for the panel to handle stone placement based on user clicks.
     */
    private void setupMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Mouse clicked at: " + e.getX() + ", " + e.getY());

                int cellWidth = (getWidth() - 2 * padding) / gridWidth;
                int cellHeight = (getHeight() - 2 * padding) / gridHeight;

                int x = e.getX() / cellWidth;
                int y = e.getY() / cellHeight;

                if (x < 0 || x >= gridWidth || y < 0 || y >= gridHeight) {
                    System.out.println("Click outside of valid grid area.");
                    return;
                }

                if (gridGame.getCurrentPlayer() == gridGame.getPlayer1()) {
                    if (x >= 0 && x < gridWidth && y >= 0 && y < gridHeight) {
                        placeStone(x, y);
                    }
                }

            }
        });
    }

    /**
     * Overrides the paintComponent method to draw the grid, stones, and sticks.
     *
     * @param g The Graphics object used for drawing operations.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        int usableWidth = panelWidth - (2 * padding);
        int usableHeight = panelHeight - (2 * padding);

        int cellWidth = usableWidth / gridWidth;
        int cellHeight = usableHeight / gridHeight;

        int stoneRadius = Math.min(cellWidth, cellHeight) / 4;
        int stoneDiameter = stoneRadius * 2;

        g.setColor(Color.LIGHT_GRAY);

        for (int i = 0; i <= gridWidth; i++) {
            int x = padding + i * cellWidth;
            g.drawLine(x, padding, x, padding + usableHeight);
        }
        for (int j = 0; j <= gridHeight; j++) {
            int y = padding + j * cellHeight;
            g.drawLine(padding, y, padding + usableWidth, y);
        }

        for (int i = 0; i <= gridWidth; i++) {
            for (int j = 0; j <= gridHeight; j++) {
                int x = padding + i * cellWidth;
                int y = padding + j * cellHeight;
                int stoneX = x - stoneRadius;
                int stoneY = y - stoneRadius;

                g.drawOval(stoneX, stoneY, stoneDiameter, stoneDiameter);
            }
        }


        for (CoordPair stick : sticks) {
            g.setColor(Color.BLACK);
            if (stick.type == 1) {
                int startX = padding + stick.x1 * cellWidth;
                int startY = padding + stick.y1 * cellHeight;
                int endX = startX + cellWidth;
                g.fillRect(startX, startY - 2, cellWidth, 4);
                //g.drawLine(startX, startY, endX, startY);
            } else if (stick.type == 2) {
                int startX = padding + stick.x1 * cellWidth;
                int startY = padding + stick.y1 * cellHeight;
                int endY = startY + cellHeight;
                g.fillRect(startX - 2, startY, 4, cellWidth);
                //g.drawLine(startX, startY, startX, endY);
            }
        }

        var nodeStates = gridGame.getNodeStates();

        for (int i = 0; i < nodeStates.length; i++) {
            for (int j = 0; j < nodeStates[i].length; j++) {
                int state = nodeStates[i][j];
                if (state == 3 || state == 4) {
                    int x = padding + i * cellWidth;
                    int y = padding + j * cellHeight;
                    int stoneX = x - stoneRadius;
                    int stoneY = y - stoneRadius;

                    g.setColor(state == 3 ? Color.RED : Color.BLUE);
                    g.fillOval(stoneX, stoneY, stoneDiameter, stoneDiameter);
                }
            }
        }

    }

    /**
     * Places a stone on the grid at the specified coordinates.
     *
     * @param x The x-coordinate where the stone is to be placed.
     * @param y The y-coordinate where the stone is to be placed.
     */
    public void placeStone(int x, int y) {
        System.out.println("Placing stone at: " + x + ", " + y);

        if (x >= 0 && x < gridWidth && y >= 0 && y < gridHeight) {
            if (isValidLocation(x, y)) {
                int state = gridGame.getCurrentPlayer() == gridGame.getPlayer1() ? 3 : 4;
                gridGame.setNodeState(x, y, state);
                gridGame.setLastPlacedStone(new Point(x, y));
                gridGame.playerMoved();
                //gridGame.switchPlayer();
                repaint();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid move. You must place next to the last stone.", "Invalid Move", JOptionPane.WARNING_MESSAGE);
            }
        }

        if (gridGame.isGameOver()) {
            JOptionPane.showMessageDialog(this, "Game over! " + gridGame.getCurrentPlayer().getName() + " cannot move.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Checks if the specified location is a valid placement for a stone.
     *
     * @param x The x-coordinate to check.
     * @param y The y-coordinate to check.
     * @return true if the location is valid for placing a stone, {@code false} otherwise.
     */
    public boolean isValidLocation(int x, int y) {
        if (!(gridGame.getNodeState(x, y) == 0)) {
            return false;
        }

        if (gridGame.getLastPlacedStone() == null) {
            return true;
        }

        Point lastStone = gridGame.getLastPlacedStone();
        int lastX = lastStone.x;
        int lastY = lastStone.y;

        for (CoordPair stick : sticks) {
            if (stick.type == 1) {
                if ((stick.x1 == lastX && stick.y1 == lastY && stick.x2 == x && stick.y2 == y) ||
                        (stick.x2 == lastX && stick.y2 == lastY && stick.x1 == x && stick.y1 == y)) {
                    return true;
                }
            } else if (stick.type == 2) {
                if ((stick.x1 == lastX && stick.y1 == lastY && stick.x2 == x && stick.y2 == y) ||
                        (stick.x2 == lastX && stick.y2 == lastY && stick.x1 == x && stick.y1 == y)) {
                    return true;
                }
            }
        }

        return false;
    }

    public List<CoordPair> getSticks() {
        return sticks;
    }

}



