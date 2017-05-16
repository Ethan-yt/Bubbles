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
    private PaintingPanel panel;//绘图面板

    public MainFrame() {
        super("气泡屏保");

        setUndecorated(true);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        panel = new PaintingPanel();
        this.setContentPane(panel);
        setVisible(true);
        panel.startAddBallThread(this.getBounds());
        //按ESC或回车退出
        this.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_ENTER)
                    System.exit(0);
            }
        });

        //双击打开设置
        this.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    SettingDialog f = new SettingDialog(MainFrame.this);
                    f.setVisible(true);
                    try {
                        panel.image = ImageIO.read(new FileInputStream(f.imagePath));
                    } catch (Exception e1) {
                        panel.image = panel.screenImage;//默认为屏幕截图
                    }
                    panel.repaint();
                }


            }
        });


        //--------------------------皮肤设置--------------------------------
        String[] DEFAULT_FONT = new String[]{"Table.font", "TableHeader.font", "CheckBox.font", "Tree.font",
                "Viewport.font", "ProgressBar.font", "RadioButtonMenuItem.font", "ToolBar.font", "ColorChooser.font",
                "ToggleButton.font", "Panel.font", "TextArea.font", "Menu.font", "TableHeader.font", "TextField.font",
                "OptionPane.font", "MenuBar.font", "Button.font", "Label.font", "PasswordField.font", "ScrollPane.font",
                "MenuItem.font", "ToolTip.font", "List.font", "EditorPane.font", "Table.font", "TabbedPane.font",
                "RadioButton.font", "CheckBoxMenuItem.font", "TextPane.font", "PopupMenu.font", "TitledBorder.font",
                "ComboBox.font"};
        try {
            org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
            UIManager.put("RootPane.setupButtonVisible", false);

            // 调整默认字体
            for (int i = 0; i < DEFAULT_FONT.length; i++)
                UIManager.put(DEFAULT_FONT[i], new Font("微软雅黑", Font.PLAIN, 14));
        } catch (Exception e) {
            try {
                for (int i = 0; i < DEFAULT_FONT.length; i++)
                    UIManager.put(DEFAULT_FONT[i], new Font("楷体", Font.PLAIN, 14));
            } catch (Exception e0) {
            }
        }
    }


}
