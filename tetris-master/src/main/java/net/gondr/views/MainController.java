package net.gondr.views;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import net.gondr.domain.Player;
import net.gondr.domain.ScoreVO;
import net.gondr.tetris.App;
import net.gondr.tetris.Game;

public class MainController {
	Player player1;
	Player player2;
	
	boolean preview = true;
	
	@FXML
	private Canvas gameCanvas;
	
	@FXML
	private Button button;
	
	@FXML
	private Canvas nextBlockCanvas;
	
	@FXML
	private Canvas nextBlockCanvas2;
	
	@FXML
	private Label scoreLabel;
	
	@FXML 
	private ListView<ScoreVO> listView;
	private ObservableList<ScoreVO> list;
	
	@FXML
	public void previewButton() {
		System.out.println("ds");
		if(preview) {
			player1.setPreview(false);
			player2.setPreview(false);
			button.setText("Preview OFF");
		}
		else {
			player1.setPreview(true);
			player2.setPreview(true);
			button.setText("Preview ON");
		}
		preview = !preview;
	}
	
	@FXML
	public void initialize() {
		System.out.println("메인 레이아웃 생성 완료");
		list = FXCollections.observableArrayList();
		listView.setItems(list);
		App.app.game = new Game(gameCanvas, nextBlockCanvas, nextBlockCanvas2, scoreLabel, list);
		player1 = App.app.game.player;
		player2 = App.app.game.player2;
	}
	
	public void gameStart() {
		App.app.game.gameStart();
	}
}
