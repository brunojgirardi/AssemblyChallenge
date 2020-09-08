package model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class ScheduledConversationsList {

	private List<List<Conversation>> datedConversationsList;
	private List<String> finalList;
	private int trackCount;
	private String scheduledTime;
	
	public ScheduledConversationsList() {
		datedConversationsList = new ArrayList<List<Conversation>>();
		finalList = new ArrayList<String>();
	}
	
	public List<List<Conversation>> getScheduledConversationsList(List<List<Conversation>> combForMornSessions,
			List<List<Conversation>> combForEveSessions) {

		int totalPossibleDays = combForMornSessions.size();

		for (int dayCount = 0; dayCount < totalPossibleDays; dayCount++) {
			List<Conversation> ConversationList = new ArrayList<Conversation>();

			Date date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm ");
			date.setHours(9);
			date.setMinutes(0);
			date.setSeconds(0);

			trackCount = dayCount + 1;
			scheduledTime = dateFormat.format(date);

			System.out.println("Linha de montagem " + trackCount + ":");
			finalList.add("\n Linha de montagem " + Integer.toString(trackCount) + ": \n");

			List<Conversation> mornSessionConversationList = combForMornSessions.get(dayCount);
			for (Conversation Conversation : mornSessionConversationList) {
				Conversation.setSchedulingTime(scheduledTime);
				System.out.println(scheduledTime + Conversation.getTitle());
				finalList.add(scheduledTime + Conversation.getTitle() + "\n");

				scheduledTime = getNextScheduledTime(date, Conversation.getTimeDuration());
				ConversationList.add(Conversation);
			}

			int lunchTimeInterval = 60;
			Conversation lunchConversation = new Conversation("Almoço", "Almoço", 60);
			lunchConversation.setSchedulingTime(scheduledTime);
			ConversationList.add(lunchConversation);
			System.out.println(scheduledTime + "Almoço");
			finalList.add(scheduledTime + "Almoço \n");

			scheduledTime = getNextScheduledTime(date, lunchTimeInterval);
			List<Conversation> eveSessionConversationList = combForEveSessions.get(dayCount);
			for (Conversation Conversation : eveSessionConversationList) {
				Conversation.setSchedulingTime(scheduledTime);
				ConversationList.add(Conversation);
				System.out.println(scheduledTime + Conversation.getTitle());
				finalList.add(scheduledTime + Conversation.getTitle() + "\n");

				scheduledTime = getNextScheduledTime(date, Conversation.getTimeDuration());
			}

			Conversation ginasticaLaboralConversation = new Conversation("Ginástica Laboral", "Ginástica Laboral", 60);
			ginasticaLaboralConversation.setSchedulingTime(scheduledTime);
			ConversationList.add(ginasticaLaboralConversation);
			System.out.println(scheduledTime + "Ginástica Laboral \n");
			finalList.add(scheduledTime + "Ginástica Laboral \n\n");

			datedConversationsList.add(ConversationList);
		}

		showResultsInScreen();

		return datedConversationsList;

	}
	
	public void showResultsInScreen() {
		String formattedList = Arrays.toString(finalList.toArray()).replace("[", "").replace("]", "").replace(",", "");
		JOptionPane pane = new JOptionPane(formattedList);

		JDialog dialog = pane.createDialog(null, "Results");
		dialog.setSize(380, 620);
		dialog.setVisible(true);
	}
	
	public String getNextScheduledTime(Date date, int timeDuration) {
		long timeInLong = date.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm ");

		long timeDurationInLong = timeDuration * 60 * 1000;
		long newTimeInLong = timeInLong + timeDurationInLong;

		date.setTime(newTimeInLong);
		String str = dateFormat.format(date);
		return str;
	}
}
