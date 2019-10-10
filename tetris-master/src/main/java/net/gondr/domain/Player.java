package net.gondr.domain;

import java.util.Random;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import net.gondr.tetris.App;

public class Player {
	private Point2D[][][] shape = new Point2D[7][][];

	private int current = 0;
	private int rotate = 0;
	private Color[] colorSet = new Color[2];

	private Random rnd;

	private int x = 5;
	private int y = 2;

	private int playerNum;

	private int preX;
	private int preY;

	private Block[][] board;

	private int nextBlock;

	private int saveWidth;

	private boolean nextPossible = false;
	
	public boolean previewOn = true;

	public Player(Block[][] board, int boardWidthNum, int num) {
		this.board = board;
		this.x = (boardWidthNum / 3) * num;
		this.y = 2;
		this.saveWidth = boardWidthNum;
		playerNum = num;
		shape[0] = new Point2D[2][];
		shape[0][0] = getPointArray("0,-1:0,0:0,1:0,2");
		shape[0][1] = getPointArray("-1,0:0,0:1,0:2,0");
		shape[1] = new Point2D[1][];
		shape[1][0] = getPointArray("0,0:1,0:0,1:1,1");
		shape[2] = new Point2D[4][];
		shape[2][0] = getPointArray("0,-2:0,-1:0,0:1,0");
		shape[2][1] = getPointArray("0,1:0,0:1,0:2,0");
		shape[2][2] = getPointArray("-1,0:0,0:0,1:0,2");
		shape[2][3] = getPointArray("-2,0:-1,0:0,0:0,-1");

		shape[3] = new Point2D[4][];
		shape[3][0] = getPointArray("0,-2:0,-1:0,0:-1,0");
		shape[3][1] = getPointArray("0,-1:0,0:1,0:2,0");
		shape[3][2] = getPointArray("0,0:1,0:0,1:0,2");
		shape[3][3] = getPointArray("-2,0:-1,0:0,0:0,1");

		shape[4] = new Point2D[2][];
		shape[4][0] = getPointArray("0,0:-1,0:0,-1:1,-1");
		shape[4][1] = getPointArray("0,0:0,-1:1,0:1,1");

		shape[5] = new Point2D[2][];
		shape[5][0] = getPointArray("0,0:0,-1:-1,-1:1,0");
		shape[5][1] = getPointArray("0,0:1,0:1,-1:0,1");

		shape[6] = new Point2D[4][];
		shape[6][0] = getPointArray("0,0:0,-1:-1,0:1,0");
		shape[6][1] = getPointArray("0,0:0,-1:1,0:0,1");
		shape[6][2] = getPointArray("0,0:0,1:-1,0:1,0");
		shape[6][3] = getPointArray("0,0:-1,0:0,-1:0,1");

		colorSet[0] = Color.rgb(97, 20, 39);
		colorSet[1] = Color.rgb(149, 137, 118);

		rnd = new Random();
		current = rnd.nextInt(shape.length);
		nextBlock = rnd.nextInt(shape.length);

		getPrePosition();
		draw(false);
	}
	
	private void resetPre() {
		for(int i = 0; i < 20; i++) {
			for(int j = 0; j < 12; j++) {
				board[i][j].setPre(false);
			}
		}
	}
	
	public void setPreview(boolean preview) {
		draw(false);
		this.previewOn = preview;
		resetPre();
		getPrePosition();
		draw(false);
	}
	
	private void getPrePosition() {
		preX = x;
		preY = y;

		while (true) {
			preY += 1;
			if (!checkPossible(preX, preY, true)) {
				preY -= 1;
				break;
			}
		}

	}

	private void setPlayerData() {
		for (int i = 0; i < shape[current][rotate].length; i++) {
			Point2D point = shape[current][rotate][i];
			int bx = (int) point.getX() + x;
			int by = (int) point.getY() + y;

			board[by][bx].setPlayerData(0);
		}
	}

	private void draw(boolean remove) {
		for (int i = 0; i < shape[current][rotate].length; i++) {
			Point2D point = shape[current][rotate][i];
			int px = (int) point.getX() + preX;
			int py = (int) point.getY() + preY;
			try {
				if(previewOn) {
					board[py][px].setPreData(!remove, colorSet[playerNum - 1]);
				}
			} catch (Exception e) {
				System.out.println(py + ", " + px + ", " + preX + ", " + preY);
			}
			int bx = (int) point.getX() + x;
			int by = (int) point.getY() + y;
			board[by][bx].setData(!remove, colorSet[playerNum - 1]);

			board[by][bx].setPlayerData(playerNum);
		}
	}

	public Point2D[] getPointArray(String pointStr) {
		Point2D[] arr = new Point2D[4];
		String[] pointList = pointStr.split(":");
		for (int i = 0; i < pointList.length; i++) {
			String[] point = pointList[i].split(",");
			double x = Double.parseDouble(point[0]);
			double y = Double.parseDouble(point[1]);
			arr[i] = new Point2D(x, y);
		}
		return arr;
	}

	public void keyHandler(KeyEvent e) {
		int dx = 0, dy = 0;
		boolean rot = false;
		if (playerNum == 2) {
			if (e.getCode() == KeyCode.LEFT) {
				dx -= 1;
			} else if (e.getCode() == KeyCode.RIGHT) {
				dx += 1;
			} else if (e.getCode() == KeyCode.UP) {
				rot = true;
			}

			move(dx, dy, rot);

			if (e.getCode() == KeyCode.DOWN) {
				down();
			} else if (e.getCode() == KeyCode.ENTER) {
				while (!down()) {
				}
			}
		} else if (playerNum == 1) {
			if (e.getCode() == KeyCode.A) {
				dx -= 1;
			} else if (e.getCode() == KeyCode.D) {
				dx += 1;
			} else if (e.getCode() == KeyCode.W) {
				rot = true;
			}

			move(dx, dy, rot);

			if (e.getCode() == KeyCode.S) {
				down();
			} else if (e.getCode() == KeyCode.SPACE) {
				while (!down()) {
				}
			}
		}

	}

	public void move(int dx, int dy, boolean rot) {
		draw(true);
		x += dx;
		y += dy;
		if (rot) {
			rotate = (rotate + 1) % shape[current].length;
		}

		if (!checkPossible(x, y, false)) {
			x -= dx;
			y -= dy;
			if (rot) {
				rotate = rotate - 1 < 0 ? shape[current].length - 1 : rotate - 1;
			}
		}
		getPrePosition();
		draw(false);
	}

	public boolean down() {
		draw(true);
		setPlayerData();
		getPrePosition();
		y += 1;

		if (!checkPossible(x, y, false)) {
			y -= 1;
			draw(false);
			setPlayerData();
			App.app.game.checkLineStatus();
			if (nextPossible) {
				getNextBlock();
			}
			draw(false);
			
			return true;
		}
		draw(false);
		return false;
	}

	private void getNextBlock() {
		current = nextBlock;
		nextBlock = rnd.nextInt(shape.length);
		x = (saveWidth / 3) * playerNum;
		y = 2;
		rotate = 0;

		if (!checkPossible(x, y, false)) {
			if(!nextPossible) {
				getPrePosition();
				return;
			}
			draw(true);
			App.app.game.setGameOver();
		}
		getPrePosition();
		
	}

	private boolean checkPossible(int x, int y, boolean pre) {
		for (int i = 0; i < shape[current][rotate].length; i++) {
			int bx = (int) shape[current][rotate][i].getX() + x;
			int by = (int) shape[current][rotate][i].getY() + y;

			if (bx < 0 || by < 0 || bx >= 12 || by >= 20) {
				nextPossible = true;
				return false;
			}

			if (board[by][bx].getFill()) {
				if (board[by][bx].getPlayerNum() != 0) {
					nextPossible = false;
				} else {
					nextPossible = true;
				}
				return false;

			}
		}
		return true;
	}

	public void render(GraphicsContext gc, double width, double height) {
		Color color = colorSet[playerNum - 1];
		Point2D[] block = shape[nextBlock][0];
		gc.clearRect(0, 0, width, height);
		
		gc.setFill(color.BLACK);
		gc.fillRoundRect(0, 0, width, height, 0, 0);
		
		gc.setFill(color.rgb(68, 68, 68));
		gc.fillRoundRect(3, 3, width -6, height -6, 0, 0);
		
		double x = width / 2;
		double y = height / 2;
		double size = width / 4 - 12;
		if (nextBlock == 0) {
			y -= size;
		}

		for (int i = 0; i < block.length; i++) {
			double dx = x + block[i].getX() * size;
			double dy = y + block[i].getY() * size;
			
			
			gc.setFill(color.BLACK);
			gc.fillRoundRect(dx, dy, size, size, 0, 0);

			gc.setFill(color);
			gc.fillRoundRect(dx + 2, dy + 2, size - 4, size - 4, 0, 0);
			
			

		}
	}
}
