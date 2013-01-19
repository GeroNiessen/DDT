package de.codecentric.ddt.web;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.github.wolfie.refresher.Refresher;
import com.github.wolfie.refresher.Refresher.RefreshListener;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

import com.vaadin.ui.VerticalLayout;


public class LogComponent extends CustomComponent {

	private static final long serialVersionUID = 2230437374249008072L;

	public LogComponent(){
		setSizeFull();
		final Panel logPanel = new Panel("Log");
				
		logPanel.setScrollable(true);
		logPanel.setSizeFull();
		
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		
		layout.addComponent(logPanel);
		layout.setExpandRatio(logPanel, 1.0f);
		
		final Label logView = new Label("", Label.CONTENT_PREFORMATTED);
		logPanel.addComponent(logView);

		final Refresher refresher = new Refresher();
		refresher.addListener(new RefreshListener() {
			private static final long serialVersionUID = -3059855849829483172L;

			public void refresh(Refresher source) {

				logPanel.setScrollTop(1000000);
				logPanel.requestRepaint();
				source.setRefreshInterval(0); // Disable
			}
		});
		layout.addComponent(refresher);

		final Logger logger = Logger.getLogger("");
		logger.addHandler(new Handler() {
			@Override
			public void publish(LogRecord record) {
				if (! isLoggable(record))
					return;

				if (getFormatter() == null)
					setFormatter(new SimpleFormatter());

				String content = (String) logView.getValue();
				String entry = getFormatter().format(record);
				String newContent = content + "\n" + entry;
				logView.setValue(newContent);

				// Scroll to the end + hack
				logPanel.setScrollTop(1000000); // Keep it at the end
				refresher.setRefreshInterval(100); // Enable
			}

			@Override
			public void flush() {
			}

			@Override
			public void close() throws SecurityException {
			}
		});

		layout.addComponent(new Button("Clear Log", new Button.ClickListener() {
			private static final long serialVersionUID = -7229654113202510280L;

			public void buttonClick(ClickEvent event) {
				logView.setValue("");
			}
		}));

		setCompositionRoot(layout);
	}
}