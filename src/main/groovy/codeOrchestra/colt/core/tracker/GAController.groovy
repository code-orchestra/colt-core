package codeOrchestra.colt.core.tracker
import javafx.beans.property.ObjectProperty
import javafx.beans.value.ChangeListener
import javafx.event.*
import javafx.scene.Node as FXNode
import javafx.scene.input.InputMethodEvent
import javafx.stage.Stage

/**
 * @author Dima Kruk
 */
class GAController {
    private static final GAController instance = new GAController();

    static GAController getInstance() {
        return instance
    }

    GATracker tracker =  GATracker.instance

    Stage curStage

    class GAPageInfo {
        String url
        String title

        GAPageInfo(String url, String title) {
            this.url = url
            this.title = title
        }
    }

    HashMap<FXNode, GAPageInfo> pagesMap = new HashMap<>()

    class GAEventInfo {
        String category
        String action

        GAEventInfo(String category, String action) {
            this.category = category
            this.action = action
        }
    }

    HashMap<EventTarget, GAEventInfo> eventsMap = new HashMap<>()

    private GAController() {
    }

    private EventHandler<Event> eventHandler = new EventHandler<Event>() {
        @Override
        void handle(Event it) {
//            println("it -> " + it)
            if (it instanceof ActionEvent) {
//                println "action > ${it.target}"

                if (eventsMap.containsKey(it.target)) {
                    GAEventInfo info = eventsMap[it.target]
                    tracker.trackEventWithPage(info.category, info.category)
                }
            }

            if (it instanceof InputMethodEvent) {
//                println "focus > $it"
            }
        }
    }

    void start(Stage stage) {
        curStage?.removeEventFilter(EventType.ROOT, eventHandler)

        curStage = stage
        curStage.addEventFilter(EventType.ROOT, eventHandler)
    }

    void registerEvent(EventTarget target, String category, String action) {
        eventsMap[target] = new GAEventInfo(category, action)
    }

    void setPageContainer(ObjectProperty container) {
        container.addListener({observableValue, FXNode oldNode, FXNode newNode ->
            GAPageInfo info = pagesMap[newNode]
            if (info) {
                tracker.trackPageView(info.url, info.title)
            }
        } as ChangeListener)
    }

    void registerPage(FXNode node, String url, String title) {
        pagesMap[node] = new GAPageInfo(url, title)
    }
}
