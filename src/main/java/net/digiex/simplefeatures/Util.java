package net.digiex.simplefeatures;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Util {
	// The player can stand inside these materials
	private static final Set<Integer> AIR_MATERIALS = new HashSet<Integer>();

	private static final HashSet<Byte> AIR_MATERIALS_TARGET = new HashSet<Byte>();
	static {
		AIR_MATERIALS.add(Material.AIR.getId());
		AIR_MATERIALS.add(Material.SAPLING.getId());
		AIR_MATERIALS.add(Material.POWERED_RAIL.getId());
		AIR_MATERIALS.add(Material.DETECTOR_RAIL.getId());
		AIR_MATERIALS.add(Material.LONG_GRASS.getId());
		AIR_MATERIALS.add(Material.DEAD_BUSH.getId());
		AIR_MATERIALS.add(Material.YELLOW_FLOWER.getId());
		AIR_MATERIALS.add(Material.RED_ROSE.getId());
		AIR_MATERIALS.add(Material.BROWN_MUSHROOM.getId());
		AIR_MATERIALS.add(Material.RED_MUSHROOM.getId());
		AIR_MATERIALS.add(Material.TORCH.getId());
		AIR_MATERIALS.add(Material.REDSTONE_WIRE.getId());
		AIR_MATERIALS.add(Material.SEEDS.getId());
		AIR_MATERIALS.add(Material.SIGN_POST.getId());
		AIR_MATERIALS.add(Material.WOODEN_DOOR.getId());
		AIR_MATERIALS.add(Material.LADDER.getId());
		AIR_MATERIALS.add(Material.RAILS.getId());
		AIR_MATERIALS.add(Material.WALL_SIGN.getId());
		AIR_MATERIALS.add(Material.LEVER.getId());
		AIR_MATERIALS.add(Material.STONE_PLATE.getId());
		AIR_MATERIALS.add(Material.IRON_DOOR_BLOCK.getId());
		AIR_MATERIALS.add(Material.WOOD_PLATE.getId());
		AIR_MATERIALS.add(Material.REDSTONE_TORCH_OFF.getId());
		AIR_MATERIALS.add(Material.REDSTONE_TORCH_ON.getId());
		AIR_MATERIALS.add(Material.STONE_BUTTON.getId());
		AIR_MATERIALS.add(Material.SNOW.getId());
		AIR_MATERIALS.add(Material.SUGAR_CANE_BLOCK.getId());
		AIR_MATERIALS.add(Material.DIODE_BLOCK_OFF.getId());
		AIR_MATERIALS.add(Material.DIODE_BLOCK_ON.getId());
		AIR_MATERIALS.add(Material.TRAP_DOOR.getId());
		AIR_MATERIALS.add(Material.PUMPKIN_STEM.getId());
		AIR_MATERIALS.add(Material.MELON_STEM.getId());
		AIR_MATERIALS.add(Material.VINE.getId());
		AIR_MATERIALS.add(Material.FENCE_GATE.getId());
		AIR_MATERIALS.add(Material.WATER_LILY.getId());
		AIR_MATERIALS.add(Material.NETHER_FENCE.getId());
		AIR_MATERIALS.add(Material.NETHER_WARTS.getId());

		for (Integer integer : AIR_MATERIALS) {
			AIR_MATERIALS_TARGET.add(integer.byteValue());
		}
		AIR_MATERIALS_TARGET.add((byte) Material.WATER.getId());
		AIR_MATERIALS_TARGET.add((byte) Material.STATIONARY_WATER.getId());
	}

	private static DecimalFormat dFormat = new DecimalFormat("#0.00",
			DecimalFormatSymbols.getInstance(Locale.US));
	private static transient final Pattern URL_PATTERN = Pattern
			.compile("((?:(?:https?)://)?[\\w-_\\.]{2,})\\.([a-z]{2,3}(?:/\\S+)?)");

	private static transient final Pattern VANILLA_PATTERN = Pattern
			.compile("\u00A7+[0-9A-FK-ORa-fk-or]");

	private static transient final Pattern REPLACE_PATTERN = Pattern
			.compile("&([0-9a-fk-or])");

	private static transient final Pattern VANILLA_COLOR_PATTERN = Pattern
			.compile("\u00A7+[0-9A-Fa-f]");
	private static transient final Pattern VANILLA_MAGIC_PATTERN = Pattern
			.compile("\u00A7+[Kk]");

	private static transient final Pattern VANILLA_FORMAT_PATTERN = Pattern
			.compile("\u00A7+[L-ORl-or]");

	private static transient final Pattern REPLACE_COLOR_PATTERN = Pattern
			.compile("&([0-9a-f])");

	private static transient final Pattern REPLACE_MAGIC_PATTERN = Pattern
			.compile("&(k)");

	private static transient final Pattern REPLACE_FORMAT_PATTERN = Pattern
			.compile("&([l-or])");

	public static String blockURL(final String input) {
		if (input == null) {
			return null;
		}
		String text = URL_PATTERN.matcher(input).replaceAll("$1 $2");
		while (URL_PATTERN.matcher(text).find()) {
			text = URL_PATTERN.matcher(text).replaceAll("$1 $2");
		}
		return text;
	}

	public static ItemStack convertBlockToItem(final Block block) {
		final ItemStack is = new ItemStack(block.getType(), 1, (short) 0,
				block.getData());
		switch (is.getType()) {
		case WOODEN_DOOR:
			is.setType(Material.WOOD_DOOR);
			is.setDurability((short) 0);
			break;
		case IRON_DOOR_BLOCK:
			is.setType(Material.IRON_DOOR);
			is.setDurability((short) 0);
			break;
		case SIGN_POST:
		case WALL_SIGN:
			is.setType(Material.SIGN);
			is.setDurability((short) 0);
			break;
		case CROPS:
			is.setType(Material.SEEDS);
			is.setDurability((short) 0);
			break;
		case CAKE_BLOCK:
			is.setType(Material.CAKE);
			is.setDurability((short) 0);
			break;
		case BED_BLOCK:
			is.setType(Material.BED);
			is.setDurability((short) 0);
			break;
		case REDSTONE_WIRE:
			is.setType(Material.REDSTONE);
			is.setDurability((short) 0);
			break;
		case REDSTONE_TORCH_OFF:
		case REDSTONE_TORCH_ON:
			is.setType(Material.REDSTONE_TORCH_ON);
			is.setDurability((short) 0);
			break;
		case DIODE_BLOCK_OFF:
		case DIODE_BLOCK_ON:
			is.setType(Material.DIODE);
			is.setDurability((short) 0);
			break;
		case DOUBLE_STEP:
			is.setType(Material.STEP);
			break;
		case TORCH:
		case RAILS:
		case LADDER:
		case WOOD_STAIRS:
		case COBBLESTONE_STAIRS:
		case LEVER:
		case STONE_BUTTON:
		case FURNACE:
		case DISPENSER:
		case PUMPKIN:
		case JACK_O_LANTERN:
		case WOOD_PLATE:
		case STONE_PLATE:
		case PISTON_STICKY_BASE:
		case PISTON_BASE:
		case IRON_FENCE:
		case THIN_GLASS:
		case TRAP_DOOR:
		case FENCE:
		case FENCE_GATE:
		case NETHER_FENCE:
			is.setDurability((short) 0);
			break;
		case FIRE:
			return null;
		case PUMPKIN_STEM:
			is.setType(Material.PUMPKIN_SEEDS);
			break;
		case MELON_STEM:
			is.setType(Material.MELON_SEEDS);
			break;
		}
		return is;
	}

	public static String formatAsCurrency(final double value) {
		String str = dFormat.format(value);
		if (str.endsWith(".00")) {
			str = str.substring(0, str.length() - 3);
		}
		return str;
	}

	public static String formatMessage(final Player user,
			final String permBase, final String input) {
		if (input == null) {
			return null;
		}
		String message = formatString(user, permBase, input);
		return message;
	}

	public static String formatString(final Player user, final String permBase,
			final String input) {
		if (input == null) {
			return null;
		}
		String message;
		if (user.isOp()) {
			message = Util.replaceColor(input, REPLACE_COLOR_PATTERN);
		} else {
			message = Util.stripColor(input, VANILLA_COLOR_PATTERN);
		}
		if (user.isOp()) {
			message = Util.replaceColor(message, REPLACE_MAGIC_PATTERN);
		} else {
			message = Util.stripColor(message, VANILLA_MAGIC_PATTERN);
		}
		if (user.isOp()) {
			message = Util.replaceColor(message, REPLACE_FORMAT_PATTERN);
		} else {
			message = Util.stripColor(message, VANILLA_FORMAT_PATTERN);
		}
		return message;
	}

	public static Location getSafeDestination(final Location loc)
			throws Exception {
		if (loc == null || loc.getWorld() == null) {
			throw new Exception("No destination set");
		}
		final World world = loc.getWorld();
		int x = loc.getBlockX();
		int y = (int) Math.round(loc.getY());
		int z = loc.getBlockZ();

		while (isBlockAboveAir(world, x, y, z)) {
			y -= 1;
			if (y < 0) {
				break;
			}
		}

		while (isBlockUnsafe(world, x, y, z)) {
			y += 1;
			if (y >= world.getHighestBlockYAt(x, z)) {
				x += 1;
				break;
			}
		}
		while (isBlockUnsafe(world, x, y, z)) {
			y -= 1;
			if (y <= 1) {
				x += 1;
				y = world.getHighestBlockYAt(x, z);
				if (x - 32 > loc.getBlockX()) {
					throw new Exception("There is a hole in the floor!");
				}
			}
		}
		return new Location(world, x + 0.5D, y, z + 0.5D, loc.getYaw(),
				loc.getPitch());
	}

	private static boolean isBlockAboveAir(final World world, final int x,
			final int y, final int z) {
		return AIR_MATERIALS.contains(world.getBlockAt(x, y - 1, z).getType()
				.getId());
	}

	public static boolean isBlockUnsafe(final World world, final int x,
			final int y, final int z) {
		final Block below = world.getBlockAt(x, y - 1, z);
		if (below.getType() == Material.LAVA
				|| below.getType() == Material.STATIONARY_LAVA) {
			return true;
		}

		if (below.getType() == Material.FIRE) {
			return true;
		}

		if ((!AIR_MATERIALS.contains(world.getBlockAt(x, y, z).getType()
				.getId()))
				|| (!AIR_MATERIALS.contains(world.getBlockAt(x, y + 1, z)
						.getType().getId()))) {
			return true;
		}
		return isBlockAboveAir(world, x, y, z);
	}

	public static boolean isInt(final String sInt) {
		try {
			Integer.parseInt(sInt);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public static String joinList(Object... list) {
		return joinList(", ", list);
	}

	public static String joinList(String seperator, Object... list) {
		StringBuilder buf = new StringBuilder();
		for (Object each : list) {
			if (buf.length() > 0) {
				buf.append(seperator);
			}

			if (each instanceof Collection) {
				buf.append(joinList(seperator, ((Collection<?>) each).toArray()));
			} else {
				try {
					buf.append(each.toString());
				} catch (Exception e) {
					buf.append(each.toString());
				}
			}
		}
		return buf.toString();
	}

	public static String lastCode(final String input) {
		int pos = input.lastIndexOf("§");
		if (pos == -1 || (pos + 1) == input.length()) {
			return "";
		}
		return input.substring(pos, pos + 2);
	}

	private static String replaceColor(final String input, final Pattern pattern) {
		return pattern.matcher(input).replaceAll("\u00a7$1");
	}

	public static String replaceFormat(final String input) {
		if (input == null) {
			return null;
		}
		return REPLACE_PATTERN.matcher(input).replaceAll("\u00a7$1");
	}

	public static double roundDouble(final double d) {
		return Math.round(d * 100.0) / 100.0;
	}

	private static String stripColor(final String input, final Pattern pattern) {
		return pattern.matcher(input).replaceAll("");
	}

	public static String stripFormat(final String input) {
		if (input == null) {
			return null;
		}
		return VANILLA_PATTERN.matcher(input).replaceAll("");
	}

	private Util() {
	}
}