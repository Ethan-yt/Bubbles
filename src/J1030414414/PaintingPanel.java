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
	public ArrayList<Ball> balls = new ArrayList<Ball>();// ��������С��

	public BufferedImage image;
	public BufferedImage screenImage;

	public PaintingPanel() {

		this.setLayout(null);
		// ������Ļ��ͼ
		try {
			Robot robot = new Robot();
			image = screenImage = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));

		} catch (AWTException e1) {
			e1.printStackTrace();
		}

		DebugPanel debugPanel = new DebugPanel(balls);// fps���

		debugPanel.setBounds(0, 0, 300, 350);// �̶���С��λ��
		this.add(debugPanel);
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(500);// ÿ0.5�����һ��fps
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
		// ��������һ���߳����С����Ϊÿ���һ����Ҫ��ʱ���������̻߳��������
		new Thread(new Runnable() {
			public void run() {
				// ���10��С��
				for (int i = 0; i < Bubbles.BALLNUMBER; i++) {
					Random r = new Random();
					// ����С�����λ��
					Ball ball = new Ball(balls, bounds);
					ball.setX(Bubbles.BALLRADIUS * 1.5);
					ball.setY(Bubbles.BALLRADIUS * 1.5);
					ball.setVx(1 + r.nextInt(10) * 0.1);
					ball.setVy(0 + r.nextInt(20) * 0.1);
					// ball.setOpaque(true);
					PaintingPanel.this.add(ball);

					new Thread(ball).start();// ����С���߳�
					balls.add(ball);// �������
					try {
						Thread.sleep(1000);// ÿ1�����һ��С��
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
		// ������
		g2d.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);
	}
}
