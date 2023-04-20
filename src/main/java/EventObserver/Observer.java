package EventObserver;

import MainStudioComponents.GameObject;
import EventObserver.events.Event;

public interface Observer
{
    void onNotify(GameObject obj, Event event);
}
