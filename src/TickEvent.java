import java.util.ArrayList;
import java.util.EventObject;

class TickEvent extends EventObject {
    private int changedHeadColumn;
    private int changedHeadRow;
    private ArrayList<Integer> columsOfBody;
    private ArrayList<Integer> rowsOfBody;
    private ArrayList<Integer> foodRow;
    private ArrayList<Integer> foodColumn;
    private ArrayList<Integer> blankColums;
    private ArrayList<Integer> blankRows;

    public TickEvent(Object source, ArrayList<Integer> columsOfBody, ArrayList<Integer> rowsOfBody, int changedHeadColumn, int changedHeadRow, ArrayList<Integer> foodColumn, ArrayList<Integer> foodRow, ArrayList<Integer> blankColums, ArrayList<Integer> blankRows) {
        super(source);
        this.columsOfBody = columsOfBody;
        this.rowsOfBody = rowsOfBody;
        this.changedHeadColumn = changedHeadColumn;
        this.changedHeadRow = changedHeadRow;
        this.foodColumn = foodColumn;
        this.foodRow = foodRow;
        this.blankColums = blankColums;
        this.blankRows = blankRows;
    }

    public ArrayList<Integer> getColumsOfBody() {
        return columsOfBody;
    }

    public ArrayList<Integer> getRowsOfBody() {
        return rowsOfBody;
    }

    public int getChangedHeadColumn() {
        return changedHeadColumn;
    }

    public int getChangedHeadRow() {
        return changedHeadRow;
    }

    public ArrayList<Integer> getFoodRow() {
        return foodRow;
    }

    public ArrayList<Integer> getFoodColumn() {
        return foodColumn;
    }

    public ArrayList<Integer> getBlankColums() {
        return blankColums;
    }

    public ArrayList<Integer> getBlankRows() {
        return blankRows;
    }
}
