package com.jeffsul.tetris;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Block {
	private enum BlockShape {SQUARE, PRONG, SNAKE, SNAKE2, HOOK, HOOK2, LINE};
		
	private static final int SQ = 20;
	
	public int x = 4 * SQ;
	public int y = -SQ;
	private Point[] squares;
	private Color color;
	private Point centre = null;
		
	public Block(Tetris t) {
		switch (BlockShape.values()[(int) (Math.random() * 7)]) {
			case SQUARE:
				squares = new Point[] {new Point(x, y), new Point(x + SQ, y), new Point(x + SQ, y + SQ), new Point(x, y + SQ)};
				centre = null;
				color = Color.RED;
				break;
			case PRONG:
				squares = new Point[] {new Point(x, y + SQ), new Point(x+SQ, y), new Point(x+SQ, y+SQ), new Point(x+SQ, y+2*SQ)};
				color = Color.YELLOW;
				centre = squares[2];
				break;
			case HOOK:
				squares = new Point[] {new Point(x, y), new Point(x+SQ, y), new Point(x+SQ, y+SQ), new Point(x+SQ, y+2*SQ)};
				color = Color.ORANGE;
				centre = squares[2];
				break;
			case HOOK2:
				squares = new Point[] {new Point(x, y), new Point(x+SQ, y), new Point(x, y+SQ), new Point(x, y+2*SQ)};
				color = Color.GREEN;
				centre = squares[2];
				break;
			case SNAKE:
				squares = new Point[] {new Point(x, y), new Point(x+SQ, y), new Point(x+SQ, y+SQ), new Point(x+2*SQ, y+SQ)};
				color = Color.MAGENTA;
				centre = squares[2];
				break;
			case SNAKE2:
				squares = new Point[] {new Point(x, y+SQ), new Point(x+SQ, y), new Point(x+SQ, y+SQ), new Point(x+SQ*2, y)};
				color = Color.CYAN;
				centre = squares[2];
				break;
			case LINE:
				squares = new Point[] {new Point(x, y), new Point(x+SQ, y), new Point(x+SQ*2, y), new Point(x+SQ*3, y)};
				color = Color.BLUE;
				centre = squares[2];
				break;
		}
	}
	
	public int getMinY() {
		int min = Integer.MAX_VALUE;
		for (Point sq : squares) {
			min = Math.min(sq.y, min);
		}
		return min;
	}
	
	public void move() {
		for (Point sq : squares) {
			sq.y += SQ;
		}
	}
	
	public void moveX(int xVal, Point[][] sqrs) {
		int dist = xVal * SQ;
		for (Point sq : squares) {
			if (nearX(sq.x + dist, sq.y, sqrs)) {
				return;
			}
		}
		
		for (Point sq : squares) {
			sq.x += dist;
		}
	}
	
	public void draw(Graphics g, boolean nextBlock, boolean microMode) {
		Graphics2D g2 = (Graphics2D) g;
		if (microMode) {
			BufferedImage img = null;
			try {
				img = ImageIO.read(new File(getClass().getResource("microsoft.jpg").getFile()));
			} catch (Exception e) { }
			
			for (Point sq : squares) {
				if (!nextBlock) {
					g2.drawImage(img, sq.x, sq.y, SQ, SQ, null);
				} else {
					g2.drawImage(img, sq.x - x + 20, sq.x - y + 20, SQ, SQ, null);
				}
			}
			return;
		}
		
		GeneralPath l = new GeneralPath();
		for (Point sq : squares) {
			if (!nextBlock) {
				l.moveTo(sq.x + 1, sq.y + 1);
				l.lineTo(sq.x + SQ - 1, sq.y + 1);
				l.lineTo(sq.x + SQ - 1, sq.y + SQ - 1);
				l.lineTo(sq.x + 1, sq.y + SQ - 1);
				l.lineTo(sq.x + 1, sq.y + 1);
			} else {
				l.moveTo(sq.x - x + 20, sq.y - y + 20);
				l.lineTo(sq.x+SQ - x + 20, sq.y - y + 20);
				l.lineTo(sq.x+SQ - x + 20, sq.y+SQ - y + 20);
				l.lineTo(sq.x - x + 20, sq.y+SQ - y + 20);
				l.lineTo(sq.x - x + 20, sq.y - y + 20);
			}
		}
		l.closePath();
		g2.setColor(Color.GRAY);
		g2.draw(new BasicStroke(1.5f).createStrokedShape(l));
		g2.setColor(color);
		g2.fill(l);
		g2.draw(l);
	}
	
	public boolean hasStopped(Point[][] sqrs) {
		for (int i = 0; i < squares.length; i++) {
			if (nearFloor(squares[i].x, squares[i].y+SQ, sqrs)) {
				return true;
			}
		}
		return false;
	}
	
	public void rotate(Point[][] sqrs) {
		if (centre == null) {
			return;
		}
		
		int squaresLength = squares.length;
		Point[] newSquares = new Point[squaresLength];
		for (int i = 0; i < squaresLength; i++)
			newSquares[i] = new Point(squares[i].x, squares[i].y);
		
		boolean canRotate = true;
		for (int i = 0; i < squares.length; i++) {
			Point square = squares[i];
			if (square != centre) {
				newSquares[i].x = centre.x - square.y - centre.y;
				newSquares[i].y = centre.y + square.x - centre.x;
				if (nearX(newSquares[i].x, newSquares[i].y, sqrs) || nearFloor(newSquares[i].x, newSquares[i].y, sqrs)) {
					canRotate = false;
					break;
				}
			}
		}
		
		if (canRotate) {
			for (int i = 0; i < newSquares.length; i++) {
				squares[i].x = newSquares[i].x;
				squares[i].y = newSquares[i].y;
			}
		}
	}
	
	public boolean nearFloor(int x, int y, Point[][] squares) {
		if (y >= Tetris.HEIGHT) {
			return true;
		}
		if (y < 0) {
			return false;
		}
		if (squares[x/20][y/20] == null) {
			return false;
		}
		return true;
	}
	
	public boolean nearX(int x, int y, Point[][] squares) {
		if (y < 0) {
			return false;
		}
		if (x >= Tetris.WIDTH || x < 0 || y >= Tetris.HEIGHT) {
			return true;
		}
		if (squares[x / 20][y / 20] == null) {
			return false;
		}
		return true;
	}
	
	public Point[] getSquares() {
		return squares;
	}
}

