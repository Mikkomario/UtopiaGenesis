package genesis_test;

import genesis_util.HelpMath;
import genesis_util.Vector3D;

/**
 * This program tests the basic functions of 3d vectors
 * @author Mikko Hilpinen
 * @since 24.3.2015
 */
public class GenesisVectorTest
{
	// CONSTRUCTOR	------------------------------
	
	private GenesisVectorTest()
	{
		// The interface is static
	}

	
	// MAIN METHOD	-----------------------------
	
	/**
	 * Starts the test
	 * @param args Not used
	 */
	public static void main(String[] args)
	{
		Vector3D v1 = new Vector3D(500, 0, 0);
		Vector3D v2 = new Vector3D(10, 1);
		
		System.out.println("Surface normal of  " + v1  + " and " + v2 + " = " + 
				Vector3D.getSurfaceNormal(v1, v2));
		Vector3D v1xv2 = v1.crossProduct(v2);
		System.out.println("Cross product = " + v1xv2);
		
		System.out.println("v1 z direction = " + v1.getZDirection());
		System.out.println("Cross product z direction = " + v1xv2.getZDirection());
		
		System.out.println("Cross produc cross v1 = " + v1xv2.crossProduct(v1));
		
		System.out.println("Dot pruduct = " + v1.dotProduct(v2));
		
		System.out.println("v1 rotated around y axis 90 degrees = " + v1.withYDirection(90));
		
		Vector3D v3 = new Vector3D(1, 0);
		Vector3D v4 = new Vector3D(2, -1);
		
		System.out.println("Point direction " + v3 + " -> " + v4 + " = " + 
				HelpMath.pointDirection(v3, v4));
	}
}
