package apple.configs.test;

import apple.configs.AppleConfigsPlugin;
import apple.configs.factory.AppleConfigLike;
import apple.configs.factory.AppleConfigModule;

import java.io.File;
import java.util.List;

public class ExampleModule implements AppleConfigModule {
    public ExampleModule() {
        this.registerConfigs();
    }

    @Override
    public File getDataFolder() {
        return AppleConfigsPlugin.get().getDataFolder();
    }

    @Override
    public List<AppleConfigLike> getConfigs() {
        return List.of(
                configJson(TestConfig.class, "TestConfigJson"),
                configYaml(TestConfig.class, "TestConfigYml"),
                configFolder("path",
                             configJson(TestConfig.class, "TestConfigJson2", "path"),
                             configYaml(TestConfig.class, "TestConfigYml2", "path")
                )
        );
    }
}
