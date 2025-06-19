import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
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

    String[] pokeList = { //track cardNames
        "darkness",
        "double",
        "fairy",
        "fighting",
        "fire",
        "grass",
        "lightning",
        "metal",
        "psychic",
        "water"
    };
    // If you need the second set, declare it separately:
    String[] characterCardList = {
        "char_brook_b",
        "char_chopper_b",
        "char_franky_b",
        "char_luffy_b",
        "char_nami_b",
        "char_robin_b",
        "char_sanji_b",
        "char_zoro_b",
        "char_usopp_b",
        "char_jinbe_b"
    };

    String[] cardList = pokeList.clone(); // default

    int rows = 4;
    int columns = 5;
    int cardWidth = 90;
    int cardHeight = 128;

    ArrayList<Card> cardSet; //create a deck of cards with cardNames and cardImageIcons
    ImageIcon cardBackImageIcon;

    int boardWidth = columns * cardWidth; //5*128 = 640px
    int boardHeight = rows * cardHeight; //4*90 = 360px

    JFrame frame = new JFrame("Pokemon Match Cards");
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

    GameMode selectedMode = GameMode.COUNT_UP;
    int countdownSeconds = 5; // default seconds for COUNT_DOWN mode

    
    void play() {
        textPanel = new JPanel();
        boardPanel = new JPanel();
        restartGamePanel = new JPanel();

        // showHomeFrame();
        // showGameModeDialog();
        setupCards();
        shuffleCards();

        frame.setLayout(new BorderLayout());
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
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
                        // Reset frame & back to play()
                        restohome();
                    }
                });
            // timer.start();
        } else if (selectedMode == GameMode.COUNT_UP) {
            seconds = 0;
            timerLabel.setText("Time: " + seconds);
            timer = new Timer(1000, e -> {
                    seconds++;
                    timerLabel.setText("Time: " + seconds);
                });
            // timer.start();
        } else { // FREE MODE
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
                                // caard matched
                                // Disable the matched cards
                                // card1Selected.setEnabled(false);
                                // card2Selected.setEnabled(false);
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
        restartButton.setPreferredSize(new Dimension(boardWidth-80 , 30));
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
                    // timer.start();
                } else if (selectedMode == GameMode.COUNT_UP) {
                    seconds = 0;
                    timerLabel.setText("Time: " + seconds);
                    // timer.start();
                } else {
                    timerLabel.setText("Free Mode");
                }
            });
        // Panel tombol restart dan home
        restartGamePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        restartGamePanel.add(restartButton);

        // Tambahkan home button kecil
        JButton homeButton = new JButton("Home");
        homeButton.setFont(new Font("Arial", Font.PLAIN, 12));
        homeButton.setPreferredSize(new Dimension(80, 28));
        restartGamePanel.add(homeButton);

        frame.add(restartGamePanel, BorderLayout.SOUTH);
        // Action home button
        homeButton.addActionListener(e -> {
            restohome();
        });

        frame.pack();
        frame.setVisible(true);
        
        //start game
        hideCardTimer = new Timer(1500, e -> hideCards());

        hideCardTimer.setRepeats(false);
        hideCardTimer.start();

    }


    void setupCards() {
        cardSet = new ArrayList<Card>();
        for (String cardName : cardList) {
            //load each card image
            Image cardImg = new ImageIcon(getClass().getResource("./img/" + cardName + ".jpg")).getImage();
            ImageIcon cardImageIcon = new ImageIcon(cardImg.getScaledInstance(cardWidth, cardHeight, java.awt.Image.SCALE_SMOOTH));

            //create card object and add to cardSet
            Card card = new Card(cardName, cardImageIcon);
            cardSet.add(card);
        }
        cardSet.addAll(cardSet);

        //load the back card image
        Image cardBackImg = new ImageIcon(getClass().getResource("./img/back.jpg")).getImage();
        cardBackImageIcon = new ImageIcon(cardBackImg.getScaledInstance(cardWidth, cardHeight, java.awt.Image.SCALE_SMOOTH));
    }

    void shuffleCards() {
        //shuffle
        for (int i = 0; i < cardSet.size(); i++) {
            int j = (int) (Math.random() * cardSet.size()); //get random index
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
                JOptionPane.showMessageDialog(frame, "Congrats! You win!\nFinis Time: " + seconds + " Seconds", "Winning!!", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "Congrats! You win!", "Winning!!", JOptionPane.INFORMATION_MESSAGE);
            }
            // Reset frame and back to play()
            restohome();
        }
    }

    void restohome(){
        // frame.getContentPane().removeAll();
        // frame.repaint();
        frame.dispose(); // close the current frame
        errorCount = 0;
        textLabel.setText("Errors: " + Integer.toString(errorCount));
        if (timer != null) timer.stop();

        // Create a new JFrame and set it up
        frame = new JFrame("Pokemon Match Cards");
        showHomeFrame();
    }

    void showHomeFrame() {
        JFrame homeFrame = new JFrame("Pokemon Memory Cards - Home");
        homeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        homeFrame.setSize(400, 300);
        homeFrame.setLocationRelativeTo(null);
        homeFrame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Pilih Mode
        JLabel modeLabel = new JLabel("Pilih Mode:");
        String[] modeOptions = {"StopWatch", "Time Attack", "Free Mode"};
        JComboBox<String> modeCombo = new JComboBox<>(modeOptions);

        // Pilih Tema
        JLabel themeLabel = new JLabel("Pilih Tema:");
        String[] themeOptions = {"Default", "Pokemon", "One Piece"};
        JComboBox<String> themeCombo = new JComboBox<>(themeOptions);

        // Tombol Play
        JButton playButton = new JButton("Play");

        // Layout
        gbc.gridx = 0; gbc.gridy = 0;
        homeFrame.add(modeLabel, gbc);
        gbc.gridx = 1;
        homeFrame.add(modeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        homeFrame.add(themeLabel, gbc);
        gbc.gridx = 1;
        homeFrame.add(themeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        homeFrame.add(playButton, gbc);

        // Action Play
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Set mode sesuai pilihan
                int modeIdx = modeCombo.getSelectedIndex();
                if (modeIdx == 1) {
                    selectedMode = GameMode.COUNT_DOWN;
                } else if (modeIdx == 2) {
                    selectedMode = GameMode.FREE;
                } else {
                    selectedMode = GameMode.COUNT_UP;
                }
                // Set tema sesuai pilihan (implementasi tema sesuai kebutuhan)
                String selectedTheme = (String) themeCombo.getSelectedItem();
                if (selectedTheme.equals("Pokemon")) {
                    cardList = pokeList.clone(); // Use Pokemon cards
                } else if (selectedTheme.equals("One Piece")) {
                    cardList = characterCardList.clone(); // Use One Piece cards
                } else {
                    cardList = characterCardList.clone(); // Default to character cards
                }
                
                homeFrame.dispose(); // Tutup frame home
                play(); // Mulai game
            }
        });

        homeFrame.setVisible(true);
    }
}

// TODO: perbanyak mode dan tema, tambahkan home button di game/play()