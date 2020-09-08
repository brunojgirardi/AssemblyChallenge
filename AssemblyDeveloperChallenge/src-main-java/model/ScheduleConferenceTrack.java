package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScheduleConferenceTrack {

	private List<List<Conversation>> morningSessionsCombinations;
	private List<List<Conversation>> eveningSessionsCombinations;
	private ScheduledConversationsList scheduledConversationsList;
	
	public ScheduleConferenceTrack() {
		morningSessionsCombinations = new ArrayList<List<Conversation>>();
		eveningSessionsCombinations = new ArrayList<List<Conversation>>();
		scheduledConversationsList = new ScheduledConversationsList();
	}
	
	public List<List<Conversation>> getScheduleConferenceTrack(List<Conversation> ConversationsList) throws Exception {

		int perDayMinTime = 6 * 60;
		int totalConversationsTime = getTotalConversationsTime(ConversationsList);
		int totalPossibleDays = totalConversationsTime / perDayMinTime;

		List<Conversation> ConversationsListFor = new ArrayList<Conversation>();
		ConversationsListFor.addAll(ConversationsList);
		Collections.sort(ConversationsListFor);

		morningSessionsCombinations = getPossibleCombinationSessionEvent(ConversationsListFor, totalPossibleDays, true);

		for (List<Conversation> ConversationList : morningSessionsCombinations) {
			ConversationsListFor.removeAll(ConversationList);
		}

		eveningSessionsCombinations = getPossibleCombinationSessionEvent(ConversationsListFor, totalPossibleDays, false);

		for (List<Conversation> ConversationList : eveningSessionsCombinations) {
			ConversationsListFor.removeAll(ConversationList);
		}

		int AFTERNOON_TIME_MINUTES = 240;
		if (!ConversationsListFor.isEmpty()) {
			List<Conversation> scheduledConversationList = new ArrayList<Conversation>();
			for (List<Conversation> ConversationList : eveningSessionsCombinations) {
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
		
		return scheduledConversationsList.getScheduledConversationsList(morningSessionsCombinations, eveningSessionsCombinations);
	}
	
	public List<List<Conversation>> getPossibleCombinationSessionEvent(List<Conversation> ConversationsListForOperation, int totalPossibleDays,
			boolean morningSession) {
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
	
	public static int getTotalConversationsTime(List<Conversation> ConversationsList) {
		if (ConversationsList == null || ConversationsList.isEmpty())
			return 0;

		int totalTime = 0;
		for (Conversation Conversation : ConversationsList) {
			totalTime += Conversation.getTimeDuration();
		}
		return totalTime;
	}
	
}
