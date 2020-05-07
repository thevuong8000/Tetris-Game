import javax.swing.Timer;
import javax.swing.JPanel;
import javax.imageio.ImageIO;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Random;


public class Board extends JPanel implements KeyListener{

	private final int blockSize = 30;
	private final int blockWidth = 10, blockHeight = 20;
	private BufferedImage blocks;
	private Shape shapes[] = new Shape[7];
	private Shape curShape;
	private Shape nextShape;
	private Random rand;

	private Timer timer;
	private final int FPS = 60;
	private final int delay = 1000/FPS;

	private int index;
	private int nextIndex;
	private int[][] table;

	private int score;
	private boolean gameOver;
	private boolean inGame;

	public Board(){
		try{
			blocks = ImageIO.read(Board.class.getResource("textures/blocks.png"));
		} 
		catch(IOException e){
			e.printStackTrace();
		}

		timer = new Timer(delay, new ActionListener(){
			public void actionPerformed(ActionEvent e){
				update();
				repaint();
			}
		});

		table = new int[blockHeight][blockWidth];

		for(int i = 0; i < blockHeight; i++){
			for(int j = 0; j < blockWidth; j++){
				table[i][j] = -1;
			}
		}

		rand = new Random();
		score = 0;
		gameOver = false;
		inGame = false;

		timer.start();

		shapes[0] = new Shape(blocks.getSubimage(0, 0, blockSize, blockSize), new int[][]{
			{1, 1, 1, 1} // I-shape
		}, this);

		shapes[1] = new Shape(blocks.getSubimage(blockSize, 0, blockSize, blockSize), new int[][]{
			{1, 1, 0},
			{0, 1, 1}	// Z-shape
		}, this);

		shapes[2] = new Shape(blocks.getSubimage(2 * blockSize, 0, blockSize, blockSize), new int[][]{
			{0, 1, 1},
			{1, 1, 0}	// S-shape
		}, this);

		shapes[3] = new Shape(blocks.getSubimage(3 * blockSize, 0, blockSize, blockSize), new int[][]{
			{1, 0, 0},
			{1, 1, 1}	// L-shape
		}, this);

		shapes[4] = new Shape(blocks.getSubimage(4 * blockSize, 0, blockSize, blockSize), new int[][]{
			{0, 0, 1},
			{1, 1, 1}	// J-shape
		}, this);

		shapes[5] = new Shape(blocks.getSubimage(5 * blockSize, 0, blockSize, blockSize), new int[][]{
			{1, 1, 1},
			{0, 1, 0}	// T-shape
		}, this);

		shapes[6] = new Shape(blocks.getSubimage(6 * blockSize, 0, blockSize, blockSize), new int[][]{
			{1, 1},
			{1, 1}		// O-shape
		}, this);

		curShape = null;
		nextIndex = rand.nextInt(7);
		nextShape = new Shape(shapes[nextIndex].getBlocks(), shapes[nextIndex].getCoords(), this);
		// getNewShape();
	}

	private void reset(){
		for(int i = 0; i < blockHeight; i++){
			for(int j = 0; j < blockWidth; j++){
				table[i][j] = -1;
			}
		}

		score = 0;
		gameOver = false;
		inGame = true;

		curShape = null;
		nextIndex = rand.nextInt(7);
		nextShape = new Shape(shapes[nextIndex].getBlocks(), shapes[nextIndex].getCoords(), this);

		timer.start();
	}

	private void checkLosing(){
		if(!curShape.collision()) return;
		gameOver = true;
		inGame = false;
	}

	public void getNewShape(){
		index = nextIndex;
		curShape = nextShape;
		nextIndex = rand.nextInt(7);
		nextShape = new Shape(shapes[nextIndex].getBlocks(), shapes[nextIndex].getCoords(), this);
		checkLosing();
	}

	private boolean isFulFilled(int row[]){
		for(int square : row){
			if(square == -1) return false;
		}
		return true;
	}

	private void dropDown(int lastRow){
		while(lastRow > 0){
			table[lastRow] = table[lastRow - 1];
			lastRow--;
		}
		for(int i = 0; i < blockWidth; i++) table[0][i] = -1;
	}

	public void checkFullLine(){
		for(int i = 0; i < blockHeight; i++){
			if(!isFulFilled(table[i])) continue;
			score++;
			dropDown(i);
		}
	}

	public void update(){
		if(curShape == null) return;
		try{
			curShape.update();
		} catch(Exception e){
			System.out.println(e);
		}
		
	}

	public void paintComponent(Graphics g){
		super.paintComponent(g);
		if(curShape != null) curShape.render(g);
		nextShape.setNextShapePosition(g);
		g.setColor(Color.pink);
		g.setFont(new Font("TimesRoman", 0, 25));
		g.drawString("Score: " + score, 350, 100);
		
		g.setColor(Color.black);

		for(int i = 0; i < blockHeight; i++){
			for(int j = 0; j < blockWidth; j++){
				if(table[i][j] == -1) continue;
				g.drawImage(shapes[table[i][j]].getBlocks(), 
					j * blockSize,
					i * blockSize, null);
			}
		}

		for(int i = 0; i <= blockHeight; i++){
			g.drawLine(0, i * blockSize, blockWidth * blockSize, i * blockSize);
		}

		for(int i = 0; i <= blockWidth; i++){
			g.drawLine(i * blockSize, 0, i * blockSize, blockHeight * blockSize);
		}

		if(gameOver){
			g.setColor(Color.red);
			g.setFont(new Font("TimesRoman", Font.BOLD, 90));
			g.drawString("You're", 70, 250);
			g.drawString("Fucked up", 50, 310);

			timer.stop();
		}
	}

	public int getBlockSize(){
		return blockSize;
	}

	public int getBlockWidth(){
		return blockWidth;
	}

	public int getBlockHeight(){
		return blockHeight;
	}

	public int getCurShapeIndex(){
		return index;
	}

	public int[][] getTable(){
		return table;
	}

	public void updateTable(int x, int y, int value){
		table[x][y] = value;
	}

	@Override
	public void keyPressed(KeyEvent e){
		if(e.getKeyCode() == KeyEvent.VK_SPACE && !inGame){
			reset();
			getNewShape();
			return;
		}
		// if(gameOver) return;
		if(e.getKeyCode() == KeyEvent.VK_LEFT)
			curShape.setDeltaX(-1);
		else if(e.getKeyCode() == KeyEvent.VK_RIGHT)
			curShape.setDeltaX(1);
		else if(e.getKeyCode() == KeyEvent.VK_DOWN)
			curShape.FastAndFurious();
			// curShape.getDown();
		else if(e.getKeyCode() == KeyEvent.VK_UP)
			curShape.rotate();
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
			System.exit(0);
	}

	@Override
	public void keyReleased(KeyEvent e){
		if(e.getKeyCode() == KeyEvent.VK_DOWN)
			curShape.beNormal();
	}

	@Override
	public void keyTyped(KeyEvent e){

	}
}