package utopia.genesis.generics;

import utopia.flow.generics.BasicDataType;
import utopia.flow.generics.DataType;
import utopia.flow.generics.DataTypeTreeNode;
import utopia.flow.generics.DataTypes;
import utopia.flow.generics.Value;
import utopia.genesis.util.Line;
import utopia.genesis.util.Transformation;
import utopia.genesis.util.Vector3D;

/**
 * These are the data types introduced in the Genesis project
 * @author Mikko Hilpinen
 * @since 15.5.2016
 * @see #initialise()
 */
public enum GenesisDataType implements DataType
{
	/**
	 * The vector as a data type
	 * @see Vector3D
	 */
	VECTOR,
	/**
	 * The line formed by two vectors
	 * @see Line
	 */
	LINE,
	/**
	 * A transformation dictating how an object is drawn / interacted with
	 * @see Transformation
	 */
	TRANSFORMATION;
	
	
	// ATTRIBUTES	--------------------
	
	private static boolean initialised = false;

	
	// IMPLEMENTED METHODS	------------
	
	@Override
	public String getName()
	{
		return toString();
	}
	
	
	// OTHER METHODS	----------------
	
	/**
	 * Wraps a vector in a value
	 * @param vector A vector
	 * @return The vector wrapped into a value
	 */
	public static Value Vector(Vector3D vector)
	{
		return new Value(vector, VECTOR);
	}
	
	/**
	 * Wraps a line into a value
	 * @param line a line
	 * @return The line wrapped into a value
	 */
	public static Value Line(Line line)
	{
		return new Value(line, LINE);
	}
	
	/**
	 * Wraps a transformation into a value
	 * @param transformation a transformation
	 * @return The transformation wrapped into a value
	 */
	public static Value Transformation(Transformation transformation)
	{
		return new Value(transformation, TRANSFORMATION);
	}
	
	/**
	 * Parses the vector value of a value
	 * @param value a value
	 * @return The vector value of that value
	 */
	public static Vector3D valueToVector(Value value)
	{
		return (Vector3D) value.parseTo(VECTOR);
	}
	
	/**
	 * Parses a line value of a value
	 * @param value a value
	 * @return The line value of that value
	 */
	public static Line valueToLine(Value value)
	{
		return (Line) value.parseTo(LINE);
	}
	
	/**
	 * Parses a transformation value of a value
	 * @param value a value
	 * @return The transformation value of the value
	 */
	public static Transformation valueToTransformation(Value value)
	{
		return (Transformation) value.parseTo(TRANSFORMATION);
	}
	
	/**
	 * Initialises genesis data types, their parsing and element parsing. This method 
	 * should be called before the data types are actually used
	 */
	public static void initialise()
	{
		if (!initialised)
		{
			initialised = true;
			
			// Introduces the new data types
			DataTypes types = DataTypes.getInstance();
			DataTypeTreeNode object = types.get(BasicDataType.OBJECT);
			types.add(new DataTypeTreeNode(VECTOR, object));
			types.add(new DataTypeTreeNode(LINE, object));
			types.add(new DataTypeTreeNode(TRANSFORMATION, object));
			
			// Adds parsing for the new types as well
			types.addParser(GenesisDataTypeParser.getInstance());
			types.introduceSpecialParser(new GenesisElementValueParser());
		}
	}
}
