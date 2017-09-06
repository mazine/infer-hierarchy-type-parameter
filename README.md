# Infer hierarchy type parameters 
![Travis](https://travis-ci.org/mazine/infer-hierarchy-type-parameter.svg?branch=master)

Java/Kotlin utility to infer actual type for inherited type parameters


## Usage 

If you have a class hierarchy where base class has type parameters, and you need to access actual type
defined in inherited class you may pass it to constructor or defined an abstract method for that. 

#### Without the library
```kotlin
abstract class Dao<E: Any> {
  abstract val entityType: Class<E>
  fun getTypeName() = entityType.name
}

class UserDao<User>: Dao<User>() {
  override val entityType = User::class.java
}

class GroupDao<Group>: Dao<Group>() {
  override val entityType = Group::class.java
}
```

#### With the library
But information about the type variable value for the class `UserDao`  is actually available. You can get
it using the method `inferTypeParameter` from the library.  
```kotlin
abstract class Dao<E: Any> {
  open val entityType: Class<E> = inferTypeParameterClass(Dao::class.java, "E", javaClass)
  fun getTypeName() = entityType.name
}

class UserDao<User>: Dao<User>()

class GroupDao<Group>: Dao<Group>()
```

### Gradle and Maven
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.mazine/infer-hierarchy-type-parameter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.mazine/infer-hierarchy-type-parameter)

#### Gradle
``` groovy
repositories {
    mavenCentral()
}
dependencies {
     compile 'com.github.mazine:infer-hierarchy-type-parameter:$version'
}
```

#### Maven
```xml
<dependency>
    <groupId>com.github.mazine</groupId>
    <artifactId>infer-hierarchy-type-parameter</artifactId>
    <version>$version</version>
</dependency>
```
