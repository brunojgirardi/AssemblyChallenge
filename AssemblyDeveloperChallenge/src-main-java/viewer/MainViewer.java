package viewer;

import java.awt.Button;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.ConferenceSystem;

public class MainViewer {

	private JFrame frmMainwindow;
	private JTextField txtDirectory;
	String myFile;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainViewer window = new MainViewer();
					window.frmMainwindow.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainViewer() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmMainwindow = new JFrame();
		frmMainwindow.setForeground(Color.DARK_GRAY);
		frmMainwindow.setIconImage(
				Toolkit.getDefaultToolkit().getImage(MainViewer.class.getResource("/javax/swing/plaf/metal/icons/ocean/hardDrive.gif")));
		frmMainwindow.setTitle("﻿Problem Statement - Assembly Line");
		frmMainwindow.setResizable(false);
		frmMainwindow.getContentPane().setFont(new Font("Eras Light ITC", Font.PLAIN, 11));
		frmMainwindow.setBounds(100, 100, 523, 163);
		frmMainwindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMainwindow.getContentPane().setLayout(null);

		JLabel lblNewLabel = new JLabel("Select the directory of the file:");
		lblNewLabel.setFont(new Font("SimSun", Font.PLAIN, 11));
		lblNewLabel.setBounds(38, 26, 234, 14);
		frmMainwindow.getContentPane().add(lblNewLabel);

		txtDirectory = new JTextField();
		txtDirectory.setEnabled(false);
		txtDirectory.setToolTipText("");
		txtDirectory.setBounds(38, 51, 351, 20);
		frmMainwindow.getContentPane().add(txtDirectory);
		txtDirectory.setColumns(10);

		JButton btnGenerate = new JButton("Generate");
		btnGenerate.setFont(new Font("SimSun", Font.PLAIN, 11));
		btnGenerate.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String dir = null;
				dir = txtDirectory.getText();

				try {
					this.executeFile(dir);
				} catch (Exception ee) {
					JOptionPane.showMessageDialog(null, "Error while taking data from file." + ee.getStackTrace());
				}
			}

			// Runs the method to read the file and begin the process of this program
			private void executeFile(String dir) {
				ConferenceSystem conferenceSystem = new ConferenceSystem();
				try {
					conferenceSystem.conferenceAgenda(dir);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}

		});

		btnGenerate.setBounds(38, 82, 85, 23);
		frmMainwindow.getContentPane().add(btnGenerate);

		Button button = new Button("Search file");
		button.setFont(new Font("SimSun", Font.PLAIN, 11));
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JFileChooser fs = new JFileChooser();
				fs.setDialogTitle("Choose the file:");
				fs.setFileSelectionMode(JFileChooser.FILES_ONLY);

				FileNameExtensionFilter filter = new FileNameExtensionFilter("Text", "txt");

				fs.setFileFilter(filter);
				int check = fs.showOpenDialog(button);

				if (check == JFileChooser.APPROVE_OPTION) {

					File file = fs.getSelectedFile();
					txtDirectory.setText(file.getPath());
				}
			}
		});
		button.setBounds(399, 49, 70, 22);
		frmMainwindow.getContentPane().add(button);
	}
}
