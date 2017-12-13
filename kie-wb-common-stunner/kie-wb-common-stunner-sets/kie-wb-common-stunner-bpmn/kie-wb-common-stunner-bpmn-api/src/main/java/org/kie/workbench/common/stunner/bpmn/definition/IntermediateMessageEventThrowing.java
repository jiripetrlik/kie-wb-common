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

package org.kie.workbench.common.stunner.bpmn.definition;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.CircleDimensionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.Radius;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.message.MessageEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Description;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Title;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.Morph;
import org.kie.workbench.common.stunner.core.definition.builder.Builder;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@Definition(graphFactory = NodeFactory.class, builder = IntermediateMessageEventThrowing.IntermediateMessageEventThrowingBuilder.class)
@Morph(base = BaseThrowingIntermediateEvent.class)
@FormDefinition(
        startElement = "general",
        policy = FieldPolicy.ONLY_MARKED
)
public class IntermediateMessageEventThrowing extends BaseThrowingIntermediateEvent {

    @Title
    public static final transient String title = "Intermediate Message Event";

    @Description
    public static final transient String description = "The throwing message event sends a message to a communication partner and afterwards continues process execution.";

    @PropertySet
    @FormField(afterElement = "general")
    @Valid
    protected MessageEventExecutionSet executionSet;

    @NonPortable
    public static class IntermediateMessageEventThrowingBuilder implements Builder<IntermediateMessageEventThrowing> {

        @Override
        public IntermediateMessageEventThrowing build() {
            return new IntermediateMessageEventThrowing(new BPMNGeneralSet(""),
                                                        new DataIOSet(),
                                                        new BackgroundSet(),
                                                        new FontSet(),
                                                        new CircleDimensionSet(new Radius()),
                                                        new MessageEventExecutionSet());
        }
    }

    public IntermediateMessageEventThrowing() {
    }

    public IntermediateMessageEventThrowing(final @MapsTo("general") BPMNGeneralSet general,
                                            final @MapsTo("dataIOSet") DataIOSet dataIOSet,
                                            final @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                                            final @MapsTo("fontSet") FontSet fontSet,
                                            final @MapsTo("dimensionsSet") CircleDimensionSet dimensionsSet,
                                            final @MapsTo("executionSet") MessageEventExecutionSet executionSet) {
        super(general,
              dataIOSet,
              backgroundSet,
              fontSet,
              dimensionsSet);
        this.executionSet = executionSet;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean hasInputVars() {
        return true;
    }

    @Override
    public boolean isSingleInputVar() {
        return true;
    }

    @Override
    protected void initLabels() {
        super.initLabels();
        labels.add("messageflow_start");
    }

    public MessageEventExecutionSet getExecutionSet() {
        return executionSet;
    }

    public void setExecutionSet(MessageEventExecutionSet executionSet) {
        this.executionSet = executionSet;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(super.hashCode(),
                                         executionSet.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof IntermediateMessageEventThrowing) {
            IntermediateMessageEventThrowing other = (IntermediateMessageEventThrowing) o;
            return super.equals(other) &&
                    executionSet.equals(other.executionSet);
        }
        return false;
    }
}