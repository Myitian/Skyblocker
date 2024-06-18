package de.hysky.skyblocker.skyblock.item.slottext.adders;

import de.hysky.skyblocker.skyblock.item.slottext.SlotText;
import de.hysky.skyblocker.skyblock.item.slottext.SlotTextAdder;
import de.hysky.skyblocker.utils.RomanNumerals;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//This class is split into 3 inner classes as there are multiple screens for showing catacombs levels, each with different slot ids or different style of showing the level.
//It's still kept in 1 main class for organization purposes.
public class CatacombsLevelAdder {
	private CatacombsLevelAdder() {
	}

	public static class Dungeoneering extends SlotTextAdder {
		private static final Pattern LEVEL_PATTERN = Pattern.compile(".*?(?:(?<arabic>\\d+)|(?<roman>\\S+))? ?✯?");
		public Dungeoneering() {
			super("^Dungeoneering");
		}

		@Override
		public @NotNull List<SlotText> getText(Slot slot) {
			switch (slot.id) {
				case 12, 29, 30, 31, 32, 33 -> {
					Matcher matcher = LEVEL_PATTERN.matcher(slot.getStack().getName().getString());
					if (!matcher.matches()) return List.of();
					String arabic = matcher.group("arabic");
					String roman = matcher.group("roman");
					if (arabic == null && roman == null) return List.of(SlotText.bottomLeft(Text.literal("0").formatted(Formatting.RED)));
					String level;
					if (arabic != null) {
						if (!NumberUtils.isDigits(arabic)) return List.of(); //Sanity check
						level = arabic;
					} else { // roman != null
						if (!RomanNumerals.isValidRomanNumeral(roman)) return List.of(); //Sanity check
						level = String.valueOf(RomanNumerals.romanToDecimal(roman));
					}

					return List.of(SlotText.bottomLeft(Text.literal(level).withColor(0xFFDDC1)));
				}
				default -> {
					return List.of();
				}
			}
		}
	}

	public static class DungeonClasses extends SlotTextAdder {

		public DungeonClasses() {
			super("^Dungeon Classes"); //Applies to both screens as they are same in both the placement and the style of the level text.
		}

		@Override
		public @NotNull List<SlotText> getText(Slot slot) {
			switch (slot.id) {
				case 11, 12, 13, 14, 15 -> {
					String level = getBracketedLevelFromName(slot.getStack());
					if (!NumberUtils.isDigits(level)) return List.of();
					return List.of(SlotText.bottomLeft(Text.literal(level).withColor(0xFFDDC1)));
				}
				default -> {
					return List.of();
				}
			}
		}
	}

	public static class ReadyUp extends SlotTextAdder {

		public ReadyUp() {
			super("^Ready Up");
		}

		@Override
		public @NotNull List<SlotText> getText(Slot slot) {
			switch (slot.id) {
				case 29, 30, 31, 32, 33 -> {
					String level = getBracketedLevelFromName(slot.getStack());
					if (!NumberUtils.isDigits(level)) return List.of();
					return List.of(SlotText.bottomLeft(Text.literal(level).withColor(0xFFDDC1)));
				}
				default -> {
					return List.of();
				}
			}
		}
	}

	public static String getBracketedLevelFromName(ItemStack itemStack) {
		String name = itemStack.getName().getString();
		if (!name.startsWith("[Lvl ")) return null;
		int index = name.indexOf(']');
		if (index == -1) return null;
		return name.substring(5, index);
	}
}
