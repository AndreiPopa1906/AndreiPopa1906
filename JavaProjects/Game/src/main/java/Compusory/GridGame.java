package Compusory;

import Bonus.GameAI;
import Homework.Player;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * @author Popa Andrei
 */
public class GridGame extends JFrame{
    private JPanel mainPanel;
    private JPanel configPanel;
    private JPanel controlPanel;
    private DrawingPanel drawingPanel;
    private JButton createButton;
    private JButton loadButton;
    private JButton saveButton;
    private JButton exitButton;
    private JComboBox<Integer> gridWidthComboBox;
    private JComboBox<Integer> gridHeightComboBox;
    /**
     * Game state
     */
    private int[][] nodeStates;

    //-----Homework------

    private Player currentPlayer;
    private Player player1;
    private Player player2;
    private Point lastPlacedStone = null;

    private List<CoordPair> loadedSticks;

    //-----Homework-------

    //-----Bonus--------

    /**
     * AI component for the game
     */
    private GameAI gameAI;

    /**
     * Initializes the game with a default grid size and sets up the GUI components.
     */
    public GridGame() {
        super("Grid Game");

        initializeNodeStates(10, 10);

        player1 = new Player("Player 1", Color.RED);
        player2 = new Player("Player 2", Color.BLUE);
        currentPlayer = player1;

        List<CoordPair> initialSticks = initializeGameWithRandomSticks(10, 10);
        loadedSticks = new ArrayList<>(initialSticks); 

        this.gameAI = new GameAI(this);

        createMainPanel();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
    }

    /**
     * Starts a new game with selected grid dimensions from the configuration panel.
     */
    private void startNewGame() {
        int gridWidth = (int) gridWidthComboBox.getSelectedItem();
        int gridHeight = (int) gridHeightComboBox.getSelectedItem();

        initializeNodeStates(gridWidth, gridHeight);
        loadedSticks = initializeGameWithRandomSticks(gridWidth, gridHeight);

        if (drawingPanel != null) {
            mainPanel.remove(drawingPanel);
        }

        currentPlayer = player1;
        lastPlacedStone = null;

        drawingPanel = new DrawingPanel(this, gridWidth, gridHeight, loadedSticks);
        mainPanel.add(drawingPanel, BorderLayout.CENTER);

        mainPanel.revalidate();
        mainPanel.repaint();
    }


    /**
     * Creates and arranges the main panel and its components, including configuration, control, and drawing panels
     */

    private void createMainPanel() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        createConfigPanel();
        createControlPanel();
        createDrawingPanel();

        mainPanel.add(configPanel, BorderLayout.NORTH);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        if (nodeStates == null || drawingPanel == null) {
            startNewGame();
        } else {
            mainPanel.add(drawingPanel, BorderLayout.CENTER);
        }

        this.add(mainPanel);
    }

    /**
     * Constructs the configuration panel, allowing users to select grid dimensions and create a new grid
     */

    private void createConfigPanel() {
        configPanel = new JPanel();

        JLabel gridWidthLabel = new JLabel("Grid width:");
        gridWidthComboBox = new JComboBox<>(new Integer[]{10, 20, 30, 40, 50});
        gridWidthComboBox.setSelectedIndex(0);

        JLabel gridHeightLabel = new JLabel("Grid height:");
        gridHeightComboBox = new JComboBox<>(new Integer[]{10, 20, 30, 40, 50});
        gridHeightComboBox.setSelectedIndex(0);

        createButton = new JButton("Create");
        createButton.addActionListener(e -> startNewGame());

        configPanel.add(gridWidthLabel);
        configPanel.add(gridWidthComboBox);
        configPanel.add(gridHeightLabel);
        configPanel.add(gridHeightComboBox);
        configPanel.add(createButton);
    }


    /**
     * Initializes the states of the nodes in the grid with specified dimensions
     *
     * @param gridWidth  The width of the grid.
     * @param gridHeight The height of the grid.
     */
    public void initializeNodeStates(int gridWidth, int gridHeight) {
        nodeStates = new int[gridHeight + 1][gridWidth + 1];
        for (int i = 0; i <= gridHeight; i++) {
            Arrays.fill(nodeStates[i], 0);
        }
    }
    public void setNodeState(int x, int y, int state) {
        if (x >= 0 && x < nodeStates.length && y >= 0 && y < nodeStates[x].length) {
            nodeStates[x][y] = state;
        } else {
            System.out.println("Index out of bounds: x=" + x + ", y=" + y);
        }
    }

    public int getNodeState(int x, int y) {
        if (x >= 0 && x < nodeStates.length && y >= 0 && y < nodeStates[x].length) {
            return nodeStates[x][y];
        } else {
            System.out.println("Index out of bounds: x=" + x + ", y=" + y);
            return -1;
        }
    }

    /**
     * Creates the control panel with buttons for loading, saving, and exiting the game
     */
    private void createControlPanel() {
        controlPanel = new JPanel();

        JButton saveImageButton = new JButton("Save Image");
        saveImageButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save as PNG");
            fileChooser.setFileFilter(new FileNameExtensionFilter("PNG Images", "png"));
            int option = fileChooser.showSaveDialog(GridGame.this);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".png")) {
                    file = new File(file.getParentFile(), file.getName() + ".png");
                }
                exportPanelToImage(drawingPanel, file);
            }
        });

        saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Game State");
            int userSelection = fileChooser.showSaveDialog(GridGame.this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                saveGameStateToFile(fileToSave);
            }
        });

        loadButton = new JButton("Load");
        loadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Load Game State");
            int userSelection = fileChooser.showOpenDialog(GridGame.this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToLoad = fileChooser.getSelectedFile();
                try {
                    loadGameStateFromFile(fileToLoad);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
                mainPanel.remove(drawingPanel);
                drawingPanel = new DrawingPanel(GridGame.this, nodeStates[0].length - 1, nodeStates.length - 1);
                mainPanel.add(drawingPanel, BorderLayout.CENTER);
                mainPanel.revalidate();
                mainPanel.repaint();
            }
        });



//        loadButton = new JButton("Load");
//        loadButton.addActionListener(e -> {
//            JFileChooser fileChooser = new JFileChooser();
//            int option = fileChooser.showOpenDialog(GridGame.this);
//            if (option == JFileChooser.APPROVE_OPTION) {
//                File file = fileChooser.getSelectedFile();
//                loadGame(file);
//            }
//        });
//
//        saveButton = new JButton("Save");
//
//        saveButton.addActionListener(e -> {
//            JFileChooser fileChooser = new JFileChooser();
//            fileChooser.setDialogTitle("Save as PNG");
//            fileChooser.setFileFilter(new FileNameExtensionFilter("PNG Images", "png"));
//            int option = fileChooser.showSaveDialog(GridGame.this);
//            if (option == JFileChooser.APPROVE_OPTION) {
//                File file = fileChooser.getSelectedFile();
//                if (!file.getName().toLowerCase().endsWith(".png")) {
//                    file = new File(file.getParentFile(), file.getName() + ".png");
//                }
//                exportPanelToImage(drawingPanel, file);
//            }
//        });

        // Save ActionListener from homework

//        saveButton.addActionListener(e -> {
//            JFileChooser fileChooser = new JFileChooser();
//            int option = fileChooser.showSaveDialog(GridGame.this);
//            if (option == JFileChooser.APPROVE_OPTION) {
//                File file = fileChooser.getSelectedFile();
//                saveGame(file);
//            }
//        });

        exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));

        controlPanel.add(saveImageButton);
        controlPanel.add(loadButton);
        controlPanel.add(saveButton);
        controlPanel.add(exitButton);
    }

    /**
     * Creates and arranges the drawing panel, where the grid is visually represented
     */
    private void createDrawingPanel() {
        int gridWidth = (int) gridWidthComboBox.getSelectedItem();
        int gridHeight = (int) gridHeightComboBox.getSelectedItem();
        if (drawingPanel != null) {
            mainPanel.remove(drawingPanel);
        }
        drawingPanel = new DrawingPanel(this, gridWidth, gridHeight);
        mainPanel.add(drawingPanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }


    /**
     * Initializes the game with random sticks based on the grid dimensions.
     *
     * @param gridWidth  The width of the grid.
     * @param gridHeight The height of the grid.
     * @return A list of CoordPair objects representing the sticks' positions.
     */
    private List<CoordPair> initializeGameWithRandomSticks(int gridWidth, int gridHeight) {
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
     * Checks if the game is over based on the current state.
     *
     * @return true if the game is over, false otherwise.
     */
    public boolean isGameOver() {
        if (lastPlacedStone == null) {
            return false;
        }

        int lastX = lastPlacedStone.x;
        int lastY = lastPlacedStone.y;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int newX = lastX + dx;
                int newY = lastY + dy;

                if (dx == 0 && dy == 0) {
                    continue;
                }

                if (newX >= 0 && newX < nodeStates.length && newY >= 0 && newY < nodeStates[newX].length) {
                    if (drawingPanel.isValidLocation(newX, newY)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }


    //-------------Homework------------------

    /**
     * Exports the current game board to an image file.
     *
     * @param panel The drawing panel to export.
     * @param file  The file to save the exported image to.
     */
    public void exportPanelToImage(DrawingPanel panel, File file) {
        BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = image.createGraphics();
        panel.paint(graphics2D);
        try {
            ImageIO.write(image, "png", file);
            JOptionPane.showMessageDialog(this, "Game board exported successfully to " + file.getAbsolutePath(), "Export Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while exporting the game board.", "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //---------Bonus--------------

    /**
     * Saves the current game state to a file. This includes player information, the grid state, and the current game configuration.
     *
     * @param file The file to save the game state to.
     */
    private void saveGameStateToFile(File file) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(nodeStates);
            out.writeObject(player1);
            out.writeObject(player2);
            out.writeObject(currentPlayer);
            out.writeObject(lastPlacedStone);
            out.writeObject(drawingPanel.getSticks());
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Could not save the game state.", "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Loads a game state from a file. This restores the grid state, player information, and current game configuration.
     *
     * @param file The file from which to load the game state.
     * @throws IOException If there is an error reading the file.
     * @throws ClassNotFoundException If the class of a serialized object cannot be found.
     */
    private void loadGameStateFromFile(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            nodeStates = (int[][]) in.readObject();
            player1 = (Player) in.readObject();
            player2 = (Player) in.readObject();
            currentPlayer = (Player) in.readObject();
            lastPlacedStone = (Point) in.readObject();
            loadedSticks = (List<CoordPair>) in.readObject();
        }
    }

    /**
     * Refreshes the drawing panel to reflect the current game state. This method is typically called after loading a game.
     */
    public void refreshDrawingPanelWithCurrentState() {
        if (drawingPanel != null) {
            drawingPanel.repaint();
        }

        if (isGameOver()) {
            JOptionPane.showMessageDialog(this,
                    "Game Over! Winner: " + (currentPlayer == player1 ? player2.getName() : player1.getName()),
                    "Game Over",
                    JOptionPane.INFORMATION_MESSAGE);

            createButton.setEnabled(false);
            saveButton.setEnabled(false);
            loadButton.setEnabled(false);

            int playAgain = JOptionPane.showConfirmDialog(this,
                    "Would you like to play again?",
                    "Play Again?",
                    JOptionPane.YES_NO_OPTION);

            if (playAgain == JOptionPane.YES_OPTION) {
                startNewGame();
            } else {
                System.exit(0);
            }
        }
    }

    public int[][] getNodeStates() {
        return nodeStates;
    }
    public void setLastPlacedStone(Point lastPlacedStone) {
        this.lastPlacedStone = lastPlacedStone;
    }
    public void switchPlayer() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
    }
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    public Player getPlayer1() {
        return player1;
    }
    public Player getPlayer2() {
        return player2;
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GridGame());
    }
    public List<CoordPair> getLoadedSticks() {
        return loadedSticks;
    }
    public Point getLastPlacedStone() {
        return lastPlacedStone;
    }

    //-------Bonus--------------

    /**
     * Updates the game state when a player has made a move. This method checks if the game is over and switches the active player.
     */
    public void playerMoved() {
        if (!isGameOver()) {
            switchPlayer();
            if (currentPlayer == player2) {
                gameAI.aiTurn();
            }
            if (isGameOver()) {
                endGame();
            }
        } else {
            endGame();
        }
    }

    /**
     * Ends the current game, declaring the winner and asking if the players would like to start a new game.
     */
    public void endGame() {
        String winnerName = (currentPlayer == player1) ? player2.getName() : player1.getName();
        JOptionPane.showMessageDialog(this,
                "Game Over! Winner: " + winnerName,
                "Game Over",
                JOptionPane.INFORMATION_MESSAGE);

        int response = JOptionPane.showConfirmDialog(this,
                "Would you like to play again?",
                "Play Again?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {
            startNewGame();
        } else {
            JOptionPane.showMessageDialog(this, "Thank you for playing!");
            dispose();
            System.exit(0);
        }
    }

    /**
     * Refreshes the drawing panel with the data loaded from a file. This method is called after loading a game state to update the visual representation.
     */

    private void refreshDrawingPanelWithLoadedData() {
        if (drawingPanel != null) {
            mainPanel.remove(drawingPanel);
        }
        if (isGameOver()) {
            JOptionPane.showMessageDialog(this,
                    "Game Over! Winner: " + (currentPlayer == player1 ? player2.getName() : player1.getName()),
                    "Game Over",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        drawingPanel = new DrawingPanel(this, nodeStates[0].length - 1, nodeStates.length - 1, loadedSticks);
        mainPanel.add(drawingPanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void loadGame() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Game State");
        int userSelection = fileChooser.showOpenDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            try {
                loadGameStateFromFile(fileToLoad);
                refreshDrawingPanelWithLoadedData();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Could not load the game. " + e.getMessage(), "Load Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveGame(File file) {
        try (PrintWriter out = new PrintWriter(file)) {
            int gridWidth = (int) gridWidthComboBox.getSelectedItem();
            int gridHeight = (int) gridHeightComboBox.getSelectedItem();

            for (int i = 0; i < gridWidth; i++) {
                for (int j = 0; j < gridHeight; j++) {
                    int state = getNodeState(i, j);
                    out.println(i + "," + j + "," + state);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while saving the game.", "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }




}

