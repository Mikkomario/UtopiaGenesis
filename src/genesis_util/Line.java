package genesis_util;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

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
	 * Calculates the intersection points between the line and a circle
	 * @param origin The circle's origin
	 * @param radius The circle's radius
	 * @return The intersection points between the circle and the line
	 */
	public List<Vector3D> circleIntersection2D(Vector3D origin, double radius)
	{
		List<Vector3D> points = new ArrayList<>();
		
		// A and B are for simplification
		// A = (y2 - y1) / (x2 - x1)
		double A = (getEnd().getSecond() - getStart().getSecond()) / 
				(getEnd().getFirst() - getStart().getFirst());
		// B = -Ax1 + y1
		double B = -A * getStart().getFirst() + getStart().getSecond();
		
		// a, b and c form the equation: ax^2 + bx + c = 0
		// a = 1 + A^2
		double a = 1 + Math.pow(A, 2);
		// b = -2 * (x0 + A * (-0.5A - B + y0))
		double b = -2 * (origin.getFirst() + A * (-0.5 * A - B + origin.getSecond()));
		// c = x0^2 + B * (B - 2y0) + y0^2 - r^2
		double c = Math.pow(origin.getFirst(), 2) + B * (B - 2 * origin.getSecond()) + 
				Math.pow(origin.getSecond(), 2) - Math.pow(radius, 2);
		
		// C and D are for solving the equation
		// D = b^2 - 4ac
		double D = Math.pow(b, 2) - 4 * a * c;
		
		if (D < 0)
			return points;
		
		// C = sqrt(D)
		double C = Math.sqrt(D);
		
		// Calculates the first point
		// xp1 = (-b + C) / 2a
		double xp1 = (-b + C) / 2 * a;
		// y = A * (x - x1) + y1
		double yp1 = A * (xp1 - getStart().getFirst()) + getStart().getSecond();
		points.add(new Vector3D(xp1, yp1));
		
		// There may also be a second point
		if (!HelpMath.areApproximatelyEqual(D, 0))
		{
			// xp2 = (-b - C) / 2a
			double xp2 = (-b - C) / 2 * a;
			double yp2 = A * (xp2 - getStart().getFirst() + getStart().getSecond());
			points.add(new Vector3D(xp2, yp2));
		}
		
		return points;
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
