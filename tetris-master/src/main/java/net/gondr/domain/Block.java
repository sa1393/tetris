package net.gondr.domain;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Block {
	private Color color;
	private boolean fill;
	public boolean pre;
	private double x;
	private double y;
	private double size;
	private double borderSize;
	private int playerNum;
	
	public Block(double x, double y, double size) {
		color = Color.WHITE;
		fill = false;
		pre = false;
		this.x = x;
		this.y = y;
		this.size = size;
		this.borderSize = 2;
		playerNum = 0;
	}
	
	public void setPreData(boolean pre, Color color) {
		this.pre = pre;
		this.color = color;
	}
	
	public void render(GraphicsContext gc) {
		if(fill) {
			gc.setFill(color.BLACK.darker());
			gc.fillRoundRect(x, y, size, size, 0, 0);
			
			gc.setFill(color);
			gc.fillRoundRect(x + borderSize, y + borderSize, size - 2* borderSize, size - 2*borderSize, 0, 0);
		}else if(pre) {
			gc.setFill(Color.BLACK.darker());
			gc.fillRoundRect(x, y, size, size, 0, 0);
			
			gc.setFill(color.brighter().brighter());
			
			gc.fillRoundRect(
				x + borderSize, y + borderSize,
				size - 1* borderSize, size - 1*borderSize,
				0, 0);
		}
		else {
			gc.setFill(color.rgb(72, 72, 72));
			gc.fillRoundRect(x, y, size, size, 0, 0);
			
			gc.setFill(color.BLACK);
			gc.fillRoundRect(x + borderSize, y + borderSize, size - 2* borderSize, size - 2*borderSize, 0, 0);
		}
	}
	
	public void setData(boolean fill, Color color) {
		this.fill = fill;
		this.color = color;
	}
	
	public void setPlayerData(int playerNum) {
		this.playerNum = playerNum;
	}
	
	public int getPlayerNum() {
		return playerNum;
	}
	
	public boolean getFill() {
		return fill;
	}
	
	public Color getColor() {
		return color;
	}
	
	public boolean getPre() {
		return pre;
	}

	public void getPre(boolean pre) {
		this.pre = pre;
	}

	public void copyData(Block block) {
		this.pre = block.getPre();
		this.fill = block.getFill();
		this.color = block.getColor();
	}
	
	public void setPre(boolean pre) {
		this.pre = pre;
	}
}





