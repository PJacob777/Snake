import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

class Snake extends Thread implements ChangedDirectionListeners, AddPlayerListener {
    private ArrayList<AddPlayerListener> addPlayerListeners;
    private ArrayList<ShowBestPlayersListener> showBestPlayersListeners;
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
        ArrayList<Player> arrayList = new ArrayList<>();
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
                int d = r << 8 * i;
                value |= d;
            }
            arrayList.add(new Player(nick, value));
        }
        arrayList.sort(null);
        this.bestScores = arrayList;
        bestScores.remove(bestScores.size() - 1);
        fis.close();
        for (Player p : bestScores)
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
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        int points = 0;
        while (game && !((this.getNumberOfRow(head) == 0 && newDirection == Direction.UP) || (this.getNumberOfRow(head) == 15 && newDirection == Direction.DOWN) || (head % 25 == 0 && newDirection == Direction.RIGHT) || (head % 25 == 1 && newDirection == Direction.LEFT))) {
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

            for (Integer i : copyOfFood) {
                if (head == i) {
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
                previousPosition = tmp;
                blankRows.add(this.getNumberOfRow(tmp));
                blankColums.add(this.getNumberOfColumn(tmp));

            }
            tail = previousPosition;
            //teraz dodaj poruszanie sie dla Ciala, jesli lista turn jest pusta to poruszasz sie z aktualnym direction, dopiero po osiagnieciu turn zmieniasz direction, po przejsciu tail przez turn usun turn i usun preciousDirection
            isEmptyFood(foodRow, foodColumn);
            fireTickEventListeners(changedHeadColumn, changedHeadRow, columsOfBody, rowsOfBody, foodRow, foodColumn, blankColums, blankRows);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        fireShowBestPlayersListeners(bestScores);
        Player lastScore = bestScores.get(bestScores.size() - 1);
        if (points > lastScore.getScore()) {
            bestScores.remove(lastScore);
            fireAddPlayerListeners(points);

        }

        System.out.println("Koniec gry");
    }

    private void fireAddPointsListeners() {
        AddPointEvent addPointEvent = new AddPointEvent(this);
        for (AddPointListeners a : listeners)
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
        if (IsGoodDirection(newDirection, event.getNewDirection())) {
            this.newDirection = event.getNewDirection();
        }
        System.out.println(this.newDirection);
    }

    private void isEmptyFood(ArrayList<Integer> foodRow, ArrayList<Integer> foodColumn) {
        if (food.isEmpty())
            addFood(foodRow, foodColumn);
    }

    private void writeToFile() throws IOException {
        File file = new File("D:\\bin\\result.bin");
        FileOutputStream fos = new FileOutputStream(file);
        for (Player p : bestScores) {
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

    public void addAddPointListeners(AddPointListeners addPointListeners) {
        listeners.add(addPointListeners);
    }

    public void addTickEventListeners(TickEventListeners a) {
        tickEventListeners.add(a);
    }

    private void fireTickEventListeners(int changedHeadColumn, int changedHeadRow, ArrayList<Integer> columsOfBody, ArrayList<Integer> rowsOfBody, ArrayList<Integer> foodRow, ArrayList<Integer> foodColumn, ArrayList<Integer> blankColums, ArrayList<Integer> blankRows) {
        TickEvent event = new TickEvent(this, columsOfBody, rowsOfBody, changedHeadColumn, changedHeadRow, foodColumn, foodRow, blankColums, blankRows);
        for (TickEventListeners l : tickEventListeners)
            l.updateCells(event);
    }

    public void addShowBestPlayersListener(ShowBestPlayersListener listener) {
        showBestPlayersListeners.add(listener);
    }

    private void fireShowBestPlayersListeners(ArrayList<Player> list) {
        ShowBestPlayers evt = new ShowBestPlayers(this, list);
        for (ShowBestPlayersListener e : showBestPlayersListeners)
            e.showTheBestPlayers(evt);

    }

    public void addAddPlayerListener(AddPlayerListener listener) {
        addPlayerListeners.add(listener);
    }

    private void fireAddPlayerListeners(int b) {
        AddPlayerEvent addPlayerEvent = new AddPlayerEvent(this, "", b);
        for (AddPlayerListener a : addPlayerListeners)
            a.addPlayer(addPlayerEvent);
    }

    @Override
    public void addPlayer(AddPlayerEvent evt) {
        System.out.println();
        System.out.println(evt.getNick());
        System.out.println(evt.getScore());
        bestScores.add(new Player(evt.getNick(), evt.getScore()));
        bestScores.sort(null);
        try {
            writeToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
