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
import util.ObjectInstance;
import util.PolygonMesh;
import util.ShaderLocationsVault;
import util.ShaderProgram;

/**
 * This class represents the model of orbits all celestial body in this solar system except the
 * planet Sefar which goes a rotating spiral orbit around the sun, including 4 planets and their
 * satellites that moving around them.
 */
public class Orbit extends ASolarSystemModel {

  private FloatBuffer color = FloatBuffer.wrap(new float[]{1, 1, 1, 0});

  public Orbit(GLAutoDrawable gla, ShaderProgram program, ShaderLocationsVault shaderLocations) {
    super(gla, program, shaderLocations);
  }

  /**
   * Initialize the orbit that is to be drawn, which is a circle.
   *
   * @param gla the canvas that elements will be drawn on
   */
  @Override
  protected void initObjects(GLAutoDrawable gla) {
    GL3 gl = gla.getGL().getGL3();

    List<Vector4f> positions = new ArrayList<Vector4f>();

    int radius = 1;
    int SLICES = 100;

    for (int i = 0; i <= SLICES; i++) {
      double angle = 2 * Math.PI * i / SLICES;
      positions.add(new Vector4f((float) (radius * Math.cos(angle)),
          (float) (radius * Math.sin(angle)), 0, 1));
    }

    // add the last vertex
    // positions.add(new Vector4f(radius, 0, 0, 1));

    List<Integer> indices = new ArrayList<>();

    for (int i = 0; i <= SLICES; i++) {
      indices.add(i);
    }

    //set up vertex attributes (in this case we have only position)
    List<IVertexData> vertexData = new ArrayList<>();
    VertexAttribProducer producer = new VertexAttribProducer();
    for (Vector4f pos : positions) {
      IVertexData v = producer.produce();
      v.setData("position", new float[]{pos.x,
          pos.y,
          pos.z,
          pos.w});
      vertexData.add(v);
    }

    //now we create a polygon mesh object
    PolygonMesh mesh = new PolygonMesh();

    mesh.setVertexData(vertexData);
    mesh.setPrimitives(indices);

    mesh.setPrimitiveType(GL.GL_LINE_LOOP);
    mesh.setPrimitiveSize(2);

    Map<String, String> shaderToVertexAttribute = new HashMap<>();

    //currently there are two per-vertex attributes: position and color
    shaderToVertexAttribute.put("vPosition", "position");
    meshObject = new ObjectInstance(gl, program, shaderLocations, shaderToVertexAttribute, mesh,
        "Orbit");
  }

  /**
   * Draw orbits of planets on the canvas given according to a certain look at position.
   *
   * @param gla the canvas that element will be drawn on
   * @param lookAt the position where camera is
   */
  @Override
  public void draw(GLAutoDrawable gla, Matrix4f lookAt) {
    GL3 gl = gla.getGL().getGL3();
    //send the color of the triangle
    gl.glUniform4fv(
        shaderLocations.getLocation("vColor"), 1, color);

    drawEarthOrbit(gla, lookAt);
    drawVenusOrbit(gla, lookAt);
    drawMercuryOrbit(gla, lookAt);
    drawAiurOrbit(gla, lookAt);
    ++time;
  }

  /**
   * Draw the orbit of the Earth and the moon, which both are circular orbits, one centered on the
   * sun and the other on the Earth.
   *
   * @param gla the canvas that element will be drawn on
   * @param lookAt the position where camera is
   */
  private void drawEarthOrbit(GLAutoDrawable gla, Matrix4f lookAt) {
    Matrix4f earthOrbit = new Matrix4f(lookAt).scale(500, 500, 500);
    drawOrbit(gla, earthOrbit);
    Matrix4f moonOrbit = new Matrix4f(lookAt)
        .rotate((float) Math.toRadians(time * 2), 0, 0, 1)
        .translate(500, 0, 0).scale(50, 50, 50);
    drawOrbit(gla, moonOrbit);
  }

  /**
   * Draw the orbit of the Venus, which goes a circular orbit around the sun but on a different
   * plane as other planets.
   *
   * @param gla the canvas that element will be drawn on
   * @param lookAt the position where camera is
   */
  private void drawVenusOrbit(GLAutoDrawable gla, Matrix4f lookAt) {
    Matrix4f venusOrbit = new Matrix4f(lookAt).scale(360, 360, 360);
    Matrix4f offset = new Matrix4f().identity();
    offset.setColumn(0, new Vector4f(1, 0, 1, 0).normalize());
    offset.setColumn(2, new Vector4f(-1, 0, 1, 0).normalize());
    venusOrbit.mul(offset);
    drawOrbit(gla, venusOrbit);
  }

  /**
   * Draw the orbit of the Mercury, which goes a circular orbits.
   *
   * @param gla the canvas that element will be drawn on
   * @param lookAt the position where camera is
   */
  private void drawMercuryOrbit(GLAutoDrawable gla, Matrix4f lookAt) {
    Matrix4f mercuryOrbit = new Matrix4f(lookAt).scale(150, 150, 150);
    drawOrbit(gla, mercuryOrbit);
  }

  /**
   * Draw the orbit of the planet Aiur and its two moons. The planet Aiur goes an circular orbit
   * around the sun but with an offset so it is centering not at the sun. One moon goes an perfect
   * circular orbit around the planet Aiur. Another moon goes an circular orbit around the planet
   * Aiur but with an offset so it is centering not at the planet.
   *
   * @param gla the canvas that element will be drawn on
   * @param lookAt the position where camera is
   */
  private void drawAiurOrbit(GLAutoDrawable gla, Matrix4f lookAt) {
    Matrix4f aiurOrbit = new Matrix4f(lookAt);
    aiurOrbit.translate(100, 0, 0);
    aiurOrbit.scale(1000, 1000, 1000);
    drawOrbit(gla, aiurOrbit);

    Matrix4f moon1Orbit = new Matrix4f(lookAt);
    moon1Orbit.translate(100, 0, 0);
    moon1Orbit.rotateZ((float) Math.toRadians(-time * 0.3));
    moon1Orbit.translate(0, -1000, 0);
    moon1Orbit.scale(100f, 100f, 100f);
    drawOrbit(gla, moon1Orbit);

    Matrix4f moon2Orbit = new Matrix4f(lookAt);
    moon2Orbit.translate(100, 0, 0);
    moon2Orbit.rotateZ((float) Math.toRadians(-time * 0.3));
    moon2Orbit.translate(100, 0, 0);
    moon2Orbit.translate(0, -1000, 0);
    moon2Orbit.scale(250, 250, 250);
    drawOrbit(gla, moon2Orbit);
  }


  /**
   * Draw the orbit according to given modelView on the canvas.
   *
   * @param gla the canvas that element will be drawn on
   * @param modelView the transformation that is to be applied on the orbit
   */
  private void drawOrbit(GLAutoDrawable gla, Matrix4f modelView) {
    GL3 gl = gla.getGL().getGL3();
    FloatBuffer fb16 = Buffers.newDirectFloatBuffer(16);
    gl.glUniformMatrix4fv(
        shaderLocations.getLocation("modelview"),
        1, false, modelView.get(fb16));
    meshObject.draw(gla);
  }

  @Override
  public void dispose(GLAutoDrawable gla) {
    this.meshObject.cleanup(gla);
  }
}
