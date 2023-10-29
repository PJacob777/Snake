class Player implements Comparable<Player> {
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
