import com.jogamp.opengl.GLAutoDrawable;
import util.ObjectInstance;
import util.ShaderLocationsVault;
import util.ShaderProgram;

/**
 * This abstract class of ISolarSystemModel provides some common fields and a constructor that may
 * be useful for classes extending this class.
 */
abstract class ASolarSystemModel implements ISolarSystemModel {

  protected ObjectInstance meshObject;
  protected ShaderProgram program;
  protected ShaderLocationsVault shaderLocations;
  protected int time;

  /**
   * Construct a abstract class and initialize all fields.
   *
   * @param gla the canvas that elements will be drawn on
   * @param program a shader program
   * @param shaderLocations a ShaderLocationsVault that stores locations of shader
   */
  ASolarSystemModel(GLAutoDrawable gla, ShaderProgram program,
      ShaderLocationsVault shaderLocations) {
    this.program = program;
    this.shaderLocations = shaderLocations;
    this.initObjects(gla);
    this.time = 0;
  }

  /**
   * Initialize the object that will be drawn. Since object cannot be pre-defined, all classes
   * extending this class must implement this method so that the object is initialized.
   *
   * @param gla the canvas that elements will be drawn on
   */
  abstract void initObjects(GLAutoDrawable gla);
}
