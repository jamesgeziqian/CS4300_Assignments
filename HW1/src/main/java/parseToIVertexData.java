import org.joml.Vector4f;
import util.IVertexData;

import java.util.ArrayList;
import java.util.List;

class parseToIVertexData {
    static List<IVertexData> doIt(List<Vector4f> positions) {
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
}
