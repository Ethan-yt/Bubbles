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
 * 小球类
 * 
 * 继承自JComponent - 可以add到JPanel中
 * 接口Runnable - 可以被线程执行
 * 保存了每个小球的基本信息
 */
@SuppressWarnings("serial")
public class Ball extends JComponent implements Runnable {

	private static final double RATE = 1.5;// 时间换算系数，越大每帧球运行距离越长
	private static final double G = 0.005;// 重力加速度常量
	private int R = Bubbles.BALLRADIUS;// 球半径
	private double vx, vy, x, y;// 球速度在x,y的分量以及球的坐标
	private Rectangle wall;// 屏幕大小
	private ArrayList<Ball> balls;// 小球的引用，便于检测碰撞
	private long time;// 上一帧的时间

	private Color color;// 当前球的颜色
	private int colorCount;// 颜色渐变动画计数 每10帧渐变一次
	private float hue;// 色调

	private int style = Bubbles.STYLE;// 小球样式：0-无色，1-立体，2-扁平，3-windows

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
	 * 构造函数 要求提供其他小球的引用，屏幕的大小
	 */
	public Ball(ArrayList<Ball> balls, Rectangle wall) {

		this.balls = balls;
		this.wall = wall;
		this.time = System.nanoTime();// 记录当前时间
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
	 * 小球进程 每帧微调当前小球颜色，检测碰撞并计算下一帧位置
	 */
	@Override
	public void run() {

		// 死循环，每次循环为一帧
		while (true) {
			// 颜色渐变动画 每10帧渐变一次
			if (colorCount == 10 && Bubbles.COLORCHANGE) {
				changeColor();
				colorCount = 0;
			}

			collision();// 检测碰撞并计算下一帧

			// 半径被更改后，调整小球半径
			if (Bubbles.BALLRADIUS != R) {
				R = Bubbles.BALLRADIUS;
			}
			// 样式被更改后，调整小球样式
			if (Bubbles.STYLE != style) {
				style = Bubbles.STYLE;
			}
			time = System.nanoTime();// 记录当前时间
			if (Bubbles.DEBUG)// debug模式的帧计数，统计fps
				frameCount++;
			if (Bubbles.COLORCHANGE)// 颜色渐变动画计数，每10帧渐变一次
				colorCount++;
			try {
				Thread.sleep(Bubbles.DELAY);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	// 颜色渐变动画
	private void changeColor() {
		hue += 0.005;// 色调微调
		this.color = new Color(Color.HSBtoRGB(hue, 0.7F, 0.7F));
	}

	/*
	 * 检测小球碰撞并计算下一帧的位置
	 */
	private void collision() {
		// 由于小球碰撞要引起速度，位置的改变，为了线程安全加入线程同步语句

		synchronized (balls) {
			// t为上一帧和当前帧时间差
			double t = RATE * (System.nanoTime() - time) / 10000000;
			if (Bubbles.GARVITY)
				vy += G * t;
			// 检测墙壁碰撞
			wallCollision(t);
			// 检测是否与其他小球碰撞
			for (int i = 0; i < balls.size(); i++) {
				Ball ball = balls.get(i);
				if (ball == this || ball == null)
					continue;
				ballCollision(ball, t);
			}
			// 计算下一帧的位置
			x = getNextX(t);
			y = getNextY(t);
			// 移动小球
			if (Bubbles.SHADOW) {
				this.setBounds((int) x - R - 10, (int) y - R - 10, (int) (R * 2.4) + 20, (int) (R * 2.5) + 20);
			} else {
				this.setBounds((int) x - R - 10, (int) y - R - 10, R * 2 + 20, R * 2 + 20);
			}
		}
	}

	/*
	 * 小球与小球的碰撞
	 */
	public void ballCollision(Ball ball, double t) {

		// 下一帧小球与小球之间的距离
		double dx = ball.getNextX(t) - this.getNextX(t);
		double dy = ball.getNextY(t) - this.getNextY(t);
		double nextDis = Math.sqrt(dx * dx + dy * dy);
		// 当前帧小球与小球之间的距离
		dx = ball.x - x;
		dy = ball.y - y;
		double thisDis = Math.sqrt(dx * dx + dy * dy);

		// 若下一帧小球间距小于R*2 且 当前帧小球距离大于R*2
		// 则将要发生碰撞
		if (nextDis < R * 2 && thisDis > R * 2) {
			// 首先改变两球的颜色
			this.hue = this.hue + 0.05F;
			ball.hue = ball.hue + 0.05F;
			this.color = new Color(Color.HSBtoRGB(hue, 0.7F, 0.7F));
			ball.color = new Color(Color.HSBtoRGB(ball.hue, 0.7F, 0.7F));
			// 微调dx以便计算（分母不可为0）
			if (dx == 0)
				dx = 0.00001;
			// 记录两球球心连线夹角
			double angle = Math.atan(dy / dx);

			// 碰撞后，ball在S2系中x轴速度记做s2_vx2，y轴速度记做s2_vy2。
			// ball的x轴速度即为this的x轴速度。将this两个分量vx，vy投影到S2系中即可。
			// ball的y轴速度不变，将ball两个分量vx，vy投影至S2系中即可。
			double s2_vx2 = vx * Math.cos(angle) + vy * Math.sin(angle);
			double s2_vy2 = -ball.vx * Math.sin(angle) + ball.vy * Math.cos(angle);
			// 碰撞后，this在S2系中x轴速度记做s2_vx1，y轴速度记做s2_vy1。
			// 情况和上述情况类似，交换变量即可。
			double s2_vx1 = ball.vx * Math.cos(angle) + ball.vy * Math.sin(angle);
			double s2_vy1 = -vx * Math.sin(angle) + vy * Math.cos(angle);

			// 将ball在S2系中的变量转换至S1系中。
			ball.setVx(s2_vx2 * Math.cos(angle) - s2_vy2 * Math.sin(angle));
			ball.setVy(s2_vx2 * Math.sin(angle) + s2_vy2 * Math.cos(angle));
			// 将this在S2系中的变量转换至S1系中。
			vx = s2_vx1 * Math.cos(angle) - s2_vy1 * Math.sin(angle);
			vy = s2_vx1 * Math.sin(angle) + s2_vy1 * Math.cos(angle);

		}
	}

	/*
	 * 检测与墙壁的碰撞
	 */
	public void wallCollision(double t) {
		// 下一帧的位置
		double nextX = this.getNextX(t);
		double nextY = this.getNextY(t);
		// 当下一帧超出墙壁时，即将发生碰撞
		// 根据相关推论，做出相应的改变。（推导过程详见文档）
		if (nextX - R < 0 || nextX + R > wall.getWidth())
			vx = -vx;
		if (nextY - R < 0 || nextY + R > wall.getHeight())
			vy = -vy;
	}

	/*
	 * 绘制当前小球
	 */
	@Override
	protected void paintComponent(Graphics arg0) {
		// TODO 自动生成的方法存根
		super.paintComponent(arg0);
		Graphics2D g2d = (Graphics2D) arg0;
		// 抗锯齿
		if (Bubbles.ANTIALIASING) {
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);// 抗锯齿
		}

		// 绘制阴影
		if (Bubbles.SHADOW && style != 0)
			drawShadow(g2d);
		// 绘制小球
		drawBall(g2d);
	}

	/*
	 * 绘制小球
	 */
	private void drawBall(Graphics2D g2d) {
		Ellipse2D ballEllipse = new Ellipse2D.Double(10, 10, R * 2, R * 2);
		switch (style) {
		case 0:// 无色模式，只绘制边框
			g2d.setColor(color);
			g2d.setStroke(new BasicStroke(2));
			g2d.draw(ballEllipse);
			break;
		case 1:// 立体模式
		{
			Point2D center = new Point2D.Double(10 + R, 10 + R);
			// 焦点偏移，制造光源效果
			Point2D focus = new Point2D.Double(10 + R * 0.6, 10 + R * 0.6);
			float[] dist = { 0f, 1.0f };
			// 由白色渐变至小球当前颜色
			Color[] colors = { Color.white, color };
			// 创建渐变
			Paint ballPaint = new RadialGradientPaint(center, R, focus, dist, colors, CycleMethod.NO_CYCLE);
			g2d.setPaint(ballPaint);
			g2d.fill(ballEllipse);
			break;
		}
		case 2:// 扁平模式，用纯色填充小球
			g2d.setColor(color);
			g2d.setPaint(null);
			g2d.fill(ballEllipse);
			break;
		case 3:// windows模式，样式较为复杂，分三步
		{
			// 第一步，画出小球
			Point2D center = new Point2D.Double(10 + R, 10 + R);
			float[] dist = { 0.5f, 1.0f };
			Color c0 = new Color(color.getRed(), color.getGreen(), color.getBlue(), 0);
			// 由颜色相同完全透明，渐变至小球颜色
			Color[] colors = { c0, color };
			// 创建渐变
			Paint ballPaint = new RadialGradientPaint(center, R * 1.4F, dist, colors, CycleMethod.NO_CYCLE);
			g2d.setPaint(ballPaint);
			g2d.fill(ballEllipse);
		} {
			// 第二步，画反光
			Point2D center = new Point2D.Double(10 + R, 10 + R);
			float[] dist = { 0.5f, 1.0f };
			Color c0 = new Color(255, 255, 255, 0);
			Color c1 = new Color(255, 255, 255);
			// 由白色透明渐变至白色
			Color[] colors = { c0, c1 };
			// 创建渐变
			Paint lightPaint = new RadialGradientPaint(center, R * 1.6F, dist, colors, CycleMethod.NO_CYCLE);
			// 建立一个椭圆，只在椭圆范围内绘制反光
			Ellipse2D lightEllipse = new Ellipse2D.Double(10 + R * 0.14F, 10 + R * 0.02F, R * 1.72F, R * 1.4F);
			g2d.setPaint(lightPaint);
			g2d.fill(lightEllipse);
		} {
			// 第三步，画高光
			// 建立高光的椭圆
			Ellipse2D lightEllipse = new Ellipse2D.Double(10 + R * 0.30, 10 + R * 0.30, R * 0.34, R * 0.11);
			// 旋转椭圆
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
	 * 绘制阴影
	 */
	private void drawShadow(Graphics2D g2d) {
		Ellipse2D shadowEllipse = new Ellipse2D.Double(10 + R * 0.3, 10 + R * 0.4, R * 0.3 + R * 1.1 * 2,
				R * 0.3 + R * 1.1 * 2);
		RadialGradientPaint shadowPaint = getShadowPaint();
		g2d.setPaint(shadowPaint);
		g2d.fill(shadowEllipse);
	}

	/*
	 * 获得阴影Paint对象
	 */
	private RadialGradientPaint getShadowPaint() {
		Point2D center = new Point2D.Double(10 + R * 1.4, 10 + R * 1.5);
		float[] dist = { 0.8f, 1f };
		// 黑色，完全透明
		Color c0 = new Color(0, 0, 0, 0);
		// 黑色，10%透明
		Color c1 = new Color(0, 0, 0, 25);
		Color[] colors = { c1, c0 };
		return new RadialGradientPaint(center, (float) (R * 1.1), dist, colors, CycleMethod.NO_CYCLE);

	}
}
