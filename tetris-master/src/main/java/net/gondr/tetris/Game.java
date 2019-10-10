package net.gondr.tetris;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.animation.AnimationTimer;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import net.gondr.domain.Block;
import net.gondr.domain.Player;
import net.gondr.domain.ScoreVO;
import net.gondr.util.JDBCUtil;

public class Game {
	private GraphicsContext gc;
	public Block[][] board;
	
	private Block vBlock = new Block(20, 20, 10);

	// 게임판의 너비와 높이를 저장
	private double width;
	private double height;

	private AnimationTimer mainLoop; // 게임의 메인 루프
	private long before;

	public Player player; // 지금 움직이는 블록
	public Player player2;
	private double blockDownTime = 0;

	private int score = 0;

	private Canvas nextBlockCanvas;
	private Canvas nextBlockCanvas2;
	private GraphicsContext nbgc;
	private GraphicsContext nbgc2;
	
	private double nbWidth;
	private double nbHeight;
	private double nbWidth2; 
	private double nbHeight2;

	private Label scoreLabel;

	private boolean gameOver = false;

	private ObservableList<ScoreVO> list;

	public Game(Canvas canvas, Canvas nextBlockCanvas, Canvas nextBlockCanvas2, Label scoreLabel, ObservableList<ScoreVO> list) {
		this.list = list;
		width = canvas.getWidth();
		height = canvas.getHeight();
		this.nextBlockCanvas = nextBlockCanvas;
		this.nextBlockCanvas2 = nextBlockCanvas2;
		this.scoreLabel = scoreLabel;

		this.nbgc = this.nextBlockCanvas.getGraphicsContext2D();
		this.nbWidth = this.nextBlockCanvas.getWidth();
		this.nbHeight = this.nextBlockCanvas.getHeight();
		
		this.nbgc2 = this.nextBlockCanvas2.getGraphicsContext2D();
		this.nbWidth2 = this.nextBlockCanvas2.getWidth();
		this.nbHeight2 = this.nextBlockCanvas2.getHeight();

		double size = (width - 4) / 12;
		board = new Block[20][12]; // 게임판을 만들어주고

		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 12; j++) {
				board[i][j] = new Block(j * size + 2, i * size + 2, size);
			}
		}
		gc = canvas.getGraphicsContext2D();

		mainLoop = new AnimationTimer() {
			@Override
			public void handle(long now) {
				update((now - before) / 1200000000d);
				before = now;
				render();
			}
		};

		before = System.nanoTime();
		player = new Player(board, 12, 1);
		player2 = new Player(board, 12, 2);
		vBlock.setData(false, Color.WHITE);
		vBlock.setPreData(false, Color.WHITE);

		gameOver = true;

		reloadTopScore();
	}

	public void gameStart() {
		gameOver = false;
		mainLoop.start();
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 12; j++) {
				board[i][j].setData(false, Color.WHITE);
				board[i][j].setPreData(false, Color.WHITE);
			}
		}
	}

	public void reloadTopScore() {
		list.clear();
		Connection con = JDBCUtil.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM tetris ORDER BY score DESC LIMIT 0, 12";

		try {
			pstmt = con.prepareStatement(sql); // SQL을 준비
			rs = pstmt.executeQuery(); // 실행

			while (rs.next()) {
				ScoreVO temp = new ScoreVO();
				temp.setId(rs.getInt("id"));
				temp.setScore(rs.getInt("score"));
				temp.setName(rs.getString("name"));

				list.add(temp);
			}

		} catch (Exception e) {
			System.out.println("DB 접속 오류");
		} finally {
			JDBCUtil.close(rs);
			JDBCUtil.close(pstmt);
			JDBCUtil.close(con);
		}
	}

	public void update(double delta) {
		if (gameOver)
			return;

		blockDownTime += delta;

		double limit = 0.5 - score / 100d;

		if (limit < 0.1) {
			limit = 0.1;
		}

		if (blockDownTime >= limit) {
			player.down();
			player2.down();
			blockDownTime = 0;
		}
	}

	public void checkLineStatus() {
		for (int i = 19; i >= 0; i--) {
			boolean clear = true;
			for (int j = 0; j < 12; j++) {
				if (!board[i][j].getFill()) {
					clear = false;
					break;
				}
			}

			if (clear) {
				score++;
				for (int j = 0; j < 12; j++) {
					board[i][j].setData(false, Color.WHITE);
					board[i][j].setPreData(false, Color.WHITE);
				}

				for (int k = i - 1; k >= 0; k--) {
					for (int j = 0; j < 12; j++) {
						if(board[k+1][j].getPlayerNum() != 0) {
						}
						else if(board[k][j].getPlayerNum() != 0) {
							board[k+1][j].copyData(vBlock);
						}
						else {
							board[k + 1][j].copyData(board[k][j]);
						}
					}
				}

				for (int j = 0; j < 12; j++) {
					board[0][j].setData(false, Color.WHITE);
					board[0][j].setPreData(false, Color.WHITE);
				}
				i++;
				for(int l = 0; l < 20; l++) {
					for(int m = 0; m < 12; m++) {
						board[l][m].setPre(false);
					}
				}
			}
		}

	}

	public void render() {
		gc.clearRect(0, 0, width, height);
		gc.setStroke(Color.rgb(0, 0, 0));
		gc.setLineWidth(2);
		gc.strokeRect(0, 0, width, height);

		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 12; j++) {
				board[i][j].render(gc);
			}
		}

		scoreLabel.setText("Score : \n" + score);

		player.render(nbgc, nbWidth, nbHeight);
		player2.render(nbgc2, nbWidth2, nbHeight2);

		if (gameOver) {
			gc.setFont(new Font("Arial", 30));
			gc.setTextAlign(TextAlignment.CENTER);
			gc.strokeText("Game Over", width / 2, height / 2);
		}

	}

	public void keyHandler(KeyEvent e) {
		if (gameOver)
			return;
		player.keyHandler(e);
		player2.keyHandler(e);
	}

	public void setGameOver() {
		gameOver = true;
		render();
		mainLoop.stop();

		App.app.openPopup(score);
	}
}
