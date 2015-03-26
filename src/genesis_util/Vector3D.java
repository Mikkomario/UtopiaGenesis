package genesis_util;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * Vector3D is an array of three number values. Vector3Ds can be used for representing 
 * points and other three number pairs. The vector is immutable once created.
 * 
 * @author Mikko Hilpinen
 * @since 18.11.2014
 */
public class Vector3D
{	
	// ATTRIBUTES	------------------------------------------
	
	private final double first, second, third;
	
	
	// CONSTRUCTOR	------------------------------------------
	
	/**
	 * Creates a new vector
	 * 
	 * @param first The first number in the vector
	 * @param second The second number in the vector
	 * @param third The third number in the vector
	 */
	public Vector3D(double first, double second, double third)
	{
		this.first = first;
		this.second = second;
		this.third = third;
	}
	
	/**
	 * Creates a new vector by copying the given vector
	 * 
	 * @param other The vector from which the values are copied from
	 */
	public Vector3D(final Vector3D other)
	{
		this.first = other.first;
		this.second = other.second;
		this.third = other.third;
	}
	
	/**
	 * Creates a new 2D vector
	 * @param first The first number in the vector
	 * @param second The second number in the vector
	 */
	public Vector3D(double first, double second)
	{
		this.first = first;
		this.second = second;
		this.third = 0;
	}
	
	/**
	 * Creates a new 2D vector by copying the information from a point
	 * @param point The point that is copied into vector
	 */
	public Vector3D(final Point2D.Double point)
	{
		this.first = point.x;
		this.second = point.y;
		this.third = 0;
	}
	
	/**
	 * Creates a new 2D vector by copying the information from a point
	 * @param point The point that is copied into vector
	 */
	public Vector3D(final Point point)
	{
		this.first = point.x;
		this.second = point.y;
		this.third = 0;
	}
	
	/**
	 * Creates a new 2D vector by copying the information from a point
	 * @param point The point that is copied into vector
	 */
	public Vector3D(final Point2D point)
	{
		this.first = point.getX();
		this.second = point.getY();
		this.third = 0;
	}
	
	
	// IMPLEMENTED METHODS	----------------------------------
	
	@Override
	public String toString()
	{
		return getFirst() + "," + getSecond() + "," + getThird();
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
	 * @return The second value in the vector
	 */
	public double getThird()
	{
		return this.third;
	}
	
	/**
	 * @return The second value in the vector in int format
	 */
	public int getThirdInt()
	{
		return (int) getThird();
	}
	
	/**
	 * @return The direction of the vector around the z-axis (on x-y plane)
	 */
	public double getZDirection()
	{
		//return HelpMath.getVectorDirection(getFirst(), getSecond());
		return getZDirection(getFirst(), getSecond());
	}
	
	/**
	 * @return The direction of the vector around the y axis (on x-z plane)
	 */
	public double getYDirection()
	{
		//return HelpMath.pointYDirection(0, 0, getFirst(), getThird());
		return getZDirection(getFirst(), getThird());
	}
	
	/**
	 * @return The direction of the vector around x-axis (on y-z plane)
	 */
	public double getXDirection()
	{
		//return HelpMath.pointXDirection(0, 0, getThird(), getSecond());
		return getZDirection(getThird(), getSecond());
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
	 * @param other The other vector
	 * @return Are the two vectors paraller to each other
	 */
	public boolean isParallerWith(Vector3D other)
	{
		return HelpMath.areApproximatelyEqual(crossProductLength(other), 0);
	}
	
	/**
	 * @param other The other vector
	 * @return Is this vector perpendicular to the other vector
	 */
	public boolean isPerpendicularTo(Vector3D other)
	{
		return HelpMath.areApproximatelyEqual(this.dotProduct(other), 0);
	}
	
	/**
	 * Calculates the dot product of the two vectors
	 * @param other The other vector
	 * @return The dot product of the two vectors
	 */
	public double dotProduct(Vector3D other)
	{
		Vector3D multiplication = this.times(other);
		return multiplication.getFirst() + multiplication.getSecond() + 
				multiplication.getThird();
	}
	
	/**
	 * @param other The other vector
	 * @return The scalar projection of this vector to the other vector
	 */
	public double scalarProjection(Vector3D other)
	{
		return this.dotProduct(other) / other.getLength();
	}
	
	/**
	 * @return A vector that is perpendicular to this vector
	 */
	public Vector3D normal()
	{
		if (equalsIn2D(Vector3D.zeroVector()) && !HelpMath.areApproximatelyEqual(getThird(), 0))
			return new Vector3D(1, 0, 0);
		else
			return normal2D();
	}
	
	/**
	 * This version of the method works as if the vector had no third dimension
	 * @return A vector that is perpendicular to this vector.
	 */
	public Vector3D normal2D()
	{
		return new Vector3D(getSecond() * -1, getFirst()).normalized();
	}
	
	/**
	 * @param other The other vector
	 * @return the vector projection of this vector over the other vector
	 */
	public Vector3D vectorProjection(Vector3D other)
	{
		return other.times(this.dotProduct(other) / other.dotProduct(other));
	}
	
	/**
	 * Calculates the length of the cross product of the two vectors
	 * @param other The other vector
	 * @return The length of the cross product of these two vectors
	 */
	public double crossProductLength(Vector3D other)
	{
		// = |a||b|sin(a, b)e, |e| = 1 (in this we skip the e)
		double angleDifference = 0;
		if (!equalsIn2D(Vector3D.zeroVector()) && !other.equalsIn2D(Vector3D.zeroVector()))
			angleDifference = other.getZDirection() - getZDirection();
		else
			angleDifference = other.getYDirection() - getYDirection();
		
		return getLength() * other.getLength() * Math.sin(Math.toRadians(angleDifference));
	}
	
	/**
	 * Calculates the cross product of the two vectors
	 * @param other The other vector
	 * @return The cross product of these two vectors
	 */
	public Vector3D crossProduct(Vector3D other)
	{
		// = |a||b|sin(a, b)e, |e| = 1
		double length = crossProductLength(other);
		return getSurfaceNormal(this, other).withLength(length);
	}
	
	/**
	 * @param direction The direction of the new vector along the z-axis
	 * @return A vector with the given direction but the same length as this one
	 */
	public Vector3D withZDirection(double direction)
	{
		double x = 0, y = 0;
		double length = getLength();
		
		if (length > 0)
		{
			x = HelpMath.lendirX(length, direction);
			y = HelpMath.lendirY(length, direction);
		}
		
		return new Vector3D(x, y, getThird());
	}
	
	/**
	 * @param direction The direction of the new vector along the y-axis
	 * @return A vector with the given direction but the same length as this one
	 */
	public Vector3D withYDirection(double direction)
	{
		Vector3D zRotated = new Vector3D(getFirst(), getThird(), 0).withZDirection(direction);
		return new Vector3D(zRotated.getFirst(), getSecond(), zRotated.getSecond());
	}
	
	/**
	 * @return Returns a version of this version that has unit length
	 */
	public Vector3D asUnitVector()
	{
		return this.dividedBy(getLength());
	}
	
	/**
	 * @return Returns a version of this version that has unit length.
	 * @see #asUnitVector()
	 */
	public Vector3D normalized()
	{
		return this.asUnitVector();
	}
	
	/**
	 * @param length The length of the new vector
	 * @return a version of this vector that has the given length
	 */
	public Vector3D withLength(double length)
	{
		return this.asUnitVector().times(length);
	}
	
	/**
	 * @param other The other vector
	 * @return A combination of the two vectors
	 */
	public Vector3D plus(final Vector3D other)
	{
		return new Vector3D(getFirst() + other.getFirst(), getSecond() + other.getSecond(), 
				getThird() + other.getThird());
	}
	
	/**
	 * Increases a vectors length by the specified amount
	 * @param a How much the length is increased
	 * @return A vector with increased length
	 */
	public Vector3D plus(double a)
	{
		double length = getLength();
		double newScale = (length + a) / length;
		return times(newScale);
	}
	
	/**
	 * Decreases a vectors length by the specified amount
	 * @param a How much the length is decreased
	 * @return A vector with decreased length
	 */
	public Vector3D minus(double a)
	{
		return plus(-a);
	}
	
	/**
	 * @return A reverse of this vector (2, 3) would become (-2,-3), for example
	 */
	public Vector3D reverse()
	{
		return times(-1);
	}
	
	/**
	 * @param other The other vector
	 * @return a subtraction of the two methods
	 */
	public Vector3D minus(final Vector3D other)
	{
		return plus(other.reverse());
	}
	
	/**
	 * @param other The other vector
	 * @return a multiplication of the two vectors
	 */
	public Vector3D times(final Vector3D other)
	{
		return times(other.getFirst(), other.getSecond(), other.getThird());
	}
	
	/**
	 * @param a The multiplier for the first index
	 * @param b The multiplier for the second index
	 * @param c The multiplier for the third index
	 * @return The vector scaled with the given values. (2, 3, 1) scaled with 3, 2 and 1 would be 
	 * (6, 6, 1)
	 */
	public Vector3D times(double a, double b, double c)
	{
		return new Vector3D(getFirst() * a, getSecond() * b, getThird() * c);
	}
	
	/**
	 * @param a
	 * @return The vector scaled with the given value. (2, 3, 1) scaled with 2 would be (4, 6, 2), 
	 * for example
	 */
	public Vector3D times(double a)
	{
		return times(a, a, a);
	}
	
	/**
	 * @param other The other vector
	 * @return A division of the two vectors
	 */
	public Vector3D dividedBy(final Vector3D other)
	{
		return dividedBy(other.getFirst(), other.getSecond(), other.getThird());
	}
	
	/**
	 * @param a The divider for the first index
	 * @param b The divider for the second index
	 * @param c The divider for the third index
	 * @return The vector divided by the given values. (6, 6, 1) divided by 3, 2 and 1 would be 
	 * (2, 3, 1)
	 */
	public Vector3D dividedBy(double a, double b, double c)
	{
		return new Vector3D(getFirst() / a, getSecond() / b, getThird() / c);
	}
	
	/**
	 * @param a
	 * @return A vector divided by the given value
	 */
	public Vector3D dividedBy(double a)
	{
		return dividedBy(a, a, a);
	}
	
	/**
	 * @param other The other vector
	 * @return Are the two vectors approximately the same
	 */
	public boolean equals(Vector3D other)
	{
		return equalsIn2D(other) && 
				HelpMath.areApproximatelyEqual(getThird(), other.getThird());
	}
	
	/**
	 * @param other The other vector
	 * @return Are the two vectors approximately the same when they are projected to the 
	 * x-y plane
	 */
	public boolean equalsIn2D(Vector3D other)
	{
		return HelpMath.areApproximatelyEqual(getFirst(), other.getFirst()) && 
				HelpMath.areApproximatelyEqual(getSecond(), other.getSecond());
	}
	
	/**
	 * @return The vector projected to the x-y plane
	 */
	public Vector3D in2D()
	{
		return new Vector3D(getFirst(), getSecond(), 0);
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
	 * @return An identity vector (1, 1, 1)
	 */
	public static Vector3D identityVector()
	{
		return new Vector3D(1, 1, 1);
	}
	
	/**
	 * @return A vector with zero length
	 */
	public static Vector3D zeroVector()
	{
		return new Vector3D(0, 0, 0);
	}
	
	/**
	 * @return A unit vector that goes along x-axis (1, 0, 0)
	 */
	public static Vector3D unitVector()
	{
		return new Vector3D(1, 0, 0);
	}
	
	/**
	 * @param zDirection The direction the unit vector will be facing along the z-axis (in degrees)
	 * @return A unit vector with the given direction
	 */
	public static Vector3D unitVector(double zDirection)
	{
		return unitVector().withZDirection(zDirection);
	}
	
	/**
	 * @param zDirection The direction the unit vector will be facing along the z-axis (in degrees)
	 * @param yDirection The direction the unit vector will be facing along the y-axis (in degrees)
	 * @return A unit vector with the given direction
	 */
	public static Vector3D unitVector(double zDirection, double yDirection)
	{
		return unitVector(zDirection).withYDirection(yDirection);
	}
	
	/**
	 * Parses a vector from a string.
	 * @param s The string that is parsed into vector format. "1.0,2" will be interpreted as 
	 * (1.0, 2.0) as will "1.0, 2". "1.0" will be interpreted as (1.0, 0) while "" will be 
	 * interpreted as (0, 0)
	 * @return A vector parsed from the string
	 * @throws NumberFormatException If the contents of the string cannot be parsed into numbers
	 */
	public static Vector3D parseFromString(String s) throws NumberFormatException
	{
		String[] arguments = s.split(",");
		
		double first = 0;
		double second = 0;
		double third = 0;
		
		if (arguments.length >= 1)
			first = Double.parseDouble(arguments[0].trim());
		if (arguments.length >= 2)
			second = Double.parseDouble(arguments[1].trim());
		if (arguments.length >= 3)
			third = Double.parseDouble(arguments[2].trim());
		
		return new Vector3D(first, second, third);
	}
	
	/**
	 * Calculates a normal for a surface
	 * @param v1 The first vector that forms the surface
	 * @param v2 The second vector that forms the surface
	 * @return A normal perpendicular to the surface
	 */
	public static Vector3D getSurfaceNormal(Vector3D v1, Vector3D v2)
	{
		Vector3D u = v1.normalized();
		Vector3D v = v2.normalized();
		
		/*
		 * Nx = UyVz - UzVy
			Ny = UzVx - UxVz
			Nz = UxVy - UyVx
		 */
		double x = u.getSecond() * v.getThird() - u.getThird() * v.getSecond();
		double y = u.getThird() * v.getFirst() - u.getFirst() * v.getThird();
		double z = u.getFirst() * v.getSecond() - u.getSecond() * v.getFirst();
		
		return new Vector3D(x, y, z);
	}
	
	private static double getZDirection(double x, double y)
	{
		return HelpMath.checkDirection(-(Math.toDegrees(Math.atan2(y, x))));
	}
}
