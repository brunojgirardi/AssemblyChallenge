package model;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import exceptions.InvalidTalkException;

public class ConferenceSystem {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	private List<List<Conversation>> datedConversationsList = new ArrayList<List<Conversation>>();
	private List<String> finalList = new ArrayList<String>();
	private int trackCount;
	private String scheduledTime;

	/**
	 * Schedule conference creating method.
	 * 
	 * @param fileName
	 * @return List of a Conversation List
	 * @throws Throwable
	 */
	public List<List<Conversation>> conferenceAgenda(String fileName) throws Throwable {
		List<String> ConversationList = getConversationListFromFile(fileName);
		return scheduleConference(ConversationList);
	}

	/**
	 * Schedule conference creating method.
	 * 
	 * @param ConversationList
	 * @throws Exception
	 * @return List of a Conversation List
	 */
	public List<List<Conversation>> scheduleConference(List<String> ConversationList) throws Exception {
		List<Conversation> ConversationsList = checkAndCreateConversations(ConversationList);
		return getScheduleConferenceTrack(ConversationsList);
	}

	/**
	 * Load Conversation list from input file.
	 * 
	 * @param fileName
	 * @throws Throwable
	 * @return List of Conversation
	 */
	public List<String> getConversationListFromFile(String fileName) throws Throwable {
		List<String> ConversationList = new ArrayList<String>();
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

	/**
	 * Validates the Conversation list and make sure the Conversation time and
	 * initialize the Conversation Object is up.
	 * 
	 * @param ConversationList
	 * @throws Exception
	 * @return List of Conversation
	 */
	private List<Conversation> checkAndCreateConversations(List<String> ConversationList) throws Exception {

		if (ConversationList == null) {
			throw new InvalidTalkException("Empty Conversation List");
		}

		List<Conversation> getConvListValidation = new ArrayList<Conversation>();
		@SuppressWarnings("unused")
		int ConversationCount = -1;
		String minSuffix = "min";
		String maintenanceSuffix = "maintenance";

		for (String Conversation : ConversationList) {
			int lastSpaceIndex = Conversation.lastIndexOf(" ");

			if (lastSpaceIndex == -1)
				throw new InvalidTalkException("Invalid Conversation, " + Conversation + ". Conversation time must be specify.");

			String name = Conversation.substring(0, lastSpaceIndex);
			String timeAct = Conversation.substring(lastSpaceIndex + 1);

			if (name == null || "".equals(name.trim()))
				throw new InvalidTalkException("Invalid Conversation name, " + Conversation);

			else if (!timeAct.endsWith(minSuffix) && !timeAct.endsWith(maintenanceSuffix))
				throw new InvalidTalkException("Invalid Conversation time, " + Conversation + ". Time must be in min or in maintenance");

			ConversationCount++;
			int time = 0;

			try {
				if (timeAct.endsWith(minSuffix)) {
					time = Integer.parseInt(timeAct.substring(0, timeAct.indexOf(minSuffix)));
				} else if (timeAct.endsWith(maintenanceSuffix)) {
					String maintenanceTime = timeAct.substring(0, timeAct.indexOf(maintenanceSuffix));
					if ("".equals(maintenanceTime))
						time = 5;
					else
						time = Integer.parseInt(maintenanceTime) * 5;
				}
			} catch (NumberFormatException nume) {
				throw new Exception("Unable parsing the time " + timeAct + " for this Conversation " + Conversation);
			}

			getConvListValidation.add(new Conversation(Conversation, name, time));
		}

		return getConvListValidation;
	}

	/**
	 * Schedule Conference tracks for morning and evening sessions.
	 * 
	 * @param ConversationsList
	 * @return List of a Conversation List
	 * @throws Exception return getScheduledConversationsList
	 */
	private List<List<Conversation>> getScheduleConferenceTrack(List<Conversation> ConversationsList) throws Exception {

		int perDayMinTime = 6 * 60;
		int totalConversationsTime = getTotalConversationsTime(ConversationsList);
		int totalPossibleDays = totalConversationsTime / perDayMinTime;

		List<Conversation> ConversationsListFor = new ArrayList<Conversation>();
		ConversationsListFor.addAll(ConversationsList);
		Collections.sort(ConversationsListFor);

		List<List<Conversation>> MorningSessionsCombinations = getPossibleCombinationSessionEvent(ConversationsListFor, totalPossibleDays, true);

		for (List<Conversation> ConversationList : MorningSessionsCombinations) {
			ConversationsListFor.removeAll(ConversationList);
		}

		List<List<Conversation>> EveningSessionsCombinations = getPossibleCombinationSessionEvent(ConversationsListFor, totalPossibleDays, false);

		for (List<Conversation> ConversationList : EveningSessionsCombinations) {
			ConversationsListFor.removeAll(ConversationList);
		}

		int AFTERNOON_TIME_MINUTES = 240;
		if (!ConversationsListFor.isEmpty()) {
			List<Conversation> scheduledConversationList = new ArrayList<Conversation>();
			for (List<Conversation> ConversationList : EveningSessionsCombinations) {
				int totalTime = getTotalConversationsTime(ConversationList);

				for (Conversation Conversation : ConversationsListFor) {
					int ConversationTime = Conversation.getTimeDuration();

					if (ConversationTime + totalTime <= AFTERNOON_TIME_MINUTES) {
						ConversationList.add(Conversation);
						Conversation.setScheduled(true);
						scheduledConversationList.add(Conversation);
					}
				}

				ConversationsListFor.removeAll(scheduledConversationList);
				if (ConversationsListFor.isEmpty())
					break;
			}
		}

		if (!ConversationsListFor.isEmpty()) {
			throw new Exception("Unable to schedule the whole tasks for this conference.");
		}

		return getScheduledConversationsList(MorningSessionsCombinations, EveningSessionsCombinations);
	}

	/**
	 * Try to find for a possible session combination. If morning session, each
	 * session must have a total time of three hours. Otherwise, if evening session,
	 * they must have a total time greater than three hours.
	 * 
	 * @param ConversationsListForOperation
	 * @param totalPossibleDays
	 * @param morningSession
	 * @return List of a Conversation List
	 */
	private List<List<Conversation>> getPossibleCombinationSessionEvent(List<Conversation> ConversationsListForOperation, 
			int totalPossibleDays, boolean morningSession) {
		int MORNING_TIME_MINUTES = 180;
		int AFTERNOON_TIME_MINUTES = 240;

		if (morningSession)
			AFTERNOON_TIME_MINUTES = MORNING_TIME_MINUTES;

		int ConversationListAr = ConversationsListForOperation.size();
		List<List<Conversation>> getCombinationsOfConversations = new ArrayList<List<Conversation>>();
		int possibleCombinationCount = 0;

		for (int count = 0; count < ConversationListAr; count++) {
			int startPoint = count;
			int totalTime = 0;
			List<Conversation> possibleCombinationList = new ArrayList<Conversation>();

			while (startPoint != ConversationListAr) {
				int currentCount = startPoint;
				startPoint++;
				Conversation currentConversation = ConversationsListForOperation.get(currentCount);
				if (currentConversation.isScheduled())
					continue;
				int ConversationTime = currentConversation.getTimeDuration();

				if (ConversationTime > AFTERNOON_TIME_MINUTES || ConversationTime + totalTime > AFTERNOON_TIME_MINUTES) {
					continue;
				}

				possibleCombinationList.add(currentConversation);
				totalTime += ConversationTime;

				if (morningSession) {
					if (totalTime == AFTERNOON_TIME_MINUTES)
						break;
				} else if (totalTime >= MORNING_TIME_MINUTES)
					break;
			}

			boolean validSession = false;
			if (morningSession)
				validSession = (totalTime == AFTERNOON_TIME_MINUTES);
			else
				validSession = (totalTime >= MORNING_TIME_MINUTES && totalTime <= AFTERNOON_TIME_MINUTES);

			if (validSession) {
				getCombinationsOfConversations.add(possibleCombinationList);
				for (Conversation Conversation : possibleCombinationList) {
					Conversation.setScheduled(true);
				}
				possibleCombinationCount++;
				if (possibleCombinationCount == totalPossibleDays)
					break;
			}
		}

		return getCombinationsOfConversations;
	}

	/**
	 * Prints out the scheduled Conversations with the respective awaited
	 * non-formatted text message.
	 * 
	 * @param combForMornSessions
	 * @param combForEveSessions
	 * @return List of a Conversation List
	 */
	@SuppressWarnings("deprecation")
	private List<List<Conversation>> getScheduledConversationsList(List<List<Conversation>> combForMornSessions,
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

	private void showResultsInScreen() {
		String formattedList = Arrays.toString(finalList.toArray()).replace("[", "").replace("]", "").replace(",", "");
		JOptionPane pane = new JOptionPane(formattedList);

		JDialog dialog = pane.createDialog(null, "Results");
		dialog.setSize(380, 620);
		dialog.setVisible(true);
	}

	/**
	 * Takes conversation's total time Conversations of the actual given list.
	 * 
	 * @param ConversationsList
	 * @return int type (primitive attribute) 
	 */
	public static int getTotalConversationsTime(List<Conversation> ConversationsList) {
		if (ConversationsList == null || ConversationsList.isEmpty())
			return 0;

		int totalTime = 0;
		for (Conversation Conversation : ConversationsList) {
			totalTime += Conversation.timeDuration;
		}
		return totalTime;
	}

	/**
	 * Gets the next scheduled time to be converted into String.
	 * 
	 * @param date
	 * @param timeDuration
	 * @return String type (Wrapper class)
	 */
	private String getNextScheduledTime(Date date, int timeDuration) {
		long timeInLong = date.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm ");

		long timeDurationInLong = timeDuration * 60 * 1000;
		long newTimeInLong = timeInLong + timeDurationInLong;

		date.setTime(newTimeInLong);
		String str = dateFormat.format(date);
		return str;
	}
}
