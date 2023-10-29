import java.util.ArrayList;
import java.util.EventObject;

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
