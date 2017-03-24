/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.cm.client.canvas.controls.drag;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CaseManagementDragControlImplTest {

    @Mock
    private CanvasCommandFactory<AbstractCanvasHandler> commandFactory;

    private CaseManagementDragControlImpl control;

    @Before
    public void setup() {
        this.control = spy(new CaseManagementDragControlImpl(commandFactory));
    }

    @Test
    public void testDoDragStart() {
        final Node<View<?>, Edge> e = makeElement("uuid",
                                                  "content",
                                                  10.0,
                                                  20.0,
                                                  25.0,
                                                  50.0);
        control.doDragStart(e);

        assertEquals(25.0,
                     control.getDragShapeSize()[0],
                     0.0);
        assertEquals(50.0,
                     control.getDragShapeSize()[1],
                     0.0);
    }

    @Test
    public void testDoDragUpdate() {
        final Node<View<?>, Edge> e = makeElement("uuid",
                                                  "content",
                                                  10.0,
                                                  20.0,
                                                  25.0,
                                                  50.0);

        control.doDragUpdate(e);

        verify(control,
               never()).ensureDragConstraints(any(ShapeView.class));
    }

    @Test
    public void testDoDragEnd() {
        final Node<View<?>, Edge> e = makeElement("uuid",
                                                  "content",
                                                  10.0,
                                                  20.0,
                                                  25.0,
                                                  50.0);
        control.doDragEnd(e);

        assertEquals(10.0,
                     e.getContent().getBounds().getUpperLeft().getX(),
                     0.0);
        assertEquals(20.0,
                     e.getContent().getBounds().getUpperLeft().getY(),
                     0.0);
        assertEquals(35.0,
                     e.getContent().getBounds().getLowerRight().getX(),
                     0.0);
        assertEquals(70.0,
                     e.getContent().getBounds().getLowerRight().getY(),
                     0.0);
    }

    private Node<View<?>, Edge> makeElement(final String uuid,
                                            final String content,
                                            final double x,
                                            final double y,
                                            final double w,
                                            final double h) {
        final Bounds bounds = new BoundsImpl(new BoundImpl(x,
                                                           y),
                                             new BoundImpl(x + w,
                                                           y + h));
        final Node<View<?>, Edge> node = new NodeImpl<>(uuid);
        node.setContent(new ViewImpl<>(content,
                                       bounds));
        return node;
    }
}