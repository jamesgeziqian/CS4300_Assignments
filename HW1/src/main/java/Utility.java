import org.joml.Vector4f;
import util.IVertexData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * This is a utility class
 */
public class Utility {
    /**
     * This is a static method to convert list of Vector4f to List of IvertexData
     * @param positions This is input data
     * @return Output is converted to List of IvertexData so that PolygonMesh can take in.
     */
    static List<IVertexData> parseToIVertexData(List<Vector4f> positions) {
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
        return vertexData;
    }

    // Get hour of time
    static int getHour() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    // Get minute of time
    static int getMin() {
        return Calendar.getInstance().get(Calendar.MINUTE);
    }

    // Get second of time
    static int getSec() {
        return Calendar.getInstance().get(Calendar.SECOND);
    }
}
