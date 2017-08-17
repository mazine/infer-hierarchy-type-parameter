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

fun <B, T : B> inferTypeParameter(baseClass: Class<B>, typeVariableName: String, inheritedClass: Class<T>): Type {
    val typeVariable = (baseClass.typeParameters.firstOrNull { it.name == typeVariableName }
            ?: throw IllegalArgumentException("Class ${baseClass.name} has no type parameter $typeVariableName"))
    return inferTypeParameters(baseClass, inheritedClass)[typeVariable]!!
}