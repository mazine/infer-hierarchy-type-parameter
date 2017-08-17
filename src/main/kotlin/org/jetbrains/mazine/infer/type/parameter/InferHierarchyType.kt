package org.jetbrains.mazine.infer.type.parameter

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable

/**
 * Calculates types in `inheritedClass` that relate to type parameters of the `baseClass`.
 */
fun <B, T : B> inferTypeParameters(baseClass: Class<B>, inheritedClass: Class<T>): Map<TypeVariable<*>, Type> {
    val hierarchy = generateSequence<Class<*>>(inheritedClass) { it.superclass }

    val initialMapping = hierarchy.first()
            .typeParameters
            .map { it to it }.toMap<TypeVariable<*>, Type>()

    return hierarchy
            .takeWhile { it != baseClass }
            .fold(initialMapping) { mapping, type ->
                val parameters = type.superclass.typeParameters
                val arguments = (type.genericSuperclass as? ParameterizedType)?.actualTypeArguments
                if (parameters.isNotEmpty() && arguments != null) {
                    (parameters zip arguments).map { (parameter, argument) ->
                        parameter to if (argument is TypeVariable<*> && argument in mapping.keys) {
                            mapping[argument]!!
                        } else {
                            argument
                        }
                    }.toMap()
                } else {
                    emptyMap()
                }
            }
}

/**
 * Calculates type argument in `inheritedClass` that relates to a type parameter of the `baseClass` named `typeVariableName`.
 */
fun <B, T : B> inferTypeParameter(baseClass: Class<B>, typeVariableName: String, inheritedClass: Class<T>): Type {
    val typeVariable = (baseClass.typeParameters.firstOrNull { it.name == typeVariableName }
            ?: throw IllegalArgumentException("Class ${baseClass.name} has no type parameter $typeVariableName"))
    return inferTypeParameters(baseClass, inheritedClass)[typeVariable]!!
}

/**
 * Calculates type argument in `inheritedClass` that relates to a type parameter of the `baseClass` named `typeVariableName`
 * and expects it to be an instance of `java.util.Class<V>`.
 */
fun <B, T : B, V : Any> inferTypeParameterClass(baseClass: Class<B>, typeVariableName: String, inheritedClass: Class<T>): Class<V> {
    val typeParameter = inferTypeParameter(baseClass, typeVariableName, inheritedClass)

    val clazz = when (typeParameter) {
        is Class<*> -> typeParameter
        is ParameterizedType -> typeParameter.rawType as? Class<*>
        else -> null
    }

    @Suppress("UNCHECKED_CAST")
    return ((clazz as? Class<V>) ?:
            throw IllegalArgumentException("Cannot infer class for type parameter \"$typeVariableName\" of " +
                    "${baseClass.canonicalName}<${baseClass.typeParameters.joinToString { it.name }}> " +
                    "for ${inheritedClass.canonicalName}"))
}