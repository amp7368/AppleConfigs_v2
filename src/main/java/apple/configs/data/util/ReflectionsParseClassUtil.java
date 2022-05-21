package apple.configs.data.util;

import apple.file.yml.YcmField;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public interface ReflectionsParseClassUtil {
    @Nullable
    default Field getFieldByName(String path, Class<?> currentClass) {
        for (Field field : currentClass.getFields()) {
            if (!isInstanceField(field)) continue;
            if (path.equalsIgnoreCase(getFieldName(field))) {
                return field;
            }
        }
        return null;
    }

    default List<String> getNamesOfFields(Class<?> currentClass) {
        List<String> pathNames = new ArrayList<>();
        for (Field field : currentClass.getFields()) {
            if (!isInstanceField(field)) continue;
            pathNames.add(getFieldName(field));
        }
        return pathNames;
    }

    private String getFieldName(Field field) {
        String pathname = "";
        YcmField annotation = field.getAnnotation(YcmField.class);
        if (annotation != null) pathname = annotation.pathname();
        if (pathname.isBlank()) pathname = field.getName();
        return pathname;
    }

    default boolean isInstanceField(Field field) {
        return !this.isStatic(field) && !this.isTransient(field);
    }

    default boolean isTransient(Field field) {
        return Modifier.isTransient(field.getModifiers());
    }

    default boolean isStatic(Field field) {
        return Modifier.isStatic(field.getModifiers());
    }
}
