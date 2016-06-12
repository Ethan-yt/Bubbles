package J1030414414;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class DebugPanel extends JComponent {
	private ArrayList<Ball> balls ;//小球的引用
	private int[] fps = new int[Bubbles.BALLNUMBER];//将或得到的fps保存在数组中
	
	@Override
	protected void paintComponent(Graphics arg0) {
		super.paintComponent(arg0);
		if(Bubbles.DEBUG)
		{
			Graphics2D g2d = (Graphics2D) arg0;
			g2d.setFont(new Font("宋体", Font.BOLD, 30));
			g2d.setColor(Color.blue);
			//将数组中的fps绘制到屏幕上
			for(int i = 0;i<balls.size();i++)
				g2d.drawString("BALL:" + i +" FPS:" + fps[i], 30, 30*(i+1));
		}

	}
	public void getFps()
	{
		//获得每个小球的Fps
		for(int i = 0;i<balls.size();i++)
		{
			Ball ball = balls.get(i);
			fps[i] = ball.getFps();
		}
	}
	
	public DebugPanel(ArrayList<Ball> balls) {
		this.balls = balls;
	}
}
