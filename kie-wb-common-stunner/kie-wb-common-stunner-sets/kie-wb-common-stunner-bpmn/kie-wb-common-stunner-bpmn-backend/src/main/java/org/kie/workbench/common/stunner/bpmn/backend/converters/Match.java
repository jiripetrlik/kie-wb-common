/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.converters;

import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Function;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

/**
 * Creates a pattern matching function.
 * <p>
 * Example usage:
 * <p>
 * <pre>
 *     // let be T1 a class, and let be T1a, T1b subclasses of T1
 *     // then, let be T2 a class, and let be T2a, T2b subclasses of T2
 *     // such that T1a corresponds to T2a, T1b corresponds to T2b:
 *     Match<T1, T2> m =
 *         Match.of(T1.class, T2.class)
 *           .when(T1a.class, t1aInstance -> ... create an equivalent t2a instance)
 *           .when(T1b.class, t1bInstance -> ... create an equivalent t2b instance)
 *     T1 myT1 = ...;
 *
 *     Result<T2> result = myT1.apply(myT1);
 *     // unwrap the result on success, raise an exception otherwise
 *     T2 t2 = result.value();
 * </pre>
 * @param <In> the input type of the match
 * @param <Out> the type of the result of the match
 */
public class Match<In, Out> {

    private final Class<?> outputType;
    private final LinkedList<Match.Case<?, Out>> cases = new LinkedList<>();
    private Function<In, Out> orElse;

    public Match(Class<?> outputType) {
        this.outputType = outputType;
    }

    public static <In, Out> Match<In, Out> of(Class<In> inputType, Class<Out> outputType) {
        return new Match<>(outputType);
    }

    public static <In, Out> Match<In, Node<? extends View<? extends Out>, ?>> ofNode(Class<In> inputType, Class<Out> outputType) {
        return new Match<>(outputType);
    }

    public static <In, Out> Match<Node<? extends View<? extends In>, ?>, Out> fromNode(Class<In> inputType, Class<Out> outputType) {
        return new Match<>(outputType);
    }

    public static <In, Out> Match<In, Edge<? extends View<? extends Out>, ?>> ofEdge(Class<In> inputType, Class<Out> outputType) {
        return new Match<>(outputType);
    }

    private static <T, U> Function<T, Result<U>> reportMissing(Class<?> expectedClass) {
        return t ->
                Result.failure(
                        "Not yet implemented: " +
                                Optional.ofNullable(t)
                                        .map(o -> o.getClass().getCanonicalName())
                                        .orElse("null -- expected " + expectedClass.getCanonicalName()));
    }

    private static <T, U> Function<T, Result<U>> ignored(Class<?> expectedClass) {
        return t ->
                Result.ignored(
                        "Ignored: " +
                                Optional.ofNullable(t)
                                        .map(o -> o.getClass().getCanonicalName())
                                        .orElse("null -- expected " + expectedClass.getCanonicalName()));
    }

    public <Sub> Match<In, Out> when(Class<Sub> type, Function<Sub, Out> then) {
        Function<Sub, Result<Out>> thenWrapped = sub -> Result.of(then.apply(sub));
        return when_(type, thenWrapped);
    }

    private <Sub> Match<In, Out> when_(Class<Sub> type, Function<Sub, Result<Out>> then) {
        cases.add(new Match.Case<>(type, then));
        return this;
    }

    /**
     * handle a type by throwing an error.
     * Use when the implementation is still missing, but expected to exist
     */
    public <Sub> Match<In, Out> missing(Class<Sub> type) {
        return when_(type, reportMissing(type));
    }

    public <Sub> Match<In, Out> ignore(Class<Sub> type) {
        return when_(type, ignored(type));
    }

    public Match<In, Out> orElse(Function<In, Out> then) {
        this.orElse = then;
        return this;
    }

    public Result<Out> apply(In value) {
        return cases.stream()
                .map(c -> c.match(value))
                .filter(Result::nonFailure)
                .findFirst()
                .orElse(applyFallback(value));
    }

    private Result<Out> applyFallback(In value) {
        if (orElse == null) {
            return Result.failure(value == null ? "Null" : value.getClass().getName());
        } else {
            return Result.of(orElse.apply(value));
        }
    }

    private static class Case<T, R> {

        public final Class<T> when;
        public final Function<T, Result<R>> then;

        private Case(Class<T> when, Function<T, Result<R>> then) {
            this.when = when;
            this.then = then;
        }

        public Result<R> match(Object value) {
            return when.isAssignableFrom(value.getClass()) ?
                    then.apply((T) value) : Result.failure(value.getClass().getName());
        }
    }
}
