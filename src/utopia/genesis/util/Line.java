package utopia.genesis.util;

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
	 * @param onlyPointsOnLine Should the results only contain the points that fall between 
	 * the start and end point of the line (true) or whether any point aligned with the 
	 * line should be returned (false)
	 * @return The intersection points between the circle and the line
	 */
	public List<Vector3D> circleIntersection2D(Vector3D origin, double radius, 
			boolean onlyPointsOnLine)
	{
		//http://math.stackexchange.com/questions/311921/get-location-of-vector-circle-intersection
		
		List<Vector3D> points = new ArrayList<>();
		
		/*
		 * Terms for the quadratic equation
		 * --------------------------------
		 * 
		 * a = (x1 - x0)^2 + (y1 - y0)^2
		 * b = 2 * (x1 - x0) * (x0 - cx) + 2 * (y1 - y0) * (y0 - cy)
		 * c = (x0 - cx)^2 + (y0 - cy)^2 - r^2
		 * 
		 * Where (x1, y1) is the end point, (x0, y0) is the starting point, (cx, cy) is the 
		 * circle origin and r is the circle radius
		 * 
		 * Vx = (x1 - x0), The transition vector (end - start)
		 * Vy = (y1 - y0)
		 * 
		 * Lx = (x0 - cx), The transition vector from the circle origin to the line start
		 * Ly = (y0 - cy)	(start - origin)
		 * 
		 * With this added:
		 * a = Vx^2  +  Vy^2
		 * b = 2 * Vx * Lx  +  2 * Vy * Ly
		 * c = Lx^2  +  Ly^2  -  r^2
		 */
		Vector3D V = toVector(); // The translation vector
		Vector3D L = getStart().minus(origin); // The line start in relation to the circle origin
		
		double a = Math.pow(V.getFirst(), 2) + Math.pow(V.getSecond(), 2);
		double b = 2 * V.getFirst() * L.getFirst() + 2 * V.getSecond() * L.getSecond();
		double c = Math.pow(L.getFirst(), 2) + Math.pow(L.getSecond(), 2) - Math.pow(radius, 2);
		
		/*
		 * The equation
		 * ------------
		 * 
		 * t = (-b +- sqrt(b^2 - 4*a*c)) / (2*a)
		 * Where t is the modifier for the intersection points [0, 1] would be on the line
		 * Where b^2 - 4*a*c is called the discriminant and where a != 0
		 * 
		 * If The discriminant is negative, there is no intersection
		 * If the discriminant is 0, there is a single intersection point
		 * Otherwise there are two
		 */
		if (a == 0)
			return points;
		
		double discriminant = Math.pow(b, 2) - 4*a*c;
		
		if (discriminant >= 0)
		{
			/*
			 * The intersection points
			 * -----------------------
			 * 
			 * The final intersection points are simply
			 * start + t * V
			 * Where start is the line start position, and V is the line translation vector 
			 * (end - start)
			 */
			
			// TODO: Add parameters for enter & exit points only
			double discriminantRoot = Math.sqrt(discriminant);
			// Exit point
			double t1 = (-b + discriminantRoot) / (2 * a);
			
			if (!onlyPointsOnLine || (t1 >= 0 && t1 <= 1))
				points.add(getStart().plus(V.times(t1)));
			
			if (!HelpMath.areApproximatelyEqual(discriminant, 0))
			{
				// Enter point
				double t2 = (-b - discriminantRoot) / (2 * a);
				if (!onlyPointsOnLine || (t2 >= 0 && t2 <= 1))
					points.add(getStart().plus(V.times(t2)));
			}
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
