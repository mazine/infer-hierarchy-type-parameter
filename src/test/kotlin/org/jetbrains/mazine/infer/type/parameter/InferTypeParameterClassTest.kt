package org.jetbrains.mazine.infer.type.parameter

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import kotlin.test.assertFailsWith

class InferTypeParameterClassTest {

    open class Base<P : Any, S : Any?>

    open class A<AP : Any> : Base<AP, Int>()

    open class B : A<String>()

    class C : B()

    class D<O> : B()

    class E : A<Iterable<Int>>()

    @Test
    fun `Parameterized direct inheritor should deliver inlined parameters`() {
//        assertThatTypeVariableName(inferTypeParameter(Base::class.java, "P", A::class.java))
//                .isEqualTo("AP")
        assertThat(inferTypeParameter(Base::class.java, "S", A::class.java))
                .isEqualTo(Int::class.javaObjectType)
    }

    @Test
    fun `Indirect inheritor that defines explicit type should deliver inlined parameters`() {
        assertThat(inferTypeParameterClass<Base<*, *>, B, String>(Base::class.java, "P", B::class.java))
                .isEqualTo(String::class.javaObjectType)
        assertThat(inferTypeParameterClass<Base<*, *>, B, Int>(Base::class.java, "S", B::class.java))
                .isEqualTo(Int::class.javaObjectType)
    }

    @Test
    fun `Indirect inheritor that defines no parameter should deliver inherited inlined parameters`() {
        assertThat(inferTypeParameterClass<Base<*, *>, C, String>(Base::class.java, "P", C::class.java))
                .isEqualTo(String::class.javaObjectType)
        assertThat(inferTypeParameterClass<Base<*, *>, C, Int>(Base::class.java, "S", C::class.java))
                .isEqualTo(Int::class.javaObjectType)
    }

    @Test
    fun `Parameters of the inheritor should not confuse`() {
        assertThat(inferTypeParameterClass<Base<*, *>, D<*>, String>(Base::class.java, "P", D::class.java))
                .isEqualTo(String::class.javaObjectType)
        assertThat(inferTypeParameterClass<Base<*, *>, D<*>, Int>(Base::class.java, "S", D::class.java))
                .isEqualTo(Int::class.javaObjectType)
    }

    @Test
    fun `Indirect inheritor that defines parameterized type as type argument should be supported`() {
        assertThat(inferTypeParameterClass<Base<*, *>, E, Iterable<Int>>(Base::class.java, "P", E::class.java))
                .isEqualTo(Iterable::class.javaObjectType)
        assertThat(inferTypeParameterClass<Base<*, *>, E, Int>(Base::class.java, "S", E::class.java))
                .isEqualTo(Int::class.javaObjectType)
    }

    @Test
    fun `Parameter name that does not exist should be rejected`() {
        assertFailsWith<IllegalArgumentException> {
            inferTypeParameterClass<Base<*, *>, C, String>(Base::class.java, "T", C::class.java)
        }
    }

    @Test
    fun `For subtype that does not define class for type variable exception should be thrown`() {
        val e = assertFailsWith<IllegalArgumentException> {
            inferTypeParameterClass<Base<*, *>, A<*>, String>(Base::class.java, "P", A::class.java)
        }
        assertThat(e.message).isEqualTo("Cannot infer class for type parameter \"P\" " +
                "of org.jetbrains.mazine.infer.type.parameter.InferTypeParameterClassTest.Base<P, S> " +
                "for org.jetbrains.mazine.infer.type.parameter.InferTypeParameterClassTest.A")
    }
}