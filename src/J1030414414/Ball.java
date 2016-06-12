package J1030414414;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JComponent;

/*
 * С����
 * 
 * �̳���JComponent - ����add��JPanel��
 * �ӿ�Runnable - ���Ա��߳�ִ��
 * ������ÿ��С��Ļ�����Ϣ
 */
@SuppressWarnings("serial")
public class Ball extends JComponent implements Runnable {

	private static final double RATE = 1.5;// ʱ�任��ϵ����Խ��ÿ֡�����о���Խ��
	private static final double G = 0.005;// �������ٶȳ���
	private int R = Bubbles.BALLRADIUS;// ��뾶
	private double vx, vy, x, y;// ���ٶ���x,y�ķ����Լ��������
	private Rectangle wall;// ��Ļ��С
	private ArrayList<Ball> balls;// С������ã����ڼ����ײ
	private long time;// ��һ֡��ʱ��

	private Color color;// ��ǰ�����ɫ
	private int colorCount;// ��ɫ���䶯������ ÿ10֡����һ��
	private float hue;// ɫ��

	private int style = Bubbles.STYLE;// С����ʽ��0-��ɫ��1-���壬2-��ƽ��3-windows

	// ----------getter and setter-------------
	public void setVx(double d) {
		this.vx = d;
	}

	public void setVy(double vy) {
		this.vy = vy;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setStyle(int styleType) {
		style = styleType;
	}

	// ----------getter and setter-------------

	public double getNextX(double t) {
		return x + t * vx;
	}

	public double getNextY(double t) {
		return y + t * vy;
	}

	private int frameCount;
	private long frameTime;

	public int getFps() {
		int ret = (int) (frameCount / ((System.currentTimeMillis() - frameTime) * 0.001));
		frameCount = 0;
		frameTime = System.currentTimeMillis();
		return ret;

	}

	/*
	 * ���캯�� Ҫ���ṩ����С������ã���Ļ�Ĵ�С
	 */
	public Ball(ArrayList<Ball> balls, Rectangle wall) {

		this.balls = balls;
		this.wall = wall;
		this.time = System.nanoTime();// ��¼��ǰʱ��
		if (Bubbles.DEBUG)
			frameTime = System.currentTimeMillis();
		Random r = new Random();

		hue = 0.01F * r.nextInt(100);
		this.color = new Color(Color.HSBtoRGB(hue, 0.7F, 0.7F));

		if (Bubbles.SHADOW) {
			this.setBounds((int) x - R - 10, (int) y - R - 10, (int) (R * 2.4) + 20, (int) (R * 2.5) + 20);
		} else {
			this.setBounds((int) x - R - 10, (int) y - R - 10, R * 2 + 20, R * 2 + 20);
		}
	}

	/*
	 * С����� ÿ֡΢����ǰС����ɫ�������ײ��������һ֡λ��
	 */
	@Override
	public void run() {

		// ��ѭ����ÿ��ѭ��Ϊһ֡
		while (true) {
			// ��ɫ���䶯�� ÿ10֡����һ��
			if (colorCount == 10 && Bubbles.COLORCHANGE) {
				changeColor();
				colorCount = 0;
			}

			collision();// �����ײ��������һ֡

			// �뾶�����ĺ󣬵���С��뾶
			if (Bubbles.BALLRADIUS != R) {
				R = Bubbles.BALLRADIUS;
			}
			// ��ʽ�����ĺ󣬵���С����ʽ
			if (Bubbles.STYLE != style) {
				style = Bubbles.STYLE;
			}
			time = System.nanoTime();// ��¼��ǰʱ��
			if (Bubbles.DEBUG)// debugģʽ��֡������ͳ��fps
				frameCount++;
			if (Bubbles.COLORCHANGE)// ��ɫ���䶯��������ÿ10֡����һ��
				colorCount++;
			try {
				Thread.sleep(Bubbles.DELAY);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	// ��ɫ���䶯��
	private void changeColor() {
		hue += 0.005;// ɫ��΢��
		this.color = new Color(Color.HSBtoRGB(hue, 0.7F, 0.7F));
	}

	/*
	 * ���С����ײ��������һ֡��λ��
	 */
	private void collision() {
		// ����С����ײҪ�����ٶȣ�λ�õĸı䣬Ϊ���̰߳�ȫ�����߳�ͬ�����

		synchronized (balls) {
			// tΪ��һ֡�͵�ǰ֡ʱ���
			double t = RATE * (System.nanoTime() - time) / 10000000;
			if (Bubbles.GARVITY)
				vy += G * t;
			// ���ǽ����ײ
			wallCollision(t);
			// ����Ƿ�������С����ײ
			for (int i = 0; i < balls.size(); i++) {
				Ball ball = balls.get(i);
				if (ball == this || ball == null)
					continue;
				ballCollision(ball, t);
			}
			// ������һ֡��λ��
			x = getNextX(t);
			y = getNextY(t);
			// �ƶ�С��
			if (Bubbles.SHADOW) {
				this.setBounds((int) x - R - 10, (int) y - R - 10, (int) (R * 2.4) + 20, (int) (R * 2.5) + 20);
			} else {
				this.setBounds((int) x - R - 10, (int) y - R - 10, R * 2 + 20, R * 2 + 20);
			}
		}
	}

	/*
	 * С����С�����ײ
	 */
	public void ballCollision(Ball ball, double t) {

		// ��һ֡С����С��֮��ľ���
		double dx = ball.getNextX(t) - this.getNextX(t);
		double dy = ball.getNextY(t) - this.getNextY(t);
		double nextDis = Math.sqrt(dx * dx + dy * dy);
		// ��ǰ֡С����С��֮��ľ���
		dx = ball.x - x;
		dy = ball.y - y;
		double thisDis = Math.sqrt(dx * dx + dy * dy);

		// ����һ֡С����С��R*2 �� ��ǰ֡С��������R*2
		// ��Ҫ������ײ
		if (nextDis < R * 2 && thisDis > R * 2) {
			// ���ȸı��������ɫ
			this.hue = this.hue + 0.05F;
			ball.hue = ball.hue + 0.05F;
			this.color = new Color(Color.HSBtoRGB(hue, 0.7F, 0.7F));
			ball.color = new Color(Color.HSBtoRGB(ball.hue, 0.7F, 0.7F));
			// ΢��dx�Ա���㣨��ĸ����Ϊ0��
			if (dx == 0)
				dx = 0.00001;
			// ��¼�����������߼н�
			double angle = Math.atan(dy / dx);

			// ��ײ��ball��S2ϵ��x���ٶȼ���s2_vx2��y���ٶȼ���s2_vy2��
			// ball��x���ٶȼ�Ϊthis��x���ٶȡ���this��������vx��vyͶӰ��S2ϵ�м��ɡ�
			// ball��y���ٶȲ��䣬��ball��������vx��vyͶӰ��S2ϵ�м��ɡ�
			double s2_vx2 = vx * Math.cos(angle) + vy * Math.sin(angle);
			double s2_vy2 = -ball.vx * Math.sin(angle) + ball.vy * Math.cos(angle);
			// ��ײ��this��S2ϵ��x���ٶȼ���s2_vx1��y���ٶȼ���s2_vy1��
			// ���������������ƣ������������ɡ�
			double s2_vx1 = ball.vx * Math.cos(angle) + ball.vy * Math.sin(angle);
			double s2_vy1 = -vx * Math.sin(angle) + vy * Math.cos(angle);

			// ��ball��S2ϵ�еı���ת����S1ϵ�С�
			ball.setVx(s2_vx2 * Math.cos(angle) - s2_vy2 * Math.sin(angle));
			ball.setVy(s2_vx2 * Math.sin(angle) + s2_vy2 * Math.cos(angle));
			// ��this��S2ϵ�еı���ת����S1ϵ�С�
			vx = s2_vx1 * Math.cos(angle) - s2_vy1 * Math.sin(angle);
			vy = s2_vx1 * Math.sin(angle) + s2_vy1 * Math.cos(angle);

		}
	}

	/*
	 * �����ǽ�ڵ���ײ
	 */
	public void wallCollision(double t) {
		// ��һ֡��λ��
		double nextX = this.getNextX(t);
		double nextY = this.getNextY(t);
		// ����һ֡����ǽ��ʱ������������ײ
		// ����������ۣ�������Ӧ�ĸı䡣���Ƶ���������ĵ���
		if (nextX - R < 0 || nextX + R > wall.getWidth())
			vx = -vx;
		if (nextY - R < 0 || nextY + R > wall.getHeight())
			vy = -vy;
	}

	/*
	 * ���Ƶ�ǰС��
	 */
	@Override
	protected void paintComponent(Graphics arg0) {
		// TODO �Զ����ɵķ������
		super.paintComponent(arg0);
		Graphics2D g2d = (Graphics2D) arg0;
		// �����
		if (Bubbles.ANTIALIASING) {
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);// �����
		}

		// ������Ӱ
		if (Bubbles.SHADOW && style != 0)
			drawShadow(g2d);
		// ����С��
		drawBall(g2d);
	}

	/*
	 * ����С��
	 */
	private void drawBall(Graphics2D g2d) {
		Ellipse2D ballEllipse = new Ellipse2D.Double(10, 10, R * 2, R * 2);
		switch (style) {
		case 0:// ��ɫģʽ��ֻ���Ʊ߿�
			g2d.setColor(color);
			g2d.setStroke(new BasicStroke(2));
			g2d.draw(ballEllipse);
			break;
		case 1:// ����ģʽ
		{
			Point2D center = new Point2D.Double(10 + R, 10 + R);
			// ����ƫ�ƣ������ԴЧ��
			Point2D focus = new Point2D.Double(10 + R * 0.6, 10 + R * 0.6);
			float[] dist = { 0f, 1.0f };
			// �ɰ�ɫ������С��ǰ��ɫ
			Color[] colors = { Color.white, color };
			// ��������
			Paint ballPaint = new RadialGradientPaint(center, R, focus, dist, colors, CycleMethod.NO_CYCLE);
			g2d.setPaint(ballPaint);
			g2d.fill(ballEllipse);
			break;
		}
		case 2:// ��ƽģʽ���ô�ɫ���С��
			g2d.setColor(color);
			g2d.setPaint(null);
			g2d.fill(ballEllipse);
			break;
		case 3:// windowsģʽ����ʽ��Ϊ���ӣ�������
		{
			// ��һ��������С��
			Point2D center = new Point2D.Double(10 + R, 10 + R);
			float[] dist = { 0.5f, 1.0f };
			Color c0 = new Color(color.getRed(), color.getGreen(), color.getBlue(), 0);
			// ����ɫ��ͬ��ȫ͸����������С����ɫ
			Color[] colors = { c0, color };
			// ��������
			Paint ballPaint = new RadialGradientPaint(center, R * 1.4F, dist, colors, CycleMethod.NO_CYCLE);
			g2d.setPaint(ballPaint);
			g2d.fill(ballEllipse);
		} {
			// �ڶ�����������
			Point2D center = new Point2D.Double(10 + R, 10 + R);
			float[] dist = { 0.5f, 1.0f };
			Color c0 = new Color(255, 255, 255, 0);
			Color c1 = new Color(255, 255, 255);
			// �ɰ�ɫ͸����������ɫ
			Color[] colors = { c0, c1 };
			// ��������
			Paint lightPaint = new RadialGradientPaint(center, R * 1.6F, dist, colors, CycleMethod.NO_CYCLE);
			// ����һ����Բ��ֻ����Բ��Χ�ڻ��Ʒ���
			Ellipse2D lightEllipse = new Ellipse2D.Double(10 + R * 0.14F, 10 + R * 0.02F, R * 1.72F, R * 1.4F);
			g2d.setPaint(lightPaint);
			g2d.fill(lightEllipse);
		} {
			// �����������߹�
			// �����߹����Բ
			Ellipse2D lightEllipse = new Ellipse2D.Double(10 + R * 0.30, 10 + R * 0.30, R * 0.34, R * 0.11);
			// ��ת��Բ
			AffineTransform transform = AffineTransform.getRotateInstance(-9 * Math.PI / 40, 10 + R * 0.47,
					10 + R * 0.355);
			Shape RotatedLightEllipse = transform.createTransformedShape(lightEllipse);
			g2d.setColor(Color.white);
			g2d.fill(RotatedLightEllipse);
			break;
		}
		}

	}

	/*
	 * ������Ӱ
	 */
	private void drawShadow(Graphics2D g2d) {
		Ellipse2D shadowEllipse = new Ellipse2D.Double(10 + R * 0.3, 10 + R * 0.4, R * 0.3 + R * 1.1 * 2,
				R * 0.3 + R * 1.1 * 2);
		RadialGradientPaint shadowPaint = getShadowPaint();
		g2d.setPaint(shadowPaint);
		g2d.fill(shadowEllipse);
	}

	/*
	 * �����ӰPaint����
	 */
	private RadialGradientPaint getShadowPaint() {
		Point2D center = new Point2D.Double(10 + R * 1.4, 10 + R * 1.5);
		float[] dist = { 0.8f, 1f };
		// ��ɫ����ȫ͸��
		Color c0 = new Color(0, 0, 0, 0);
		// ��ɫ��10%͸��
		Color c1 = new Color(0, 0, 0, 25);
		Color[] colors = { c1, c0 };
		return new RadialGradientPaint(center, (float) (R * 1.1), dist, colors, CycleMethod.NO_CYCLE);

	}
}
