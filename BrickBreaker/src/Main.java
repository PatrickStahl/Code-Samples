import javax.swing.JFrame;

public class Main
{
    public static void main(String[] args)
    {
        JFrame window = new JFrame();
        Gameplay gameplay = new Gameplay();

        window.setBounds(10,10,700,600);
        window.setLocationRelativeTo(null);
        window.setTitle("BrickBreaker");
        window.setResizable(false);
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        window.add(gameplay);

    }
}