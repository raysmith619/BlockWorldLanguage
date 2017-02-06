/**
 * 
 */
package BlockWorld;

/**
 * @author raysm
 *
 */
public enum BwCmdType {
	AXIS,				// Axis in display
	DISPLAY_SCENE,		// Display sceen
	ADD_OBJECT,			// Add object to list
	DELETE_OBJECT,		// Delete object
	INCLUDE_FILE,		// Include file
	INCLUDE_FILE_END,	//  end of include file
	MOVE_OBJECT,		// Move object
	MODIFY_OBJECT,		// Modify object
	QUIT_PROGRAM,		// Quit program
	LIST_CMD,			// List command line
	LOOKATEYE,			// Point at which the observer is
	DUPLICATE_CMD,		// Duplicate command
	NO_OP,				// Do nothing
	SET_CMD,			// Arithmetic statement (set ....)
						//   Initially set variable name = value
	TEXT,				// 3d text
	TEXT2D,				// 2d text
	SLIDER,				// Setup slider for variable control
	UNKNOWN,			// Unknown
}

