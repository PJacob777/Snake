import java.util.EventObject;

class AddPlayerEvent extends EventObject {
    private String nick;
    private int score;

    public AddPlayerEvent(Object source, String nick, int score) {
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
