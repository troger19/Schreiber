package com.example.schreiber.util;

public class Sentence {

	private int id;
	private String from;
	private String to;
	private String topic;
	private int known;

	public Sentence(int id, String from, String to, String topic, int known) {
		super();
		this.id = id;
		this.from = from;
		this.to = to;
		this.topic = topic;
		this.setKnown(known);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public int getKnown() {
		return known;
	}

	public void setKnown(int known) {
		this.known = known;
	}

	@Override
	public String toString() {
		return "Sentence [id=" + id + "from=" + from + ", to=" + to + ", topic=" + topic + ", known=" + known + "]";
	}

}
