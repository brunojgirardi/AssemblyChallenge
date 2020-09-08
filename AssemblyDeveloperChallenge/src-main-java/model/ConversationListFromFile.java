package model;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class ConversationListFromFile {

	private List<String> ConversationList;
	
	public List<String> getConversationListFromFile(String fileName) throws Throwable {
		ConversationList = new ArrayList<String>();
		try {

			FileInputStream fstream = new FileInputStream(fileName);

			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String stringLines = br.readLine();

			while (stringLines != null) {
				ConversationList.add(stringLines);
				stringLines = br.readLine();
			}

			in.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "File not attached. Try again." + e.getMessage(), "Alert", 2, null);
			e.getStackTrace();
			throw new Exception("File could not be founded.");
		}

		return ConversationList;
	}
	
}
