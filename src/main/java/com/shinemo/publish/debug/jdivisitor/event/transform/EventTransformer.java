/*
 * JDIVisitor
 * Copyright (C) 2014  Adrian Herrera
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.shinemo.publish.debug.jdivisitor.event.transform;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import com.shinemo.publish.debug.jdivisitor.event.VisitableAccessWatchpointEvent;
import com.shinemo.publish.debug.jdivisitor.event.VisitableBreakpointEvent;
import com.shinemo.publish.debug.jdivisitor.event.VisitableClassPrepareEvent;
import com.shinemo.publish.debug.jdivisitor.event.VisitableClassUnloadEvent;
import com.shinemo.publish.debug.jdivisitor.event.VisitableExceptionEvent;
import com.shinemo.publish.debug.jdivisitor.event.VisitableLocatableEvent;
import com.shinemo.publish.debug.jdivisitor.event.VisitableMethodEntryEvent;
import com.shinemo.publish.debug.jdivisitor.event.VisitableMethodExitEvent;
import com.shinemo.publish.debug.jdivisitor.event.VisitableModificationWatchpointEvent;
import com.shinemo.publish.debug.jdivisitor.event.VisitableMonitorContendedEnterEvent;
import com.shinemo.publish.debug.jdivisitor.event.VisitableMonitorContendedEnteredEvent;
import com.shinemo.publish.debug.jdivisitor.event.VisitableMonitorWaitEvent;
import com.shinemo.publish.debug.jdivisitor.event.VisitableMonitorWaitedEvent;
import com.shinemo.publish.debug.jdivisitor.event.VisitableStepEvent;
import com.shinemo.publish.debug.jdivisitor.event.VisitableThreadDeathEvent;
import com.shinemo.publish.debug.jdivisitor.event.VisitableThreadStartEvent;
import com.shinemo.publish.debug.jdivisitor.event.VisitableVMDeathEvent;
import com.shinemo.publish.debug.jdivisitor.event.VisitableVMDisconnectEvent;
import com.shinemo.publish.debug.jdivisitor.event.VisitableVMStartEvent;
import com.shinemo.publish.debug.jdivisitor.event.VisitableWatchpointEvent;
import com.shinemo.publish.debug.jdivisitor.event.visitor.Visitable;
import com.sun.jdi.event.AccessWatchpointEvent;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.ClassUnloadEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.ExceptionEvent;
import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.ModificationWatchpointEvent;
import com.sun.jdi.event.MonitorContendedEnterEvent;
import com.sun.jdi.event.MonitorContendedEnteredEvent;
import com.sun.jdi.event.MonitorWaitEvent;
import com.sun.jdi.event.MonitorWaitedEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.ThreadDeathEvent;
import com.sun.jdi.event.ThreadStartEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.VMStartEvent;
import com.sun.jdi.event.WatchpointEvent;

/**
 * Transforms a JDI event to a {@code Visitable} event.
 *
 * <p>
 * Once a JDI event has been transformed into a {@code Visitable} event, it can
 * be "visited" (or handled) by an {@code EventVisitor}.
 * </p>
 *
 * @author Adrian Herrera
 */
public final class EventTransformer {

    /**
     * Stores the mapping of JDI events to visitable events.
     */
    private static final Map<Class<? extends Event>, Class<? extends Visitable>> eventMap;

    // Initialise the mapping
    static {
        eventMap = new HashMap<Class<? extends Event>, Class<? extends Visitable>>();

        eventMap.put(AccessWatchpointEvent.class,
                VisitableAccessWatchpointEvent.class);
        eventMap.put(BreakpointEvent.class, VisitableBreakpointEvent.class);
        eventMap.put(ClassPrepareEvent.class, VisitableClassPrepareEvent.class);
        eventMap.put(ClassUnloadEvent.class, VisitableClassUnloadEvent.class);
        eventMap.put(ExceptionEvent.class, VisitableExceptionEvent.class);
        eventMap.put(LocatableEvent.class, VisitableLocatableEvent.class);
        eventMap.put(MethodEntryEvent.class, VisitableMethodEntryEvent.class);
        eventMap.put(MethodExitEvent.class, VisitableMethodExitEvent.class);
        eventMap.put(ModificationWatchpointEvent.class,
                VisitableModificationWatchpointEvent.class);
        eventMap.put(MonitorContendedEnteredEvent.class,
                VisitableMonitorContendedEnteredEvent.class);
        eventMap.put(MonitorContendedEnterEvent.class,
                VisitableMonitorContendedEnterEvent.class);
        eventMap.put(MonitorWaitedEvent.class,
                VisitableMonitorWaitedEvent.class);
        eventMap.put(MonitorWaitEvent.class, VisitableMonitorWaitEvent.class);
        eventMap.put(StepEvent.class, VisitableStepEvent.class);
        eventMap.put(ThreadDeathEvent.class, VisitableThreadDeathEvent.class);
        eventMap.put(ThreadStartEvent.class, VisitableThreadStartEvent.class);
        eventMap.put(VMDeathEvent.class, VisitableVMDeathEvent.class);
        eventMap.put(VMDisconnectEvent.class, VisitableVMDisconnectEvent.class);
        eventMap.put(VMStartEvent.class, VisitableVMStartEvent.class);
        eventMap.put(WatchpointEvent.class, VisitableWatchpointEvent.class);
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private EventTransformer() {
    }

    /**
     * Transforms a JDI event to a visitable event.
     *
     * @param event JDI event
     * @return The corresponding visitable event
     */
    public static Visitable transform(Event event) {
        // This assumes that an event implements only one interface
        Class<?> eventClass = event.getClass().getInterfaces()[0];
        Class<? extends Visitable> visitableEventClass = eventMap
                .get(eventClass);

        try {
            Constructor<? extends Visitable> constructor = visitableEventClass
                    .getConstructor(eventClass);
            return constructor.newInstance(event);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
