# `org.Personal.Utility` Package

The `org.Personal.Utility` package contains utility classes that provide essential functions used throughout the application. Currently, it includes the `JsonUtil` class for JSON serialization and deserialization, and the `TrustedPackages` class to manage Kafka serialization configuration.

## Utility Classes

### 1. `JsonUtil`

**Purpose**: The `JsonUtil` class provides generic methods for converting Java objects to JSON strings and vice versa. It uses the Jackson `ObjectMapper` to handle serialization and deserialization operations. This class is crucial for processing data when interacting with Kafka or handling JSON data.

**Key Methods**:

- **`serialize(T object)`**: Converts a Java object into a JSON string. This method is generic and can handle any object type. It throws a `RuntimeException` if serialization fails.

  ```java
  public static <T> String serialize(T object) {
      try {
          return objectMapper.writeValueAsString(object);
      } catch (JsonProcessingException e) {
          throw new RuntimeException("Error serializing object", e);
      }
  }
  ```

- **`deserialize(String json, Class<T> clazz)`**: Converts a JSON string into a Java object of the specified class. This method is also generic and handles various object types. It throws a `RuntimeException` if deserialization fails.

  ```java
  public static <T> T deserialize(String json, Class<T> clazz) {
      try {
          return objectMapper.readValue(json, clazz);
      } catch (JsonProcessingException e) {
          throw new RuntimeException("Error deserializing JSON", e);
      }
  }
  ```

**Usage**: The `JsonUtil` class is used whenever there is a need to convert between JSON and Java objects, such as when sending or receiving data through Kafka.

**Example**:
```java
String jsonString = JsonUtil.serialize(myObject);
MyObject myObject = JsonUtil.deserialize(jsonString, MyObject.class);
```

### 2. `TrustedPackages`

**Purpose**: The `TrustedPackages` class provides a centralized place to define trusted package names for Kafka serialization and deserialization. This class is used to configure which packages are allowed during Kafka deserialization to prevent security issues related to deserializing untrusted data.

**Key Features**:

- **`PACKAGES`**: An array of trusted package names that can be used in Kafka configurations. This ensures that only classes from these packages are deserialized.

  ```java
  public static final String[] PACKAGES = {"org.personal.kafkatemplate"};
  ```

- **Constructor**: The constructor is private and throws an exception to prevent instantiation. This class is designed as a utility class and is not meant to be instantiated.

  ```java
  private TrustedPackages() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
  ```

**Usage**: The `TrustedPackages` class is used in Kafka configuration to specify which packages are trusted for deserialization. This helps to ensure that deserialization operations are secure and only classes from trusted packages are processed.

**Example**:
```java
configProps.put(JsonDeserializer.TRUSTED_PACKAGES, String.join(",", TrustedPackages.PACKAGES));
```

## Summary

The `org.Personal.Utility` package provides essential utility classes for handling JSON data and configuring Kafka serialization. The `JsonUtil` class simplifies the process of converting between JSON and Java objects, while the `TrustedPackages` class helps to maintain security during Kafka deserialization by defining trusted packages.

---

