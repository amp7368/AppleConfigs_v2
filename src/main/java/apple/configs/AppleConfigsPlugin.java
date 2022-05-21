package apple.configs;

import apple.configs.command.AppleConfigsCommand;
import apple.configs.data.config.AppleConfig;
import apple.configs.registered.AppleConfigsDatabase;
import co.aikar.commands.PaperCommandManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AppleConfigsPlugin extends JavaPlugin {
    private static AppleConfigsPlugin instance;
    private PaperCommandManager commandManager;

    public AppleConfigsPlugin() {
        instance = this;
    }

    public static AppleConfigsPlugin get() {
        return instance;
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
        commandManager = new PaperCommandManager(this);
        new AppleConfigsCommand();
//        new ExampleModule();
    }

    public void registerConfig(AppleConfig<?> config) {
        AppleConfigsDatabase.get().registerConfig(config);
    }

    public PaperCommandManager getCommandManager() {
        return commandManager;
    }
}
