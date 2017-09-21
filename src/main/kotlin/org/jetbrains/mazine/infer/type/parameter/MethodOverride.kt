package org.jetbrains.mazine.infer.type.parameter

import java.lang.reflect.Method
import java.lang.reflect.TypeVariable
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.javaMethod

/**
 * Checks if `overridingMethod` actually overrides `superMethod`
 * considering generics.
 */
fun isOverriding(overridingMethod: Method, superMethod: Method): Boolean {
    if (superMethod == overridingMethod) return true

    val superDeclaringClass = superMethod.declaringClass as Class<Any>
    val overridingDeclaringClass = overridingMethod.declaringClass as Class<Any>
    if (!superDeclaringClass.isAssignableFrom(overridingDeclaringClass)) return false

    if (overridingMethod.name != superMethod.name) return false

    val overridingParameterTypes = overridingMethod.genericParameterTypes
    val superParameterTypes = superMethod.genericParameterTypes
    if (overridingParameterTypes.size != superParameterTypes.size) return false

    return (overridingParameterTypes zip superParameterTypes).all { (overridingType, superType) ->
        when (superType) {
            is Class<*> -> overridingType == superType
            is TypeVariable<*> -> {
                when (superType.genericDeclaration) {
                    superDeclaringClass -> {
                        val inferredTypeParameter = inferTypeParameter(superDeclaringClass, superType.name, overridingDeclaringClass)
                        if (overridingType is Class<*>) {
                            overridingType.kotlin.javaObjectType == inferredTypeParameter
                        } else {
                            overridingType == inferredTypeParameter
                        }
                    }
                    superMethod -> overridingType is TypeVariable<*> && overridingType.genericDeclaration == overridingMethod
                    else -> false
                }
            }
            else -> false
        }
    }
}

fun KFunction<*>.isOverriding(superMethod: KFunction<*>): Boolean {
    return isOverriding(
            this.javaMethod ?: throw IllegalArgumentException("Function cannot be represented as Java method"),
            superMethod.javaMethod ?: throw IllegalArgumentException("Function cannot be represented as Java method"))
}