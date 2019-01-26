import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import org.joml.Vector4f;
import util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SingleDigit {
    private static float HEIGHT = 2;
    private static int LENGTH_DIV_HEIGHT = 5;
    private static float LENGTH = HEIGHT * LENGTH_DIV_HEIGHT;
    private String namePrefix;
    private ObjectInstance[] objects;
    private GL3 gl;
    private util.ShaderProgram program;
    private util.ShaderLocationsVault shaderLocations;
    private float px, py;


    SingleDigit(float x, float y, GL3 gl,
                util.ShaderProgram program,
                util.ShaderLocationsVault shaderLocations,
                String namePrefix) {
        this.gl = gl;
        this.program = program;
        this.shaderLocations = shaderLocations;
        this.namePrefix = namePrefix;
        objects = new ObjectInstance[7];
        px = x;
        py = y;
        initObjects();
    }

    private void initObjects() {
        float blank = Math.max(1, HEIGHT / 100);

        float segAOriX = px + blank;
        float segAOriY = py;
        objects[0] = genSingleSegObj(
                genSingleSegPos(segAOriX, segAOriY, false),
                namePrefix + ".SegA");

        float segBOriX = segAOriX + LENGTH + blank;
        float segBOriY = segAOriY - blank;
        objects[1] = genSingleSegObj(
                genSingleSegPos(segBOriX, segBOriY, true),
                namePrefix + ".SegB");

        float segCOriX = segBOriX;
        float segCOriY = segBOriY - 2 * blank - LENGTH;
        objects[2] = genSingleSegObj(
                genSingleSegPos(segCOriX, segCOriY, true),
                namePrefix + ".SegC");

        float segDOriX = segAOriX;
        float segDOriY = segAOriY - 4 * blank - 2 * LENGTH;
        objects[3] = genSingleSegObj(genSingleSegPos(segDOriX, segDOriY, false),
                namePrefix + ".SegD");

        float segEOriX = segAOriX - blank;
        float segEOriY = segDOriY + blank + LENGTH;
        objects[4] = genSingleSegObj(genSingleSegPos(segEOriX, segEOriY, true),
                namePrefix + ".SegE");

        float segFOriX = segEOriX;
        float segFOriY = segBOriY;
        objects[5] = genSingleSegObj(genSingleSegPos(segFOriX, segFOriY, true),
                namePrefix + ".SegE");

        float segGOriX = segAOriX;
        float segGOriY = segEOriY + blank;
        objects[6] = genSingleSegObj(genSingleSegPos(segGOriX, segGOriY, false),
                namePrefix + ".SegG");

    }

    private ObjectInstance genSingleSegObj(List<Vector4f> positions, String str) {
        List<IVertexData> vertexData = parseToIVertexData.doIt(positions);
        PolygonMesh<IVertexData> mesh = new PolygonMesh<>();
        mesh.setVertexData(vertexData);
        mesh.setPrimitives(getIndicesSingleSeg());
        mesh.setPrimitiveType(GL.GL_TRIANGLES);
        mesh.setPrimitiveSize(3);
        Map<String, String> shaderToVertexAttribute = new HashMap<>();
        shaderToVertexAttribute.put("vPosition", "position");
        return new ObjectInstance(gl, program, shaderLocations, shaderToVertexAttribute, mesh, str);
    }

    private List<Integer> getIndicesSingleSeg() {
        ArrayList<Integer> result = new ArrayList<>();
        result.add(0);
        result.add(1);
        result.add(5);

        result.add(1);
        result.add(5);
        result.add(4);

        result.add(1);
        result.add(2);
        result.add(4);

        result.add(2);
        result.add(3);
        result.add(4);
        return result;
    }

    private ArrayList<Vector4f> genSingleSegPos(float oriX, float oriY, boolean vertical) {
        float halfHeight = HEIGHT / 2;
        float longPartialLength = LENGTH - halfHeight;
        ArrayList<Vector4f> result = new ArrayList<>();
        if (vertical) {
            result.add(new Vector4f(oriX, oriY, 0, 1f));
            result.add(new Vector4f(oriX + halfHeight, oriY - halfHeight, 0, 1f));
            result.add(new Vector4f(oriX + halfHeight, oriY - longPartialLength, 0, 1f));
            result.add(new Vector4f(oriX, oriY - LENGTH, 0, 1f));
            result.add(new Vector4f(oriX - halfHeight, oriY - longPartialLength, 0, 1f));
            result.add(new Vector4f(oriX - halfHeight, oriY - halfHeight, 0, 1f));
        } else {
            result.add(new Vector4f(oriX, oriY, 0, 1f));
            result.add(new Vector4f(oriX + halfHeight, oriY + halfHeight, 0, 1f));
            result.add(new Vector4f(oriX + longPartialLength, oriY + halfHeight, 0, 1f));
            result.add(new Vector4f(oriX + LENGTH, oriY, 0, 1f));
            result.add(new Vector4f(oriX + longPartialLength, oriY - halfHeight, 0, 1f));
            result.add(new Vector4f(oriX + halfHeight, oriY - halfHeight, 0, 1f));
        }
        return result;
    }

    void draw(GLAutoDrawable gla, int num) {
        HashMap<Integer, int[]> map = new HashMap<>();
        map.put(0, new int[]{0, 1, 2, 3, 4, 5});
        map.put(1, new int[]{1, 2});
        map.put(2, new int[]{0, 1, 3, 4, 6});
        map.put(3, new int[]{0, 1, 2, 3, 6});
        map.put(4, new int[]{1, 2, 5, 6});
        map.put(5, new int[]{0, 2, 3, 5, 6});
        map.put(6, new int[]{0, 2, 3, 4, 5, 6});
        map.put(7, new int[]{0, 1, 2});
        map.put(8, new int[]{0, 1, 2, 3, 4, 5, 6});
        map.put(9, new int[]{0, 1, 2, 3, 5, 6});
        for (int i = 0; i < map.get(num).length; ++i) {
            objects[map.get(num)[i]].draw(gla);
        }
    }

    void cleanup(GLAutoDrawable gla) {
        for (ObjectInstance obj : objects) {
            obj.cleanup(gla);
        }
    }
}
