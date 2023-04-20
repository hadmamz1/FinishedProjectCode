package components;

import MainStudioComponents.Window;
import MainStudioComponents.Camera;
import org.joml.Vector2f;
import org.joml.Vector3f;
import RenderingComponents.Drawing;
import UtilityComponents.Settings;

public class GridLines extends Component {

    @Override
    public void updateEditor(float deltaTime) {
        Camera camera = Window.getScene().camera();
        Vector2f camPos = camera.camPos;
        Vector2f projecSize = camera.getProjectionSize();

        float firstX = ((int)(camPos.x / Settings.GRID_WIDTH)) * Settings.GRID_HEIGHT;
        float firstY = ((int)(camPos.y / Settings.GRID_HEIGHT)) * Settings.GRID_HEIGHT;

        int numVtLines = (int)(projecSize.x * camera.getZoom() / Settings.GRID_WIDTH) + 2;
        int numHzLines = (int)(projecSize.y * camera.getZoom() / Settings.GRID_HEIGHT) + 2;

        float height = (int)(projecSize.y * camera.getZoom()) + (5 * Settings.GRID_HEIGHT);
        float width = (int)(projecSize.x * camera.getZoom()) + (5 * Settings.GRID_WIDTH);

        int maxLines = Math.max(numVtLines, numHzLines);
        Vector3f color = new Vector3f(0.2f, 0.2f, 0.2f);
        for (int i=0; i < maxLines; i++) {
            float x = firstX + (Settings.GRID_WIDTH * i);
            float y = firstY + (Settings.GRID_HEIGHT * i);

            if (i < numVtLines) {
                Drawing.addLine2D(new Vector2f(x, firstY), new Vector2f(x, firstY + height), color);
            }

            if (i < numHzLines) {
                Drawing.addLine2D(new Vector2f(firstX, y), new Vector2f(firstX + width, y), color);
            }
        }
    }
}