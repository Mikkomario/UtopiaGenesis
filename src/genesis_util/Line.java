package genesis_util;

import java.awt.Graphics2D;

/**
 * Line consists of a start and an end point. Unlike vectors, lines do have a position as 
 * well as direction and length.
 * 
 * @author Mikko Hilpinen
 * @since 12.12.2014
 */
public class Line
{
	// TODO: Create intersection method
	// Also could check line sweep algorithm
	
	// ATTRIBUTES	-------------------------------
	
	private final Vector3D start, end;
	
	
	// CONSTRUCTOR	-------------------------------
	
	/**
	 * Creates a new line from the first point to the other
	 * @param start The start point of the line segment
	 * @param end The end point of the line segment
	 */
	public Line(Vector3D start, Vector3D end)
	{
		// Initializes attributes
		this.start = start;
		this.end = end;
	}
	
	/**
	 * Creates a new line from the origin to the given point
	 * @param end The end point of the line segment
	 */
	public Line(Vector3D end)
	{
		// Initializes attributes
		this.start = Vector3D.zeroVector();
		this.end = end;
	}

	
	// GETTERS & SETTERS	------------------------
	
	/**
	 * @return The starting point of this line segment
	 */
	public Vector3D getStart()
	{
		return this.start;
	}
	
	/**
	 * @return The end point of this line segment
	 */
	public Vector3D getEnd()
	{
		return this.end;
	}
	
	
	// OTHER METHODS	----------------------------
	
	/**
	 * @return The line in vector form. The position information is lost but the length and 
	 * direction stay the same.
	 */
	public Vector3D toVector()
	{
		return getEnd().minus(getStart());
	}
	
	/**
	 * @return The direction of this line around the z-axis
	 */
	public double getDirection()
	{
		return this.toVector().getZDirection();
	}
	
	/**
	 * @return The length of this line segment
	 */
	public double getLength()
	{
		return this.toVector().getLength();
	}
	
	/**
	 * Draws the line on screen
	 * @param g2d The graphics object that does the drawing
	 */
	public void draw(Graphics2D g2d)
	{
		g2d.drawLine(getStart().getFirstInt(), getStart().getSecondInt(), 
				getEnd().getFirstInt(), getEnd().getSecondInt());
	}
	
	/**
	 * Checks if the two lines are parallel to each other
	 * @param other The other line
	 * @return are the two lines parallel to each other
	 */
	public boolean isParallerWith(Line other)
	{
		return this.toVector().isParallerWith(other.toVector());
	}
	
	/**
	 * @param other The other line
	 * @return Are the two lines perpendicular to each other
	 */
	public boolean isPerpendicularTo(Line other)
	{
		return this.toVector().isPerpendicularTo(other.toVector());
	}
	
	/**
	 * @return A line segment similar to this except other way around (from the end point 
	 * to start point)
	 */
	public Line reverse()
	{
		return new Line(getEnd(), getStart());
	}
	
	/**
	 * @param other The other line
	 * @return Are the two lines identical
	 */
	public boolean equals(Line other)
	{
		return getStart().equals(other.getStart()) && getEnd().equals(other.getEnd());
	}
	
	/**
	 * Creates a new line from position and vector data
	 * @param position The starting position of the line
	 * @param dirLength The vector component (direction & length) of the line
	 * @return A line with the given start position, direction and length
	 */
	public static Line createLine(Vector3D position, Vector3D dirLength)
	{
		return new Line(position, position.plus(dirLength));
	}
}
