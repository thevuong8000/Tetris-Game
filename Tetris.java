import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

class Tetris implements KeyListener{

	public static final int WIDTH = 300, HEIGHT = 600;
	private JFrame window;
	private Board board;


	public Tetris(){
		window = new JFrame("Game started!");
		window.setSize(500, 638);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLocationRelativeTo(null);

		board = new Board();

		window.add(board);
		window.addKeyListener(board);

		window.setVisible(true);
	}

    @Override
	public void keyPressed(KeyEvent e){
		if(e.getKeyCode() == KeyEvent.VK_SPACE){
			new Window();
		}
	}

	@Override
	public void keyReleased(KeyEvent e){

	}

	@Override
	public void keyTyped(KeyEvent e){

	}

	public static void main(String[] args){
		new Tetris();
	}
}