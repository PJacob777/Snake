import javax.swing.table.AbstractTableModel;

class BoardModel extends AbstractTableModel {
    private int[][] tab;

    public BoardModel() {
     /*   this.tab = new int[25][16];
        int k = 1;
        for (int i = 0 ; i < tab.length;i++)
            for (int j = 0 ; j < tab[i].length;j++)
                tab[i][j]=k++;
*/
    }

    @Override
    public int getRowCount() {
        return 16;
    }

    @Override
    public int getColumnCount() {
        return 25;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return 1;
    }
}
