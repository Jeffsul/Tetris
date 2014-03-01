package com.jeffsul.tetris;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.Timer;

public class Tetris extends JPanel implements ActionListener {	
	private static final long serialVersionUID = 1L;
	public static final int WIDTH = 200;
	public static final int HEIGHT = 420;
	
	private boolean running = true;
	private boolean gameOver;
	private boolean paused;
	
	private Graphics g;
	private Image img;
	
	private Block currBlock;
	private Block upcomingBlock;
	
	private boolean downKey;
	
	private Point[][] squares;
	private ArrayList<Block> blocks;
	
	private int score;
	private int level;
	
	private Timer t;
	private int speed;
	private int initialSpeed = 500;
	
	private String textBuffer;
	private boolean microMode;
	
	private TetrisGame tetrisGame;
	
	public Tetris(TetrisGame tg) {
		super();
		tetrisGame = tg;
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				int code = event.getKeyCode();
				if (code == KeyEvent.VK_ESCAPE || code == KeyEvent.VK_Q) {
					running = false;
				}
				if (!gameOver && !paused) {
					if (code == KeyEvent.VK_RIGHT) {
						currBlock.moveX(1, squares);
						render();
					} else if (code == KeyEvent.VK_LEFT) {
						currBlock.moveX(-1, squares);
						render();
					} else if (code == KeyEvent.VK_UP) {
						currBlock.rotate(squares);
						render();
					} else if (code == KeyEvent.VK_DOWN) {
						if (!downKey) {
							t.setDelay(speed / 4);
							downKey = true;
						}
					} else if (code == KeyEvent.VK_SPACE) {
						while (!currBlock.hasStopped(squares)) {
							currBlock.move();
						}
						render();
					} else if (code == KeyEvent.VK_ENTER) {
						if (textBuffer.equals("will")) {
							setScore(999999999);
							level = 1;
						} else if (textBuffer.equals("jeff")) {
							squares = new Point[WIDTH/20][HEIGHT/20];
							blocks = new ArrayList<Block>();
						} else if (textBuffer.equals("alex")) {
							Point[] squares = currBlock.getSquares();
							for (int i = 0; i < squares.length; i++) {
								if (Math.random() > 0.5)
									squares[i].x += (int) (Math.random() * 60);
								else
									squares[i].x -= (int) (Math.random() * 60);
								if (Math.random() > 0.5)
									squares[i].y += (int) (Math.random() * 60);
								else
									squares[i].y -= (int) (Math.random() * 60);
							}
						} else if (textBuffer.equals("matt")) {
							currBlock = new Block(Tetris.this);
						} else if (textBuffer.equals("neil")) {
							microMode = true;
						}
						textBuffer = "";
					} else {
						textBuffer += event.getKeyChar();
					}
				}
			}
			
			public void keyReleased(KeyEvent e) {
				int code = e.getKeyCode();
				if (!gameOver && !paused) {
					if (code == KeyEvent.VK_DOWN) {
						t.setDelay(speed);
						downKey = false;
					}
				}
			}
		});
		
		setFocusable(true);
		requestFocus();
	}
	
	public void startGame() {	
		if (t != null) {
			t.stop();
		} else {
			t = new Timer(speed, this);
		}
		
		textBuffer = "";
		microMode = false;
		
		gameOver = false;
		paused = false;
		downKey = false;
		
		currBlock = null;
		upcomingBlock = new Block(this);
		
		squares = new Point[WIDTH / 20][HEIGHT / 20];
		blocks = new ArrayList<Block>();
		score = 0;
		level = 1;
		tetrisGame.setScore(score);
		tetrisGame.setLevel(level);
		
		speed = initialSpeed - (level-1) * 40;
		t.setDelay(speed);
		t.start();
		requestFocus();
	}
	
	public void pause() {
		if (!gameOver) {
			paused = true;
		}
	}
	
	public void resume() {
		paused = false;
		requestFocus();
	}
	
	private void setDifficulty(int sp) {
		speed = sp;
		t.setDelay(speed);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (!running) {
			System.exit(0);
		}
		
		update();
		render();
	}
	
	private void update() {
		if (!gameOver && !paused) {
			if (currBlock == null) {
				setNewBlock();
			}
			if (currBlock.hasStopped(squares)) {
				if (currBlock.getMinY() <= 0) {
					gameOver = true;
					return;
				}
				
				setScore(level);
				tetrisGame.setScore(score);
				
				blocks.add(currBlock);
				
				Point[] sq = currBlock.getSquares();
				for (int i = 0; i < sq.length; i++) {
					squares[sq[i].x / 20][sq[i].y / 20] = sq[i];
				}
				setNewBlock();
				completeLine();
			} else {
				currBlock.move();
			}
		}
	}
	
	private void setNewBlock() {
		currBlock = upcomingBlock;
		upcomingBlock = new Block(this);
		tetrisGame.setNextBlock(upcomingBlock);
	}
	
	private void render() {
		if (img == null) {
			img = createImage(WIDTH, HEIGHT);
			if (img == null) {
				return;
			}
			g = img.getGraphics();
		}
		
		if (gameOver) {
			g.setColor(Color.BLACK);
			g.setFont(new Font("Helvetica", Font.BOLD, 18));
			g.drawString("Game Over!", 20, 20);
			paint();
			return;
		}
		if (paused) {
			g.setColor(Color.BLACK);
			g.setFont(new Font("Helvetica", Font.BOLD, 18));
			g.drawString("Paused", 60, 20);
			paint();
			return;
		}
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		currBlock.draw(g, false, microMode);
		for (int i = 0; i < blocks.size(); i++) {
			blocks.get(i).draw(g, false, microMode);
		}
		
		paint();
	}
	
	private void paint() {
		Graphics g;
		try {
			g = this.getGraphics();
			if (g != null && img != null) {
				g.drawImage(img, 0, 0, null);
			}
			g.dispose();
		} catch (Exception e) {}
	}
	
	private void completeLine() {
		int count = 0;
		for (int y = 0; y < squares[0].length; y++) {
			boolean completed = true;
			for (int x = 0; x < squares.length; x++) {
				if (squares[x][y] == null) {
					completed = false;
					break;
				}
			}
			if (completed) {
				count++;
				removeLine(y);
			}
		}
		setScore(count * count * level * 10);
	}
	
	private void removeLine(int y) {
		for (int x = 0; x < squares.length; x++) {
			squares[x][y].y = -100;
		}
		
		for (int x = 0; x < squares.length; x++) {
			for (int i = y - 1; i >= 0; i--) {
				if (i == 0) {
					squares[x][0] = null;
				} else {
					if (squares[x][i] != null) {
						squares[x][i].y += 20;
					}
					squares[x][i + 1] = squares[x][i];
				}
			}
		}
	}
	
	private void setScore(int s) {
		score += s;
		tetrisGame.setScore(score);
		if (((double)score) / ((double)level * 100.0) >= level && level < 15) {
			level++;
			setDifficulty(initialSpeed - (level-1) * 40);
			tetrisGame.setLevel(level);
		}	
	}
	
	public boolean isMicroMode() {
		return microMode;
	}
}
