# `org.Personal.Domain.Generic` Package

The `org.Personal.Domain.Generic` package contains core abstractions and conventions used across different entities in the domain model. One of the key components in this package is the `IEntity` interface. This interface defines a set of common properties and methods that are essential for all domain entities, providing a consistent structure and behavior across different types of entities.

## `IEntity` Interface

The `IEntity` interface is a generic interface designed to be implemented by all domain entities. It provides a common set of methods that ensure uniformity and consistency in the way entities handle their core attributes.

### Interface Definition

```java
package org.Personal.Domain.Generic;

import java.io.Serializable;
import java.time.LocalDateTime;

public interface IEntity<ID extends Serializable> {
    ID getId();
    void setId(ID id);

    LocalDateTime getCreatedDate();
    void setCreatedDate(LocalDateTime createdDate);

    LocalDateTime getUpdatedDate();
    void setUpdatedDate(LocalDateTime updatedDate);

    boolean isDeleted();
    void setDeleted(boolean deleted);
}
```

### Properties and Methods

- **`ID getId()` / `void setId(ID id)`**: These methods handle the unique identifier for the entity. The `ID` type is generic, allowing the interface to be used with different types of identifiers (e.g., `Long`, `UUID`).

- **`LocalDateTime getCreatedDate()` / `void setCreatedDate(LocalDateTime createdDate)`**: These methods manage the creation date of the entity. The `LocalDateTime` type ensures that the date and time are accurately recorded.

- **`LocalDateTime getUpdatedDate()` / `void setUpdatedDate(LocalDateTime updatedDate)`**: These methods manage the last updated date of the entity. This helps in tracking changes and ensuring that updates are properly recorded.

- **`boolean isDeleted()` / `void setDeleted(boolean deleted)`**: These methods manage the soft deletion status of the entity. Setting an entity as deleted allows for a logical deletion without physically removing the entity from the database.

### Purpose and Benefits

1. **Consistency Across Entities**: By implementing the `IEntity` interface, all domain entities adhere to a common structure. This uniformity simplifies the management of entities and makes the codebase easier to understand and maintain.

2. **Encapsulation of Common Properties**: The `IEntity` interface encapsulates common properties such as `id`, `createdDate`, `updatedDate`, and `deleted` status. This avoids duplication of code and ensures that all entities follow the same conventions.

3. **Flexible Identifier Types**: The use of a generic `ID` type allows the interface to be adapted for different types of identifiers, providing flexibility in how entities are identified.

4. **Support for Soft Deletion**: The `isDeleted` property enables soft deletion, which is useful for maintaining historical records and supporting audit requirements.

5. **Enhanced Maintainability**: Having a common interface for entities improves maintainability and makes it easier to apply global changes or enhancements to all entities in the system.

## Usage

To use the `IEntity` interface, implement it in your domain entities. For example:

```java
package org.Personal.Domain.Postgres.Entities;

import org.Personal.Domain.Generic.IEntity;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class User implements IEntity<Long> {
    @Id
    private Long id;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private boolean deleted;

    // Getters and setters
}
```

In this example, the `User` entity implements `IEntity`, inheriting its properties and methods, and ensuring consistency with other entities.

## Summary

The `org.Personal.Domain.Generic` package provides essential abstractions for domain entities through the `IEntity` interface. This interface defines a standard set of properties and methods for managing entity attributes, improving consistency, maintainability, and flexibility across the domain model.

---
