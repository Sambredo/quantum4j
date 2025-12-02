package com.quantum4j.core.math;

import java.util.Objects;

/**
 * Immutable representation of a complex number.
 * <p>
 * A complex number z = a + bi is represented with real part {@code a} and imaginary part {@code b}. This class provides
 * arithmetic operations and constants for common values.
 * </p>
 */
public final class Complex {
    /** Complex zero: 0 + 0i */
    public static final Complex ZERO = new Complex(0.0, 0.0);
    /** Complex one: 1 + 0i */
    public static final Complex ONE = new Complex(1.0, 0.0);

    private final double re;
    private final double im;

    /**
     * Construct a complex number from real and imaginary parts.
     *
     * @param re
     *            the real part
     * @param im
     *            the imaginary part
     */
    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    /**
     * Get the real part.
     *
     * @return the real part
     */
    public double getRe() {
        return re;
    }

    /**
     * Get the imaginary part.
     *
     * @return the imaginary part
     */
    public double getIm() {
        return im;
    }

    /**
     * Add another complex number to this one.
     *
     * @param other
     *            the complex number to add
     *
     * @return a new complex number representing this + other
     */
    public Complex add(Complex other) {
        return new Complex(this.re + other.re, this.im + other.im);
    }

    /**
     * Subtract another complex number from this one.
     *
     * @param other
     *            the complex number to subtract
     *
     * @return a new complex number representing this - other
     */
    public Complex sub(Complex other) {
        return new Complex(this.re - other.re, this.im - other.im);
    }

    /**
     * Multiply this complex number by another.
     *
     * @param other
     *            the complex number to multiply
     *
     * @return a new complex number representing this × other
     */
    public Complex mul(Complex other) {
        double r = this.re * other.re - this.im * other.im;
        double i = this.re * other.im + this.im * other.re;
        return new Complex(r, i);
    }

    /**
     * Multiply this complex number by a scalar.
     *
     * @param scalar
     *            the real scalar to multiply
     *
     * @return a new complex number representing this × scalar
     */
    public Complex mul(double scalar) {
        return new Complex(this.re * scalar, this.im * scalar);
    }

    /**
     * Get the squared absolute value (magnitude squared) of this complex number.
     *
     * @return |z|² = a² + b²
     */
    public double absSquared() {
        return re * re + im * im;
    }

    /**
     * Get the complex conjugate.
     *
     * @return a new complex number with negated imaginary part
     */
    public Complex conjugate() {
        return new Complex(re, -im);
    }

    @Override
    public String toString() {
        return String.format("(%f %+fi)", re, im);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Complex))
            return false;
        Complex c = (Complex) o;
        return Double.compare(re, c.re) == 0 && Double.compare(im, c.im) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(re, im);
    }
}

