import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import util.IVertexData;
import util.Material;
import util.ObjectInstance;
import util.PolygonMesh;
import util.ShaderLocationsVault;
import util.ShaderProgram;

public class HollowCircle extends ASimpleObjectInstance {

  HollowCircle(
      GL3 gl,
      ShaderProgram program,
      ShaderLocationsVault shaderLocations,
      String name,
      float r, float g, float b) {
    super(gl, program, shaderLocations, name, r, g, b);
    init();
  }

  private void init() {
    int NUM_SIDE = 360;
    //set up positions
    List<Vector4f> positions = new ArrayList<Vector4f>();
    for (int i = 0; i < NUM_SIDE + 1; i++) {
      double theta = i * Math.PI * 2 / NUM_SIDE;
      positions.add(new Vector4f((float) Math.cos(theta), (float) Math.sin(theta), 0f, 1f));
    }

    // set up indices
    List<Integer> indices = new ArrayList<>();
    for (int i = 0; i < NUM_SIDE + 1; i++) {
      indices.add(i);
    }
    // add object to object list
    usualObjAdd(positions, indices, GL.GL_LINE_STRIP);
  }


  public void draw(GLAutoDrawable gla, Matrix4f outModelView, Matrix4f proj) {
    GL3 gl = gla.getGL().getGL3();
    FloatBuffer fb16 = Buffers.newDirectFloatBuffer(16);
    FloatBuffer fb4 = Buffers.newDirectFloatBuffer(4);

    gl.glUniformMatrix4fv(
        shaderLocations.getLocation("projection"),
        1, false, proj.get(fb16));

    //pass the modelview matrix to the shader
    gl.glUniformMatrix4fv(
        shaderLocations.getLocation("modelview"),
        1, false, outModelView.get(fb16));

    //send the color of the triangle
    gl.glUniform4fv(
        shaderLocations.getLocation("vColor")
        , 1, material.getAmbient().get(fb4));

    //gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL3.GL_LINE); //OUTLINES

    meshObjList.get(0).draw(gla);
  }

}
