package isse.mbr.MiniBrassWeb.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;

import isse.mbr.MiniBrassWeb.shared.parsing.MiniBrassCompiler;
import isse.mbr.MiniBrassWeb.shared.parsing.MiniBrassParseException;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MiniBrassWeb implements EntryPoint {

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final Button sendButton = new Button("Compile");
		final TextArea modelField = new TextArea();
		modelField.setText("% your minibrass model here");

		final TextArea outputField = new TextArea();
		outputField.setReadOnly(true);
		outputField.setWidth("90%");
		outputField.setHeight("10em");
		outputField.setText("% converted minizinc model will appear here");
		
		// We can add style names to widgets
		sendButton.addStyleName("sendButton");

		// Add the nameField and sendButton to the RootPanel
		// Use RootPanel.get() to get the entire body element
		RootPanel.get("modelContainer").add(modelField);
		RootPanel.get("sendButtonContainer").add(sendButton);
		RootPanel.get("outputContainer").add(outputField);

		// Focus the cursor on the name field when the app loads
		modelField.setFocus(true);
		modelField.selectAll();
		modelField.setWidth("90%");
		modelField.setHeight("30em");

		// Create a handler for the sendButton and nameField
		class MyHandler implements ClickHandler {
			/**
			 * Fired when the user clicks on the sendButton.
			 */
			public void onClick(ClickEvent event) {
				sendNameToServer();
			}

			/**
			 * Send the name from the nameField to the server and wait for a response.
			 */
			private void sendNameToServer() {
				// First, we validate the input.
				try {
					String model = modelField.getText();
					outputField.setText("% compiling...");
					String result = new MiniBrassCompiler().compile(model);
					outputField.setText(result);
				} catch (MiniBrassParseException e) {
					outputField.setText("% " + e.toString());
				}
			}
		}

		// Add a handler to send the name to the server
		MyHandler handler = new MyHandler();
		sendButton.addClickHandler(handler);
	}
}
