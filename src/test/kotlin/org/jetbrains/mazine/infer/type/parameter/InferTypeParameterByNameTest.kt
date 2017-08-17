package org.jetbrains.mazine.infer.type.parameter

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable
import kotlin.test.assertFailsWith

class InferTypeParameterByNameTest {

    open class Base<P : Any, S : Any?>

    open class A<AP : Any> : Base<AP, Int>()

    open class B : A<String>()

    class C : B()

    class D<O> : B()

    @Test
    fun `Class itself should deliver its own type variables`() {
        assertThatTypeVariableName(inferTypeParameter(Base::class.java, "P", Base::class.java))
                .isEqualTo("P")
        assertThatTypeVariableName(inferTypeParameter(Base::class.java, "S", Base::class.java))
                .isEqualTo("S")
    }

    @Test
    fun `Parameterized direct inheritor should deliver inlined parameters`() {
        assertThatTypeVariableName(inferTypeParameter(Base::class.java, "P", A::class.java))
                .isEqualTo("AP")
        assertThat(inferTypeParameter(Base::class.java, "S", A::class.java))
                .isEqualTo(Int::class.javaObjectType)
    }

    @Test
    fun `Indirect inheritor that defines explicit type should deliver inlined parameters`() {
        assertThat(inferTypeParameter(Base::class.java, "P", B::class.java))
                .isEqualTo(String::class.javaObjectType)
        assertThat(inferTypeParameter(Base::class.java, "S", B::class.java))
                .isEqualTo(Int::class.javaObjectType)
    }

    @Test
    fun `Indirect inheritor that defines no parameter should deliver inherited inlined parameters`() {
        assertThat(inferTypeParameter(Base::class.java, "P", C::class.java))
                .isEqualTo(String::class.javaObjectType)
        assertThat(inferTypeParameter(Base::class.java, "S", C::class.java))
                .isEqualTo(Int::class.javaObjectType)
    }

    @Test
    fun `Parameters of the inheritor should not confuse`() {
        assertThat(inferTypeParameter(Base::class.java, "P", D::class.java))
                .isEqualTo(String::class.javaObjectType)
        assertThat(inferTypeParameter(Base::class.java, "S", D::class.java))
                .isEqualTo(Int::class.javaObjectType)
    }

    @Test
    fun `Parameter name that does not exist should be rejected`() {
        assertFailsWith<IllegalArgumentException> {
            inferTypeParameter(Base::class.java, "T", C::class.java)
        }
    }

    private fun assertThatTypeVariableName(type: Type) = assertThat((type as? TypeVariable<*>)?.name)
}