package utopia.genesis.event;

import utopia.inception.handling.HandlerType;

/**
 * GenesisHandlerType contains all the different HandlerTypes used in Utopia Genesis module.
 * 
 * @author Mikko Hilpinen
 * @since 16.11.2014
 */
public enum GenesisHandlerType implements HandlerType
{
	/**
	 * MouseHandler handles mouseListeners and informs them about mouse events
	 * @see MouseListenerHandler
	 */
	MOUSEHANDLER,
	/**
	 * KeyHandler handles keyListeners and informs them about keyboard events
	 * @see KeyListenerHandler
	 */
	KEYHANDLER,
	/**
	 * DrawableHandler handles Drawables and draws them
	 * @see DrawableHandler
	 */
	DRAWABLEHANDLER,
	/**
	 * ActorHandler handles Actors and informs them about step events
	 * @see ActorHandler
	 */
	ACTORHANDLER;

	
	// IMPLEMENTED METHODS	---------------------------------------
	
	@Override
	public Class<?> getSupportedHandledClass()
	{
		switch (this)
		{
			case MOUSEHANDLER: return MouseListener.class;
			case KEYHANDLER: return KeyListener.class;
			case DRAWABLEHANDLER: return Drawable.class;
			case ACTORHANDLER: return Actor.class;
			
			default: return null;
		}
	}

}
