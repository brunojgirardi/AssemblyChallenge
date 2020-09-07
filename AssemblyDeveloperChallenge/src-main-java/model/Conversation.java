package model;

public class Conversation implements Comparable<Object> {
	String title, name, scheduledTime;
	int timeDuration;
	boolean scheduled = false;

	/**
	 * Conference class constructor for initial data entry
	 * 
	 * @param title
	 * @param name
	 * @param time
	 */
	public Conversation(String title, String name, int time) {
		this.title = title;
		this.name = name;
		this.timeDuration = time;
	}
	
	/**
	 * Does the sorting of all the data into a decending order on.
	 * 
	 * @param obj
	 * @return
	 */
	@Override
	public int compareTo(Object obj) {
		Conversation Conversation = (Conversation) obj;
		if (this.timeDuration > Conversation.timeDuration)
			return -1;
		else if (this.timeDuration < Conversation.timeDuration)
			return 1;
		else
			return 0;
	}

	/**
	 * Defines the scheduled boolean condition.
	 * 
	 * @param scheduled
	 */
	public void setScheduled(boolean scheduled) {
		this.scheduled = scheduled;
	}

	/**
	 * Takes the scheduled boolean condition defined previously.
	 * 
	 * @return scheduled
	 */
	public boolean isScheduled() {
		return scheduled;
	}

	/**
	 * Defines the Conversations scheduled time in this format: (AM/PM)
	 * hour:minutes.
	 * 
	 * @param scheduledTime
	 */
	public void setSchedulingTime(String scheduledTime) {
		this.scheduledTime = scheduledTime;
	}

	/**
	 * Takes the scheduled time.
	 * 
	 * @return
	 */
	public String getScheduledTime() {
		return scheduledTime;
	}

	/**
	 * Takes the Conversation's time duration.
	 * 
	 * @return
	 */
	public int getTimeDuration() {
		return timeDuration;
	}

	/**
	 * Take the Conversation title.
	 * 
	 * @return
	 */
	public String getTitle() {
		return title;
	}
}
