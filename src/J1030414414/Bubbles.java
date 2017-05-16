package J1030414414;

public class Bubbles{
	static final int DELAY = 10;//帧与帧之间的延时，单位为毫秒
	static final int BALLNUMBER = 10;//小球个数
	static boolean DEBUG = true;//是否调试模式的选项
	static boolean SHADOW = true;//显示阴影的选项
	static boolean ANTIALIASING = true;//开启抗锯齿的选项
	static boolean COLORCHANGE = true;//开启颜色改变动画的选项
	static boolean GARVITY = true;//开启颜色改变动画的选项
	static int BALLRADIUS = 100;//小球半径
	static int STYLE = 3;//小球半径

	
	public static void main(String args[]) {
		new MainFrame();
	}
}