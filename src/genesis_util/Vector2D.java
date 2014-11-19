package genesis_util;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * Vector2 is an array of two number values. Vector2s can be used for representing positions 
 * and other x-y-pairs, for example. The vector is immutable once created.
 * 
 * @author Mikko Hilpinen
 * @since 18.11.2014
 */
public class Vector2D
{
	// TODO: Add dot product, cross product, length, direction
	
	// ATTRIBUTES	------------------------------------------
	
	private final double first, second;
	
	
	// CONSTRUCTOR	------------------------------------------
	
	/**
	 * Creates a new vector
	 * 
	 * @param first The first number in the vector
	 * @param second The second number in the vector
	 */
	public Vector2D(double first, double second)
	{
		this.first = first;
		this.second = second;
	}
	
	/**
	 * Creates a new vector by copying the given vector
	 * 
	 * @param other The vector from which the values are copied from
	 */
	public Vector2D(final Vector2D other)
	{
		this.first = other.first;
		this.second = other.second;
	}
	
	/**
	 * Creates a new vector by copying the information from a point
	 * @param point The point that is copied into vector
	 */
	public Vector2D(final Point2D.Double point)
	{
		this.first = point.x;
		this.second = point.y;
	}
	
	/**
	 * Creates a new vector by copying the information from a point
	 * @param point The point that is copied into vector
	 */
	public Vector2D(final Point point)
	{
		this.first = point.x;
		this.second = point.y;
	}

	
	// GETTERS & SETTERS	----------------------------------
	
	/**
	 * @return The first value in the vector
	 */
	public double getFirst()
	{
		return this.first;
	}
	
	/**
	 * @return The second value in the vector
	 */
	public double getSecond()
	{
		return this.second;
	}
	
	
	// OTHER METHODS	--------------------------------------
	
	/**
	 * @param other The other vector
	 * @return A combination of the two vectors
	 */
	public Vector2D plus(final Vector2D other)
	{
		return new Vector2D(getFirst()+ other.getFirst(), getSecond() + other.getSecond());
	}
	
	/**
	 * @return A reverse of this vector (2, 3) would become (-2,-3), for example
	 */
	public Vector2D reverse()
	{
		return times(-1);
	}
	
	/**
	 * @param other The other vector
	 * @return a subtraction of the two methods
	 */
	public Vector2D minus(final Vector2D other)
	{
		return plus(other.reverse());
	}
	
	/**
	 * @param other The other vector
	 * @return a multiplication of the two vectors
	 */
	public Vector2D times(final Vector2D other)
	{
		return new Vector2D(getFirst() * other.getFirst(), getSecond() * other.getSecond());
	}
	
	/**
	 * @param a
	 * @return The vector scaled with the given value. (2, 3) scaled with 2 would be (4, 6), 
	 * for example
	 */
	public Vector2D times(double a)
	{
		return new Vector2D(getFirst() * a, getSecond() * a);
	}
	
	/**
	 * @param other The other vector
	 * @return A division of the two vectors
	 */
	public Vector2D dividedBy(final Vector2D other)
	{
		return new Vector2D(getFirst() / other.getFirst(), getSecond() / other.getSecond());
	}
	
	/**
	 * @param a
	 * @return A vector divided by the given value
	 */
	public Vector2D dividedBy(double a)
	{
		return new Vector2D(getFirst() / a, getSecond() / a);
	}
	
	/**
	 * @param other The other vector
	 * @return Are the two vectors equal
	 */
	public boolean equals(Vector2D other)
	{
		return getFirst() == other.getFirst() && getSecond() == other.getSecond();
	}
	
	/**
	 * @param other The other vector
	 * @return Are the two vectors approximately the same
	 */
	public boolean equalsApproximately(Vector2D other)
	{
		return (int) (getFirst() * 1000) == (int) (other.getFirst() * 1000) && 
				(int) (getSecond() * 1000) == (int) (other.getSecond() * 1000);
	}
	
	/**
	 * @return A point created from this vector
	 */
	public Point2D.Double toPoint()
	{
		return new Point2D.Double(getFirst(), getSecond());
	}
	
	/**
	 * @return A dimension created from this vector
	 */
	public Dimension toDimension()
	{
		return new Dimension((int) getFirst(), (int) getSecond());
	}
	
	/**
	 * @return An identity vector (1, 1)
	 */
	public static Vector2D identityVector()
	{
		return new Vector2D(1, 1);
	}
}
