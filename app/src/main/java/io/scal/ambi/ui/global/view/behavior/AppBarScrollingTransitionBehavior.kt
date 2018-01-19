package io.scal.ambi.ui.global.view.behavior

import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

@Suppress("unused")
class AppBarScrollingTransitionBehavior : AppBarLayout.ScrollingViewBehavior {

    constructor() : super()
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        return child is ViewGroup || super.layoutDependsOn(parent, child, dependency)
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        var result = false
        if (super.layoutDependsOn(parent, child, dependency)) {
            result = super.onDependentViewChanged(parent, child, dependency)
        }
        if (child is ViewGroup) {
            var i = 0
            val count = child.childCount
            while (i < count) {
                val childView = child.getChildAt(i)
                if (childView is CoordinatorLayout) {
                    notifyCoordinatorLayout(childView, parent.getDependencies(child))
                }
                ++i
            }
        }
        return result
    }

    private fun notifyCoordinatorLayout(coordinatorLayout: CoordinatorLayout, dependencies: List<View>): Boolean {
        var result = false
        var i = 0
        val count = coordinatorLayout.childCount
        while (i < count) {
            val child = coordinatorLayout.getChildAt(i)
            val layoutParams = child.layoutParams as? CoordinatorLayout.LayoutParams
            if (null != layoutParams) {
                val behavior = layoutParams.behavior
                if (behavior is ParentTransitionBehavior) {
                    result = (behavior as ParentTransitionBehavior).onDependenciesViewChanged(coordinatorLayout, child, dependencies) || result
                }
            }
            ++i
        }
        return result
    }

}