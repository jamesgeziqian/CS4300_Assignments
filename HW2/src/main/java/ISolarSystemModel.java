import com.jogamp.opengl.GLAutoDrawable;
import org.joml.Matrix4f;

/**
 * This interface defines some methods that an element in the solar system must implement. Each
 * element of solar system model must be able to draw itself on
 */
public interface ISolarSystemModel {

  /**
   * Draw the element on the canvas given according to a certain look at position.
   *
   * @param gla the canvas that element will be drawn on
   * @param lookAt the position where camera is
   */
  void draw(GLAutoDrawable gla, Matrix4f lookAt);

  /**
   * Clean up the object when disposing.
   *
   * @param gla the canvas that objects will be cleaned up
   */
  void dispose(GLAutoDrawable gla);
}
