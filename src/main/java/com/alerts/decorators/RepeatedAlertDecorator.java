package com.alerts.decorators;

import com.alerts.AlertInterface;

/**
 * Decorator that adds functionality for alerts that need to be rechecked or repeated over a set interval.
 */
public class RepeatedAlertDecorator extends AlertDecorator {
    public RepeatedAlertDecorator(AlertInterface alert) {
        super(alert);
    }

    @Override
    public String getCondition() {
        return decoratedAlert.getCondition() + " (REPEATED)";
    }
}
