package net.digiex.simplefeatures;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.bukkit.GameMode;

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
	private int experience = 0;
	private float exhaustion;
	private int fireTicks = 0;
	private int level = 0;
	private int remainingAir = 20;
	private float saturation;
	private int totalExperience = 0;

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getInventory() {
		return this.inventory;
	}

	public void setInventory(String inventory) {
		this.inventory = inventory;
	}

	public GameMode getGamemode() {
		return GameMode.getByValue(this.gameMode);
	}

	public void setGameMode(GameMode gameMode) {
		this.gameMode = gameMode.getValue();
	}

	public String getPlayerName() {
		return this.playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public String getArmor() {
		return this.armor;
	}

	public void setArmor(String armor) {
		this.armor = armor;
	}

	public int getFood() {
		return this.food;
	}

	public void setFood(int foodlevel) {
		this.food = foodlevel;
	}

	public int getHealth() {
		return this.health;
	}

	public void setHealth(int healthlevel) {
		this.health = healthlevel;
	}

	public int getGameMode() {
		return gameMode;
	}

	public void setGameMode(int gameMode) {
		this.gameMode = gameMode;
	}

	public int getExperience() {
		return experience;
	}

	public void setExperience(int experience) {
		this.experience = experience;
	}

	public float getExhaustion() {
		return exhaustion;
	}

	public void setExhaustion(float exhaustion) {
		this.exhaustion = exhaustion;
	}

	public int getFireTicks() {
		return fireTicks;
	}

	public void setFireTicks(int fireTicks) {
		this.fireTicks = fireTicks;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getRemainingAir() {
		return remainingAir;
	}

	public void setRemainingAir(int remainingAir) {
		this.remainingAir = remainingAir;
	}

	public float getSaturation() {
		return saturation;
	}

	public void setSaturation(float saturation) {
		this.saturation = saturation;
	}

	public int getTotalExperience() {
		return totalExperience;
	}

	public void setTotalExperience(int totalExperience) {
		this.totalExperience = totalExperience;
	}
}