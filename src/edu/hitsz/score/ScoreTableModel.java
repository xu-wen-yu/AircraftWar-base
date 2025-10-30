package edu.hitsz.score;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoreTableModel extends AbstractTableModel {

    private static final String[] COLUMN_NAMES = {"排名", "玩家", "分数", "时间"};
    private List<Score> scores = new ArrayList<>();

    public void setScores(List<Score> scores) {
        if (scores == null) {
            this.scores = new ArrayList<>();
        } else {
            this.scores = new ArrayList<>(scores);
        }
        fireTableDataChanged();
    }

    public Score getScoreAt(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= scores.size()) {
            return null;
        }
        return scores.get(rowIndex);
    }

    public List<Score> getScores() {
        return Collections.unmodifiableList(scores);
    }

    @Override
    public int getRowCount() {
        return scores.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0 || columnIndex == 2) {
            return Integer.class;
        }
        return String.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Score score = scores.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return rowIndex + 1;
            case 1:
                return score.getScorename();
            case 2:
                return score.getScore();
            case 3:
                return score.getTimeString();
            default:
                return "";
        }
    }
}
