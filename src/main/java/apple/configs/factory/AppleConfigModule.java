package apple.configs.factory;

import apple.configs.data.config.AppleConfig;
import apple.configs.data.config.AppleConfigProps;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface AppleConfigModule extends AppleConfigFactory {
    File getDataFolder();

    @Override
    default AppleConfigModule getModule() {
        return this;
    }

    default void registerConfigs() {
        getConfigs()
                .stream()
                .flatMap(config -> Arrays.stream(config.build(AppleConfigProps.empty())))
                .forEach(AppleConfig::register);
    }

    default List<AppleConfigLike> getConfigs() {
        return Collections.emptyList();
    }
}
