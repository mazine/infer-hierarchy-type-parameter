package org.jetbrains.mazine.infer.type.parameter

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class IsMethodOverridingTest {

    open class Figure<A> {
        open fun compare(that: A, p: String, i: Int): Boolean = false

        open fun <T> genericMethod(p: T) = Unit
    }

    open class Rectangle<B> : Figure<B>() {
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
        override fun compare(that: B, o: String, i: Int): Boolean {
            return super.compare(that, o, i)
        }

        fun compare2(o: String): Boolean = true
    }

    open class Square : Rectangle<Int>() {
        override fun compare(that: Int, o: String, i: Int): Boolean {
            return super.compare(that, o, i)
        }

        override fun <S> genericMethod(p: S) = Unit
    }

    open class Circle : Figure<Int>() {
        override fun compare(that: Int, p: String, i: Int): Boolean {
            return super.compare(that, p, i)
        }
    }

    @Test
    fun `method should override itself`() {
        assertThat(Square::compare.isOverriding(Square::compare)).isTrue()
    }

    @Test
    fun `method should override method of a parent class if signatures match`() {
        assertThat(Square::compare.isOverriding(Rectangle<*>::compare)).isTrue()
    }

    @Test
    fun `method should override method of a parent class if signatures match and generics`() {
        assertThat(Rectangle<*>::compare.isOverriding(Figure<*>::compare)).isTrue()
    }

    @Test
    fun `method should override method of an ancestor class if signatures match`() {
        assertThat(Square::compare.isOverriding(Figure<*>::compare)).isTrue()
    }

    @Test
    fun `method should not override method if signatures do not match`() {
        assertThat(Square::compare.isOverriding(Square::compare2)).isFalse()
    }

    @Test
    fun `method should not override method if they do not belong to the same hierarchy`() {
        assertThat(Square::compare.isOverriding(Circle::compare)).isFalse()
    }

    @Test
    fun `method should override method considering its own generic parameters`() {
        assertThat(isOverriding(Square::class.java.declaredMethods.first { it.name == "genericMethod" }, Figure::class.java.declaredMethods.first { it.name == "genericMethod" })).isTrue()
    }
}