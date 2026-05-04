package com.alerts.decorators;

import com.alerts.AlertInterface;

/**
 * Decorator that adds prioritization tagging to alerts needing urgent medical attention.
 */
public class PriorityAlertDecorator extends AlertDecorator{
    private String priorityLevel;

    public PriorityAlertDecorator(AlertInterface alert, String priority) {
        super(alert);
        this.priorityLevel = priority;
    }

    @Override
    public String getCondition() {
        return "[" + priorityLevel + " PRIORITY] " + decoratedAlert.getCondition();
    }
}
