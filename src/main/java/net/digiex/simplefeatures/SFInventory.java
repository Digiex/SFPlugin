package net.digiex.simplefeatures;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;

@Entity
@Table(name = "sf_inventory")
public class SFInventory {

	@Id
	private int id;

	@NotNull
	private String playerName;

	@NotEmpty
	private String inventory;
	private String armor;

	private int health = 20;
	private int food = 20;
	private int gameMode = 0;
	private float exp = 0;
	private float exhaustion;
	private int fireTicks = 0;
	private int level = 0;
	private int remainingAir = 20;
	private float saturation;
	private int totalExperience = 0;

	public String getArmor() {
		return armor;
	}

	public float getExhaustion() {
		return exhaustion;
	}

	public float getExp() {
		return exp;
	}

	public int getFireTicks() {
		return fireTicks;
	}

	public int getFood() {
		return food;
	}

	public int getGameMode() {
		return gameMode;
	}

	public int getHealth() {
		return health;
	}

	public int getId() {
		return id;
	}

	public String getInventory() {
		return inventory;
	}

	public int getLevel() {
		return level;
	}

	public String getPlayerName() {
		return playerName;
	}

	public int getRemainingAir() {
		return remainingAir;
	}

	public float getSaturation() {
		return saturation;
	}

	public int getTotalExperience() {
		return totalExperience;
	}

	public void setArmor(String armor) {
		this.armor = armor;
	}

	public void setExhaustion(float exhaustion) {
		this.exhaustion = exhaustion;
	}

	public void setExp(float exp) {
		this.exp = exp;
	}

	public void setFireTicks(int fireTicks) {
		this.fireTicks = fireTicks;
	}

	public void setFood(int foodlevel) {
		food = foodlevel;
	}

	public void setGameMode(int gameMode) {
		this.gameMode = gameMode;
	}

	public void setHealth(int healthlevel) {
		health = healthlevel;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setInventory(String inventory) {
		this.inventory = inventory;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public void setRemainingAir(int remainingAir) {
		this.remainingAir = remainingAir;
	}

	public void setSaturation(float saturation) {
		this.saturation = saturation;
	}

	public void setTotalExperience(int totalExperience) {
		this.totalExperience = totalExperience;
	}
}