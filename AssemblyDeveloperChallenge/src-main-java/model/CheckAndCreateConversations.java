package model;

import java.util.ArrayList;
import java.util.List;

import exceptions.InvalidTalkException;

public class CheckAndCreateConversations {
	
	private List<Conversation> convListValidation;
	
	public List<Conversation> checkAndCreateConversations(List<String> ConversationList) throws Exception {

		if (ConversationList == null) {
			throw new InvalidTalkException("Empty Conversation List");
		}

		convListValidation = new ArrayList<Conversation>();
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

			convListValidation.add(new Conversation(Conversation, name, time));
		}

		return convListValidation;
	}
	
}
