package J1030414414;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JCheckBox;

@SuppressWarnings("serial")
public class SettingDialog extends JDialog {
	public boolean isDebug = true;
	public String imagePath;

	public SettingDialog(JFrame owner) {
		super(owner, "设置");

		setModal(true);
		setLocationRelativeTo(owner); // 窗体居中
		setSize(400, 500);
		JPanel comp = new JPanel();
		Container container = getContentPane();
		container.add(comp);
		comp.setLayout(new BorderLayout(10, 10));
		JPanel content = new JPanel();
		content.setLayout(new BorderLayout(10, 10));
		comp.add(content, BorderLayout.CENTER);
		comp.add(new JPanel(), BorderLayout.NORTH);
		comp.add(new JPanel(), BorderLayout.SOUTH);
		comp.add(new JPanel(), BorderLayout.EAST);
		comp.add(new JPanel(), BorderLayout.WEST);

		JPanel settingsPanel = new JPanel();
		settingsPanel.setLayout(null);

		JCheckBox cDebug = new JCheckBox("Debug模式");
		cDebug.setSelected(Bubbles.DEBUG);
		cDebug.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO 自动生成的方法存根
				Bubbles.DEBUG = cDebug.isSelected();
			}
		});
		settingsPanel.add(cDebug);
		cDebug.setBounds(0, 0, 300, 30);

		JCheckBox cShadow = new JCheckBox("启用阴影");
		cShadow.setSelected(Bubbles.SHADOW);
		cShadow.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO 自动生成的方法存根
				Bubbles.SHADOW = cShadow.isSelected();
			}
		});
		settingsPanel.add(cShadow);
		cShadow.setBounds(0, 30, 300, 30);

		JCheckBox cAntiAliasing = new JCheckBox("启用抗锯齿");
		cAntiAliasing.setSelected(Bubbles.ANTIALIASING);
		cAntiAliasing.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO 自动生成的方法存根
				Bubbles.ANTIALIASING = cAntiAliasing.isSelected();
			}
		});
		settingsPanel.add(cAntiAliasing);
		cAntiAliasing.setBounds(0, 60, 300, 30);

		JCheckBox cColorChange = new JCheckBox("启用颜色改变动画");
		cColorChange.setSelected(Bubbles.COLORCHANGE);
		cColorChange.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO 自动生成的方法存根
				Bubbles.COLORCHANGE = cColorChange.isSelected();
			}
		});

		settingsPanel.add(cColorChange);
		cColorChange.setBounds(0, 90, 300, 30);

		JCheckBox cGarvity = new JCheckBox("启用重力");
		cGarvity.setSelected(Bubbles.GARVITY);
		cGarvity.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO 自动生成的方法存根
				Bubbles.GARVITY = cGarvity.isSelected();
			}
		});

		settingsPanel.add(cGarvity);
		cGarvity.setBounds(0, 120, 300, 30);

		JSlider sBallRadius = new JSlider(20, 300, Bubbles.BALLRADIUS);
		sBallRadius.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO 自动生成的方法存根
				Bubbles.BALLRADIUS = sBallRadius.getValue();
			}
		});
		settingsPanel.add(sBallRadius);
		sBallRadius.setBounds(100, 150, 200, 30);
		JLabel lBallRadius = new JLabel("小球半径：");
		settingsPanel.add(lBallRadius);
		lBallRadius.setBounds(10, 150, 100, 30);

		String[] strStyles = { "无色", "立体", "扁平", "Windows" };
		JComboBox<String> bStyles = new JComboBox<String>(strStyles);
		bStyles.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO 自动生成的方法存根
				Bubbles.STYLE = bStyles.getSelectedIndex();
			}
		});
		settingsPanel.add(bStyles);
		bStyles.setBounds(100, 180, 200, 30);
		JLabel lStyles = new JLabel("小球样式：");
		settingsPanel.add(lStyles);
		lStyles.setBounds(10, 180, 100, 30);

		settingsPanel.setPreferredSize(new Dimension(400, 210));
		content.add(settingsPanel, BorderLayout.NORTH);

		// 遍历images目录下所有文件
		DefaultListModel<String> s = new DefaultListModel<String>();
		File root = new File("images");
		File[] files = root.listFiles();
		for (File file : files)
			s.addElement(file.getName());

		// 将文件通过列表显示出来
		JList<String> l = new JList<String>(s);
		l.setBorder(BorderFactory.createTitledBorder("请选择背景图片"));
		l.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				imagePath = root.getAbsolutePath() + "\\" + l.getSelectedValue();
			}
		});
		content.add(l, BorderLayout.CENTER);

		JButton okBtn = new JButton("确认");
		okBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SettingDialog.this.setVisible(false);
			}
		});
		content.add(okBtn, BorderLayout.SOUTH);
	}
}
