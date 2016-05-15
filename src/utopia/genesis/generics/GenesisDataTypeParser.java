package utopia.genesis.generics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import utopia.flow.generics.BasicDataType;
import utopia.flow.generics.Conversion;
import utopia.flow.generics.ConversionReliability;
import utopia.flow.generics.DataType;
import utopia.flow.generics.Value;
import utopia.flow.generics.ValueParser;
import utopia.genesis.util.Line;
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
		this.conversions.add(new Conversion(GenesisDataType.VECTOR, GenesisDataType.LINE, 
				ConversionReliability.PERFECT));
		this.conversions.add(new Conversion(BasicDataType.STRING, GenesisDataType.VECTOR, 
				ConversionReliability.DANGEROUS));
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
		
		// Vectors can be parsed from lines and strings
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
		}
		// Lines can be parsed from vectors
		else if (to.equals(GenesisDataType.LINE))
		{
			if (from.equals(GenesisDataType.VECTOR))
				return GenesisDataType.Line(new Line(GenesisDataType.valueToVector(value)));
		}
		
		throw new ValueParseException(value, to);
	}

	@Override
	public Collection<? extends Conversion> getConversions()
	{
		return this.conversions;
	}
}
