import java.awt.image.BufferedImage;
import java.awt.Graphics;

import java.util.Random;

public class Shape{
	private BufferedImage blocks;
	private int[][] coords;
	private Board board;
	private int deltaX = 0;
	private int x, y;
	private Random rand;

	private final int normalSpeed = 500;
	private final int fastSpeed = 70;

	private int dropPace;
	private long time, lastMoment;

	private boolean finish;
	private boolean gettingDown;

	public Shape(BufferedImage blocks, int[][] coords, Board board){
		this.blocks = blocks;
		this.coords = coords;
		this.board = board;

		rand = new Random();
		x = rand.nextInt(board.getBlockWidth() - coords[0].length);
		y = 0;

		time = 0;
		lastMoment = 0;

		finish = false;
		gettingDown = false;

		dropPace = normalSpeed;
	}

	private boolean isValid(){
		if(x < 0 || x + coords[0].length > board.getBlockWidth()) return false;
      	if(collision()) return false;
      	return true;
	}

	public boolean collision(){
		if(y + coords.length > board.getBlockHeight()) return true;
		for(int i = 0; i < coords.length; i++){
      		for(int j = 0; j < coords[i].length; j++){
      			if(coords[i][j] == 0) continue;
      			if(this.board.getTable()[y + i][x + j] != -1) return true;
      		}
      	}
		return false;
	}

	public void update() throws InterruptedException{

		time += System.currentTimeMillis() - lastMoment;
		lastMoment = System.currentTimeMillis();
		if(finish){
			for(int i = 0; i < coords.length; i++){
				for(int j = 0; j < coords[i].length; j++){
					if(coords[i][j] == 0) continue;
					board.getTable()[y + i][x + j] = board.getCurShapeIndex();
				}
			}
			board.checkFullLine();
			board.getNewShape();
		}

		x += deltaX;
		if(!isValid()) x -= deltaX;

		if(time >= dropPace){
			y++;
			if(collision()){
				y--;
				finish = true;
			}
			time = 0;
		}
		
		deltaX = 0;
 	}

 	public void rotate(){
 		int newShape[][] = new int[this.coords[0].length][this.coords.length];
 		
 		for(int i = 0; i < coords.length; i++){
 			for(int j = 0; j < coords[i].length; j++){
 				newShape[j][coords.length - 1 - i] = this.coords[i][j];
 			}
 		}

 		int saveCoords[][] = this.coords;
 		this.coords = newShape;
 		if(!isValid()) this.coords = saveCoords;
 	}

	public void render(Graphics g){
		for(int i = 0; i < coords.length; i++){
			for(int j = 0; j < coords[i].length; j++){
				if(coords[i][j] == 0) continue;
				g.drawImage(blocks, 
					j * board.getBlockSize() + x * board.getBlockSize(), 
					i * board.getBlockSize() + y * board.getBlockSize(), null);
			}	
		}
	}

 	public void getDown(){
 		gettingDown = true;
 		while(!collision()) y++;
 		y--;
 		finish = true;
 		gettingDown = false;
 	}

 	public void FastAndFurious(){
 		dropPace = fastSpeed;
 	}

 	public void beNormal(){
 		dropPace = normalSpeed;
 	}

	public void setDeltaX(int deltaX){
		this.deltaX = deltaX;
	}

	public BufferedImage getBlocks(){
		return blocks;
	}

	public int[][] getCoords(){
		return coords;
	}

	public void setNextShapePosition(Graphics g){
		x = 12;
		y = 5;
		render(g);
		x = rand.nextInt(board.getBlockWidth() - coords[0].length);
		y = 0;
	}
}