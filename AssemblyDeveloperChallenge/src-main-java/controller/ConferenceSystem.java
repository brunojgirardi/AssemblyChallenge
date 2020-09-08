package controller;

import java.util.ArrayList;
import java.util.List;

import model.CheckAndCreateConversations;
import model.Conversation;
import model.ConversationListFromFile;
import model.ScheduleConferenceTrack;

public class ConferenceSystem {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	private List<List<Conversation>> datedConversationsList = new ArrayList<List<Conversation>>();

	private ConversationListFromFile conversationListFromFile;
	private CheckAndCreateConversations checkAndCreateConversations;
	private ScheduleConferenceTrack scheduleConferenceTrack;

	public ConferenceSystem() {
		conversationListFromFile = new ConversationListFromFile();
		checkAndCreateConversations = new CheckAndCreateConversations();
		scheduleConferenceTrack = new ScheduleConferenceTrack();
	}

	/**
	 * Schedule conference creating method.
	 * 
	 * @param fileName
	 * @return List of a Conversation List
	 * @throws Throwable
	 */
	public List<List<Conversation>> conferenceAgenda(String fileName) throws Throwable {
		List<String> ConversationList = conversationListFromFile.getConversationListFromFile(fileName);
		this.scheduleConference(ConversationList);
		return this.datedConversationsList;
	}

	/**
	 * Schedule conference creating method.
	 * 
	 * @param ConversationList
	 * @throws Exception
	 * @return List of a Conversation List
	 */
	public List<List<Conversation>> scheduleConference(List<String> ConversationList) throws Exception {
		List<Conversation> ConversationsList = checkAndCreateConversations.checkAndCreateConversations(ConversationList);
		return scheduleConferenceTrack.getScheduleConferenceTrack(ConversationsList);
	}

	public ConversationListFromFile getConversationListFromFile() {
		return conversationListFromFile;
	}

	public void setConversationListFromFile(ConversationListFromFile conversationListFromFile) {
		this.conversationListFromFile = conversationListFromFile;
	}

	public CheckAndCreateConversations getCheckAndCreateConversations() {
		return checkAndCreateConversations;
	}

	public void setCheckAndCreateConversations(CheckAndCreateConversations checkAndCreateConversations) {
		this.checkAndCreateConversations = checkAndCreateConversations;
	}

	public ScheduleConferenceTrack getScheduleConferenceTrack() {
		return scheduleConferenceTrack;
	}

	public void setScheduleConferenceTrack(ScheduleConferenceTrack scheduleConferenceTrack) {
		this.scheduleConferenceTrack = scheduleConferenceTrack;
	}

}
