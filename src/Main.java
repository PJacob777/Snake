import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(
                Window::new
        );
    }
}
class AddPlayerEvent extends EventObject {
    private String nick;
    private int score;
    public AddPlayerEvent(Object source,String nick,int score) {
        super(source);
        this.nick = nick;
        this.score = score;
    }

    public String getNick() {
        return nick;
    }

    public int getScore() {
        return score;
    }
}
interface AddPlayerListener {
    void addPlayer (AddPlayerEvent evt);
}
class AddPointEvent extends EventObject {
    public AddPointEvent(Object source) {
        super(source);
    }
}
interface AddPointListeners {
    void AddPoint (AddPointEvent evt);
}
class Board extends JTable implements GetSnake, TickEventListeners{
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
                if(snake == null)
                    return null;
                else
                {
                    if(snake.getNumberOfColumn(snake.getHead())==column && snake.getNumberOfRow(snake.getHead())==row){
                        jPanel.setBackground(Color.RED);
                        return jPanel;
                    }
                    for (int i :snake.getFood()) {
                        if (snake.getNumberOfColumn(i) == column && snake.getNumberOfRow(i) == row) {
                            jPanel.setBackground(Color.GREEN);
                            return jPanel;
                        }
                    }
                    for (int i : snake.getBody()){
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
        for(int i = 0 ; i < columnOfBlank.size(); i ++){
            this.setValueAt(jPanel, rowsOfBlank.get(i),columnOfBlank.get(i));
        }
        jPanel.setBackground(Color.RED);
        int columnOfHead = evt.getChangedHeadColumn();
        int rowOfHead = evt.getChangedHeadRow();
        this.setValueAt(jPanel,rowOfHead,columnOfHead);
        jPanel.setBackground(Color.BLACK);
        for(int i = 0 ; i < columnOfBody.size(); i ++){
            this.setValueAt(jPanel, rowsOfBody.get(i),columnOfBody.get(i));
        }
        if(!columnOfFood.isEmpty()) {
            jPanel.setBackground(Color.GREEN);
            for (int i = 0; i < columnOfFood.size(); i++) {
                this.setValueAt(jPanel, rowsOfFood.get(i), columnOfFood.get(i));
            }
        }
        repaint();

    }

}
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
interface ChangedDirectionListeners {
    void directionChanged(ChangeDirectionEvent event);
}
class ChangeDirectionEvent extends EventObject {
    private Direction newDirection;

    public ChangeDirectionEvent(Object source, Direction newDirection) {
        super(source);
        this.newDirection = newDirection;
    }

    public Direction getNewDirection() {
        return newDirection;
    }
}
enum Direction {
    UP,DOWN,LEFT,RIGHT;
}
interface GetSnake {
    void setSnake(Snake s);
}
class Player implements Comparable<Player>{
    private String nick;
    private int score;

    public Player(String nick, int score) {
        this.nick = nick;
        this.score = score;
    }

    @Override
    public int compareTo(Player o) {
        return o.score - this.score;
    }

    public String getNick() {
        return nick;
    }

    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        return nick + " " + score;
    }
}
class ScoreBoard extends JPanel implements AddPointListeners{
    private int Score;
    private JTextField jTextField;
    public ScoreBoard() {
        this.Score = 0;
        JLabel jLabel = new JLabel("Score");
        this.jTextField = new JTextField(Score+"");
        jTextField.setEditable(false);
        this.add(jLabel);
        this.add(jTextField);
    }

    @Override
    public void AddPoint(AddPointEvent evt) {
        Score++;
        jTextField.setText(Score+"");
        repaint();
    }
}
class ShowBestPlayers extends EventObject {
    private ArrayList players;
    public ShowBestPlayers(Object source, ArrayList<Player> players) {
        super(source);
        this.players = new ArrayList<>(players);
    }

    public ArrayList getPlayers() {
        return players;
    }
}
interface ShowBestPlayersListener {
    void showTheBestPlayers(ShowBestPlayers evt);
}
class Snake extends Thread implements ChangedDirectionListeners,AddPlayerListener{
    private ArrayList <AddPlayerListener> addPlayerListeners;
    private ArrayList <ShowBestPlayersListener> showBestPlayersListeners;
    private ArrayList<Integer> body;
    private int head;
    private ArrayList<Integer> turn;
    private Direction direction;
    private Direction newDirection;
    private ArrayList<Integer> food;
    private ArrayList<Player> bestScores;
    private boolean game;
    private int[][] tab;
    private int tail;
    private ArrayList<AddPointListeners> listeners;
    private ArrayList<TickEventListeners> tickEventListeners;

    public Snake() {
        addPlayerListeners = new ArrayList<>();
        showBestPlayersListeners = new ArrayList<>();
        tickEventListeners = new ArrayList<>();
        body = new ArrayList<>();
        listeners = new ArrayList<>();
        turn = new ArrayList<>();
        direction = null;
        newDirection = Direction.UP;
        bestScores = new ArrayList<>();
        head = (int) (Math.random() * 400);
        tail = head;
        food = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            food.add((int) (Math.random() * 400));
        }
        game = true;
        this.tab = new int[16][25];
        int k = 1;
        for (int i = 0; i < tab.length; i++)
            for (int j = 0; j < tab[i].length; j++)
                tab[i][j] = k++;
    }

    public boolean IsGoodDirection(Direction direction, Direction newDirection) {
        if (direction == Direction.UP && newDirection == Direction.DOWN)
            return false;
        if (direction == Direction.LEFT && newDirection == Direction.RIGHT)
            return false;
        if (direction == Direction.RIGHT && newDirection == Direction.LEFT)
            return false;
        if (direction == Direction.DOWN && newDirection == Direction.UP)
            return false;
        return true;
    }

    private void readFromFile() throws IOException {
        File file = new File("D:\\bin\\result.bin");
        ArrayList <Player> arrayList= new ArrayList<>();
        int r = 0;
        FileInputStream fis = new FileInputStream(file);
        while (r != -1) {
            r = fis.read();
            int lengthOfNick = r;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < lengthOfNick; i++) {
                r = fis.read();
                sb.append((char) (r));
            }
            String nick = sb.toString();
            int value = 0;
            for (int i = 0; i < 4; i++) {
                r = fis.read();
                int d = r << 8 *i ;
                value |= d;
            }
            arrayList.add(new Player(nick,value));
        }
        arrayList.sort(null);
        this.bestScores = arrayList;
        bestScores.remove(bestScores.size()-1);
        fis.close();
        for(Player p : bestScores)
            System.out.println(p);


    }

    private void addFood(ArrayList<Integer> foodRow, ArrayList<Integer> foodColumn) {
        for (int i = 0; i < 5; i++) {
            int tmp = (int) (Math.random() * 400);
            food.add(tmp);
            foodRow.add(this.getNumberOfRow(tmp));
            foodColumn.add(this.getNumberOfColumn(tmp));
        }
    }

    @Override
    public void run() {
        try {
            readFromFile();
        }catch (IOException ex){
            ex.printStackTrace();
        }
        int points = 0;
        while (game&&!((this.getNumberOfRow(head)==0&& newDirection==Direction.UP) ||(this.getNumberOfRow(head)==15 && newDirection==Direction.DOWN)||(head%25==0 && newDirection==Direction.RIGHT)||(head%25==1 && newDirection==Direction.LEFT))) {
            int previousPosition = head;
            int changedHeadColumn = 0;
            int changedHeadRow = 0;
            ArrayList<Integer> columsOfBody = new ArrayList<>();
            ArrayList<Integer> rowsOfBody = new ArrayList<>();
            ArrayList<Integer> foodRow = new ArrayList<>();
            ArrayList<Integer> foodColumn = new ArrayList<>();
            ArrayList<Integer> blankColums = new ArrayList<>();
            ArrayList<Integer> blankRows = new ArrayList<>();
            ArrayList<Integer> copyOfFood = new ArrayList<>();
            for (Integer i : food)
                copyOfFood.add(i);
            blankColums.add(this.getNumberOfColumn(previousPosition));
            blankRows.add(this.getNumberOfRow(previousPosition));
            for (int i : body) {
                if (i == head) {
                    game = false;
                }
            }

            for(Integer i : copyOfFood){
                if(head==i) {
                    fireAddPointsListeners();
                    food.remove(i);
                    body.add(tail);
                    blankColums.add(this.getNumberOfColumn(i));
                    blankRows.add(this.getNumberOfRow(i));
                    points++;
                }
            }
            switch (newDirection) {
                case UP -> {
                    head -= 25;
                    changedHeadColumn = this.getNumberOfColumn(head);
                    changedHeadRow = this.getNumberOfRow(head);
                }
                case DOWN -> {
                    head += 25;
                    changedHeadColumn = this.getNumberOfColumn(head);
                    changedHeadRow = this.getNumberOfRow(head);
                }
                case LEFT -> {
                    head -= 1;
                    changedHeadColumn = this.getNumberOfColumn(head);
                    changedHeadRow = this.getNumberOfRow(head);
                }
                case RIGHT -> {
                    head += 1;
                    changedHeadColumn = this.getNumberOfColumn(head);
                    changedHeadRow = this.getNumberOfRow(head);
                }
            }


            for (int i = 0; i < body.size(); i++) {
                int tmp = 0;
                tmp = body.get(i);
                body.set(i, previousPosition);
                columsOfBody.add(this.getNumberOfColumn(previousPosition));
                rowsOfBody.add(this.getNumberOfRow(previousPosition));
                previousPosition= tmp;
                blankRows.add(this.getNumberOfRow(tmp));
                blankColums.add(this.getNumberOfColumn(tmp));

            }
            tail = previousPosition;
            //teraz dodaj poruszanie sie dla Ciala, jesli lista turn jest pusta to poruszasz sie z aktualnym direction, dopiero po osiagnieciu turn zmieniasz direction, po przejsciu tail przez turn usun turn i usun preciousDirection
            isEmptyFood( foodRow, foodColumn);
            fireTickEventListeners(changedHeadColumn,changedHeadRow,columsOfBody,rowsOfBody,foodRow,foodColumn,blankColums,blankRows);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        fireShowBestPlayersListeners(bestScores);
        Player lastScore = bestScores.get(bestScores.size()-1);
        if(points>lastScore.getScore()){
            bestScores.remove(lastScore);
            fireAddPlayerListeners(points);

        }

        System.out.println("Koniec gry");
    }

    private void fireAddPointsListeners() {
        AddPointEvent addPointEvent = new AddPointEvent(this);
        for(AddPointListeners a : listeners)
            a.AddPoint(addPointEvent);
    }

    public int getNumberOfColumn(int seek) {
        for (int i = 0; i < tab.length; i++)
            for (int j = 0; j < tab[i].length; j++)
                if (seek == tab[i][j])
                    return j;
        return -1;
    }

    public int getNumberOfRow(int seek) {
        for (int i = 0; i < tab.length; i++)
            for (int j = 0; j < tab[i].length; j++)
                if (seek == tab[i][j])
                    return i;
        return -1;
    }

    public ArrayList<Integer> getBody() {
        return body;
    }

    public int getHead() {
        return head;
    }

    public ArrayList<Integer> getTurn() {
        return turn;
    }

    public Direction getDirection() {
        return direction;
    }

    public Direction getNewDirection() {
        return newDirection;
    }

    public ArrayList<Integer> getFood() {
        return food;
    }

    public ArrayList<Player> getBestScores() {
        return bestScores;
    }

    public boolean isGame() {
        return game;
    }

    public int[][] getTab() {
        return tab;
    }

    public int getTail() {
        return tail;
    }

    @Override
    public void directionChanged(ChangeDirectionEvent event) {
        if(IsGoodDirection(newDirection,event.getNewDirection())) {
            this.newDirection = event.getNewDirection();
        }
        System.out.println(this.newDirection);
    }
    private void isEmptyFood(ArrayList<Integer> foodRow, ArrayList<Integer> foodColumn){
        if(food.isEmpty())
            addFood(foodRow,foodColumn);
    }
    private void writeToFile()throws IOException{
        File file = new File("D:\\bin\\result.bin");
        FileOutputStream fos = new FileOutputStream(file);
        for(Player p : bestScores) {
            String nick = p.getNick();
            int score = p.getScore();
            fos.write(nick.length());
            for (int i = 0; i < nick.length(); i++) {
                fos.write(nick.charAt(i));
            }
            for (int i = 0; i < 4; i++) {
                fos.write(score >> 8 * i);
            }


        }
        fos.close();
    }
    public void addAddPointListeners(AddPointListeners addPointListeners){
        listeners.add(addPointListeners);
    }
    public void addTickEventListeners(TickEventListeners a){
        tickEventListeners.add(a);
    }
    private void fireTickEventListeners(int changedHeadColumn,int changedHeadRow,ArrayList<Integer> columsOfBody,ArrayList<Integer> rowsOfBody ,ArrayList<Integer> foodRow ,ArrayList<Integer> foodColumn ,ArrayList<Integer> blankColums ,ArrayList<Integer> blankRows){
        TickEvent event = new TickEvent(this,columsOfBody,rowsOfBody,changedHeadColumn,changedHeadRow,foodColumn,foodRow,blankColums,blankRows);
        for (TickEventListeners l : tickEventListeners)
            l.updateCells(event);
    }
    public void addShowBestPlayersListener(ShowBestPlayersListener listener){
        showBestPlayersListeners.add(listener);
    }
    private void fireShowBestPlayersListeners(ArrayList<Player> list){
        ShowBestPlayers evt = new ShowBestPlayers(this,list);
        for (ShowBestPlayersListener e : showBestPlayersListeners)
            e.showTheBestPlayers(evt);

    }
    public void addAddPlayerListener(AddPlayerListener listener){
        addPlayerListeners.add(listener);
    }
    private void fireAddPlayerListeners(int b){
        AddPlayerEvent addPlayerEvent = new AddPlayerEvent(this,"",b);
        for(AddPlayerListener a : addPlayerListeners)
            a.addPlayer(addPlayerEvent);
    }

    @Override
    public void addPlayer(AddPlayerEvent evt) {
        System.out.println();
        System.out.println(evt.getNick());
        System.out.println(evt.getScore());
        bestScores.add(new Player(evt.getNick(),evt.getScore()));
        bestScores.sort(null);
        try {
            writeToFile();
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
class TickEvent extends EventObject {
    private int changedHeadColumn;
    private int changedHeadRow;
    private ArrayList<Integer> columsOfBody;
    private ArrayList<Integer> rowsOfBody;
    private ArrayList<Integer> foodRow;
    private ArrayList<Integer> foodColumn;
    private ArrayList<Integer> blankColums;
    private ArrayList<Integer> blankRows;

    public TickEvent(Object source, ArrayList<Integer> columsOfBody, ArrayList<Integer> rowsOfBody, int changedHeadColumn, int changedHeadRow, ArrayList<Integer> foodColumn,ArrayList<Integer> foodRow,ArrayList<Integer> blankColums, ArrayList<Integer> blankRows) {
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
interface TickEventListeners {
    void updateCells(TickEvent evt);
}
class Window extends JFrame implements ShowBestPlayersListener,AddPlayerListener{
    private ArrayList<ChangedDirectionListeners> list;
    private JPanel boardPanel;
    private ArrayList <AddPlayerListener> addPlayerListeners;
    public Window() {
        addPlayerListeners = new ArrayList<>();
        this.list = new ArrayList<>();
        Board board = new Board();
        JPanel jPanel = new JPanel();
        boardPanel = jPanel;
        this.setLayout(new BorderLayout());
        jPanel.add(board);
        ScoreBoard scoreBoard = new ScoreBoard();
        this.add(scoreBoard,BorderLayout.EAST);
        this.add(jPanel,BorderLayout.WEST);
        this.setSize(1280,840);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setFocusable(true);
        Snake snake = new Snake();
        list.add(snake);
        addPlayerListeners.add(snake);
        snake.addAddPlayerListener(this);
        snake.addShowBestPlayersListener(this);
        board.setSnake(snake);
        snake.addAddPointListeners(scoreBoard);
        snake.addTickEventListeners(board);
        snake.start();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP -> {
                        System.out.println("up");
                        fireChangeDirectionListeners(Direction.UP);
                    }
                    case KeyEvent.VK_DOWN -> {
                        System.out.println("down");
                        fireChangeDirectionListeners(Direction.DOWN);
                    }
                    case KeyEvent.VK_LEFT -> {
                        System.out.println("left");
                        fireChangeDirectionListeners(Direction.LEFT);
                    }
                    case KeyEvent.VK_RIGHT -> {
                        System.out.println("right");
                        fireChangeDirectionListeners(Direction.RIGHT);
                    }
                }
            }
        });
    }
    private void fireChangeDirectionListeners(Direction direction){
        ChangeDirectionEvent evt = new ChangeDirectionEvent(this,direction);
        for(ChangedDirectionListeners e : list)
            e.directionChanged(evt);
    }

    @Override
    public void showTheBestPlayers(ShowBestPlayers evt) {
        boardPanel.removeAll();
        JTable jTable = new JTable();
        ArrayList<Player> best = new ArrayList<>(evt.getPlayers());
        jTable.setModel(new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return best.size();
            }

            @Override
            public int getColumnCount() {
                return 2;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                return switch (columnIndex){
                    case 0 -> best.get(rowIndex).getNick();
                    case 1 -> best.get(rowIndex).getScore();
                    default -> null;
                };
            }
        });
        boardPanel.add(jTable,BorderLayout.WEST);
        boardPanel.revalidate();
        boardPanel.repaint();
    }

    @Override
    public void addPlayer(AddPlayerEvent evt) {
        JDialog jDialog = new JDialog();
        JLabel jLabel = new JLabel("Gratuluje jestes w top 10");
        JTextField jTextField = new JTextField();
        JButton jButton = new JButton("Potwierdz");
        jDialog.setLayout(new BorderLayout());
        jDialog.add(jLabel,BorderLayout.NORTH);
        jDialog.add(jTextField,BorderLayout.CENTER);
        jDialog.add(jButton,BorderLayout.SOUTH);
        jDialog.setVisible(true);

        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nick;
                nick= jTextField.getText();
                fireAddPlayerListener(nick,evt.getScore());
            }
        });

    }
    private void fireAddPlayerListener(String s,int a){
        AddPlayerEvent addPlayerEvent = new AddPlayerEvent(this,s,a);
        for(AddPlayerListener c : addPlayerListeners)
            c.addPlayer(addPlayerEvent);
    }
}


