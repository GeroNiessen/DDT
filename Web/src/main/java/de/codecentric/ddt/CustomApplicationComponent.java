package de.codecentric.ddt;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Component;

public interface CustomApplicationComponent extends Component {
	void setApplication(BeanItem<de.codecentric.ddt.Application> application);
}
