package framework.serializer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class JsonSerializer {

    public static <T> String serializeList(List<T> list) throws IllegalAccessException {
        if (list.isEmpty()) {
            return "[]";
        } else {
            List<String> elements = new LinkedList<>();
            for (T item: list) {
                elements.add(JsonSerializer.serialize(item));
            }
            return elements.stream().collect(Collectors.joining(",", "[", "]"));
        }
    }

    public static <T> String serialize(T source) throws IllegalAccessException {
        Class<?> klass = source.getClass();

        Field[] fields = klass.getDeclaredFields();
        List<String> keyValues = new ArrayList<>();

        for (Field field: fields) {
            boolean isAccessible = field.canAccess(source);
            field.setAccessible(true);
            String keyValue;
            String key = field.getName();
            if (field.getType() == String.class) {
                keyValue = String.format("\"%s\":\"%s\"", key, field.getType().cast(field.get(source)));
            } else {
                keyValue = String.format("\"%s\":", key) + field.get(source);
            }
            keyValues.add(keyValue);
            field.setAccessible(isAccessible);
        }

        return keyValues.stream().collect(Collectors.joining(",", "{", "}"));

    }

    public static <T> T deserialize(String json, Class<T> target) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        T object = target.getConstructor().newInstance();
        String[] keyValues = json.substring(1, json.length() - 1).split(",");
        for (String keyValue: keyValues) {
            String[] keyAndValue = keyValue.split(":");
            String key = keyAndValue[0].trim().replace("\"", "");
            String value = keyAndValue[1].trim().replace("\"", "");
            Field currentField = target.getDeclaredField(key);
            boolean isAccessible = currentField.canAccess(object);
            currentField.setAccessible(true);

            Class<?> typeValue = currentField.getType();

            switch (typeValue.getSimpleName()) {
                case "String" -> currentField.set(object, value);
                case "Integer", "int" -> currentField.set(object, Integer.parseInt(value));
                case "Double", "double" -> currentField.set(object, Double.parseDouble(value));
                case "Float", "float" -> currentField.set(object, Float.parseFloat(value));
                case "Boolean", "boolean" -> currentField.set(object, Boolean.parseBoolean(value));
                default -> currentField.set(object, null);
            }

            currentField.setAccessible(isAccessible);
        }
        return object;
    }


}
