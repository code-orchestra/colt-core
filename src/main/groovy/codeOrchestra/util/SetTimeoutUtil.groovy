package codeOrchestra.util

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.event.EventHandler
import javafx.util.Duration

/**
 * @author Eugene Potapenko
 */
class SetTimeoutUtil {
    static void setTimeout(int duration, Closure closure){
        new Timeline(new KeyFrame(new Duration(duration.toDouble()), {
            closure.call()
        } as EventHandler))
    }
}
