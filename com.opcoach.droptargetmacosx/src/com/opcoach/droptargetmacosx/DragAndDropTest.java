package com.opcoach.droptargetmacosx;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.dnd.URLTransfer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DragAndDropTest {
	

	public static StringBuffer displayTransfers(Transfer[] transfers) {
	    StringBuffer result = new StringBuffer();

	    if (transfers == null || transfers.length == 0) {
	        result.append("No transfers available.\n");
	        return result;
	    }
	    
	    result.append("Supported transfers:\n");
	    for (Transfer transfer : transfers) {
	        if (transfer instanceof TextTransfer) {
	            result.append("TextTransfer is supported.\n");
	        } else if (transfer instanceof URLTransfer) {
	            result.append("URLTransfer is supported.\n");
	        } else {
	            result.append("Unknown transfer: ").append(transfer.getClass().getSimpleName()).append("\n");
	        }
	    }
	    
	    return result;
	}
	
	
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
        DropTarget dropTarget = new DropTarget(dropLabel, DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_DEFAULT);
        Transfer[] types = new Transfer[] {TextTransfer.getInstance(), URLTransfer.getInstance()};
        dropTarget.setTransfer(types);

        // Add a listener to handle the dragEnter event and highlight the drop zone
        dropTarget.addDropListener(new DropTargetAdapter() {
            @Override
            public void dragEnter(DropTargetEvent event) {
                dropLabel.setBackground(new Color(display, 180, 180, 255)); // Highlight color on dragEnter
                event.detail = DND.DROP_COPY;
            }

            @Override
            public void dropAccept(DropTargetEvent event) {
            	// TODO Auto-generated method stub
            	System.out.println("Enter in drop accept");
            	super.dropAccept(event);
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
                } else if (FileTransfer.getInstance().isSupportedType(event.currentDataType)) {
                    String[] files = (String[]) FileTransfer.getInstance().nativeToJava(event.currentDataType);
                    if (files != null && files.length > 0) {
                        dropLabel.setText("File(s) dropped: " + String.join(", ", files));
                        for (String file : files) {
                            System.out.println("File dropped: " + file);
                        }
                    } else {
                        dropLabel.setText("No files were dropped.");
                    }
                } else {
                    // Handle unsupported transfer types
                    dropLabel.setText("Unsupported transfer type dropped.");
                    System.out.println("Unsupported transfer type dropped.");
                }

                // Log all available transfer types
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
        	public void dragStart(DragSourceEvent event) {
        		// TODO Auto-generated method stub
        		super.dragStart(event);
        		DragSource source = (DragSource)event.getSource();
        		Label t = (Label) source.getControl();
                System.out.println("Dragging TextTransfer from Text Widget: " + t.getText() + "Transfers : " +  displayTransfers(source.getTransfer()));

        	}
            @Override
            public void dragSetData(DragSourceEvent event) {
                if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
                    event.data = dragSourceLabel.getText(); // Transfer text
                    System.out.println("Dragging TextTransfer: " + event.data);
                } else if (URLTransfer.getInstance().isSupportedType(event.dataType)) {
                    event.data = dragSourceLabel.getText(); // Transfer URL
                    System.out.println("Dragging URLTransfer: " + event.data);
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
        	public void dragStart(DragSourceEvent event) {
        		// TODO Auto-generated method stub
        		super.dragStart(event);
        		DragSource source = (DragSource)event.getSource();
        		Text t = (Text) source.getControl();
                System.out.println("Dragging TextTransfer from Text Widget: " + t.getText()  + "Transfers : " +  displayTransfers(source.getTransfer()));

        	}
            @Override
            public void dragSetData(DragSourceEvent event) {
                if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
                    event.data = editableText.getText(); // Transfer editable text
                    System.out.println("Dragging TextTransfer from Text Widget: " + event.data);
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