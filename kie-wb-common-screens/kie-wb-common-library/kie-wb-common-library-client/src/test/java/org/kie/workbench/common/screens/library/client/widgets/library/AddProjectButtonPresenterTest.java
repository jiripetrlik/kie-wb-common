/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.library.client.widgets.library;

import java.util.ArrayList;
import java.util.List;

import org.guvnor.common.services.project.client.security.ProjectController;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.screens.project.AddProjectPopUpPresenter;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.library.client.widgets.common.MenuResourceHandlerWidget;
import org.kie.workbench.common.widgets.client.handlers.NewWorkspaceProjectHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AddProjectButtonPresenterTest {

    @Mock
    private AddProjectButtonPresenter.View view;

    @Mock
    private ManagedInstance<AddProjectPopUpPresenter> addProjectPopUpPresenters;

    @Mock
    private ManagedInstance<MenuResourceHandlerWidget> menuResourceHandlerWidgets;

    @Mock
    private ManagedInstance<NewWorkspaceProjectHandler> newProjectHandlers;

    @Mock
    private org.kie.workbench.common.screens.projecteditor.client.handlers.NewWorkspaceProjectHandler newDefaultProjectHandler;

    @Mock
    private NewResourcePresenter newResourcePresenter;

    @Mock
    private ProjectController projectController;

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private AddProjectPopUpPresenter addProjectPopUpPresenter;

    @Mock
    private MenuResourceHandlerWidget menuResourceHandlerWidget;

    private AddProjectButtonPresenter presenter;

    @Before
    public void setup() {
        doReturn(true).when(projectController).canCreateProjects();
        doReturn(addProjectPopUpPresenter).when(addProjectPopUpPresenters).get();
        doReturn(menuResourceHandlerWidget).when(menuResourceHandlerWidgets).get();

        presenter = spy(new AddProjectButtonPresenter(view,
                                                      addProjectPopUpPresenters,
                                                      menuResourceHandlerWidgets,
                                                      newProjectHandlers,
                                                      newDefaultProjectHandler,
                                                      newResourcePresenter,
                                                      projectController,
                                                      libraryPlaces));
    }

    @Test
    public void initTest() {
        NewWorkspaceProjectHandler otherNewWorkspaceProjectHandler1 = mock(NewWorkspaceProjectHandler.class);
        doReturn(true).when(otherNewWorkspaceProjectHandler1).canCreate();
        NewWorkspaceProjectHandler otherNewWorkspaceProjectHandler2 = mock(NewWorkspaceProjectHandler.class);
        doReturn(false).when(otherNewWorkspaceProjectHandler2).canCreate();

        List<NewResourceHandler> handlers = new ArrayList<>();
        handlers.add(newDefaultProjectHandler);
        handlers.add(otherNewWorkspaceProjectHandler1);
        handlers.add(otherNewWorkspaceProjectHandler2);
        doReturn(handlers).when(presenter).getNewProjectHandlers();

        presenter.init();

        verify(view,
               never()).hideOtherProjects();
        verify(presenter,
               times(1)).addNewProjectHandler(any());
        verify(presenter).addNewProjectHandler(otherNewWorkspaceProjectHandler1);
        verify(view,
               times(1)).addOtherProject(any());
    }

    @Test
    public void initWithoutOtherProjectsTest() {
        List<NewResourceHandler> handlers = new ArrayList<>();
        doReturn(handlers).when(presenter).getNewProjectHandlers();

        presenter.init();

        verify(view).hideOtherProjects();
        verify(presenter,
               never()).addNewProjectHandler(any());
        verify(view,
               never()).addOtherProject(any());
    }

    @Test
    public void addProjectWithPermissionTest() {
        presenter.addProject();

        verify(addProjectPopUpPresenter).show();
    }

    @Test
    public void addProjectWithoutPermissionTest() {
        doReturn(false).when(projectController).canCreateProjects();

        presenter.addProject();

        verify(addProjectPopUpPresenter,
               never()).show();
    }
}
