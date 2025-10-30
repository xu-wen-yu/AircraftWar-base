package edu.hitsz.application;

import edu.hitsz.mode.Difficulte;
import edu.hitsz.mode.Easy;
import edu.hitsz.mode.Medium;
import edu.hitsz.mode.ModeSelect;
import music.SoundManager;

import javax.swing.*;
import java.awt.*;

/**
 * 程序入口
 * @author hitsz
 */
public class Main {

    public static final int WINDOW_WIDTH = 512;
    public static final int WINDOW_HEIGHT = 768;

    public static void main(String[] args) {

        System.out.println("Hello Aircraft War");

        // 获得屏幕的分辨率，初始化 Frame
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        JFrame frame = new JFrame("Aircraft War");
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setResizable(false);
        //设置窗口的大小和位置,居中放置
        frame.setBounds(((int) screenSize.getWidth() - WINDOW_WIDTH) / 2, 0,
                WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 在启动游戏前，弹出难度与音效选择对话框
        JPanel optionsPanel = new JPanel(new GridLayout(0, 1));
        ButtonGroup group = new ButtonGroup();
        JRadioButton easy = new JRadioButton("Easy", true);
        JRadioButton medium = new JRadioButton("Medium");
        JRadioButton hard = new JRadioButton("Difficult");
        group.add(easy);
        group.add(medium);
        group.add(hard);
        JCheckBox soundBox = new JCheckBox("Enable Sound", true);
        optionsPanel.add(new JLabel("Select Difficulty:"));
        optionsPanel.add(easy);
        optionsPanel.add(medium);
        optionsPanel.add(hard);
        optionsPanel.add(soundBox);

        int result = JOptionPane.showConfirmDialog(frame, optionsPanel, "Game Options", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        boolean soundEnabled = true;
        String difficulty = "easy";
        if (result == JOptionPane.OK_OPTION) {
            soundEnabled = soundBox.isSelected();
            if (medium.isSelected()) {
                difficulty = "medium";
            } else if (hard.isSelected()) {
                difficulty = "difficult";
            }
        } else {
            // 用户取消或关闭对话框，退出程序
            System.exit(0);
        }
        ModeSelect mode;
        // 根据难度设定背景（这里仅替换地图背景）
        switch (difficulty) {
            case "medium":
                mode = new Medium();
                ImageManager.loadBackground("src/images/bg2.jpg");
                break;
            case "difficult":
                mode = new Difficulte();
                ImageManager.loadBackground("src/images/bg3.jpg");
                break;
            default:
                mode = new Easy();
                ImageManager.loadBackground("src/images/bg.jpg");
        }

        SoundManager.getInstance().setSoundEnabled(soundEnabled);

        Game game = new Game(soundEnabled, mode);
        frame.add(game);
        frame.setVisible(true);
        game.action();
    }
}
