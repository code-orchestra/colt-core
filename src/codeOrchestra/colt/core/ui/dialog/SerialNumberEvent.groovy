package codeOrchestra.colt.core.ui.dialog

import javafx.event.Event
import javafx.event.EventType

/**
 * @author Alexander Eliseyev
 */
class SerialNumberEvent extends Event {

    private static final EventType<SerialNumberEvent> TYPE = new EventType<SerialNumberEvent>("COLT_SERIAL_NUMBER")

    String serialNumber;

    SerialNumberEvent(String serialNumber = null) {
        super(TYPE)
        this.serialNumber = serialNumber
    }

    boolean isCancelled() {
        serialNumber == null
    }

}
