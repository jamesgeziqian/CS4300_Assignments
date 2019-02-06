import com.jogamp.opengl.GLAutoDrawable;
import org.joml.Matrix4f;

public interface ISimpleObjectInstance {

  void draw(GLAutoDrawable gla, Matrix4f outModelView, Matrix4f proj);

  void cleanup(GLAutoDrawable gla);
}
