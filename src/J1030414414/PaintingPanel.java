package J1030414414;

import java.awt.AWTException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JPanel;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

@SuppressWarnings("serial")
public class PaintingPanel extends JPanel {
	public ArrayList<Ball> balls = new ArrayList<Ball>();// 保存所有小球

	public BufferedImage image;
	public BufferedImage screenImage;

	public PaintingPanel() {

		this.setLayout(null);
		// 保存屏幕截图
		try {
			Robot robot = new Robot();
			image = screenImage = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));

		} catch (AWTException e1) {
			e1.printStackTrace();
		}

		DebugPanel debugPanel = new DebugPanel(balls);// fps面板

		debugPanel.setBounds(0, 0, 300, 350);// 固定大小，位置
		this.add(debugPanel);
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(500);// 每0.5秒更新一次fps
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					debugPanel.getFps();
					debugPanel.repaint();

				}

			}
		}).start();
	}

	public void startAddBallThread(Rectangle bounds) {
		// 单独开辟一个线程添加小球，因为每添加一个球要延时，若在主线程会造成阻塞
		new Thread(new Runnable() {
			public void run() {
				// 添加10个小球
				for (int i = 0; i < Bubbles.BALLNUMBER; i++) {
					Random r = new Random();
					// 设置小球基本位置
					Ball ball = new Ball(balls, bounds);
					ball.setX(Bubbles.BALLRADIUS * 1.5);
					ball.setY(Bubbles.BALLRADIUS * 1.5);
					ball.setVx(1 + r.nextInt(10) * 0.1);
					ball.setVy(0 + r.nextInt(20) * 0.1);
					// ball.setOpaque(true);
					PaintingPanel.this.add(ball);

					new Thread(ball).start();// 启动小球线程
					balls.add(ball);// 添加引用
					try {
						Thread.sleep(1000);// 每1秒添加一个小球
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		// 画背景
		g2d.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);
	}
}
