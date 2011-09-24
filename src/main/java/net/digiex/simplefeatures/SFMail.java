package net.digiex.simplefeatures;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotNull;

@Entity()
@Table(name = "sf_mail")
public class SFMail {
	@Id
	private int id;

	@NotNull
	private String fromPlayer;

	@NotNull
	private String toPlayer;

	@NotNull
	private String message;

	@NotNull
	private long timestamp;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFromPlayer() {
		return fromPlayer;
	}

	public void setFromPlayer(String fromPlayer) {
		this.fromPlayer = fromPlayer;
	}

	public String getToPlayer() {
		return toPlayer;
	}

	public void setToPlayer(String toPlayer) {
		this.toPlayer = toPlayer;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public void newMail(String fromPlayer, String toPlayer, String message) {
		newMail(fromPlayer, toPlayer, message, System.currentTimeMillis());
	}

	public void newMail(String fromPlayer, String toPlayer, String message,
			long timestamp) {
		this.fromPlayer = fromPlayer;
		this.toPlayer = toPlayer;
		this.timestamp = timestamp;
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
