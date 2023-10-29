import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;

class Board extends JTable implements GetSnake, TickEventListeners {
    private Snake snake;
    private JPanel jPanel;


    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        return super.getCellRenderer(row, column);
    }

    public Board() {
        this.setModel(new BoardModel());


        this.setShowGrid(false);
        this.setRowSelectionAllowed(false);
        this.setColumnSelectionAllowed(false);
        this.setFocusable(false);
        this.setIntercellSpacing(new Dimension(0, 0));
        this.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        for (int i = 0; i < this.getColumnCount(); i++) {
            this.getColumnModel().getColumn(i).setMaxWidth(20);
            this.getColumnModel().getColumn(i).setMinWidth(20);
            this.getColumnModel().getColumn(i).setPreferredWidth(20);
        }
        JPanel jPanel = new JPanel();
        this.jPanel = jPanel;
        DefaultTableCellRenderer tableCell = new DefaultTableCellRenderer();
        this.setDefaultRenderer(Object.class, new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (snake == null)
                    return null;
                else {
                    if (snake.getNumberOfColumn(snake.getHead()) == column && snake.getNumberOfRow(snake.getHead()) == row) {
                        jPanel.setBackground(Color.RED);
                        return jPanel;
                    }
                    for (int i : snake.getFood()) {
                        if (snake.getNumberOfColumn(i) == column && snake.getNumberOfRow(i) == row) {
                            jPanel.setBackground(Color.GREEN);
                            return jPanel;
                        }
                    }
                    for (int i : snake.getBody()) {
                        if (snake.getNumberOfColumn(i) == column && snake.getNumberOfRow(i) == row) {
                            jPanel.setBackground(Color.BLACK);
                            return jPanel;
                        }
                    }
                }
                return null;
            }
        });
    }

    @Override
    public void setSnake(Snake s) {
        this.snake = s;
    }

    @Override
    public void updateCells(TickEvent evt) {
        ArrayList<Integer> columnOfBody = evt.getColumsOfBody();
        ArrayList<Integer> rowsOfBody = evt.getRowsOfBody();
        ArrayList<Integer> columnOfFood = evt.getFoodColumn();
        ArrayList<Integer> rowsOfFood = evt.getFoodRow();
        ArrayList<Integer> rowsOfBlank = evt.getBlankRows();
        ArrayList<Integer> columnOfBlank = evt.getBlankColums();
        jPanel.setBackground(Color.WHITE);
        for (int i = 0; i < columnOfBlank.size(); i++) {
            this.setValueAt(jPanel, rowsOfBlank.get(i), columnOfBlank.get(i));
        }
        jPanel.setBackground(Color.RED);
        int columnOfHead = evt.getChangedHeadColumn();
        int rowOfHead = evt.getChangedHeadRow();
        this.setValueAt(jPanel, rowOfHead, columnOfHead);
        jPanel.setBackground(Color.BLACK);
        for (int i = 0; i < columnOfBody.size(); i++) {
            this.setValueAt(jPanel, rowsOfBody.get(i), columnOfBody.get(i));
        }
        if (!columnOfFood.isEmpty()) {
            jPanel.setBackground(Color.GREEN);
            for (int i = 0; i < columnOfFood.size(); i++) {
                this.setValueAt(jPanel, rowsOfFood.get(i), columnOfFood.get(i));
            }
        }
        repaint();

    }

}
