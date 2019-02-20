# CS4300 HW3

## Set Config Files to Run the Code

  All the Config files are under `src/main/resources/configs`

  `tower_global.config` is displying the building with a global camera. Press `t` to switch to turntable camera. Press `g` to switch back to global camera.

  `tower_ring.config` is displaying the same building with truntable camera as starting camera position.

  `YMCA_global.config` is displaying the "YMCA" poses with a global camera. Press `t` to switch to turntable camera. Press `g` to switch back to global camera.

## Answer Questions

1. What is a scene graph made of? What are its various parts and what is the function of each part (in the code, not in general)?

    A scene graph is made of `root`, `meshes`, `nodes`, `textures` and `renderer`. `root` stores the very begining node of a scene graph tree. All other components in the graph are children. `meshes` stores (name, polygon mesh) pairs. It helps import and store polygon for child nodes so that they just need to name the polygon to reuse them. They are stored in a map for fast search. `nodes` are (name, Inode) pair of all nodes in the graph for fast search. `textures` stores the texture information, which are pairs of (name, texture file path). `renderer` is the object that renders the scene graph.

2. How is the scene graph drawn, and how does it ensure the correct transformation gets applied to the correct part?

    An `IScenegraphRenderer` is tied to a `Scenegraph` object and all polygon meshes stored in the `Scenegraph` is then passed to that renderer. As a result, when the child nodes want to draw a certain polygon, they just provide the name rather than the actural polygon mesh.

    Then, `View` class calls the draw method in `Scenegraph`. draw consumes a stack of matrices where represents the last transformation from the world coordinates to the camera coordinates. Then, `Scenegraph` passes this matrices stack down to the `ScenegraphRenderer` along with the root of the `Scenegraph` to draw, which then passes this stack and it self to the root, an `INode`. Each kind of `INode` modifies the modelView matrices stack in its own way. i.e. the `GroupNode` passes the stack to each of its child; and the `TransformNode` applies the transformation stored in itself to a copy of peek of the stack and then pass the stack down to its child which means it multiplies its transformation to the modelView stack; the `LeafNode` then passes the final, finished modelView, material, and texture and the name of the object (the polygon mesh is already stored in renderer) to renderer and renderer will then draw the final object.

    All transformation to a object is stored in the stack of matrices. We go through the tree to a actual `LeafNode` and pick up all necessary transformation to render the `LeafNode`. The tree structure and the modelView stack ensures that a `LeafNode` is correctly drawn.

3. What is the use of the GL3ScenegraphRenderer class? If you wanted to create a textual rendering of the scene graph (e.g. a textual description of each node) how would you write such a renderer?

    `GL3ScenegraphRenderer` stores all the texture, material and objectInstances information. With a modelView and names of object, texture or matiral info that provided by a `LeafNode`, A `GL3ScenegraphRenderer` can draw the node correctly by calling drawMesh method.

    Designing a textual renderer:

      We still want to make use of the Scenegraph data structure. In stead of storing matrices and objectInstances, we want to store the textual description of them. We want to change the modelView stack to a text buffer that stores the textual discription of each matrix. Then, when it comes to the actual rendering, we print the description of a objectInstances along with all the transformation it goes through in text from the text buffer.
