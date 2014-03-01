package com.jeffsul.tetris;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class TetrisGame extends JFrame {	
	private Tetris tetris;
	
	private JLabel scoreLbl;
	private JLabel levelLbl;
	private JPanel nextBlockPnl;

	public TetrisGame() {
		super("Jetris");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setLayout(new BorderLayout());
		
		tetris = new Tetris(this);
		
		JPanel titlePnl = new JPanel();
		titlePnl.setBackground(Color.BLACK);
		JLabel titleLbl = new JLabel("Jetris!");
		titleLbl.setForeground(Color.WHITE);
		titleLbl.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		titleLbl.setFont(new Font("Helvetica", Font.BOLD, 24));
		titlePnl.add(titleLbl);
		add(titlePnl, BorderLayout.PAGE_START);
		
		JPanel sidePnl = new JPanel();
		sidePnl.setLayout(new BoxLayout(sidePnl, BoxLayout.Y_AXIS));
		sidePnl.setPreferredSize(new Dimension(125, Tetris.HEIGHT));
		sidePnl.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		JPanel sidePnl1 = new JPanel();
		sidePnl1.setLayout(new BoxLayout(sidePnl1, BoxLayout.Y_AXIS));
		JLabel nextBlockLbl = new JLabel("Next Block:");
		sidePnl1.add(nextBlockLbl);
		nextBlockPnl = new JPanel();
		sidePnl1.add(nextBlockPnl);
		sidePnl.add(sidePnl1);
		
		JPanel scorePnl = new JPanel();
		scorePnl.setLayout(new BoxLayout(scorePnl, BoxLayout.Y_AXIS));
		
		JButton newGameBtn = new JButton("New Game");
		newGameBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tetris.startGame();
			}
		});
		scorePnl.add(newGameBtn);
		
		final JButton pauseBtn = new JButton("Pause Game");
		pauseBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (pauseBtn.getText() == "Pause Game") {
					tetris.pause();
					pauseBtn.setText("Resume Game");
				} else {
					tetris.resume();
					pauseBtn.setText("Pause Game");
				}
			}
		});
		scorePnl.add(pauseBtn);
		
		scoreLbl = new JLabel("Score: 0");
		levelLbl = new JLabel("Level: 1");
		scorePnl.add(scoreLbl);
		scorePnl.add(levelLbl);
		sidePnl.add(scorePnl);
		
		add(sidePnl, BorderLayout.LINE_END);
		add(tetris, BorderLayout.CENTER);
		
		pack();
		setVisible(true);
	}
	
	public void setNextBlock(Block b) {
		Graphics g = nextBlockPnl.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 100, 105);
		b.draw(g, true, tetris.isMicroMode());
		g.dispose();
	}
	
	public void setScore(int score) {
		scoreLbl.setText("Score: " + score);
	}
	
	public void setLevel(int level) {
		levelLbl.setText("Level: " + level);
	}
	
	public static void main(String[] args) {
		new TetrisGame();
	}
}

