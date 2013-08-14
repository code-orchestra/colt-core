package codeOrchestra.colt.core.model.monitor

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue

/**
 * @author Dima Kruk
 */
class ChangingMonitor {
    private static ChangingMonitor ourInstance = new ChangingMonitor()

    public static ChangingMonitor getInstance() {
        return ourInstance
    }

    ArrayList<ObservableValue> items
    boolean changed

    private ChangingMonitor() {
        items = Collections.EMPTY_LIST
    }

    void reset() {
        changed = false
    }

    void clear() {
        items.each {remove(it)}
        items.clear()
    }

    void add(ObservableValue value) {
        value.addListener(changeListener)
        items.add(value)
    }

    void addAll(ObservableValue... values) {
        values.each {add(it)}
    }

    void remove(ObservableValue value) {
        value.removeListener(changeListener)
    }

    private ChangeListener changeListener = new ChangeListener() {
        @Override
        void changed(ObservableValue observableValue, Object t, Object t1) {
            changed = true
        }
    }
}
