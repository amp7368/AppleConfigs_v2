package apple.configs.data.config;

import apple.configs.AppleConfigsPlugin;
import apple.configs.data.util.ReflectionsParseClassUtil;
import apple.configs.factory.AppleConfigLike;
import apple.configs.factory.AppleConfigModule;
import apple.configs.registered.AppleConfigService;
import apple.utilities.database.ajd.AppleAJDInstImpl;
import apple.utilities.threading.service.queue.AsyncTaskQueue;
import apple.utilities.util.FileFormatting;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class AppleConfig<DBType> implements ReflectionsParseClassUtil {
    private final AppleConfigModule module;
    private final Class<DBType> dbType;
    private final String name;
    private AppleAJDInstImpl<DBType, AsyncTaskQueue> database;
    private String[] path;
    private String extension;

    public AppleConfig(String name, AppleConfigModule module, Class<DBType> dbType) {
        this.name = name;
        this.module = module;
        this.dbType = dbType;
    }

    public void build(SerializingProp serializing, AppleConfigProps props) {
        this.path = props.path();
        this.extension = serializing.extension();
        this.database = new AppleAJDInstImpl<>(this.dbType, this.getFile(), AppleConfigService.get().taskCreator());
        serializing.handleDatabase(database);
        this.database.loadOrMake();
    }

    private File getFile() {
        File rootFolder = this.module.getDataFolder();
        File folder = FileFormatting.folderWithChildren(rootFolder, this.path);
        return new File(folder, name + extension);
    }

    public void save() {
        this.database.save();
    }

    public void register() {
        AppleConfigsPlugin.get().registerConfig(this);
    }

    public String getName() {
        return this.name;
    }

    public AppleConfigPath getFullName() {
        return new AppleConfigPath(this.name, this.path);
    }

    public List<String> iteratePath() {
        ArrayList<String> iteratePath = new ArrayList<>(List.of(this.path));
        iteratePath.add(name);
        return iteratePath;
    }

    @Nullable
    public List<String> autoCompleteFields(String[] fieldPath) {
        @NotNull Class<?> innerClass = this.dbType;
        @Nullable Object innerObj = this.database.getInstance();
        for (int i = 0; i < fieldPath.length; i++) {
            if (fieldPath[i].isBlank()) continue;
            if (innerObj == null) return null;
            @Nullable Field innerField = getFieldByName(fieldPath[i], innerClass);
            if (innerField == null) {
                if (i == fieldPath.length - 1) break;
                return Collections.singletonList(objToString(innerObj));
            } else {
                innerClass = innerField.getType();
                innerObj = getFieldFromObj(innerObj, innerField);
            }
        }
        List<String> fieldNames = getNamesOfFields(innerClass);
        fieldNames.add(objToString(innerObj));
        return fieldNames;
    }

    public boolean setValue(String[] fieldPath, String value) {
        if (fieldPath.length == 0) return false;
        @NotNull Class<?> innerClass = this.dbType;
        @Nullable Field innerField = getFieldByName(fieldPath[0], innerClass);
        @Nullable Object innerObj = this.database.getInstance();
        for (int i = 1; i < fieldPath.length; i++) {
            if (innerObj == null || innerField == null) return false;
            innerObj = getFieldFromObj(innerObj, innerField);
            innerField = getFieldByName(fieldPath[i], innerField.getType());
        }
        if (innerField == null) return false;

        try {
            innerField.set(innerObj, new Gson().fromJson(value, innerField.getType()));
        } catch (IllegalAccessException e) {
            return false;
        }
        this.save();
        return true;
    }

    @Nullable
    private Object getFieldFromObj(@NotNull Object obj, @NotNull Field field) {
        field.trySetAccessible();
        try {
            return field.get(obj);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    private String objToString(Object currentObj) {
        return new Gson().toJson(currentObj);
    }

    private enum SerializingProp {
        JSON(db -> db.setSerializingJson(), FileFormatting.JSON_EXTENSION), YAML(db -> db.setSerializingYaml(), FileFormatting.YML_EXTENSION);

        private final Consumer<AppleAJDInstImpl<?, ?>> handleDatabase;
        private final String extension;

        SerializingProp(Consumer<AppleAJDInstImpl<?, ?>> handleDatabase, String extension) {
            this.handleDatabase = handleDatabase;
            this.extension = "." + extension;
        }

        public <DBType> void handleDatabase(AppleAJDInstImpl<DBType, AsyncTaskQueue> database) {
            this.handleDatabase.accept(database);
        }

        public String extension() {
            return this.extension;
        }
    }

    public static class Builder<DBType> implements AppleConfigLike {
        private final AppleConfig<DBType> built;
        private final AppleConfigProps props;
        private SerializingProp serializing = SerializingProp.JSON;

        public Builder(String name, AppleConfigModule module, Class<DBType> dbType, String[] path) {
            this.props = new AppleConfigProps(path);
            this.built = new AppleConfig<>(name, module, dbType);
            asJson();
        }


        @Override
        public AppleConfig<?>[] build(AppleConfigProps outerProps) {
            AppleConfigProps compiledProps = this.props.addOuterProps(outerProps);
            this.built.build(serializing, compiledProps);
            return new AppleConfig<?>[]{built};
        }

        public AppleConfig<DBType> getConfig() {
            return this.built;
        }

        public Builder<DBType> asJson() {
            this.serializing = SerializingProp.JSON;
            return this;
        }

        public Builder<DBType> asYaml() {
            this.serializing = SerializingProp.YAML;
            return this;
        }

    }
}
