package codeOrchestra.colt.core.ui.components.inputFormsNew.group

import codeOrchestra.colt.core.ui.components.fileset.FilesetInput
import codeOrchestra.colt.core.ui.components.inputForms.CBForm
import codeOrchestra.colt.core.ui.components.inputForms.CTBForm
import codeOrchestra.colt.core.ui.components.inputForms.FormType
import codeOrchestra.colt.core.ui.components.inputForms.ITypedForm
import codeOrchestra.colt.core.ui.components.inputForms.LTBForm
import codeOrchestra.colt.core.ui.components.inputForms.RTBForm
import javafx.collections.ListChangeListener
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.layout.VBox

/**
 * @author Dima Kruk
 */
class FormGroupNew extends VBox {
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

    FormGroupNew() {
        label = new Label()
        label.styleClass.add("legend")
        setMargin(label, new Insets(0, 0, -2, 19))

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

    public void initMargins(javafx.scene.Node prev, javafx.scene.Node cur) {
//        if (prev instanceof ITypedForm && cur instanceof ITypedForm) {
//            if (cur instanceof CTBForm) {
//                if (prev instanceof CTBForm) {
//                    if (prev.type == FormType.SIMPLE) {
//                        setMargin(cur, new Insets(18 - SPASING, 0, 0, 0))
//                    } else {
//                        setMargin(cur, new Insets(25 - SPASING, 0, 0, 0))
//                    }
//                } else if (prev.type != FormType.SIMPLE) {
//                    setMargin(cur, new Insets(22 - SPASING, 0, 0, 0))
//                }
//                if (prev instanceof CBForm) {
//                    setMargin(cur, new Insets(24 - SPASING, 0, 0, 0))
//                }
//            }
//            if (cur instanceof RTBForm) {
//                if (prev instanceof RTBForm) {
//                    if (prev.type == FormType.SIMPLE) {
//                        setMargin(cur, new Insets(18 - SPASING, 0, 0, 0))
//                    } else {
//                        setMargin(cur, new Insets(25 - SPASING, 0, 0, 0))
//                    }
//                }
//            }
//            if (cur instanceof LTBForm || cur instanceof FilesetInput) {
//                if (prev.type == FormType.SIMPLE) {
//                    setMargin(cur, new Insets(18 - SPASING, 0, 0, 0))
//                } else {
//                    setMargin(cur, new Insets(19 - SPASING, 0, 0, 0))
//                }
//            }
//            if (cur instanceof CBForm) {
//                setMargin(cur, new Insets(23 - SPASING, 0, 0, 0))
//            }
//        }
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
