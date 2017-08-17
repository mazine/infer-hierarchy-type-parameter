# Infer hierarchy type parameters 
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
  open val entityType = inferTypeParameter(Dao::class.java, "E", javaClass) as Class<E>
  fun getTypeName() = entityType.name
}

class UserDao<User>: Dao<User>()

class GroupDao<Group>: Dao<Group>()
```

### Gradle and Maven
[![Release](https://jitpack.io/v/mazine/infer-hierarchy-type-parameter.svg)](https://jitpack.io/#mazine/infer-hierarchy-type-parameter)

#### Gradle
``` groovy
repositories {
    jcenter()
    maven { url "https://jitpack.io" }
}
dependencies {
     compile 'com.github.mazine:infer-hierarchy-type-parameter:$version'
}
```

#### Maven
``` xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
```
<dependency>
    <groupId>com.github.mazine</groupId>
    <artifactId>infer-hierarchy-type-parameter</artifactId>
    <version>$version</version>
</dependency>
```
