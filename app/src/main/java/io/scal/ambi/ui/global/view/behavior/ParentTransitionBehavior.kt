package io.scal.ambi.ui.global.view.behavior

import android.support.design.widget.CoordinatorLayout
import android.view.View

interface ParentTransitionBehavior {

    fun onDependenciesViewChanged(coordinatorLayout: CoordinatorLayout, child: View, dependencies: List<View>): Boolean
}