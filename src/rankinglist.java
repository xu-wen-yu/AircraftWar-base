import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;

public class rankinglist {
    private JPanel panel1;
    private JTable table1;
    private JTextPane titleText;
    private JScrollPane scrollPane;

    public rankinglist() {
        $$$setupUI$$$();
        initializeComponents();
    }

    private void initializeComponents() {
        titleText.setEditable(false);
        titleText.setBackground(panel1.getBackground());
        titleText.setFocusable(false);
        table1.setFillsViewportHeight(true);
        table1.setRowHeight(24);
    }

    public JPanel getPanel() {
        return panel1;
    }

    public JTable getTable() {
        return table1;
    }

    public void setTableModel(TableModel model) {
        table1.setModel(model);
    }

    private void $$$setupUI$$$() {
        panel1 = new JPanel(new BorderLayout());

        titleText = new JTextPane();
        titleText.setText("得分排行榜");
        titleText.setPreferredSize(new Dimension(150, 50));
        panel1.add(titleText, BorderLayout.NORTH);

        scrollPane = new JScrollPane();
        panel1.add(scrollPane, BorderLayout.CENTER);
        table1 = new JTable();
        scrollPane.setViewportView(table1);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}
