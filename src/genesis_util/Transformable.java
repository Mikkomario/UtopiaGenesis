package genesis_util;

/**
 * Transformable objects have a transformation and can be transformed.
 * 
 * @author Mikko Hilpinen
 * @since 3.12.2014
 */
public interface Transformable
{
	/**
	 * @return The object's current transformation
	 */
	public Transformation getTransformation();
	
	/**
	 * Changes the object's current transformation
	 * @param t The new transformation the object will receive
	 */
	public void setTrasformation(Transformation t);
	
	
	// OTHER METHODS	----------------------
	
	/**
	 * Transforms a transformable object relative to its previous transformation
	 * @param transformed The object that will be transformed
	 * @param transformation The transformation that will be added to the object
	 */
	public static void transform(Transformable transformed, Transformation transformation)
	{
		transformed.setTrasformation(transformed.getTransformation().plus(transformation));
	}
}
