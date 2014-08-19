/*
 * Copyright (C) 2013 INRIA
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package fr.inria.lille.commons.spoon.collectable;

import static java.util.Arrays.asList;

import java.util.Collection;

import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.CompositeFilter;
import spoon.reflect.visitor.filter.FilteringOperator;
import xxl.java.container.classic.MetaSet;
import fr.inria.lille.commons.spoon.filter.BeforeLocationFilter;
import fr.inria.lille.commons.spoon.filter.InBlockFilter;
import fr.inria.lille.commons.spoon.filter.VariableAssignmentFilter;
import fr.inria.lille.commons.spoon.util.SpoonElementLibrary;

public class CollectableValueFinder {
	
	public static CollectableValueFinder firstInstance() {
		/* Refer to Singleton.createSingleton() */
		return new CollectableValueFinder();
	}
	
	protected CollectableValueFinder() {}
	
	public Collection<String> findFromStatement(CtStatement statement) {
		Collection<String> collectables = MetaSet.newHashSet();
		addCollectableVariables(collectables, statement);
		return collectables;
	}
	
	public Collection<String> findFromIf(CtIf ifStatement) {
		Collection<String> collectables = findFromStatement(ifStatement);
		addCollectableSubconditions(collectables, ifStatement.getCondition());
		return collectables;
	}
	
	public Collection<String> findFromWhile(CtWhile loop) {
		Collection<String> collectables = findFromStatement(loop);
		addCollectableSubconditions(collectables, loop.getLoopingExpression());
		return collectables;
	}
	
	private void addCollectableVariables(Collection<String> collectables, CtStatement statement) {
		ReachableVariableVisitor variableVisitor = new ReachableVariableVisitor(statement);
		Collection<CtVariable<?>> reachedVariables = variablesInitializedBefore(statement, variableVisitor.reachedVariables());
		collectables.addAll(variableNames(reachedVariables));
	}
	
	private void addCollectableSubconditions(Collection<String> collectables, CtExpression<Boolean> condition) {
		SubconditionVisitor extractor = new SubconditionVisitor(condition);
		collectables.addAll(extractor.subexpressions());
	}
	
	protected Collection<String> variableNames(Collection<CtVariable<?>> reachedVariables) {
		Collection<String> names = MetaSet.newHashSet();
		for (CtVariable<?> variable : reachedVariables) {
			names.add(nameFor(variable));
		}
		return names;
	}
	
	private String nameFor(CtVariable<?> variable) {
		String simpleName = variable.getSimpleName();
		if (SpoonElementLibrary.isField(variable)) {
			String declaringClass = ((CtField<?>) variable).getDeclaringType().getSimpleName();
			if (SpoonElementLibrary.hasStaticModifier(variable)) {
				simpleName = declaringClass + "." + simpleName;
			} else {
				if (declaringClass.isEmpty()) {
					/* only when 'variable' is a field of an Anonymous Class */
					simpleName = "this." + simpleName;
				} else {
					simpleName = declaringClass + ".this." + simpleName;
				}
			}
		}
		return simpleName;
	}
	
	private Collection<CtVariable<?>> variablesInitializedBefore(CtStatement statement, Collection<CtVariable<?>> reachedVariables) {
		Collection<CtVariable<?>> initializedVariables = MetaSet.newHashSet();
		for (CtVariable<?> variable : reachedVariables) {
			if (! SpoonElementLibrary.isLocalVariable(variable) || wasInitializedBefore(statement, variable)) {
				initializedVariables.add(variable);
			}
		}
		return initializedVariables;
	}
	
	private boolean wasInitializedBefore(CtStatement statement, CtVariable<?> variable) {
		if (variable.getDefaultExpression() == null) {
			CtBlock<?> block = variable.getParent(CtBlock.class);
			Filter<CtAssignment<?,?>> filter = initializationAssignmentsFilterFor(variable, statement);
			return ! block.getElements(filter).isEmpty();
		}
		return true;
	}
	
	private Filter<CtAssignment<?,?>> initializationAssignmentsFilterFor(CtVariable<?> variable, CtStatement statement) {
		VariableAssignmentFilter variableAssignment = new VariableAssignmentFilter(variable);
		BeforeLocationFilter<CtAssignment<?,?>> beforeLocation = new BeforeLocationFilter(CtAssignment.class, statement.getPosition());
		InBlockFilter<CtAssignment<?,?>> inVariableDeclarationBlock = new InBlockFilter(CtAssignment.class, asList(variable.getParent(CtBlock.class)));
		InBlockFilter<CtAssignment<?,?>> inStatementBlock = new InBlockFilter(CtAssignment.class, asList(statement.getParent(CtBlock.class)));
		Filter<CtAssignment<?,?>> inBlockFilter = new CompositeFilter(FilteringOperator.UNION, inStatementBlock, inVariableDeclarationBlock);
		return new CompositeFilter(FilteringOperator.INTERSECTION, variableAssignment, beforeLocation, inBlockFilter);
	}
}