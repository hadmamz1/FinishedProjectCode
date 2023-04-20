package MainStudioComponents;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {
    private Matrix4f projecMatrix, viewMatrix, inverseProjec, inverseView;
    public Vector2f camPos;
    private float projectionWidth = 6;
    private float projectionHeight = 3;
    private Vector2f projectionSize = new Vector2f(projectionWidth, projectionHeight);
    private float zoom = 1.0f;


    public Camera(Vector2f camPos) {
        this.camPos = camPos;
        this.projecMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.inverseProjec = new Matrix4f();
        this.inverseView = new Matrix4f();
        modifyProjec();
    }

    public void modifyProjec() {
        projecMatrix.identity();
        projecMatrix.ortho(0.0f, projectionSize.x * this.zoom,
                0.0f, projectionSize.y * zoom, 0.0f, 100.0f);
        projecMatrix.invert(inverseProjec);
    }

    // Defines how the Camera is looking
    public Matrix4f getViewMatrix() {
        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
        this.viewMatrix.identity();
        viewMatrix.lookAt(new Vector3f(camPos.x, camPos.y, 20.0f),
                cameraFront.add(camPos.x, camPos.y, 0.0f),
                cameraUp);
        this.viewMatrix.invert(inverseView);

        return this.viewMatrix;
    }

    public Matrix4f getProjecMatrix() {
        return this.projecMatrix;
    }

    public Matrix4f getInverseProjec() {
        return this.inverseProjec;
    }

    public Matrix4f getInverseView() {
        return this.inverseView;
    }

    public Vector2f getProjectionSize() {
        return this.projectionSize;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public void addZoom(float val) {
        this.zoom += val;
    }
}