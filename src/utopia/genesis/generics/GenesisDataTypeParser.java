package utopia.genesis.generics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import utopia.flow.generics.BasicDataType;
import utopia.flow.generics.Conversion;
import utopia.flow.generics.ConversionReliability;
import utopia.flow.generics.DataType;
import utopia.flow.generics.DataTypeException;
import utopia.flow.generics.Model;
import utopia.flow.generics.Value;
import utopia.flow.generics.ValueParser;
import utopia.flow.generics.Variable;
import utopia.genesis.util.Line;
import utopia.genesis.util.Transformation;
import utopia.genesis.util.Vector3D;

/**
 * This parser handles data types introduced in the Genesis project
 * @author Mikko Hilpinen
 * @since 15.5.2016
 */
public class GenesisDataTypeParser implements ValueParser
{
	// ATTRIBUTES	-------------------
	
	private static GenesisDataTypeParser instance = null;
	private List<Conversion> conversions = new ArrayList<>();
	
	
	// CONSTRUCTOR	-------------------
	
	private GenesisDataTypeParser()
	{
		// Line loses some data when transformed into a vector
		this.conversions.add(new Conversion(GenesisDataType.LINE, GenesisDataType.VECTOR, 
				ConversionReliability.DATA_LOSS));
		this.conversions.add(new Conversion(BasicDataType.STRING, GenesisDataType.VECTOR, 
				ConversionReliability.DANGEROUS));
		this.conversions.add(new Conversion(GenesisDataType.TRANSFORMATION, GenesisDataType.VECTOR, 
				ConversionReliability.MEANING_LOSS));
		this.conversions.add(new Conversion(BasicDataType.MODEL, GenesisDataType.VECTOR, 
				ConversionReliability.DANGEROUS));
		
		this.conversions.add(new Conversion(GenesisDataType.VECTOR, GenesisDataType.LINE, 
				ConversionReliability.MEANING_LOSS));
		this.conversions.add(new Conversion(BasicDataType.MODEL, GenesisDataType.LINE, 
				ConversionReliability.DANGEROUS));
		
		this.conversions.add(new Conversion(GenesisDataType.VECTOR, GenesisDataType.TRANSFORMATION, 
				ConversionReliability.MEANING_LOSS));
		this.conversions.add(new Conversion(BasicDataType.MODEL, GenesisDataType.TRANSFORMATION, 
				ConversionReliability.DANGEROUS));
		
		this.conversions.add(new Conversion(GenesisDataType.VECTOR, BasicDataType.MODEL, 
				ConversionReliability.PERFECT));
		this.conversions.add(new Conversion(GenesisDataType.LINE, BasicDataType.MODEL, 
				ConversionReliability.PERFECT));
		this.conversions.add(new Conversion(GenesisDataType.TRANSFORMATION, BasicDataType.MODEL, 
				ConversionReliability.PERFECT));
	}
	
	/**
	 * @return The singular parser instance
	 */
	public static GenesisDataTypeParser getInstance()
	{
		if (instance == null)
			instance = new GenesisDataTypeParser();
		return instance;
	}

	@Override
	public Value cast(Value value, DataType to) throws ValueParseException
	{
		DataType from = value.getType();
		
		try
		{
			// Vectors can be parsed from lines, strings, transformations and models
			if (to.equals(GenesisDataType.VECTOR))
			{
				if (from.equals(BasicDataType.STRING))
				{
					try
					{
						return GenesisDataType.Vector(Vector3D.parseFromString(value.toString()));
					}
					catch (NumberFormatException e)
					{
						throw new ValueParseException(value.getObjectValue(), from, to, e);
					}
				}
				else if (from.equals(GenesisDataType.LINE))
					return GenesisDataType.Vector(GenesisDataType.valueToLine(value).toVector());
				else if (from.equals(BasicDataType.MODEL))
				{
					Model<?> model = value.toModel();
					double[] xyz = {0, 0, 0};
					
					if (model.containsAttribute("x"))
						xyz[0] = model.getAttributeValue("x").toDouble();
					if (model.containsAttribute("y"))
						xyz[1] = model.getAttributeValue("y").toDouble();
					if (model.containsAttribute("z"))
						xyz[2] = model.getAttributeValue("z").toDouble();
					
					return GenesisDataType.Vector(new Vector3D(xyz));
				}
				else if (from.equals(GenesisDataType.TRANSFORMATION))
				{
					Transformation t = GenesisDataType.valueToTransformation(value);
					return GenesisDataType.Vector(t.getPosition());
				}
			}
			// Lines can be parsed from vectors and models
			else if (to.equals(GenesisDataType.LINE))
			{
				if (from.equals(GenesisDataType.VECTOR))
					return GenesisDataType.Line(new Line(GenesisDataType.valueToVector(value)));
				else if (from.equals(BasicDataType.MODEL))
				{
					Model<?> model = value.toModel();
					Vector3D start = Vector3D.ZERO;
					Vector3D end = Vector3D.ZERO;
					
					if (model.containsAttribute("start"))
						start = GenesisDataType.valueToVector(model.getAttributeValue("start"));
					if (model.containsAttribute("end"))
						end = GenesisDataType.valueToVector(model.getAttributeValue("end"));
					
					return GenesisDataType.Line(new Line(start, end));
				}
			}
			// Transformations can be parsed from models and vectors (position)
			else if (to.equals(GenesisDataType.TRANSFORMATION))
			{
				if (from.equals(GenesisDataType.VECTOR))
				{
					Vector3D vector = GenesisDataType.valueToVector(value);
					return GenesisDataType.Transformation(new Transformation(vector));
				}
				else if (from.equals(BasicDataType.MODEL))
				{
					Model<?> model = value.toModel();
					double rotation = 0;
					Vector3D position = Vector3D.ZERO;
					Vector3D scaling = Vector3D.IDENTITY;
					Vector3D shear = Vector3D.ZERO;
					
					if (model.containsAttribute("rotation"))
						rotation = model.getAttributeValue("rotation").toDouble();
					if (model.containsAttribute("position"))
						position = GenesisDataType.valueToVector(model.getAttributeValue("position"));
					if (model.containsAttribute("scaling"))
						scaling = GenesisDataType.valueToVector(model.getAttributeValue("scaling"));
					if (model.containsAttribute("shear"))
						shear = GenesisDataType.valueToVector(model.getAttributeValue("shear"));
					
					return GenesisDataType.Transformation(new Transformation(position, scaling, 
							shear, rotation));
				}
			}
			// Any of the genesis types can be parsed into a model
			else if (to.equals(BasicDataType.MODEL))
			{
				if (from.equals(GenesisDataType.VECTOR))
				{
					Vector3D vector = GenesisDataType.valueToVector(value);
					Model<Variable> model = Model.createBasicModel();
					
					model.setAttributeValue("x", Value.Double(vector.getX()));
					model.setAttributeValue("y", Value.Double(vector.getY()));
					model.setAttributeValue("z", Value.Double(vector.getZ()));
					
					return Value.Model(model);
				}
				else if (from.equals(GenesisDataType.LINE))
				{
					Line line = GenesisDataType.valueToLine(value);
					Model<Variable> model = Model.createBasicModel();
					
					model.setAttributeValue("start", GenesisDataType.Vector(line.getStart()));
					model.setAttributeValue("end", GenesisDataType.Vector(line.getEnd()));
					
					return Value.Model(model);
				}
				else if (from.equals(GenesisDataType.TRANSFORMATION))
				{
					Transformation t = GenesisDataType.valueToTransformation(value);
					Model<Variable> model = Model.createBasicModel();
					
					model.setAttributeValue("rotation", Value.Double(t.getAngle()));
					model.setAttributeValue("position", GenesisDataType.Vector(t.getPosition()));
					model.setAttributeValue("scaling", GenesisDataType.Vector(t.getScaling()));
					model.setAttributeValue("shear", GenesisDataType.Vector(t.getShear()));
					
					return Value.Model(model);
				}
			}
		}
		catch (DataTypeException e)
		{
			throw new ValueParseException(value.getObjectValue(), from, to, e);
		}
		
		throw new ValueParseException(value, to);
	}

	@Override
	public Collection<? extends Conversion> getConversions()
	{
		return this.conversions;
	}
}
