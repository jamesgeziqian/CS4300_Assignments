import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import org.joml.Vector4f;
import util.IVertexData;
import util.ObjectInstance;
import util.PolygonMesh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TwoDots {
    private static float RADIUS = 2;
    private static int NUM_OF_TRI_IN_CIRCLE = 32;
    private ObjectInstance[] objects;
    private GL3 gl;
    private util.ShaderProgram program;
    private util.ShaderLocationsVault shaderLocations;
    private float px, py, interval;
    private String namePrefix;

    TwoDots(float x, float y, float interval, GL3 gl,
            util.ShaderProgram program,
            util.ShaderLocationsVault shaderLocations,
            String namePrefix) {
        this.gl = gl;
        this.program = program;
        this.shaderLocations = shaderLocations;
        this.px = x;
        this.py = y;
        this.interval = interval;
        this.namePrefix = namePrefix;
        objects = new ObjectInstance[2];
        initObjects();
    }

    private void initObjects() {
        objects[0] = genSingleDotObj(px, py + interval / 2, namePrefix + ".Dot1");
        objects[1] = genSingleDotObj(px, py - interval / 2, namePrefix + ".Dot2");
    }

    void draw(GLAutoDrawable gla) {
        for (ObjectInstance obj : objects) {
            obj.draw(gla);
        }
    }

    void cleanup(GLAutoDrawable gla) {
        for (ObjectInstance obj : objects) {
            obj.cleanup(gla);
        }
    }

    private ObjectInstance genSingleDotObj(float x, float y, String str) {
        ArrayList<Vector4f> positions = new ArrayList<>();
        float theta = (float) Math.PI * 2 / NUM_OF_TRI_IN_CIRCLE;

        positions.add(new Vector4f(x, y, 0, 1f));
        for (int i = 0; i < NUM_OF_TRI_IN_CIRCLE; ++i) {
            positions.add(
                    new Vector4f(x + RADIUS * (float) Math.cos(theta * i),
                            y + RADIUS * (float) Math.sin(theta * i),
                            0, 1f));
        }
        positions.add(new Vector4f(x + RADIUS, y, 0, 1f));
        List<IVertexData> vertexData = Utility.parseToIVertexData(positions);
        PolygonMesh<IVertexData> mesh = new PolygonMesh<>();
        mesh.setVertexData(vertexData);
        mesh.setPrimitives(getIndicesSingleDot());
        mesh.setPrimitiveType(GL.GL_TRIANGLE_FAN);
        mesh.setPrimitiveSize(3);
        Map<String, String> shaderToVertexAttribute = new HashMap<>();
        shaderToVertexAttribute.put("vPosition", "position");
        return new ObjectInstance(gl, program, shaderLocations, shaderToVertexAttribute, mesh, str);
    }

    private List<Integer> getIndicesSingleDot() {
        ArrayList<Integer> result = new ArrayList<>();
        for (int i =0; i < NUM_OF_TRI_IN_CIRCLE + 2; ++i) {
            result.add(i);
        }
        return result;
    }


}
