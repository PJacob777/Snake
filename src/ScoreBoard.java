import javax.swing.*;

class ScoreBoard extends JPanel implements AddPointListeners {
    private int Score;
    private JTextField jTextField;

    public ScoreBoard() {
        this.Score = 0;
        JLabel jLabel = new JLabel("Score");
        this.jTextField = new JTextField(Score + "");
        jTextField.setEditable(false);
        this.add(jLabel);
        this.add(jTextField);
    }

    @Override
    public void AddPoint(AddPointEvent evt) {
        Score++;
        jTextField.setText(Score + "");
        repaint();
    }
}
