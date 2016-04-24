package utopia.genesis.util;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * Vector3D is an array of three number values. Vector3Ds can be used for representing 
 * points and other three number pairs. The vector is immutable once created.
 * @author Mikko Hilpinen
 * @since 18.11.2014
 */
public class Vector3D
{	
	// ATTRIBUTES	------------------------------------------
	
	/**
	 * An identity vector (1, 1, 1)
	 */
	public static final Vector3D IDENTITY =  new Vector3D(1, 1, 1);
	/**
	 * A vector with zero length (0, 0, 0)
	 */
	public static final Vector3D ZERO = new Vector3D(0, 0, 0);
	/**
	 * A unit vector that goes along x-axis (1, 0, 0)
	 */
	public static final Vector3D UNIT = new Vector3D(1, 0, 0);
	
	private final double[] xyz;
	
	
	// CONSTRUCTOR	------------------------------------------
	
	/**
	 * Creates a new vector
	 * @param x The x coordinate / the first number in the vector
	 * @param y The y coordinate / the second number in the vector
	 * @param z The z coordinate / the third number in the vector
	 */
	public Vector3D(double x, double y, double z)
	{
		this.xyz = new double[] {x, y, z};
	}
	
	/**
	 * Creates a new vector by copying the given vector
	 * @param other The vector from which the values are copied from
	 */
	public Vector3D(final Vector3D other)
	{
		this.xyz = other.xyz;
	}
	
	/**
	 * Creates a new 2D vector
	 * @param x The x coordinate / the first number in the vector
	 * @param y The y coordinate / the second number in the vector
	 */
	public Vector3D(double x, double y)
	{
		this.xyz = new double[] {x, y, 0};
	}
	
	/**
	 * Creates a new 1D vector
	 * @param x The x coordinate / the first number in the vector
	 */
	public Vector3D(double x)
	{
		this.xyz = new double[] {x, 0, 0};
	}
	
	/**
	 * Wraps a double array into a vector
	 * @param array a double array. Indices after index 2 are ignored.
	 */
	public Vector3D(double[] array)
	{
		this.xyz = new double[3];
		for (int i = 0; i < 3; i++)
		{
			if (array.length <= i)
				this.xyz[i] = 0;
			else
				this.xyz[i] = array[i];
		}
	}
	
	/**
	 * Creates a new 2D vector by copying the information from a point
	 * @param point The point that is copied into vector
	 */
	public Vector3D(final Point2D.Double point)
	{
		this.xyz = new double[] {point.getX(), point.getY(), 0};
	}
	
	/**
	 * Creates a new 2D vector by copying the information from a point
	 * @param point The point that is copied into vector
	 */
	public Vector3D(final Point point)
	{
		this.xyz = new double[] {point.getX(), point.getY(), 0};
	}
	
	/**
	 * Creates a new 2D vector by copying the information from a point
	 * @param point The point that is copied into vector
	 */
	public Vector3D(final Point2D point)
	{
		this.xyz = new double[] {point.getX(), point.getY(), 0};
	}
	
	/**
	 * Wraps a dimension into a 2D vector
	 * @param dimension The dimension that is wrapped
	 */
	public Vector3D(final Dimension dimension)
	{
		this.xyz = new double[] {dimension.getWidth(), dimension.getHeight(), 0};
	}
	
	
	// IMPLEMENTED METHODS	----------------------------------
	
	@Override
	public String toString()
	{
		return getX() + "," + getY() + "," + getZ();
	}

	
	// GETTERS & SETTERS	----------------------------------
	
	/**
	 * Finds the number at a certain point in the vector
	 * @param index The index of the requested point [0, 2]
	 * @return The number at that index
	 * @throws IndexOutOfBoundsException If the index was not within the vector
	 */
	public double get(int index) throws IndexOutOfBoundsException
	{
		return this.xyz[index];
	}
	
	/**
	 * @return How many indices there are in the vector. Always 3.
	 */
	public int size()
	{
		return this.xyz.length;
	}
	
	/**
	 * @return The x-coordinate of the vector
	 */
	public double getX()
	{
		return this.xyz[0];
	}
	
	/**
	 * @return The x-coordinate of the vector
	 */
	public int getXInt()
	{
		return (int) getX();
	}
	
	/**
	 * @return The first value in the vector
	 * @deprecated Please use {@link #getX()} instead
	 */
	public double getFirst()
	{
		return getX();
	}
	
	/**
	 * @return The first value in the vector in int format
	 * @deprecated Please use {@link #getXInt()} instead
	 */
	public int getFirstInt()
	{
		return getXInt();
	}
	
	/**
	 * @return The y-coordinate of the vector
	 */
	public double getY()
	{
		return this.xyz[1];
	}
	
	/**
	 * @return The y-coordinate of the vector
	 */
	public int getYInt()
	{
		return (int) getY();
	}
	
	/**
	 * @return The second value in the vector
	 * @deprecated Please use {@link #getY()} instead
	 */
	public double getSecond()
	{
		return getY();
	}
	
	/**
	 * @return The second value in the vector in int format
	 * @deprecated Please use {@link #getYInt()} instead
	 */
	public int getSecondInt()
	{
		return getYInt();
	}
	
	/**
	 * @return The z-coordinate of the vector
	 */
	public double getZ()
	{
		return this.xyz[2];
	}
	
	/**
	 * @return The z-coordinate of the vector
	 */
	public int getZInt()
	{
		return (int) getZ();
	}
	
	/**
	 * @return The second value in the vector
	 * @deprecated Please use {@link #getZ()} instead
	 */
	public double getThird()
	{
		return getZ();
	}
	
	/**
	 * @return The second value in the vector in int format
	 * @deprecated Please use {@link #getZInt()} instead
	 */
	public int getThirdInt()
	{
		return getZInt();
	}
	
	/**
	 * @return The direction of the vector around the z-axis (on x-y plane)
	 */
	public double getZDirection()
	{
		return getZDirection(getX(), getY());
	}
	
	/**
	 * @return The direction of the vector around the y axis (on x-z plane)
	 */
	public double getYDirection()
	{
		return getZDirection(getX(), getZ());
	}
	
	/**
	 * @return The direction of the vector around x-axis (on z-y plane)
	 */
	public double getXDirection()
	{
		return getZDirection(getZ(), getY());
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
	 * @return The vector as an array of 3 (x, y, z)
	 */
	public double[] toArray()
	{
		return this.xyz.clone();
	}
	
	/**
	 * The vector as an array of 0, 1, 2 or 3
	 * @param maxLength The maximum length of the array [0, 3]
	 * @return The (partial) vector as an array
	 */
	public double[] toArray(int maxLength)
	{
		if (maxLength >= 3)
			return toArray();
		else if (maxLength <= 0)
			return new double[0];
		else
		{
			double[] array = new double[maxLength];
			for (int i = 0; i < maxLength; i++)
			{
				array[i] = this.xyz[i];
			}
			return array;
		}
	}
	
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
		int sum = 0;
		for (double d : multiplication.xyz)
		{
			sum += d;
		}
		return sum;
	}
	
	/**
	 * Projects this vector over another vector
	 * @param other The other vector
	 * @return The scalar projection of this vector to the other vector
	 */
	public double scalarProjection(Vector3D other)
	{
		return this.dotProduct(other) / other.getLength();
	}
	
	/**
	 * @return A unit vector that is perpendicular to this vector
	 */
	public Vector3D normal()
	{
		if (equalsIn2D(ZERO) && !HelpMath.areApproximatelyEqual(getZ(), 0))
			return new Vector3D(1);
		else
			return normal2D();
	}
	
	/**
	 * This version of the method works as if the vector had no third dimension
	 * @return A unit vector that is perpendicular to this vector.
	 */
	public Vector3D normal2D()
	{
		return new Vector3D(getY() * -1, getX()).normalized();
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
		if (!equalsIn2D(ZERO) && !other.equalsIn2D(ZERO))
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
		
		return new Vector3D(x, y, getZ());
	}
	
	/**
	 * @param direction The direction of the new vector along the y-axis
	 * @return A vector with the given direction but the same length as this one
	 */
	public Vector3D withYDirection(double direction)
	{
		Vector3D zRotated = new Vector3D(getX(), getZ(), 0).withZDirection(direction);
		return new Vector3D(zRotated.getX(), getY(), zRotated.getY());
	}
	
	/**
	 * @return Returns a version of this version that has unit length
	 */
	public Vector3D asUnitVector()
	{
		return withDividedLength(getLength());
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
	 * @param lengthScaling How much the vector's length is scaled
	 * @return The scaled vector
	 */
	public Vector3D withScaledLength(double lengthScaling)
	{
		return times(lengthScaling);
	}
	
	/**
	 * @param lengthDivision How much the vector's length is divided
	 * @return The divided vector
	 */
	public Vector3D withDividedLength(double lengthDivision)
	{
		return dividedBy(lengthDivision);
	}
	
	/**
	 * @param lengthIncrease How much the vector's length is increased
	 * @return The increased vector
	 */
	public Vector3D withIncreasedLength(double lengthIncrease)
	{
		double length = getLength();
		return withScaledLength((length + lengthIncrease) / length);
	}
	
	/**
	 * @param lengthDecrease How much the vector's length is decreased
	 * @return The decreased vector
	 */
	public Vector3D withDecreasedLength(double lengthDecrease)
	{
		return withIncreasedLength(-lengthDecrease);
	}
	
	/**
	 * @param other The other vector
	 * @return A combination of the two vectors
	 */
	public Vector3D plus(final Vector3D other)
	{
		if (other == null)
			return this;
		return plus(other.getX(), other.getY(), other.getZ());
	}
	
	/**
	 * Increases the vector by given proportions
	 * @param xPlus How much the vector is increased along the x-axis
	 * @param yPlus How much the vector is increased along the y-axis
	 * @param zPlus How much the vector is increased along the z-axis
	 * @return An increased vector
	 */
	public Vector3D plus(double xPlus, double yPlus, double zPlus)
	{
		return new Vector3D(getX() + xPlus, getY() + yPlus, getZ() + zPlus);
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
	 * Decreases the vector by give proportions
	 * @param xMinus How much the vector is decreased along the x-axis
	 * @param yMinus How much the vector is decreased along the y-axis
	 * @param zMinus How much the vector is decreased along the z-axis
	 * @return A decreased vector
	 */
	public Vector3D minus(double xMinus, double yMinus, double zMinus)
	{
		return plus(-xMinus, -yMinus, -zMinus);
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
		return times(other.getX(), other.getY(), other.getZ());
	}
	
	/**
	 * @param xScale How much the vector is scaled along the x-axis
	 * @param yScale How much the vector is scaled along the y-axis 
	 * @param zScale How much the vector is scaled along the z-axis
	 * @return The vector scaled with the given values. (2, 3, 1) scaled with 3, 2 and 1 would be 
	 * (6, 6, 1)
	 */
	public Vector3D times(double xScale, double yScale, double zScale)
	{
		return new Vector3D(getX() * xScale, getY() * yScale, getZ() * zScale);
	}
	
	/**
	 * @param scaling How much the vector is scaled along all axes
	 * @return The vector scaled with the given value. (2, 3, 1) scaled with 2 would be (4, 6, 2), 
	 * for example
	 */
	public Vector3D times(double scaling)
	{
		return times(scaling, scaling, scaling);
	}
	
	/**
	 * @param other The other vector
	 * @return A division of the two vectors
	 */
	public Vector3D dividedBy(final Vector3D other)
	{
		return dividedBy(other.getX(), other.getY(), other.getZ());
	}
	
	/**
	 * @param xDivide The divider for the x index
	 * @param yDivide The divider for the y index
	 * @param zDivide The divider for the z index
	 * @return The vector divided by the given values. (6, 6, 1) divided by 3, 2 and 1 would be 
	 * (2, 3, 1)
	 */
	public Vector3D dividedBy(double xDivide, double yDivide, double zDivide)
	{
		// Can't divide with 0
		if (xDivide == 0)
			xDivide = 1;
		if (yDivide == 0)
			yDivide = 1;
		if (zDivide == 0)
			zDivide = 1;
		
		return new Vector3D(getX() / xDivide, getY() / yDivide, getZ() / zDivide);
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
		return equalsIn(size(), other);
	}
	
	/**
	 * @param other The other vector
	 * @return Are the two vectors approximately the same when they are projected to the 
	 * x-y plane
	 */
	public boolean equalsIn2D(Vector3D other)
	{
		return equalsIn(2, other);
	}
	
	private boolean equalsIn(int dimensions, Vector3D other)
	{
		for (int i = 0; i < dimensions; i++)
		{
			if (!HelpMath.areApproximatelyEqual(get(i), other.get(i)))
				return false;
		}
		
		return true;
	}
	
	/**
	 * @return The vector projected to the x-y plane
	 */
	public Vector3D in2D()
	{
		return new Vector3D(getX(), getY(), 0);
	}
	
	/**
	 * @return A point created from this vector
	 */
	public Point2D.Double toPoint()
	{
		return new Point2D.Double(getX(), getY());
	}
	
	/**
	 * @return A dimension created from this vector
	 */
	public Dimension toDimension()
	{
		return new Dimension(getXInt(), getYInt());
	}
	
	/**
	 * Draws the vector as a point
	 * @param radius The radius of the circle that represents the point
	 * @param g2d The object that does the actual drawing
	 */
	public void drawAsPoint(int radius, Graphics2D g2d)
	{
		g2d.drawOval(getXInt() - radius / 2, getYInt() - radius / 2, radius, radius);
	}
	
	/**
	 * Draws the vector as a line from origin (0, 0) to the vector's "position"
	 * @param g2d The object that does the actual drawing
	 */
	public void drawAsLine(Graphics2D g2d)
	{
		g2d.drawLine(0, 0, getXInt(), getYInt());
	}
	
	/**
	 * @param zDirection The direction the unit vector will be facing along the z-axis (in degrees)
	 * @return A unit vector with the given direction
	 */
	public static Vector3D unitVector(double zDirection)
	{
		return UNIT.withZDirection(zDirection);
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
	 * @param s The string that is parsed into vector format. "1.0,2,0" will be interpreted as 
	 * (1.0, 2.0, 0.0) as will "1.0, 2". "1.0" will be interpreted as (1.0, 0, 0) while "" will be 
	 * interpreted as (0, 0, 0)
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
		double x = u.getY() * v.getZ() - u.getZ() * v.getY();
		double y = u.getZ() * v.getX() - u.getX() * v.getZ();
		double z = u.getX() * v.getY() - u.getY() * v.getX();
		
		return new Vector3D(x, y, z);
	}
	
	private static double getZDirection(double x, double y)
	{
		return HelpMath.checkDirection(-(Math.toDegrees(Math.atan2(y, x))));
	}
}
