package genesis_util;

import java.awt.Dimension;
import java.awt.Graphics2D;
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
	
	/**
	 * Creates a new vector by copying the information from a point
	 * @param point The point that is copied into vector
	 */
	public Vector2D(Point2D point)
	{
		this.first = point.getX();
		this.second = point.getY();
	}
	
	
	// IMPLEMENTED METHODS	----------------------------------
	
	@Override
	public String toString()
	{
		return getFirst() + "," + getSecond();
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
	 * @return The first value in the vector in int format
	 */
	public int getFirstInt()
	{
		return (int) getFirst();
	}
	
	/**
	 * @return The second value in the vector
	 */
	public double getSecond()
	{
		return this.second;
	}
	
	/**
	 * @return The second value in the vector in int format
	 */
	public int getSecondInt()
	{
		return (int) getSecond();
	}
	
	/**
	 * @return The direction of the vector (in degrees)
	 */
	public double getDirection()
	{
		return HelpMath.getVectorDirection(getFirst(), getSecond());
	}
	
	/**
	 * @return The length of the vector
	 */
	public double getLength()
	{
		return Math.sqrt(this.dotProduct(this));
	}
	
	
	// OTHER METHODS	--------------------------------------
	
	/**
	 * Draws the vector as a point
	 * @param radius The radius of the circle that represents the point
	 * @param g2d The object that does the actual drawing
	 */
	public void drawAsPoint(int radius, Graphics2D g2d)
	{
		g2d.drawOval(getFirstInt() - radius / 2, getSecondInt() - radius / 2, radius, radius);
	}
	
	/**
	 * Draws the vector as a line from origin to the vector's "position"
	 * @param g2d The object that does the actual drawing
	 */
	public void drawAsLine(Graphics2D g2d)
	{
		g2d.drawLine(0, 0, getFirstInt(), getSecondInt());
	}
	
	/**
	 * @param other The other vector
	 * @return Are the two vectors paraller to each other
	 */
	public boolean isParallerWith(Vector2D other)
	{
		//return HelpMath.areApproximatelyEqual(getDirection(), other.getDirection());
		return HelpMath.areApproximatelyEqual(this.crossProductLength(other), 0);
	}
	
	/**
	 * @param other The other vector
	 * @return How much the directions of the two vectors differ [0, 180]
	 */
	public double getSeparatingAngle(Vector2D other)
	{
		return HelpMath.getAngleDifference180(getDirection(), other.getDirection());
	}
	
	/**
	 * @param other The other vector
	 * @return Is this vector perpendicular to the other vector
	 */
	public boolean isPerpendicularTo(Vector2D other)
	{
		return HelpMath.areApproximatelyEqual(this.dotProduct(other), 0);
	}
	
	/**
	 * Calculates the dot product of the two vectors
	 * @param other The other vector
	 * @return The dot product of the two vectors
	 */
	public double dotProduct(Vector2D other)
	{
		Vector2D multiplication = this.times(other);
		return multiplication.getFirst() + multiplication.getSecond();
	}
	
	/**
	 * @param other The other vector
	 * @return The scalar projection of this vector to the other vector
	 */
	public double scalarProjection(Vector2D other)
	{
		return this.dotProduct(other) / other.getLength();
	}
	
	/**
	 * @return A vector that is perpendicular to this vector
	 */
	public Vector2D normal()
	{
		return new Vector2D(getSecond() * -1, getFirst()).normalized();
	}
	
	/**
	 * @param other The other vector
	 * @return the vector projection of this vector over the other vector
	 */
	public Vector2D vectorProjection(Vector2D other)
	{
		return other.times(this.dotProduct(other) / other.dotProduct(other));
	}
	
	/**
	 * Calculates the length of the cross product of the two vectors
	 * @param other The other vector
	 * @return The length of the cross product of these two vectors
	 */
	public double crossProductLength(Vector2D other)
	{
		// = |a||b|sin(a, b)e, |e| = 1 (can't use e in 2D space)
		return getLength() * other.getLength() * Math.sin(Math.toRadians(other.getDirection() 
				- getDirection()));
	}
	
	/**
	 * @param direction The direction of the new vector
	 * @return A vector with the given direction but the same length as this one
	 */
	public Vector2D withDirection(double direction)
	{
		double x = 0, y = 0;
		double length = getLength();
		
		if (length > 0)
		{
			x = HelpMath.lendirX(length, direction);
			y = HelpMath.lendirY(length, direction);
		}
		
		return new Vector2D(x, y);
	}
	
	/**
	 * @return Returns a version of this version that has unit length
	 */
	public Vector2D asUnitVector()
	{
		return this.dividedBy(getLength());
	}
	
	/**
	 * @return Returns a version of this version that has unit length. Works just like another 
	 * method
	 * @see #asUnitVector()
	 */
	public Vector2D normalized()
	{
		return this.asUnitVector();
	}
	
	/**
	 * @param length The length of the new vector
	 * @return a version of this vector that has the given length
	 */
	public Vector2D withLength(double length)
	{
		return this.asUnitVector().times(length);
	}
	
	/**
	 * Projects the vector to the given axis / direction
	 * @param direction The direction the vector is projected to
	 * @return The projection of this vector to the given axis
	 */
	public Vector2D getProjection(double direction)
	{
		double newLength = HelpMath.getDirectionalForce(getDirection(), 
				getLength(), direction);
		return this.withDirection(direction).withLength(newLength);
	}
	
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
		return times(other.getFirst(), other.getSecond());
	}
	
	/**
	 * @param a The multiplier for the first index
	 * @param b The multiplier for the second index
	 * @return The vector scaled with the given values. (2, 3) scaled with 3 and 2 would be 
	 * (6, 6)
	 */
	public Vector2D times(double a, double b)
	{
		return new Vector2D(getFirst() * a, getSecond() * b);
	}
	
	/**
	 * @param a
	 * @return The vector scaled with the given value. (2, 3) scaled with 2 would be (4, 6), 
	 * for example
	 */
	public Vector2D times(double a)
	{
		return times(a, a);
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
	 * @return Are the two vectors approximately the same
	 */
	public boolean equals(Vector2D other)
	{
		return HelpMath.areApproximatelyEqual(getFirst(), other.getFirst()) && 
				HelpMath.areApproximatelyEqual(getSecond(), other.getSecond());
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
		return new Dimension(getFirstInt(), getSecondInt());
	}
	
	/**
	 * @return An identity vector (1, 1)
	 */
	public static Vector2D identityVector()
	{
		return new Vector2D(1, 1);
	}
	
	/**
	 * @return A vector with zero length
	 */
	public static Vector2D zeroVector()
	{
		return new Vector2D(0, 0);
	}
	
	/**
	 * @return A unit vector that has direction of 0
	 */
	public static Vector2D unitVector()
	{
		return new Vector2D(1, 0);
	}
	
	/**
	 * @param direction The direction the unit vector will be facing (in degrees)
	 * @return A unit vector with the given direction
	 */
	public static Vector2D unitVector(double direction)
	{
		return unitVector().withDirection(direction);
	}
	
	/**
	 * Parses a vector from a string.
	 * @param s The string that is parsed into vector format. "1.0,2" will be interpreted as 
	 * (1.0, 2.0) as will "1.0, 2". "1.0" will be interpreted as (1.0, 0) while "" will be 
	 * interpreted as (0, 0)
	 * @return A vector parsed from the string
	 * @throws NumberFormatException If the contents of the string cannot be parsed into numbers
	 */
	public static Vector2D parseFromString(String s) throws NumberFormatException
	{
		String[] arguments = s.split(",");
		
		double first = 0;
		double second = 0;
		
		if (arguments.length >= 1)
			first = Double.parseDouble(arguments[0].trim());
		if (arguments.length >= 2)
			second = Double.parseDouble(arguments[1].trim());
		
		return new Vector2D(first, second);
	}
}
