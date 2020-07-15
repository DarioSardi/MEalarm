package monitor;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;

public class Monitor {
	JLabel value0 = new JLabel("--");
	JLabel value1 = new JLabel("--");
	JLabel value2 = new JLabel("--");
	JLabel value3 = new JLabel("--");
	public Monitor() {
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(30,30,10,30));
		panel.setLayout(new GridLayout(0,1));
		panel.add(value0);
		panel.add(value1);
		panel.add(value2);
		panel.add(value3);
		frame.add(panel,BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("value monitor");
		frame.pack();
		frame.setVisible(true);
	}
	
	
	public void updateData(float[] data) {
		value0.setText(String.valueOf(data[0]));
		value1.setText(String.valueOf(data[1]));
		value2.setText(String.valueOf(data[2]));
		value3.setText(String.valueOf(data[3]));
	}
}
