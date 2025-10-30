import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class level {
    private JPanel panel;
    private JButton difficulteButton;
    private JButton mediumButton;
    private JButton eazyButton;
    private JCheckBox soundCheckBox;

    private String selectedDifficulty = "easy";
    private boolean soundEnabled = true;
    private final Runnable gameStarter;

    public level(Runnable gameStarter) {
        this.gameStarter = gameStarter;
        $$$setupUI$$$();
        initializeComponents();
    }

    private void initializeComponents() {
        soundCheckBox.setSelected(true);

        ActionListener listener = event -> {
            Object source = event.getSource();
            if (source == eazyButton) {
                selectedDifficulty = "easy";
            } else if (source == mediumButton) {
                selectedDifficulty = "medium";
            } else if (source == difficulteButton) {
                selectedDifficulty = "difficult";
            }
            startGame();
        };

        eazyButton.addActionListener(listener);
        mediumButton.addActionListener(listener);
        difficulteButton.addActionListener(listener);
    }

    private void startGame() {
        soundEnabled = soundCheckBox.isSelected();
        if (gameStarter != null) {
            gameStarter.run();
        }
    }

    public JPanel getPanel() {
        return panel;
    }

    public String getSelectedDifficulty() {
        return selectedDifficulty;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    private void $$$setupUI$$$() {
        panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        difficulteButton = new JButton();
        difficulteButton.setText("Difficult");
        gbc.gridy = 0;
        panel.add(difficulteButton, gbc);

        mediumButton = new JButton();
        mediumButton.setText("Medium");
        gbc.gridy = 1;
        panel.add(mediumButton, gbc);

        eazyButton = new JButton();
        eazyButton.setText("Easy");
        gbc.gridy = 2;
        panel.add(eazyButton, gbc);

        soundCheckBox = new JCheckBox();
        soundCheckBox.setSelected(true);
        soundCheckBox.setText("Enable Sound");
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(soundCheckBox, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }
}
