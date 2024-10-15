package com.opcoach.droptargetmacosx;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DragAndDropTest {

    public static void main(String[] args) {
        // Setup SWT display and shell
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setText("Drag and Drop Test");
        shell.setLayout(new GridLayout(1, false));
        shell.setSize(500, 300); // Larger size to accommodate additional widgets

        // Instruction label
        Label instructionLabel = new Label(shell, SWT.NONE);
        instructionLabel.setText("Drag and drop in the area below.");
        instructionLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        // Drop target label
        Label dropLabel = new Label(shell, SWT.BORDER | SWT.WRAP);
        dropLabel.setText("Drop area...");
        dropLabel.setBackground(new Color(display, 200, 200, 190)); // Set default background color
        dropLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // Define the DropTarget with TextTransfer and URLTransfer
        DropTarget dropTarget = new DropTarget(dropLabel, DND.DROP_COPY | DND.DROP_DEFAULT);
        Transfer[] types = new Transfer[] {TextTransfer.getInstance(), URLTransfer.getInstance()};
        dropTarget.setTransfer(types);

        // Add a listener to handle the dragEnter event and highlight the drop zone
        dropTarget.addDropListener(new DropTargetAdapter() {
            @Override
            public void dragEnter(DropTargetEvent event) {
                dropLabel.setBackground(new Color(display, 180, 180, 255)); // Highlight color on dragEnter
            }

            @Override
            public void dragLeave(DropTargetEvent event) {
                dropLabel.setBackground(new Color(display, 200, 200, 190)); // Reset background color on dragLeave
            }

            @Override
            public void drop(DropTargetEvent event) {
                dropLabel.setBackground(new Color(display, 200, 200, 190)); // Reset color after drop

                if (TextTransfer.getInstance().isSupportedType(event.currentDataType)) {
                    String text = (String) TextTransfer.getInstance().nativeToJava(event.currentDataType);
                    dropLabel.setText(text != null ? "Text dropped: " + text : "No text data found.");
                    System.out.println("Received TextTransfer");
                } else if (URLTransfer.getInstance().isSupportedType(event.currentDataType)) {
                    String url = (String) URLTransfer.getInstance().nativeToJava(event.currentDataType);
                    dropLabel.setText(url != null ? "URL dropped: " + url : "No URL data found.");
                    System.out.println("Received URLTransfer");
                }
                for (TransferData data : event.dataTypes) {
                    System.out.println("Type: " + data.type);
                }
            }
        });

        // Label Drag Source with URL
        Label dragSourceLabel = new Label(shell, SWT.BORDER | SWT.WRAP);
        dragSourceLabel.setText("https://www.opcoach.com");
        dragSourceLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        // Define the DragSource for TextTransfer and URLTransfer
        DragSource dragSource = new DragSource(dragSourceLabel, DND.DROP_MOVE | DND.DROP_COPY);
        Transfer[] sourceTypes = new Transfer[] {TextTransfer.getInstance(), URLTransfer.getInstance()};
        dragSource.setTransfer(sourceTypes);

        dragSource.addDragListener(new DragSourceAdapter() {
            @Override
            public void dragSetData(DragSourceEvent event) {
                if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
                    event.data = dragSourceLabel.getText(); // Transfer text
                } else if (URLTransfer.getInstance().isSupportedType(event.dataType)) {
                    event.data = dragSourceLabel.getText(); // Transfer URL
                }
            }
        });

        // Editable Text Widget for Drag Source
        Text editableText = new Text(shell, SWT.BORDER);
        editableText.setText("Editable Text - Drag me");
        editableText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        // Define DragSource for the editable text
        DragSource textDragSource = new DragSource(editableText, DND.DROP_MOVE | DND.DROP_COPY);
        textDragSource.setTransfer(new Transfer[] {TextTransfer.getInstance()});

        textDragSource.addDragListener(new DragSourceAdapter() {
            @Override
            public void dragSetData(DragSourceEvent event) {
                if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
                    event.data = editableText.getText(); // Transfer editable text
                }
            }
        });

        // Open the shell and enter the event loop
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }
}