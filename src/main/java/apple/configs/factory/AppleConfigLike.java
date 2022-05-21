package apple.configs.factory;

import apple.configs.data.config.AppleConfig;
import apple.configs.data.config.AppleConfigProps;

public interface AppleConfigLike {
    AppleConfig<?>[] build(AppleConfigProps addedProps);

    default AppleConfig<?>[] build() {
        return this.build(AppleConfigProps.empty());
    }
}
