package J1030414414;

import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.UIManager;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {
	private PaintingPanel panel;//��ͼ���
	public MainFrame() {
		super("��������");

		setUndecorated(true);
		setExtendedState(MAXIMIZED_BOTH);// ȫ��
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		panel = new PaintingPanel();
		this.setContentPane(panel);
		setVisible(true);
		panel.startAddBallThread(this.getBounds());
		//��ESC��س��˳�
		this.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_ENTER)
					System.exit(0);
			}
		});
		
		//˫��������
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
				{
					SettingDialog f = new SettingDialog(MainFrame.this);
					f.setVisible(true);
					try {
						panel.image = ImageIO.read(new FileInputStream(f.imagePath));
					} catch (Exception e1) {
						panel.image = panel.screenImage;//Ĭ��Ϊ��Ļ��ͼ
					}
					panel.repaint();
				}
					
				
			}
		});


		//--------------------------Ƥ������--------------------------------
		String[] DEFAULT_FONT = new String[] { "Table.font", "TableHeader.font", "CheckBox.font", "Tree.font",
				"Viewport.font", "ProgressBar.font", "RadioButtonMenuItem.font", "ToolBar.font", "ColorChooser.font",
				"ToggleButton.font", "Panel.font", "TextArea.font", "Menu.font", "TableHeader.font", "TextField.font",
				"OptionPane.font", "MenuBar.font", "Button.font", "Label.font", "PasswordField.font", "ScrollPane.font",
				"MenuItem.font", "ToolTip.font", "List.font", "EditorPane.font", "Table.font", "TabbedPane.font",
				"RadioButton.font", "CheckBoxMenuItem.font", "TextPane.font", "PopupMenu.font", "TitledBorder.font",
				"ComboBox.font" };
		try {
			org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
			UIManager.put("RootPane.setupButtonVisible", false);

			// ����Ĭ������
			for (int i = 0; i < DEFAULT_FONT.length; i++)
				UIManager.put(DEFAULT_FONT[i], new Font("΢���ź�", Font.PLAIN, 14));
		} catch (Exception e) {
			try {
				for (int i = 0; i < DEFAULT_FONT.length; i++)
					UIManager.put(DEFAULT_FONT[i], new Font("����", Font.PLAIN, 14));
			} catch (Exception e0) {
			}
		}
	}


}
