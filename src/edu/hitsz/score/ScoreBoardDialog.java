package edu.hitsz.score;

import javax.swing.*;
import javax.swing.BorderFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class ScoreBoardDialog extends JDialog {

    private JPanel contentPane;
    private JTable scoreTable;
    private JButton deleteButton;
    private JButton closeButton;
    private JLabel statusLabel;
    private JLabel titleLabel;
    private JScrollPane tableScrollPane;
    private final ScoreDao scoreDao;
    private final ScoreTableModel tableModel;
    private final Score latestScore;

    public ScoreBoardDialog(Window owner, ScoreDao scoreDao, Score latestScore) {
        super(owner, "得分排行榜", ModalityType.APPLICATION_MODAL);
        this.scoreDao = scoreDao;
        this.latestScore = latestScore;
        this.tableModel = new ScoreTableModel();
        $$$setupUI$$$();
        setContentPane(contentPane);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        pack();
        setSize(520, 360);
        if (owner != null) {
            setLocationRelativeTo(owner);
        }

        setupComponents();
        setupListeners();
        getRootPane().setDefaultButton(closeButton);
        refreshData();
        highlightLatestScore();
    }

    private void setupComponents() {
        if (titleLabel != null) {
            titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));
        }
        if (scoreTable == null) {
            scoreTable = new JTable();
            if (tableScrollPane != null) {
                tableScrollPane.setViewportView(scoreTable);
            }
        }
        scoreTable.setModel(tableModel);
        scoreTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        scoreTable.setRowHeight(24);
        scoreTable.setFillsViewportHeight(true);
    }

    private void setupListeners() {
        deleteButton.addActionListener(this::onDelete);
        closeButton.addActionListener(this::onClose);
    }

    private void onDelete(ActionEvent event) {
        int[] selectedRows = scoreTable.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的记录。", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int option = JOptionPane.showConfirmDialog(this,
                "确认删除选中的" + selectedRows.length + "条记录吗？",
                "确认删除",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (option != JOptionPane.OK_OPTION) {
            return;
        }
        List<Score> toRemove = new ArrayList<>(selectedRows.length);
        for (int selectedRow : selectedRows) {
            int modelIndex = scoreTable.convertRowIndexToModel(selectedRow);
            Score score = tableModel.getScoreAt(modelIndex);
            if (score != null) {
                toRemove.add(score);
            }
        }
        scoreDao.removeScores(toRemove);
        refreshData();
    }

    private void refreshData() {
        tableModel.setScores(scoreDao.getAllScores());
        statusLabel.setText("共 " + tableModel.getRowCount() + " 条记录");
    }

    private void highlightLatestScore() {
        if (latestScore == null) {
            return;
        }
        List<Score> scores = tableModel.getScores();
        for (int i = 0; i < scores.size(); i++) {
            Score score = scores.get(i);
            if (score.equals(latestScore)) {
                scoreTable.getSelectionModel().setSelectionInterval(i, i);
                scoreTable.scrollRectToVisible(scoreTable.getCellRect(i, 0, true));
                break;
            }
        }
    }

    private void onClose(ActionEvent event) {
        dispose();
    }

    private void $$$setupUI$$$() {
        contentPane = new JPanel(new BorderLayout());
        contentPane.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        final JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        contentPane.add(panel1, BorderLayout.NORTH);
        titleLabel = new JLabel();
        titleLabel.setText("玩家得分排行榜");
        panel1.add(titleLabel);

        tableScrollPane = new JScrollPane();
        contentPane.add(tableScrollPane, BorderLayout.CENTER);
        scoreTable = new JTable();
        tableScrollPane.setViewportView(scoreTable);

        final JPanel panel2 = new JPanel(new BorderLayout());
        contentPane.add(panel2, BorderLayout.SOUTH);
        statusLabel = new JLabel();
        statusLabel.setText("共 0 条记录");
        panel2.add(statusLabel, BorderLayout.WEST);

        final JPanel panel3 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        panel2.add(panel3, BorderLayout.EAST);
        deleteButton = new JButton();
        deleteButton.setText("删除选中记录");
        panel3.add(deleteButton);
        closeButton = new JButton();
        closeButton.setText("关闭");
        panel3.add(closeButton);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
