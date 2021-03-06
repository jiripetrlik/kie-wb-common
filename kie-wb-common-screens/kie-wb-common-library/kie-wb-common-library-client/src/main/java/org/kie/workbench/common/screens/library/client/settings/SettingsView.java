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

package org.kie.workbench.common.screens.library.client.settings;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLUListElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;

@Templated
public class SettingsView implements SettingsPresenter.View,
                                     IsElement {

    private SettingsPresenter presenter;

    @Inject
    private TranslationService translationService;

    @Inject
    private Elemental2DomUtil elemental2DomUtil;

    @Inject
    @DataField("save")
    private HTMLButtonElement save;

    @Inject
    @DataField("reset")
    private HTMLButtonElement reset;

    @Inject
    @DataField("content")
    private HTMLDivElement content;

    @Inject
    @DataField("menu-items-container")
    private HTMLUListElement menuItemsContainer;

    @Override
    public void init(final SettingsPresenter presenter) {
        this.presenter = presenter;
    }

    @EventHandler("save")
    public void save(final ClickEvent event) {
        presenter.showSaveModal();
    }

    @EventHandler("reset")
    public void reset(final ClickEvent event) {
        presenter.reset();
    }

    @Override
    public void setSection(final Section section) {
        elemental2DomUtil.removeAllElementChildren(content);
        content.appendChild(section.getElement());
    }

    @Override
    public HTMLElement getMenuItemsContainer() {
        return menuItemsContainer;
    }

    @Override
    public String getSaveSuccessMessage() {
        return translationService.format(LibraryConstants.SettingsSaveSuccess);
    }

    @Override
    public String getLoadErrorMessage() {
        return translationService.format(LibraryConstants.SettingsLoadError);
    }

    @Override
    public String getSectionSetupErrorMessage(final String title) {
        return translationService.format(LibraryConstants.SettingsSectionSetupError, title);
    }

    @Override
    public void hide() {
        getElement().classList.add("settings-hidden");
    }

    @Override
    public void show() {
        getElement().classList.remove("settings-hidden");
    }

    @Override
    public void showBusyIndicator() {
        showBusyIndicator(translationService.format(LibraryConstants.Loading));
    }

    @Override
    public void showBusyIndicator(final String message) {
        BusyPopup.showMessage(message);
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

    @Templated("SettingsView.html#section-menu-item")
    public static class MenuItem implements SettingsPresenter.MenuItem.View {

        @Inject
        @Named("sup")
        private HTMLElement dirtyIndicator;

        @Inject
        @DataField("section-menu-item-link")
        private HTMLAnchorElement sectionMenuItemLink;

        private SettingsPresenter.MenuItem presenter;

        @Override
        public void init(final SettingsPresenter.MenuItem presenter) {
            this.presenter = presenter;
        }

        @EventHandler("section-menu-item-link")
        public void onSectionMenuItemLinkClicked(final ClickEvent ignore) {
            presenter.showSection();
        }

        @Override
        public void setLabel(final String label) {
            sectionMenuItemLink.textContent = label;
        }

        @Override
        public void markAsDirty(final boolean dirty) {
            if (dirty && sectionMenuItemLink.childElementCount == 0) {
                sectionMenuItemLink.appendChild(newDirtyIndicator());
            } else if (!dirty && sectionMenuItemLink.childElementCount > 0) {
                sectionMenuItemLink.removeChild(sectionMenuItemLink.lastElementChild);
            }
        }

        private HTMLElement newDirtyIndicator() {
            final HTMLElement dirtyIndicator = (HTMLElement) this.dirtyIndicator.cloneNode(false);
            dirtyIndicator.textContent = " *";
            return dirtyIndicator;
        }
    }
}
