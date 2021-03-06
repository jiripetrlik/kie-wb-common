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

package org.kie.workbench.common.stunner.client.widgets.toolbar.impl;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.client.widgets.toolbar.ToolbarView;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ClearStatesToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ClearToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.CopyToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.CutToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.DeleteSelectionToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToJpgToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToPdfToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToPngToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToSvgToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.PasteToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.RedoToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.SwitchGridToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ToolbarCommandFactory;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.UndoToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ValidateToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.VisitGraphToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.item.AbstractToolbarItem;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientFullSession;

public class EditorToolbar extends AbstractToolbar<AbstractClientFullSession> {

    protected final ManagedInstance<AbstractToolbarItem<AbstractClientFullSession>> items;
    protected final ToolbarCommandFactory commandFactory;

    public EditorToolbar(final ToolbarCommandFactory commandFactory,
                         final ManagedInstance<AbstractToolbarItem<AbstractClientFullSession>> items,
                         final ToolbarView<AbstractToolbar> view) {
        super(view);
        this.commandFactory = commandFactory;
        this.items = items;
        addDefaultCommands();
    }

    @SuppressWarnings("unchecked")
    protected void addDefaultCommands() {
        addCommand(VisitGraphToolbarCommand.class, commandFactory.newVisitGraphCommand());
        addCommand(ClearToolbarCommand.class, commandFactory.newClearCommand());
        addCommand(ClearStatesToolbarCommand.class, commandFactory.newClearStatesCommand());
        addCommand(DeleteSelectionToolbarCommand.class, commandFactory.newDeleteSelectedElementsCommand());
        addCommand(SwitchGridToolbarCommand.class, commandFactory.newSwitchGridCommand());
        addCommand(UndoToolbarCommand.class, commandFactory.newUndoCommand());
        addCommand(RedoToolbarCommand.class, commandFactory.newRedoCommand());
        addCommand(ValidateToolbarCommand.class, commandFactory.newValidateCommand());
        addCommand(ExportToPngToolbarCommand.class, commandFactory.newExportToPngToolbarCommand());
        addCommand(ExportToJpgToolbarCommand.class, commandFactory.newExportToJpgToolbarCommand());
        addCommand(ExportToSvgToolbarCommand.class, commandFactory.newExportToSvgToolbarCommand());
        addCommand(ExportToPdfToolbarCommand.class, commandFactory.newExportToPdfToolbarCommand());
        addCommand(CopyToolbarCommand.class, commandFactory.newCopyCommand());
        addCommand(CutToolbarCommand.class, commandFactory.newCutToolbarCommand());
        addCommand(PasteToolbarCommand.class, commandFactory.newPasteCommand());
    }

    @Override
    protected AbstractToolbarItem<AbstractClientFullSession> newToolbarItem() {
        return items.get();
    }

    public VisitGraphToolbarCommand getVisitGraphToolbarCommand() {
        return getCommand(VisitGraphToolbarCommand.class);
    }

    public ClearToolbarCommand getClearToolbarCommand() {
        return getCommand(ClearToolbarCommand.class);
    }

    public ClearStatesToolbarCommand getClearStatesToolbarCommand() {
        return getCommand(ClearStatesToolbarCommand.class);
    }

    public DeleteSelectionToolbarCommand getDeleteSelectionToolbarCommand() {
        return getCommand(DeleteSelectionToolbarCommand.class);
    }

    public SwitchGridToolbarCommand getSwitchGridToolbarCommand() {
        return getCommand(SwitchGridToolbarCommand.class);
    }

    public UndoToolbarCommand getUndoToolbarCommand() {
        return getCommand(UndoToolbarCommand.class);
    }

    public RedoToolbarCommand getRedoToolbarCommand() {
        return getCommand(RedoToolbarCommand.class);
    }

    public ValidateToolbarCommand getValidateToolbarCommand() {
        return getCommand(ValidateToolbarCommand.class);
    }

    public ExportToPngToolbarCommand getExportToPngToolbarCommand() {
        return getCommand(ExportToPngToolbarCommand.class);
    }

    public ExportToJpgToolbarCommand getExportToJpgToolbarCommand() {
        return getCommand(ExportToJpgToolbarCommand.class);
    }

    public ExportToSvgToolbarCommand getExportToSvgToolbarCommand() {
        return getCommand(ExportToSvgToolbarCommand.class);
    }

    public ExportToPdfToolbarCommand getExportToPdfToolbarCommand() {
        return getCommand(ExportToPdfToolbarCommand.class);
    }

    public CopyToolbarCommand getCopyToolbarCommand() {
        return getCommand(CopyToolbarCommand.class);
    }

    public CutToolbarCommand getCutToolbarCommand() {
        return getCommand(CutToolbarCommand.class);
    }

    public PasteToolbarCommand getPasteToolbarCommand() {
        return getCommand(PasteToolbarCommand.class);
    }
}
