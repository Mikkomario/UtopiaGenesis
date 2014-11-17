package genesis_util;

import genesis_graphic.Drawable;
import genesis_graphic.DrawableHandler;
import genesis_logic.Actor;
import genesis_logic.AdvancedKeyListener;
import genesis_logic.AdvancedMouseListener;

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
	 * @see genesis_logic.MouseListenerHandler
	 */
	MOUSEHANDLER,
	/**
	 * KeyHandler handles keyListeners and informs them about keyboard events
	 * @see genesis_logic.KeyListenerHandler
	 */
	KEYHANDLER,
	/**
	 * DrawableHandler handles Drawables and draws them
	 * @see DrawableHandler
	 */
	DRAWABLEHANDLER,
	/**
	 * ActorHandler handles Actors and informs them about step events
	 * @see genesis_logic.ActorHandler
	 */
	ACTORHANDLER,
	/**
	 * StateHandler handles StateListeners and informs them about stateOperator changes.
	 * @see StateOperatorListener
	 * @see StateOperator
	 */
	STATEHANDLER;

	
	// IMPLEMENTED METHODS	---------------------------------------
	
	@Override
	public Class<?> getSupportedHandledClass()
	{
		switch (this)
		{
			case MOUSEHANDLER: return AdvancedMouseListener.class;
			case KEYHANDLER: return AdvancedKeyListener.class;
			case DRAWABLEHANDLER: return Drawable.class;
			case ACTORHANDLER: return Actor.class;
			
			default: return null;
		}
	}

}
