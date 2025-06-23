import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.*;

public class MatchCards {
    class Card {
        String cardName;
        ImageIcon cardImageIcon;

        Card(String cardName, ImageIcon cardImageIcon) {
            this.cardName = cardName;
            this.cardImageIcon = cardImageIcon;
        }

        public String toString() {
            return cardName;
        }
    }

    
    int rows = 4;
    int columns = 5;
    int cardWidth = 90;
    int cardHeight = 128;

    ArrayList<Card> cardSet; //create a deck of cards with cardNames and cardImageIcons
    ImageIcon cardBackImageIcon;

    int boardWidth = columns * cardWidth; //5*90 = 450px
    int boardHeight = rows * cardHeight; //4*128 = 512px
    
    JFrame frame = new JFrame("Match Cards");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JPanel restartGamePanel = new JPanel();
    JButton restartButton = new JButton();

    int errorCount = 0;
    ArrayList<JButton> board;
    Timer hideCardTimer;
    boolean gameReady = false;
    int seconds = 0;
    JButton card1Selected;
    JButton card2Selected;
    Timer timer; 
    
    enum GameMode {
        COUNT_UP, COUNT_DOWN, FREE
    }

    //default
    String[] cardList = Theme.pokeList.clone();
    String selectedTheme= "Pokemon";
    GameMode selectedMode = GameMode.COUNT_UP;
    int countdownSeconds = 180;

    
    void play() {
        textPanel = new JPanel();
        boardPanel = new JPanel();
        restartGamePanel = new JPanel();

        setupCards();
        shuffleCards();

        frame.setLayout(new BorderLayout());
        frame.setSize(boardWidth, boardHeight);
        // frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //error text
        textLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        textLabel.setHorizontalAlignment(JLabel.LEFT);
        textLabel.setText("Errors: " + Integer.toString(errorCount));
        textLabel.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0)); // left margin

        textPanel.setLayout(new BorderLayout());
        textPanel.setPreferredSize(new Dimension(boardWidth, 30));
        textPanel.add(textLabel, BorderLayout.WEST);
        frame.add(textPanel, BorderLayout.NORTH);

        ///timer text
        JLabel timerLabel = new JLabel();
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        timerLabel.setHorizontalAlignment(JLabel.RIGHT);
        timerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 16)); // right margin
        textPanel.add(timerLabel, BorderLayout.EAST);

        if (selectedMode == GameMode.COUNT_DOWN) {
            seconds = countdownSeconds;
            timerLabel.setText("Time: " + seconds);
            timer = new Timer(1000, e -> {
                    seconds--;
                    timerLabel.setText("Time: " + seconds);
                    if (seconds <= 0) {
                        ((Timer)e.getSource()).stop();
                        JOptionPane.showMessageDialog(frame, "You Lose!!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                        gameReady = false;
                        restartButton.setEnabled(true);

                        restohome();
                    }
                });
        } else if (selectedMode == GameMode.COUNT_UP) {
            seconds = 0;
            timerLabel.setText("Time: " + seconds);
            timer = new Timer(1000, e -> {
                    seconds++;
                    timerLabel.setText("Time: " + seconds);
                });
        } else { 
            timerLabel.setText("Free Mode");
            timer = null;
        }

        ///card game board
        board = new ArrayList<JButton>();
        boardPanel.setLayout(new GridLayout(rows, columns));
        for (int i = 0; i < cardSet.size(); i++) {
            JButton tile = new JButton();
            tile.setPreferredSize(new Dimension(cardWidth, cardHeight));
            tile.setOpaque(true);
            tile.setIcon(cardSet.get(i).cardImageIcon);
            tile.setFocusable(false);
            tile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!gameReady) {
                        return;
                    }
                    JButton tile = (JButton) e.getSource();
                    if (tile.getIcon() == cardBackImageIcon) {
                        if (card1Selected == null) {
                            card1Selected = tile;
                            int index = board.indexOf(card1Selected);
                            card1Selected.setIcon(cardSet.get(index).cardImageIcon);
                        }
                        else if (card2Selected == null) {
                            card2Selected = tile;
                            int index = board.indexOf(card2Selected);
                            card2Selected.setIcon(cardSet.get(index).cardImageIcon);

                            if (card1Selected.getIcon() != card2Selected.getIcon()) {
                                errorCount += 1;
                                textLabel.setText("Errors: " + Integer.toString(errorCount));
                                hideCardTimer.start();
                            }
                            else {
                                // card matched
                                card1Selected = null;
                                card2Selected = null;
                                
                                checkWinCondition(timerLabel);
                            }
                        }
                    }
                }
            });
            board.add(tile);
            boardPanel.add(tile);
        }
        frame.add(boardPanel);

        ///restart game button
        restartButton.setFont(new Font("Arial", Font.PLAIN, 16));
        restartButton.setText("Restart Game");
        restartButton.setPreferredSize(null);
        restartButton.setFocusable(false);
        restartButton.setEnabled(false);
        restartButton.addActionListener(e -> {
                gameReady = false;
                restartButton.setEnabled(false);
                card1Selected = null;
                card2Selected = null;
                shuffleCards();

                for (int i = 0; i < board.size(); i++) {
                    board.get(i).setIcon(cardSet.get(i).cardImageIcon);
                    board.get(i).setEnabled(true); // 
                }

                errorCount = 0;
                textLabel.setText("Errors: " + Integer.toString(errorCount));
                hideCardTimer.start();

                if (timer != null) timer.stop();
                if (selectedMode == GameMode.COUNT_DOWN) {
                    seconds = countdownSeconds;
                    timerLabel.setText("Time: " + seconds);
                    
                } else if (selectedMode == GameMode.COUNT_UP) {
                    seconds = 0;
                    timerLabel.setText("Time: " + seconds);
                    
                } else {
                    timerLabel.setText("Free Mode");
                }
            });
        // add restart button
        restartGamePanel = new JPanel(new BorderLayout( 10, 0));
        restartGamePanel.add(restartButton, BorderLayout.CENTER);

        // Home button
        JButton homeButton = new JButton("Home");
        homeButton.setFont(new Font("Arial", Font.PLAIN, 12));
        homeButton.setPreferredSize(new Dimension(80, 28));
        restartGamePanel.add(homeButton, BorderLayout.EAST);

        frame.add(restartGamePanel, BorderLayout.SOUTH);
        // Action home button
        homeButton.addActionListener(e -> {
            restohome();
        });

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        //start game
        hideCardTimer = new Timer(1500, e -> hideCards());

        hideCardTimer.setRepeats(false);
        hideCardTimer.start();

    }

    void setupCards() {
        cardSet = new ArrayList<Card>();
        // Default source
        String source= "./img/poke/"; 
        String backcard= "./img/back"; 
        if (selectedTheme.equals("Pokemon")) {
            source= "./img/poke/"; 
            backcard= "./img/poke/back"; 
        } else if (selectedTheme.equals("One Piece")) {
            source= "./img/op/";
            backcard= "./img/op/back";
        } else if (selectedTheme.equals("Tarot") || selectedTheme.equals("Tarot2 (Hard)")) {
            source= "./img/tarot/";
            backcard= "./img/tarot/back2"; 
        }
        for (String cardName : cardList) {
            // System.out.println(source + cardName + ".jpg");
            //load each card image
            Image cardImg = new ImageIcon(getClass().getResource(source + cardName + ".jpg")).getImage();
            ImageIcon cardImageIcon = new ImageIcon(cardImg.getScaledInstance(cardWidth, cardHeight, java.awt.Image.SCALE_SMOOTH));

            //create card object and add to cardSet
            Card card = new Card(cardName, cardImageIcon);
            cardSet.add(card);
        }
        cardSet.addAll(cardSet);

        //load the back card image
        Image cardBackImg = new ImageIcon(getClass().getResource(backcard + ".jpg")).getImage();
        cardBackImageIcon = new ImageIcon(cardBackImg.getScaledInstance(cardWidth, cardHeight, java.awt.Image.SCALE_SMOOTH));
    }

    void shuffleCards() {
        //shuffle
        for (int i = 0; i < cardSet.size(); i++) {
            int j = (int) (Math.random() * cardSet.size());
            //swap
            Card temp = cardSet.get(i);
            cardSet.set(i, cardSet.get(j));
            cardSet.set(j, temp);
        }
        System.out.println(cardSet);
    }

    void hideCards() {
        if (gameReady && card1Selected != null && card2Selected != null) { //only flip 2 cards
            card1Selected.setIcon(cardBackImageIcon);
            card1Selected = null;
            card2Selected.setIcon(cardBackImageIcon);
            card2Selected = null;
        }
        else { //flip all cards face down
            for (int i = 0; i < board.size(); i++) {
                board.get(i).setIcon(cardBackImageIcon);
            }
            if ((selectedMode == GameMode.COUNT_DOWN || selectedMode == GameMode.COUNT_UP) && timer != null) {
                timer.start();
            }
            gameReady = true;
            restartButton.setEnabled(true);
        }
    }

    void checkWinCondition(JLabel timerLabel) {
        boolean allMatched = true;
        for (int i = 0; i < board.size(); i++) {
            if (board.get(i).getIcon() == cardBackImageIcon) {
                allMatched = false;
                break;
            }
        }
        if (allMatched) {
            gameReady = false;
            restartButton.setEnabled(true);
            if (timer != null) timer.stop();
            if (selectedMode == GameMode.COUNT_UP) {
                JOptionPane.showMessageDialog(frame, "Congrats! You win!\nFinish Time: " + seconds + " Seconds", "Winning!!", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "Congrats! You win!", "Winning!!", JOptionPane.INFORMATION_MESSAGE);
            }
            // Reset frame
            restohome();
        }
    }

    void restohome(){
        frame.dispose(); // close the current frame
        errorCount = 0;
        textLabel.setText("Errors: " + Integer.toString(errorCount));
        if (timer != null) timer.stop();

        // Create a new JFrame and set it up
        frame = new JFrame("Pokemon Match Cards");
        showHomeFrame();
    }

    void showHomeFrame() {
        JFrame homeFrame = new JFrame("Memory Cards - Home");
        homeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        homeFrame.setSize(320, 210); 
        homeFrame.setLocationRelativeTo(null); 
        homeFrame.setLayout(null);
        homeFrame.setResizable(false);
        
        // Mode
        JLabel modeLabel = new JLabel("Pilih Mode:");
        modeLabel.setBounds(20, 20, 100, 25); 
        String[] modeOptions = {"StopWatch", "Time Attack", "Free"};
        JComboBox<String> modeCombo = new JComboBox<>(modeOptions);
        modeCombo.setBounds(130, 20, 150, 25); 

        // Theme
        JLabel themeLabel = new JLabel("Pilih Tema:");
        themeLabel.setBounds(20, 60, 100, 25); 
        String[] themeOptions = {"Pokemon", "One Piece", "Tarot", "Tarot2 (Hard)"};
        JComboBox<String> themeCombo = new JComboBox<>(themeOptions);
        themeCombo.setBounds(130, 60, 150, 25);

        JButton playButton = new JButton("Play");
        playButton.setBounds(20, 110, 260, 40);

        // add components to the frame
        homeFrame.add(modeLabel);
        homeFrame.add(modeCombo);
        homeFrame.add(themeLabel);
        homeFrame.add(themeCombo);
        homeFrame.add(playButton);

        // Action Play
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int modeIdx = modeCombo.getSelectedIndex();
                if (modeIdx == 1) {
                    selectedMode = GameMode.COUNT_DOWN;
                } else if (modeIdx == 2) {
                    selectedMode = GameMode.FREE;
                } else {
                    selectedMode = GameMode.COUNT_UP;
                }

                selectedTheme = (String) themeCombo.getSelectedItem();
                if (selectedTheme.equals("Pokemon")) {
                    cardList = Theme.pokeList.clone(); // Use Pokemon cards
                } else if (selectedTheme.equals("One Piece")) {
                    cardList = Theme.characterCardList.clone(); // Use One Piece cards
                } else if (selectedTheme.equals("Tarot")) {
                    cardList = Arrays.copyOf(Theme.tarotCardList, 10); // Use 10 Tarot cards
                } else if (selectedTheme.equals("Tarot2 (Hard)")) {
                    cardList = Theme.tarotCardList.clone(); // Use all Tarot cards
                } else {
                    cardList = Theme.pokeList.clone(); // Default to poke cards
                }

                homeFrame.dispose(); 
                play(); 
            }
        });
        homeFrame.setVisible(true);
    }

}

// TODO: add limited error count, Hint, and other features
