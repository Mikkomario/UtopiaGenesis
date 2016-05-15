package utopia.genesis.generics;

import utopia.flow.generics.DataType;
import utopia.flow.generics.Value;
import utopia.flow.io.ElementValueParser;
import utopia.flow.structure.Element;
import utopia.flow.structure.TreeNode;
import utopia.genesis.util.HelpMath;
import utopia.genesis.util.Line;
import utopia.genesis.util.Vector3D;

/**
 * This class handles element parsing for Genesis data types
 * @author Mikko Hilpinen
 * @since 15.5.2016
 */
public class GenesisElementValueParser implements ElementValueParser
{	
	// IMPLEMENTED METHODS	---------------
	
	@Override
	public DataType[] getParsedTypes()
	{
		return new DataType[]{GenesisDataType.VECTOR, GenesisDataType.LINE};
	}

	@Override
	public TreeNode<Element> writeValue(Value value) throws ElementValueParsingFailedException
	{
		// With vector, the x, y and z are written in separate elements
		if (value.getType().equals(GenesisDataType.VECTOR))
		{
			Vector3D vector = GenesisDataType.valueToVector(value);
			TreeNode<Element> root = new TreeNode<>(new Element("vector"));
			addVectorElementIfNotZero(root, vector.getX(), "x");
			addVectorElementIfNotZero(root, vector.getY(), "y");
			addVectorElementIfNotZero(root, vector.getZ(), "z");
			
			return root;
		}
		// A line is formed from two vector elements
		else if (value.getType().equals(GenesisDataType.LINE))
		{
			Line line = GenesisDataType.valueToLine(value);
			TreeNode<Element> root = new TreeNode<>(new Element("line"));
			root.addChild(new TreeNode<>(new Element("start", GenesisDataType.Vector(line.getStart()))));
			root.addChild(new TreeNode<>(new Element("end", GenesisDataType.Vector(line.getEnd()))));
			
			return root;
		}
		
		throw new ElementValueParsingFailedException("Unsupported data type " + value.getType());
	}

	@Override
	public Value readValue(TreeNode<Element> element, DataType targetType)
			throws ElementValueParsingFailedException
	{
		if (targetType.equals(GenesisDataType.VECTOR))
		{
			double[] xyz = {0, 0, 0};
			for (TreeNode<Element> child : element.getChildren())
			{
				int index = 0;
				if (child.getContent().getName().equalsIgnoreCase("y"))
					index = 1;
				else if (child.getContent().getName().equalsIgnoreCase("z"))
					index = 2;
				xyz[index] = child.getContent().getContent().toDouble();
			}
			
			return GenesisDataType.Vector(new Vector3D(xyz));
		}
		else if (targetType.equals(GenesisDataType.LINE))
		{
			Vector3D start = null, end = null;
			for (TreeNode<Element> child : element.getChildren())
			{
				if (child.getContent().getName().equalsIgnoreCase("start"))
					start = GenesisDataType.valueToVector(child.getContent().getContent());
				if (child.getContent().getName().equalsIgnoreCase("end"))
					end = GenesisDataType.valueToVector(child.getContent().getContent());
			}
			return GenesisDataType.Line(new Line(start, end));
		}
		
		throw new ElementValueParsingFailedException("Unsupported target type " + targetType);
	}
	
	
	// OTHER METHODS	----------------
	
	private static void addVectorElementIfNotZero(TreeNode<Element> vectorElement, double value, String elementName)
	{
		if (!HelpMath.areApproximatelyEqual(value, 0))
			vectorElement.addChild(new TreeNode<>(new Element(elementName, Value.Double(value))));
	}
}
