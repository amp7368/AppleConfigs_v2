package apple.configs.factory;

import apple.configs.data.config.AppleConfig;
import apple.configs.data.config.AppleConfigFolder;
import apple.configs.data.config.AppleConfigProps;

import java.util.List;

public interface AppleConfigFactory {
    AppleConfigModule getModule();

    default AppleConfigFolder configFolder(String path, AppleConfigLike... configs) {
        return configFolder(List.of(path), configs);
    }

    default AppleConfigFolder configFolder(List<String> path, AppleConfigLike... configs) {
        AppleConfigProps props = new AppleConfigProps(path.toArray(String[]::new));
        return new AppleConfigFolder(props, configs);
    }

    default <DBType> AppleConfig.Builder<DBType> config(Class<DBType> dbType, String name, String... path) {
        return new AppleConfig.Builder<>(name, getModule(), dbType, path);
    }

    default <DBType> AppleConfig.Builder<DBType> configJson(Class<DBType> dbType, String name, String... path) {
        return this.config(dbType, name, path).asJson();
    }


    default <DBType> AppleConfig.Builder<DBType> configYaml(Class<DBType> dbType, String name, String... path) {
        return this.config(dbType, name, path).asYaml();
    }
}
