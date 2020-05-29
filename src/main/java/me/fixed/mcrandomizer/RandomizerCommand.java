package me.fixed.mcrandomizer;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class RandomizerCommand implements CommandExecutor, TabCompleter {
    @NotNull private final static List<String> possibleSubCommands = Arrays.asList(
            "blocks",
            "entities",
            "chests",
            "snapshot",
            "help"
    );
    @NotNull private final static String HELP_MESSAGE =
            ChatColor.AQUA + "== " + ChatColor.GREEN + "MC Randomizer " + ChatColor.AQUA + "==" + '\n' +
                    ChatColor.YELLOW + "/randomizer blocks " + ChatColor.GRAY + "Randomizes block loot table." + '\n' +
                    ChatColor.YELLOW + "/randomizer entities " + ChatColor.GRAY + "Randomizes entity loot table." + '\n' +
                    ChatColor.YELLOW + "/randomizer chests " + ChatColor.GRAY + "Randomizes chest loot table." + '\n' +
                    ChatColor.YELLOW + "/randomizer snapshot " + ChatColor.GRAY + "Saves a snapshot of this loot table in the data folder." + '\n' +
                    ChatColor.YELLOW + "/randomizer help " + ChatColor.GRAY + "Displays this message.";

    @NotNull private final MCRandomizerPlugin plugin;

    public RandomizerCommand(@NotNull MCRandomizerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(HELP_MESSAGE);
            return true;
        }
        switch (args[0].toLowerCase(Locale.ENGLISH)) {
            case "blocks":
                sender.sendMessage(ChatColor.RED + "Randomizing block loot table...");
                plugin.getRandomizer().randomizeBlockLootTable();
                sender.sendMessage(ChatColor.GREEN + "Block loot table randomized.");
                break;
            case "entities":
                sender.sendMessage(ChatColor.RED + "Randomizing entity loot table...");
                plugin.getRandomizer().randomizeEntityLootTable();
                sender.sendMessage(ChatColor.GREEN + "Entity loot table randomized.");
                break;
            case "chests":
                sender.sendMessage(ChatColor.RED + "Randomizing chest loot table...");
                plugin.getRandomizer().randomizeChestLootTable();
                sender.sendMessage(ChatColor.GREEN + "Chest loot table randomized.");
                break;
            case "snapshot":
                sender.sendMessage(ChatColor.RED + "Saving snapshot...");
                plugin.saveSnapshot();
                sender.sendMessage(ChatColor.GREEN + "Snapshot finished saving.");
                break;
            default:
                sender.sendMessage(HELP_MESSAGE);
                break;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            String check = args[0].toLowerCase(Locale.ENGLISH);
            return possibleSubCommands.parallelStream().filter(str -> str.startsWith(check)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
