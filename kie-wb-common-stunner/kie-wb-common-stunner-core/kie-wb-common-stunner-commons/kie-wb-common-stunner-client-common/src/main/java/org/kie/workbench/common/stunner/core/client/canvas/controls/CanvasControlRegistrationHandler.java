/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.canvas.controls;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jboss.errai.ioc.client.api.Disposer;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasElementListener;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasShapeListener;
import org.kie.workbench.common.stunner.core.client.command.RequiresCommandManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.graph.Element;

/**
 * Handles Canvas Control instances lifecycle.
 * @param <C> The canvas type.
 * @param <H> The canvas handler type.
 */
public class CanvasControlRegistrationHandler<C extends AbstractCanvas, H extends AbstractCanvasHandler, S extends ClientSession>
        implements RequiresCommandManager<H>,
                   CanvasControl.SessionAware<S> {

    private final List<CanvasControl<C>> canvasControls = new LinkedList<>();
    private final List<CanvasControl<H>> canvasHandlerControls = new LinkedList<>();

    private final C canvas;
    private final H handler;
    private CanvasShapeListener shapeListener;
    private CanvasElementListener elementListener;
    private CommandManagerProvider<H> commandManagerProvider;

    private Disposer<CanvasControl> disposer;

    public CanvasControlRegistrationHandler(final C canvas,
                                            final H handler,
                                            final Disposer<CanvasControl> disposer) {
        this.canvas = canvas;
        this.handler = handler;
        this.disposer = disposer;
    }

    /**
     * Registers a canvas control.
     * @param control The control instance.
     */
    public void registerCanvasControl(final CanvasControl<C> control) {
        this.canvasControls.add(control);
    }

    /**
     * Registers a canvas handler control.
     * @param control The control instance.
     */
    public void registerCanvasHandlerControl(final CanvasControl<H> control) {
        this.canvasHandlerControls.add(control);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void bind(final ClientSession session) {
        final List<CanvasControl> allControls = new ArrayList<>();
        allControls.addAll(canvasControls);
        allControls.addAll(canvasHandlerControls);
        allControls.stream()
                .filter(c -> c instanceof CanvasControl.SessionAware)
                .forEach(c -> ((CanvasControl.SessionAware) c).bind(session));
    }

    @Override
    public void unbind() {
        final List<CanvasControl> allControls = new ArrayList<>();
        allControls.addAll(canvasControls);
        allControls.addAll(canvasHandlerControls);
        allControls.stream()
                .filter(c -> c instanceof CanvasControl.SessionAware)
                .forEach(c -> ((CanvasControl.SessionAware) c).unbind());
    }

    /**
     * Enables current registered controls.
     */
    public void enable() {
        initializeListeners();
        enableControls();
    }

    /**
     * Disables current registered controls.
     */
    public void disable() {
        removeListeners();
        disableControls();
    }

    /**
     * Enables the canvas control.
     */
    public void enableCanvasControl(final CanvasControl<C> control) {
        enableControl(control, getCanvas());
    }

    /**
     * Enables the canvas handler control.
     */
    @SuppressWarnings("unchecked")
    public void enableCanvasHandlerControl(final CanvasControl<H> control) {
        enableControl(control, getCanvasHandler());
    }

    private <T> void enableControl(CanvasControl<T> control, T context) {
        if (null != control) {
            control.enable(context);
            if (null != commandManagerProvider
                    && control instanceof RequiresCommandManager) {
                ((RequiresCommandManager<H>) control).setCommandManagerProvider(commandManagerProvider);
            }
        }
    }

    /**
     * Clears the registered controls.
     * Controls can be added again and listeners will make it fire.
     */
    public void clear() {
        canvasControls.clear();
        canvasHandlerControls.clear();
    }

    /**
     * Destroys the registered controls and this instance.
     */
    public void destroy() {
        disposeControls();
        clear();
        canvas.clearRegistrationListeners();
        handler.clearRegistrationListeners();
        shapeListener = null;
        elementListener = null;
        commandManagerProvider = null;
    }

    private void disposeControls() {
        canvasControls.forEach(disposer::dispose);
        canvasHandlerControls.forEach(disposer::dispose);
    }

    @Override
    public void setCommandManagerProvider(final CommandManagerProvider<H> provider) {
        this.commandManagerProvider = provider;
    }

    private void enableControls() {
        canvasControls.forEach(this::enableCanvasControl);
        canvasHandlerControls.forEach(this::enableCanvasHandlerControl);
    }

    private void disableControls() {
        canvasControls.forEach(CanvasControl::disable);
        canvasHandlerControls.forEach(CanvasControl::disable);
    }

    private void fireRegistrationListeners(final CanvasControl<H> control,
                                           final Element element,
                                           final boolean add) {
        if (null != control && null != element && control instanceof CanvasRegistationControl) {
            final CanvasRegistationControl<H, Element> registationControl =
                    (CanvasRegistationControl<H, Element>) control;
            if (add) {
                registationControl.register(element);
            } else {
                registationControl.deregister(element);
            }
        }
    }

    private void fireRegistrationListeners(final CanvasControl<C> control,
                                           final Shape shape,
                                           final boolean add) {
        if (null != control && null != shape && control instanceof CanvasRegistationControl) {
            final CanvasRegistationControl<C, Shape> registationControl =
                    (CanvasRegistationControl<C, Shape>) control;
            if (add) {
                registationControl.register(shape);
            } else {
                registationControl.deregister(shape);
            }
        }
    }

    private void fireRegistrationUpdateListeners(final CanvasControl<H> control,
                                                 final Element element) {
        if (null != control && null != element && control instanceof AbstractCanvasHandlerRegistrationControl) {
            final AbstractCanvasHandlerRegistrationControl registationControl =
                    (AbstractCanvasHandlerRegistrationControl) control;
            registationControl.update(element);
        }
    }

    private void fireRegistrationClearListeners(final CanvasControl<H> control) {
        if (null != control && control instanceof AbstractCanvasHandlerRegistrationControl) {
            final AbstractCanvasHandlerRegistrationControl registationControl =
                    (AbstractCanvasHandlerRegistrationControl) control;
            registationControl.deregisterAll();
        }
    }

    private C getCanvas() {
        return canvas;
    }

    private H getCanvasHandler() {
        return handler;
    }

    private void initializeListeners() {
        // Canvas listeners.
        final C canvas = getCanvas();
        this.shapeListener = new CanvasShapeListener() {
            @Override
            public void register(final Shape item) {
                onRegisterShape(item);
            }

            @Override
            public void deregister(final Shape item) {
                onDeregisterShape(item);
            }

            @Override
            public void clear() {
                onClear();
            }
        };
        canvas.addRegistrationListener(shapeListener);
        // Canvas handler listeners.
        this.elementListener = new CanvasElementListener() {
            @Override
            public void update(final Element item) {
                onElementRegistration(item,
                                      false,
                                      true);
            }

            @Override
            public void register(final Element item) {
                onRegisterElement(item);
            }

            @Override
            public void deregister(final Element item) {
                onDeregisterElement(item);
            }

            @Override
            public void clear() {
                onClear();
            }
        };
        getCanvasHandler().addRegistrationListener(elementListener);
    }

    private void removeListeners() {
        if (null != shapeListener) {
            getCanvas().removeRegistrationListener(shapeListener);
        }
        if (null != elementListener) {
            getCanvasHandler().removeRegistrationListener(elementListener);
        }
    }

    private void onRegisterShape(final Shape shape) {
        onShapeRegistration(shape,
                            true);
    }

    private void onDeregisterShape(final Shape shape) {
        onShapeRegistration(shape,
                            false);
    }

    private void onRegisterElement(final Element element) {
        onElementRegistration(element,
                              true,
                              false);
    }

    private void onDeregisterElement(final Element element) {
        onElementRegistration(element,
                              false,
                              false);
    }

    private void onElementRegistration(final Element element,
                                       final boolean add,
                                       final boolean update) {
        if (update) {
            canvasHandlerControls.forEach(c -> fireRegistrationUpdateListeners(c,
                                                                               element));
        } else {
            canvasHandlerControls.forEach(c -> fireRegistrationListeners(c,
                                                                         element,
                                                                         add));
        }
    }

    private void onShapeRegistration(final Shape shape,
                                     final boolean add) {
        canvasControls.forEach(c -> fireRegistrationListeners(c,
                                                              shape,
                                                              add));
    }

    private void onClear() {
        canvasHandlerControls.forEach(this::fireRegistrationClearListeners);
    }

    protected List<CanvasControl<C>> getCanvasControls() {
        return canvasControls;
    }

    protected List<CanvasControl<H>> getCanvasHandlerControls() {
        return canvasHandlerControls;
    }
}
