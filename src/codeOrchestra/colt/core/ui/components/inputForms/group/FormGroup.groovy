package codeOrchestra.colt.core.ui.components.inputForms.group

import codeOrchestra.colt.core.ui.components.inputForms.markers.MAction
import codeOrchestra.colt.core.ui.components.inputForms.markers.MChoiceBox
import codeOrchestra.colt.core.ui.components.inputForms.markers.MInput
import codeOrchestra.colt.core.ui.components.inputForms.markers.MLabeled
import codeOrchestra.colt.core.ui.components.inputForms.markers.MSelectable
import codeOrchestra.colt.core.ui.components.inputForms.markers.MSimple
import codeOrchestra.colt.core.ui.components.inputForms.markers.Marked
import javafx.collections.ListChangeListener
import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.layout.VBox

/**
 * @author Dima Kruk
 */
class FormGroup extends VBox {
    private static final Insets TITLED = new Insets(26, 0, 23, 0)
    private static final Insets NOT_TITLED = new Insets(3, 0, 23, 0)

    private static final int SPASING = 22

    protected Label label

    String title

    boolean first
/*
    <fx:root type="javafx.scene.layout.VBox" styleClass="fieldset" maxWidth="640.0" xmlns:fx="http://javafx.com/fxml">
      <Label fx:id="label" styleClass="legend">
        <VBox.margin>
          <Insets bottom="-2" left="19" />
        </VBox.margin>
      </Label>
    </fx:root>
     */

    FormGroup() {
        label = new Label()
        label.styleClass.add("legend")
        setMargin(label, new Insets(0, 0, -2, 19))
        children.add(label)

        setFirst(false)
        makeTitled(false)

        setMaxWidth(640.0)
        setSpacing(SPASING)

        initListener()
    }

    private void initListener() {

        children.addListener({ ListChangeListener.Change<? extends javafx.scene.Node> change ->
            change.next()
            if (change.from == 1) {
                if (!title) {
                    setMargin(change.addedSubList[0], new Insets(SPASING, 0, 0, 0))
                }
            }

            if (change.from == 1 && !title) {
                setMargin(change.addedSubList[0], new Insets(SPASING, 0, 0, 0))
            } else {
                initMargins(change.list[change.from - 1] , change.addedSubList[0])
            }
            Label
            if (change.addedSubList.size() > 1) {
                for (int i = 1; i < change.addedSubList.size(); i++) {
                    initMargins(change.addedSubList[i - 1], change.addedSubList[i])
                }
            }
        } as ListChangeListener)
    }

    @Override
    protected void layoutChildren() {
        double w = 10
        children.each {
            if(it instanceof MAction) {
                w = Double.max(w, (it as MAction).inputRightAnchor)
            }
        }
        children.each {
            if((it instanceof MInput) && !(it instanceof MAction)) {
                (it as MInput).inputRightAnchor = w
            }
        }
        super.layoutChildren()
    }

    public void initMargins(javafx.scene.Node prev, javafx.scene.Node cur) {
        if (prev instanceof Marked && cur instanceof Marked) {
            if (cur instanceof MSelectable) {
                if (prev instanceof MSelectable) {
                    if (prev instanceof MSimple) {
                        setMargin(cur, new Insets(18 - SPASING, 0, 0, 0))
                    } else {
                        setMargin(cur, new Insets(25 - SPASING, 0, 0, 0))
                    }
                }
                if (prev instanceof MChoiceBox) {
                    setMargin(cur, new Insets(24 - SPASING, 0, 0, 0))
                }
            }

            if (cur instanceof MLabeled) {
                if (prev instanceof MSimple) {
                    setMargin(cur, new Insets(18 - SPASING, 0, 0, 0))
                } else {
                    setMargin(cur, new Insets(19 - SPASING, 0, 0, 0))
                }
            }

            if (cur instanceof MChoiceBox) {
                setMargin(cur, new Insets(23 - SPASING, 0, 0, 0))
            }
        }
    }

    private void makeTitled(boolean b) {
        label.visible = label.managed = b
        setPadding(b ? TITLED : NOT_TITLED)
        if(children.size() > 1) {
            setMargin(children[1], b ? null : new Insets(SPASING, 0, 0, 0))
        }
    }


    void setTitle(String value) {
        title = value
        if (title) {
            label.text = title
            makeTitled(true)
        } else {
            makeTitled(false)
        }
    }

    void setFirst(boolean first) {
        this.first = first
        first ? styleClass.remove("fieldset") : styleClass.add("fieldset")
    }
}
