import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import util.Material;
import util.ObjImporter;
import util.ObjectInstance;
import util.PolygonMesh;
import util.ShaderLocationsVault;
import util.ShaderProgram;

/**
 * This class represents the model of all celestial body in this solar system, including 5 planets
 * and their satellites that moving around them.
 */
public class CelestialBody extends ASolarSystemModel {

  private Material material;

  public CelestialBody(GLAutoDrawable gla, ShaderProgram program,
      ShaderLocationsVault shaderLocations) {
    super(gla, program, shaderLocations);
    material = new Material();
    material.setDiffuse(1, 1, 1);
    material.setSpecular(1, 1, 1);
  }

  /**
   * Initialize the object that will be drawn on the canvas, which is imported from sphere.obj.
   *
   * @param gla the canvas that elements will be drawn on
   */
  @Override
  protected void initObjects(GLAutoDrawable gla) {
    GL3 gl = gla.getGL().getGL3();

    InputStream in;
    try {
      in = new FileInputStream("models/sphere.obj");
    } catch (Exception e) {
      throw new IllegalArgumentException("Object file not found.");
    }

    PolygonMesh tmesh = ObjImporter.importFile(new VertexAttribProducer(), in, true);

    Map<String, String> shaderToVertexAttribute = new HashMap<>();

    //currently there is only one per-vertex attribute: position
    shaderToVertexAttribute.put("vPosition", "position");

    meshObject = new ObjectInstance(gl,
        program,
        shaderLocations,
        shaderToVertexAttribute,
        tmesh,
        "Celestial Body");
  }

  /**
   * Draw objects to the canvas passed in, including 5 planets and their satellites, according to
   * current state and the camera position passed in.
   *
   * @param gla the canvas that element will be drawn on
   * @param lookAt the position where camera is
   */
  @Override
  public void draw(GLAutoDrawable gla, Matrix4f lookAt) {
    GL3 gl = gla.getGL().getGL3();
    FloatBuffer fb16 = Buffers.newDirectFloatBuffer(16);
    FloatBuffer fb4 = Buffers.newDirectFloatBuffer(4);

    Map<Matrix4f, Material> system = getSystem();
    for (Entry<Matrix4f, Material> entry : system.entrySet()) {
      Matrix4f modelview = new Matrix4f(lookAt).mul(entry.getKey());

      Material material = entry.getValue();

      //pass the modelview matrix to the shader
      gl.glUniformMatrix4fv(
          shaderLocations.getLocation("modelview"),
          1, false, modelview.get(fb16));

      //send the color of the triangle
      gl.glUniform4fv(
          shaderLocations.getLocation("vColor")
          , 1, material.getAmbient().get(fb4));

      //draw the object
      meshObject.draw(gla);
    }
    ++time;
  }

  @Override
  public void dispose(GLAutoDrawable gla) {
    this.meshObject.cleanup(gla);
  }

  /**
   * Collect modelView matrices of all planet and satellites at a certain time altogether.
   *
   * @return all pairs of modelView and a material of celestial bodies stored in a map
   */
  private Map<Matrix4f, Material> getSystem() {
    Map<Matrix4f, Material> system = getSun();
    Map<Matrix4f, Material> earthSystem = getEarthSystem();
    system.putAll(earthSystem);
    Map<Matrix4f, Material> venusSystem = getVenusSystem();
    system.putAll(venusSystem);
    Map<Matrix4f, Material> mercurySystem = getMercurySystem();
    system.putAll(mercurySystem);
    Map<Matrix4f, Material> aiurSystem = getAiurSystem();
    system.putAll(aiurSystem);
    Map<Matrix4f, Material> sefarSystem = getSefarSystem();
    system.putAll(sefarSystem);
    return system;
  }

  /**
   * Get the modelView matrix and the material of the sun, which is a yellow big ball sitting at the
   * origin.
   *
   * @return a pair of modelView and a material of the sun stored in a map
   */
  private Map<Matrix4f, Material> getSun() {
    Matrix4f modelView = new Matrix4f();
    modelView.mul(new Matrix4f().scale(200, 200, 200))
        .mul(new Matrix4f().rotate((float) Math.toRadians(time), 0, 1, 0));
    Material mat = new Material(material);
    mat.setAmbient(1, 1, 0);
    Map<Matrix4f, Material> result = new HashMap<>();
    result.put(modelView, mat);
    return result;
  }

  /**
   * Get the modelView matrix and the material of the Earth and the moon, which the blue Earth
   * follows a circular orbit around the sun and the pale moon goes around a circular orbit around
   * the Earth.
   *
   * @return pairs of modelView and a material of the Earth and the moon stored in a map
   */
  private Map<Matrix4f, Material> getEarthSystem() {
    Map<Matrix4f, Material> result = new HashMap<>();

    Matrix4f earthModel = new Matrix4f();
    earthModel.rotateZ((float) Math.toRadians(time * 2));
    earthModel.translate(500, 0, 0);
    earthModel.scale(50, 50, 50);

    Material earthMat = new Material(material);
    earthMat.setAmbient(0, 0, 0.8f);

    result.put(earthModel, earthMat);

    Matrix4f moonModel = new Matrix4f(earthModel);
    moonModel.rotate((float) Math.toRadians(time * 3), 0, 0, 1);
    moonModel.translate(0, 1, 0);
    moonModel.scale(0.25f, 0.25f, 0.25f);

    Material moonMat = new Material(material);
    moonMat.setAmbient(1, 1, 1);

    result.put(moonModel, moonMat);

    return result;
  }

  /**
   * Get the modelView matrix and the material of the Venus, which is a dark yellow planet that goes
   * a circular orbit that is not on the same plane as other planets around the sun.
   *
   * @return a pair of modelView and a material of the Venus stored in a map
   */
  private Map<Matrix4f, Material> getVenusSystem() {
    Map<Matrix4f, Material> result = new HashMap<>();

    Matrix4f venusModel = new Matrix4f();

    Matrix4f offset = new Matrix4f().identity();
    offset.setColumn(0, new Vector4f(1, 0, 1, 0).normalize());
    offset.setColumn(2, new Vector4f(-1, 0, 1, 0).normalize());

    venusModel.mul(offset);

    venusModel.rotateZ((float) Math.toRadians(time * 3));
    venusModel.translate(0, 360, 0);
    venusModel.scale(45, 45, 45);

    Material venusMat = new Material(material);
    venusMat.setAmbient(0.7f, 0.7f, 0);

    result.put(venusModel, venusMat);
    return result;
  }

  /**
   * Get the modelView matrix and the material of the Mercury, which goes a circular orbit around
   * the sun.
   *
   * @return a pair of modelView and a material of the Mercury stored in a map
   */
  private Map<Matrix4f, Material> getMercurySystem() {
    Map<Matrix4f, Material> result = new HashMap<>();

    Matrix4f venusModel = new Matrix4f();
    venusModel.rotateZ((float) Math.toRadians(-time * 7));
    venusModel.translate(-150, 0, 0);
    venusModel.scale(25, 25, 25);

    Material venusMat = new Material(material);
    venusMat.setAmbient(0.7f, 0, 0.5f);

    result.put(venusModel, venusMat);
    return result;
  }

  /**
   * Get the modelView matrix and the material of the planet Aiur and its moons, which goes around
   * an circular orbit around the sun but with an offset. The center of the planet orbit thus is not
   * on the sun. Planet Aiur has two moons both go around the Aiur following circular orbit, and one
   * of them has its orbit offset. The center of one of the satellite orbit is thus not on the
   * planet Aiur.
   *
   * @return pairs of modelView and a material of the Aiur and its moons stored in a map
   */
  private Map<Matrix4f, Material> getAiurSystem() {
    Map<Matrix4f, Material> result = new HashMap<>();

    Matrix4f offset = new Matrix4f();
    offset.translate(100, 0, 0);

    Matrix4f aiurModel = new Matrix4f(offset);
    aiurModel.rotateZ((float) Math.toRadians(-time * 0.3));
    aiurModel.translate(0, -1000, 0);
    aiurModel.scale(100f, 100f, 100f);

    Material aiurMat = new Material(material);
    aiurMat.setAmbient(0, 0.6f, 1f);

    result.put(aiurModel, aiurMat);

    Matrix4f moon1Model = new Matrix4f(aiurModel);
    moon1Model.rotate((float) Math.toRadians(-time * 7), 0, 0, 1);
    moon1Model.translate(-1f, 0, 0);
    moon1Model.scale(0.2f, 0.2f, 0.2f);

    Material moon1Mat = new Material(material);
    moon1Mat.setAmbient(1, 1, 1);

    result.put(moon1Model, moon1Mat);

    Matrix4f moon2Model = new Matrix4f(aiurModel);
    moon2Model.translate(1, 0, 0);
    moon2Model.rotate((float) Math.toRadians(time), 0, 0, 1);
    moon2Model.translate(-2.5f, 0, 0);
    moon2Model.scale(0.1f, 0.1f, 0.1f);

    Material moon2Mat = new Material(material);
    moon2Mat.setAmbient(1, 1, 1);

    result.put(moon2Model, moon2Mat);

    return result;
  }

  /**
   * Get the modelView matrix and the material of the planet Sefar, which goes a rotating spiral
   * orbit around the sun.
   *
   * @return pairs of modelView and a material of the planet Sefar and the moon stored in a map
   */
  private Map<Matrix4f, Material> getSefarSystem() {
    Map<Matrix4f, Material> result = new HashMap<>();

    Matrix4f sefarModel = new Matrix4f();
    sefarModel.rotateZ((float) Math.toRadians(time));
    sefarModel.translate(-1500, 0, 0);

    sefarModel.rotateY((float) Math.toRadians(time * 10));
    sefarModel.translate(40, 40, 40);

    sefarModel.scale(25, 25, 25);

    Material sefarMat = new Material(material);
    sefarMat.setAmbient(1f, 1, 1f);

    result.put(sefarModel, sefarMat);
    return result;
  }
}
