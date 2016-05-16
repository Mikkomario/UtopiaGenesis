package utopia.genesis.generics;

import utopia.flow.generics.DataType;
import utopia.flow.generics.Value;
import utopia.flow.io.ElementValueParser;
import utopia.flow.structure.Element;
import utopia.flow.structure.Node;
import utopia.flow.structure.TreeNode;
import utopia.genesis.util.HelpMath;
import utopia.genesis.util.Line;
import utopia.genesis.util.Transformation;
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
		return new DataType[]{GenesisDataType.VECTOR, GenesisDataType.LINE, 
				GenesisDataType.TRANSFORMATION};
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
		// A transformation is formed from vector and double elements
		else if (value.getType().equals(GenesisDataType.TRANSFORMATION))
		{
			Transformation t = GenesisDataType.valueToTransformation(value);
			TreeNode<Element> root = new TreeNode<>(new Element("transformation"));
			root.addChild(new TreeNode<>(new Element("rotation", Value.Double(t.getAngle()))));
			root.addChild(new TreeNode<>(new Element("position", GenesisDataType.Vector(t.getPosition()))));
			root.addChild(new TreeNode<>(new Element("scaling", GenesisDataType.Vector(t.getScaling()))));
			root.addChild(new TreeNode<>(new Element("shear", GenesisDataType.Vector(t.getShear()))));
			
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
		else if (targetType.equals(GenesisDataType.TRANSFORMATION))
		{
			double rotation = 0;
			Vector3D position = Vector3D.ZERO;
			Vector3D scaling = Vector3D.IDENTITY;
			Vector3D shear = Vector3D.ZERO;
			
			for (Element child : Node.getNodeContent(element.getChildren()))
			{
				if (child.getName().equalsIgnoreCase("rotation"))
					rotation = child.getContent().toDouble();
				else if (child.getName().equalsIgnoreCase("position"))
					position = GenesisDataType.valueToVector(child.getContent());
				else if (child.getName().equalsIgnoreCase("scaling"))
					scaling = GenesisDataType.valueToVector(child.getContent());
				else if (child.getName().equalsIgnoreCase("shear"))
					shear = GenesisDataType.valueToVector(child.getContent());
			}
			
			return GenesisDataType.Transformation(new Transformation(position, scaling, shear, 
					rotation));
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
