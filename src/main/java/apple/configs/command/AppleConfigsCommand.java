package apple.configs.command;

import apple.configs.AppleConfigsPlugin;
import apple.configs.data.config.AppleConfig;
import apple.configs.registered.AppleConfigsDatabase;
import co.aikar.commands.*;
import co.aikar.commands.annotation.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@CommandAlias("config")
@CommandPermission("config.all")
public class AppleConfigsCommand extends BaseCommand {
    public AppleConfigsCommand() {
        PaperCommandManager commandManager = AppleConfigsPlugin.get().getCommandManager();
        CommandCompletions<BukkitCommandCompletionContext> completions = commandManager.getCommandCompletions();
        completions.registerCompletion("@config_path", this::configPathCompletions);
        commandManager.registerCommand(this);
    }

    @Default
    @CommandCompletion("@config_path")
    public void set(Player player, @Name("config_path") String[] pathAndNameStrings) {
        List<String> pathAndName = trimPathname(List.of(pathAndNameStrings));
        @Nullable AppleConfig<?> config = AppleConfigsDatabase.get().findConfig(pathAndName);
        String[] pathInConfig = trimConfigPath(config, pathAndName);
        if (pathInConfig == null || pathInConfig.length <= 1) this.commandError(pathAndNameStrings);
        String valueToSet = pathInConfig[pathInConfig.length - 1];
        String[] pathToSet = new String[pathInConfig.length - 1];
        System.arraycopy(pathInConfig, 0, pathToSet, 0, pathToSet.length);
        String pathToSetJoined = String.join(".", pathToSet);
        if (config.setValue(pathToSet, valueToSet)) {
            player.sendMessage(ChatColor.GREEN + String.format("Successfully set set %s to %s", pathToSetJoined, valueToSet));
        } else {
            player.sendMessage(ChatColor.RED + String.format("Failed to set %s to %s", pathToSetJoined, valueToSet));
        }
    }

    @NotNull
    public List<String> trimPathname(List<String> path) {
        path = new ArrayList<>(path);
        path.removeIf(String::isBlank);
        return path;
    }

    private void commandError(String[] args) {
        throw new InvalidCommandArgument(String.format("There is no config that matches '%s'", String.join(", ", args)));
    }

    private Collection<String> configPathCompletions(BukkitCommandCompletionContext context) {
        List<String> pathAndName = List.of(context.getContextValueByName(String[].class, "config_path"));
        pathAndName = trimPathname(pathAndName);

        @Nullable AppleConfig<?> config = AppleConfigsDatabase.get().findConfig(pathAndName);

        if (config == null) return AppleConfigsDatabase.get().autoComplete(pathAndName);

        @Nullable List<String> autoComplete = tabCompleteConfigFields(config, pathAndName);
        return autoComplete == null ? tabError() : autoComplete;
    }

    @Nullable
    private List<String> tabCompleteConfigFields(AppleConfig<?> config, List<String> pathAndName) {
        String[] pathInConfig = trimConfigPath(config, pathAndName);
        if (pathInConfig == null) return null;
        return config.autoCompleteFields(pathInConfig);
    }

    @Nullable
    private String[] trimConfigPath(AppleConfig<?> config, List<String> pathAndName) {
        if (config == null) return null;
        int pathSize = config.iteratePath().size();
        if (pathSize > pathAndName.size()) return null;
        return pathAndName.subList(pathSize, pathAndName.size()).toArray(String[]::new);
    }

    @NotNull
    private List<String> tabError() {
        return Collections.singletonList("error!");
    }

}
