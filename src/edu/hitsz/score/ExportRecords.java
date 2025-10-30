package edu.hitsz.score;

import javax.swing.*;
import java.awt.*;

public class ExportRecords {

    private ExportRecords() {
    }

    public static void exportRecord(Component parentComponent, int scores) {
        ScoreDao scoreDao = new ScoreDaolmpl();
        String playerName = JOptionPane.showInputDialog(parentComponent,
                "请输入玩家昵称：",
                "记录得分",
                JOptionPane.PLAIN_MESSAGE);
        if (playerName == null) {
            playerName = "Player";
        }
        playerName = playerName.trim();
        if (playerName.isEmpty()) {
            playerName = "Player";
        }

        Score latestScore = new Score(scores, playerName);
        scoreDao.doAdd(latestScore);

        Window owner = parentComponent != null
                ? SwingUtilities.getWindowAncestor(parentComponent)
                : null;
        ScoreBoardDialog dialog = new ScoreBoardDialog(owner, scoreDao, latestScore);
        dialog.setLocationRelativeTo(parentComponent);
        dialog.setVisible(true);
    }
}