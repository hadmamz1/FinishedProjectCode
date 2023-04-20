package EditorScreenComponents;

import imgui.ImGui;
import imgui.ImVec2;
import MainStudioComponents.Listener_Mouse;
import EventObserver.EventSystem;
import EventObserver.events.Event;
import EventObserver.events.EventType;
import org.joml.Vector2f;
import imgui.flag.ImGuiWindowFlags;
import MainStudioComponents.Window;

public class GameViewWindow {
    private float leftX, rightX, topY, bottomY;
    private boolean playing = false;

    public void imgui() {
        ImGui.begin("Game Viewport", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse
                | ImGuiWindowFlags.MenuBar);


        ImGui.beginMenuBar();

        if (ImGui.menuItem("Play", "", playing, !playing))
        {
            playing = true;
            EventSystem.notify(null, new Event(EventType.GameEngineStartPlay));

        }

        if (ImGui.menuItem("Stop", "", !playing, playing))
        {
            playing = false;
            EventSystem.notify(null, new Event(EventType.GameEngineStopPlay));
        }

        ImGui.endMenuBar();


        ImVec2 windowSize = getBiggestViewportSize();
        ImVec2 windowPos = getCenteredViewportPos(windowSize);

        ImGui.setCursorPos(windowPos.x, windowPos.y);

        ImVec2 topLeft = new ImVec2();
        ImGui.getCursorScreenPos(topLeft);
        topLeft.x -= ImGui.getScrollX();
        topLeft.y -= ImGui.getScrollY();
        leftX = topLeft.x;
        bottomY = topLeft.y;
        rightX = topLeft.x + windowSize.x;
        topY = topLeft.y + windowSize.y;

        int textureId = Window.getFramebuffer().getTextureId();
        ImGui.image(textureId, windowSize.x, windowSize.y, 0, 1, 1, 0);

        Listener_Mouse.setGameViewportPos(new Vector2f(topLeft.x, topLeft.y));
        Listener_Mouse.setGameViewportSize(new Vector2f(windowSize.x, windowSize.y));

        ImGui.end();
    }

    private ImVec2 getBiggestViewportSize() {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);
        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();

        float aspectWidth = windowSize.x;
        float aspectHeight = aspectWidth / Window.getTargetAspectRatio();
        if (aspectHeight > windowSize.y) {
            // We must switch to pillarbox mode
            aspectHeight = windowSize.y;
            aspectWidth = aspectHeight * Window.getTargetAspectRatio();
        }

        return new ImVec2(aspectWidth, aspectHeight);
    }

    private ImVec2 getCenteredViewportPos(ImVec2 aspectSize) {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);
        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();

        float viewportX = (windowSize.x / 2.0f) - (aspectSize.x / 2.0f);
        float viewportY = (windowSize.y / 2.0f) - (aspectSize.y / 2.0f);

        return new ImVec2(viewportX + ImGui.getCursorPosX(),
                viewportY + ImGui.getCursorPosY());
    }

    public boolean getWantCaptureMouse() {
        return Listener_Mouse.getX() >= leftX
                && Listener_Mouse.getX() <= rightX &&
                Listener_Mouse.getY() >= bottomY
                && Listener_Mouse.getY() <= topY;
    }
}