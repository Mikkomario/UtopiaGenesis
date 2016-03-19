package genesis_util;

import java.util.List;

/**
 * This class calculates some mathematical problems
 * @author Mikko Hilpinen.
 * @since 28.11.2012.
 */
public class HelpMath
{	
	// CONSTRUCTOR	-------------------------------------
	
	private HelpMath()
	{
		// Constructor is hidden since the interface is static
	}
	
	
	// OTHER METHODS	---------------------------------
	
	/**
	 * Calculates the direction around the z-axis from one point to another (in degrees)
	 * @param x1 the first point's x coordinate
	 * @param y1 the first point's y coordinate
	 * @param x2 the second point's x coordinate
	 * @param y2 the second point's y coordinate
	 * @return the direction from point 1 to point 2 in degrees
	 */
	public static double pointDirection(double x1, double y1, double x2, double y2)
	{
		return pointDirection(new Vector3D(x1, y1), new Vector3D(x2, y2));
	}
	
	/**
	 * Calculates the direction around the z-axis from one point to another (in degrees)
	 * 
	 * @param p1 The first point
	 * @param p2 The second point
	 * @return The direction from the first point to the second in degrees
	 */
	public static double pointDirection(Vector3D p1, Vector3D p2)
	{
		return p2.minus(p1).getZDirection();
	}
	
	/**
	 * Calculates the direction from one point to another around the x-axis (in degrees). 
	 * Should only be used in 3D projects.
	 *
	 * @param z1 the first point's z coordinate
	 * @param y1 the first point's y coordinate
	 * @param z2 the second point's z coordinate
	 * @param y2 the second point's y coordinate
	 * @return the direction from point 1 to point 2 in degrees around the x-axis
	 * **/
	public static double pointXDirection(double z1, double y1, double z2, double y2)
	{
		return pointXDirection(new Vector3D(0, y1, z1), new Vector3D(0, y2, z2));
	}
	
	/**
	 * Calculates the direction around the x-axis from one point to another (in degrees)
	 * 
	 * @param p1 The first point
	 * @param p2 The second point
	 * @return The direction from the first point to the second around the x-axis
	 */
	public static double pointXDirection(Vector3D p1, Vector3D p2)
	{
		return p2.minus(p1).getXDirection();
	}
	
	/**
	 * Calculates the direction from one point to another around the Y-axis (in degrees). 
	 * Should only be used in 3D projects.
	 *
	 * @param x1 the first point's z coordinate
	 * @param z1 the first point's x coordinate
	 * @param x2 the second point's z coordinate
	 * @param z2 the second point's x coordinate
	 * @return the direction from point 1 to point 2 in degrees around the y-axis
	**/
	public static double pointYDirection(double x1, double z1, double x2, double z2)
	{
		return pointYDirection(new Vector3D(x1, 0, z1), new Vector3D(x2, 0, z2));
	}
	
	/**
	 * Calculates the direction around the y-axis from one point to another (in degrees)
	 * 
	 * @param p1 The first point
	 * @param p2 The second point
	 * @return The direction from the first point to the second around the y-axis
	 */
	public static double pointYDirection(Vector3D p1, Vector3D p2)
	{
		return p2.minus(p1).getYDirection();
	}
	
	/**
	 * Calculates a distance between two points.
	 *
	 * @param x1 First point's x coordinate
	 * @param y1 First point's y coordinate
	 * @param x2 Second point's x coordinate
	 * @param y2 Second point's y coordinate
	 * @return Distance between points in pixels
	 */
	public static double pointDistance(double x1, double y1, double x2, double y2)
	{
		/*
		double a = x1 - x2;
		double b = y1 - y2;
		
		return Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
		*/
		return pointDistance2D(new Vector3D(x1, y1), new Vector3D(x2, y2));
	}
	
	/**
	 * Calculates the distance between two points in 2 dimensions
	 * @param p1 The first point
	 * @param p2 The second point
	 * @return The distance between points p1 and p2 on the x-y plane
	 */
	public static double pointDistance2D(Vector3D p1, Vector3D p2)
	{
		return p1.in2D().minus(p2.in2D()).getLength();
	}
	
	/**
	 * Calculates a distance between two points in three dimensions.
	 * @param x1 First point's x coordinate
	 * @param y1 First point's y coordinate
	 * @param z1 First point's z coordinate
	 * @param x2 Second point's x coordinate
	 * @param y2 Second point's y coordinate
	 * @param z2 Second point's z coordinate
	 * @return Distance between points in pixels
	 */
	public static double pointDistance(double x1, double y1, double z1, double x2, double y2, 
			double z2)
	{
		/*
		double deltax = x1 - x2;
		double deltay = y1 - y2;
		double deltaz = z1 - z2;
		
		double xydist = Math.sqrt(Math.pow(deltax, 2) + Math.pow(deltay, 2));
		double xyzdist = Math.sqrt(Math.pow(xydist, 2) + Math.pow(deltaz, 2));
		
		return xyzdist;
		*/
		return pointDistance3D(new Vector3D(x1, y1, z1), new Vector3D(x2, y2, z2));
	}
	
	/**
	 * Calculates a distance between two points in three dimensions.
	 * @param p1 The first point
	 * @param p2 The second point
	 * @return Distance between points in pixels
	 */
	public static double pointDistance3D(Vector3D p1, Vector3D p2)
	{
		return p2.minus(p1).getLength();
	}
	
	/**
	 * Returns the x-coordinate of a point that is <b>length</b> pixels away to direction 
	 * <b>angle</b> from the origin. Cos(direction) * length
	 * @param length How far from the origin the point is (pixels)
	 * @param direction Towards which direction from the origin the point is 
	 * (degrees) [0, 360[
	 * @return The point's x-coordinate
	 */
	public static double lendirX(double length, double direction)
	{
		return Math.cos(Math.toRadians(direction))*length;
	}
	
	/**
	 * Returns the y-coordinate of a point that is <b>length</b> pixels away 
	 * to direction <b>angle</b> from the origin. Sin(direction) * length
	 *
	 * @param length How far from the origin the point is (pixels)
	 * @param direction Towards which direction from the origin the point is 
	 * (degrees) [0, 360[
	 * @return The point's y-coordinate
	 */
	public static double lendirY(double length, double direction)
	{
		return -Math.sin(Math.toRadians(direction))*length;
	}
	
	/**
	 * Calculates a vector that has the given direction and length
	 * @param length The length of the vector
	 * @param direction The direction of the vector
	 * @return A vector with the given direction and length
	 */
	public static Vector3D lenDir(double length, double direction)
	{
		return new Vector3D(lendirX(length, direction), lendirY(length, direction));
	}
	
	/**
	 * Changes the direction to a value between 0 and 360. 
	 * For example -10 becomes 350.
	 * @param direction The direction to be adjusted (in degrees)
	 * @return The adjusted direction (in degrees)
	 */
	public static double checkDirection(double direction)
	{
		double tmpdir = direction % 360;
		
		if (tmpdir < 0)
			tmpdir += 360;
		
		return tmpdir;
	}
	
	/**
	 * Tells whether a point is between the given values
	 * @param point The point tested
	 * @param minx The smallest possible x
	 * @param maxx The largest possible x
	 * @param miny The smallest possible y
	 * @param maxy The largest possible y
	 * @return Is the point between the values
	 */
	public static boolean pointIsInRange(Vector3D point, double minx, double maxx, 
			double miny, double maxy)
	{
		return (point.getFirst() > minx && point.getSecond() > miny && point.getFirst() 
				< maxx && point.getSecond() < maxy);
	}
	
	/**
	 * Tells whether a point is in the given area (in 2D space)
	 * @param point The point tested
	 * @param min The top left corner of the area
	 * @param max The bottom right corner of the area
	 * @return Is the point in the area
	 */
	public static boolean pointIsInRange(Vector3D point, Vector3D min, Vector3D max)
	{
		return pointIsInRange(point, min.getFirst(), max.getFirst(), min.getSecond(), max.getSecond());
	}
	
	/*
	 * Calculates a force vector that has been created by projecting a force 
	 * vector to a certain direction
	 *
	 * @param basicdirection The direction of the force vector to be projected (degrees)
	 * @param basicforce The length of the force vector to be projected
	 * @param newdirection The new direction to which the vector is projected (degrees)
	 * @return The length of the new projected force vector
	 */
	/*
	public static double getDirectionalForce(double basicdirection, 
			double basicforce, double newdirection)
	{
		double projectdir = newdirection - basicdirection;
		
		return lendirX(basicforce, projectdir);
	}
	*/
	
	/**
	 * Rotates a point around the origin (along the z-axis) and returns the new position
	 * @param rotationOrigin the origin around which the point is rotated
	 * @param point The point which will be rotated
	 * @param rotation How many degrees the point is rotated around the origin
	 * @return The new position after the rotation
	 */
	public static Vector3D getRotatedPosition(Vector3D rotationOrigin, 
			Vector3D point, double rotation)
	{
		/*
		// Calculates the old and the new directions (from the origin to the point)
		double prevdir = pointDirection(rotationOrigin, point);
		double newdir = checkDirection(prevdir + rotation);
		// Also calculates the distance between the object and the point 
		// (which stays the same during the process)
		double dist = pointDistance2D(rotationOrigin, point);
		// Returns the new position after the rotation
		return new Vector3D(rotationOrigin.getFirst() + lendirX(dist, newdir), 
				rotationOrigin.getSecond() + lendirY(dist, newdir), point.getThird());
		*/
		Vector3D rotated2D = rotationOrigin.plus(Vector3D.unitVector(pointDirection(
				rotationOrigin, point) + rotation).withLength(
				pointDistance2D(rotationOrigin, point)));
		return new Vector3D(rotated2D.getFirst(), rotated2D.getSecond(), point.getThird());
	}
	
	/**
	 * Calculates the directional difference between the two angles. The 
	 * difference is somewhere between 0 and 180 degrees.
	 * @param angle1 The first angle (degrees) [0, 360[
	 * @param angle2 The second angle (degrees) [0, 360[
	 * @return The difference between the two angles in degrees [0, 180[
	 */
	public static double getAngleDifference180(double angle1, double angle2)
	{
		double angledifference = Math.abs(checkDirection(angle1) - 
				checkDirection(angle2));
		
		// > 180 = < 180
		if (angledifference > 180)
			angledifference = 360 - angledifference;
		
		return angledifference;
	}
	
	/**
	 * Calculates the directional difference between the two angles. The 
	 * difference is somewhere between 0 and 90 degrees.
	 * @param angle1 The first angle (degrees) [0, 360[
	 * @param angle2 The second angle (degrees) [0, 360[
	 * @return The difference between the two angles in degrees [0, 90[
	 */
	public static double getAngleDifference90(double angle1, double angle2)
	{
		double angledifference = getAngleDifference180(angle1, angle2);
		
		// > 90 < 90
		if (angledifference > 90)
			angledifference = 180 - angledifference;
		
		return angledifference;
	}
	
	/**
	 * Calculates an average point from multiple points
	 * @param points A list of points
	 * @return The list's average point
	 */
	public static Vector3D getAveragePoint(List<Vector3D> points)
	{
		// If there are not enought points, returns 0
		if (points == null || points.isEmpty())
			return Vector3D.zeroVector();
		
		Vector3D p = points.get(0);
		
		for (int i = 1; i < points.size(); i++)
		{
			p = p.plus(points.get(i));
		}
		
		p = p.dividedBy(points.size());
		
		return p;
	}
	
	/**
	 * Checks if two double numbers are approximately equal to each other
	 * @param first The first number
	 * @param second The second number
	 * @return Are the two numbers pretty much equal
	 */
	public static boolean areApproximatelyEqual(double first, double second)
	{
		return (int) (first * 1000) == (int) (second * 1000);
	}
}