package net.digiex.simplefeatures;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotNull;

@Entity()
@Table(name = "sf_compasspoints")
public class SFCompassPoint {
	@Id
	private int id;

	@NotNull
	private String playerName;

	@NotNull
	private String pointName;

	@NotNull
	private String worldName;

	@NotNull
	private double x;

	@NotNull
	private double y;

	@NotNull
	private double z;

	@NotNull
	private float pitch;

	@NotNull
	private float yaw;

	public int getId() {
		return id;
	}

	public float getPitch() {
		return pitch;
	}

	public String getPlayerName() {
		return playerName;
	}

	public String getWorldName() {
		return worldName;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public float getYaw() {
		return yaw;
	}

	public double getZ() {
		return z;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public void setWorldName(String worldName) {
		this.worldName = worldName;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public String getPointName() {
		return pointName;
	}

	public void setPointName(String pointName) {
		this.pointName = pointName;
	}

}
