package org.jetbrains.mazine.infer.type.parameter

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.lang.reflect.TypeVariable

class InferTypeParametersTest {

    open class Base<P : Any, S : Any?>

    open class A<AP : Any> : Base<AP, Int>()

    open class B : A<String>()

    class C : B()

    class D<O> : B()

    @Test
    fun `Class itself should deliver its own type variables`() {
        val actual = inferTypeParameters(Base::class.java, Base::class.java).values

        assertThat(actual.filterIsInstance<TypeVariable<*>>().map { it.name })
                .containsExactly("P", "S")
                .inOrder()
    }

    @Test
    fun `Parameterized direct inheritor should deliver inlined parameters`() {
        val actual = inferTypeParameters(Base::class.java, A::class.java).values

        assertThat(actual.map { it.toString() })
                .containsExactly("AP", "class ${Int::class.javaObjectType.name}")
                .inOrder()
    }

    @Test
    fun `Indirect inheritor that defines explicit type should deliver inlined parameters`() {
        val actual = inferTypeParameters(Base::class.java, B::class.java).values

        assertThat(actual)
                .containsExactly(String::class.java, Int::class.javaObjectType)
                .inOrder()
    }

    @Test
    fun `Indirect inheritor that defines no parameter should deliver inherited inlined parameters`() {
        val actual = inferTypeParameters(Base::class.java, C::class.java).values

        assertThat(actual)
                .containsExactly(String::class.java, Int::class.javaObjectType)
                .inOrder()
    }

    @Test
    fun `Parameters of the inheritor should not confuse`() {
        val actual = inferTypeParameters(Base::class.java, D::class.java).values

        assertThat(actual)
                .containsExactly(String::class.java, Int::class.javaObjectType)
                .inOrder()
    }
}