import com.jogamp.opengl.GLAutoDrawable;
import org.joml.Matrix4f;

public interface ISimpleObjectInstance {

  /**
   * Draw the object stored in class, with passed in gla, modelview and projection matrix.
   */
  void draw(GLAutoDrawable gla, Matrix4f outModelView, Matrix4f proj);

  /**
   * clean up all the objects in the class.
   */
  void cleanup(GLAutoDrawable gla);
}
