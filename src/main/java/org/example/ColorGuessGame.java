package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ColorGuessGame extends JFrame {
    private List<Color> shownColors;
    private List<Color> allColors;
    private JPanel colorPanel;
    private JPanel buttonPanel;
    private JLabel instructionLabel;
    private JButton startButton;
    private int score;
    private int currentQuestion;
    private Timer selectionTimer;
    private JLabel timerLabel;
    private int selectionsMade;

    public ColorGuessGame() {
        setTitle("Color Guess Game");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        instructionLabel = new JLabel("Press 'Start' to begin the game!", SwingConstants.CENTER);
        add(instructionLabel, BorderLayout.NORTH);

        colorPanel = new JPanel();
        colorPanel.setLayout(new GridLayout(1, 3));
        add(colorPanel, BorderLayout.CENTER);

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 3));
        add(buttonPanel, BorderLayout.SOUTH);

        startButton = new JButton("Start");
        startButton.addActionListener(new StartButtonListener());
        add(startButton, BorderLayout.WEST);

        timerLabel = new JLabel("Time Left: 10s", SwingConstants.CENTER);
        add(timerLabel, BorderLayout.EAST);

        shownColors = new ArrayList<>();
        allColors = new ArrayList<>();
        Collections.addAll(allColors, Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.ORANGE, Color.MAGENTA, Color.CYAN, Color.PINK, Color.LIGHT_GRAY);

        score = 0;
        currentQuestion = 0;
        selectionsMade = 0;
    }

    private class StartButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            score = 0;
            currentQuestion = 0;
            startGameRound();
        }
    }

    private void startGameRound() {
        if (currentQuestion >= 10) {
            instructionLabel.setText("Game Over! Final Score: " + score);
            startButton.setEnabled(true);
            return;
        }

        currentQuestion++;
        selectionsMade = 0;
        shownColors.clear();
        colorPanel.removeAll();
        buttonPanel.removeAll();

        // Randomly select 3 colors to show
        Collections.shuffle(allColors);
        for (int i = 0; i < 3; i++) {
            shownColors.add(allColors.get(i));
            JPanel colorDisplay = new JPanel();
            colorDisplay.setBackground(allColors.get(i));
            colorPanel.add(colorDisplay);
        }
        colorPanel.revalidate();
        colorPanel.repaint();

        // Wait for 2 seconds before showing the color buttons
        Timer timer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showColorOptions();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void showColorOptions() {
        colorPanel.removeAll();
        colorPanel.revalidate();
        colorPanel.repaint();

        Collections.shuffle(allColors);
        for (Color color : allColors) {
            JButton colorButton = new JButton();
            colorButton.setBackground(color);
            colorButton.setPreferredSize(new Dimension(100, 100)); // Đặt kích thước lớn hơn cho các nút màu
            colorButton.addActionListener(new ColorButtonListener(color));
            buttonPanel.add(colorButton);
        }
        buttonPanel.revalidate();
        buttonPanel.repaint();

        instructionLabel.setText("Select the colors that were shown!");
        startSelectionTimer();
    }

    private void startSelectionTimer() {
        if (selectionTimer != null && selectionTimer.isRunning()) {
            selectionTimer.stop();
        }

        final int[] timeLeft = {10};
        timerLabel.setText("Time Left: " + timeLeft[0] + "s");

        selectionTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeLeft[0]--;
                timerLabel.setText("Time Left: " + timeLeft[0] + "s");

                if (timeLeft[0] <= 0) {
                    selectionTimer.stop();
                    instructionLabel.setText("Time's up! Moving to the next round.");
                    startGameRound();
                }
            }
        });
        selectionTimer.start();
    }

    private class ColorButtonListener implements ActionListener {
        private Color color;

        public ColorButtonListener(Color color) {
            this.color = color;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (shownColors.contains(color)) {
                score++;
                shownColors.remove(color);
                instructionLabel.setText("Correct! Score: " + score);
            } else {
                instructionLabel.setText("Wrong! Score: " + score);
            }

            selectionsMade++;

            if (selectionsMade == 3) {
                if (selectionTimer != null) {
                    selectionTimer.stop();
                }
                startGameRound();
            }
        }
    }
}
