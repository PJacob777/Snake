import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

class Window extends JFrame implements ShowBestPlayersListener, AddPlayerListener {
    private ArrayList<ChangedDirectionListeners> list;
    private JPanel boardPanel;
    private ArrayList<AddPlayerListener> addPlayerListeners;

    public Window() {
        addPlayerListeners = new ArrayList<>();
        this.list = new ArrayList<>();
        Board board = new Board();
        JPanel jPanel = new JPanel();
        boardPanel = jPanel;
        this.setLayout(new BorderLayout());
        jPanel.add(board);
        ScoreBoard scoreBoard = new ScoreBoard();
        this.add(scoreBoard, BorderLayout.EAST);
        this.add(jPanel, BorderLayout.WEST);
        this.setSize(1280, 840);
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

    private void fireChangeDirectionListeners(Direction direction) {
        ChangeDirectionEvent evt = new ChangeDirectionEvent(this, direction);
        for (ChangedDirectionListeners e : list)
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
                return switch (columnIndex) {
                    case 0 -> best.get(rowIndex).getNick();
                    case 1 -> best.get(rowIndex).getScore();
                    default -> null;
                };
            }
        });
        boardPanel.add(jTable, BorderLayout.WEST);
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
        jDialog.add(jLabel, BorderLayout.NORTH);
        jDialog.add(jTextField, BorderLayout.CENTER);
        jDialog.add(jButton, BorderLayout.SOUTH);
        jDialog.setVisible(true);

        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nick;
                nick = jTextField.getText();
                fireAddPlayerListener(nick, evt.getScore());
            }
        });

    }

    private void fireAddPlayerListener(String s, int a) {
        AddPlayerEvent addPlayerEvent = new AddPlayerEvent(this, s, a);
        for (AddPlayerListener c : addPlayerListeners)
            c.addPlayer(addPlayerEvent);
    }
}
