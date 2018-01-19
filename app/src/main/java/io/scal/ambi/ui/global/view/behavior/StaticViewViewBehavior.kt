package io.scal.ambi.ui.global.view.behavior

import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.ambi.work.R

@Suppress("unused")
class StaticViewViewBehavior : CoordinatorLayout.Behavior<ViewGroup>, ParentTransitionBehavior {

    private var justToolbar: Boolean = true

    constructor()
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun layoutDependsOn(parent: CoordinatorLayout, child: ViewGroup, dependency: View): Boolean =
        dependency is AppBarLayout

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: ViewGroup, dependency: View?): Boolean =
        onDependenciesViewChanged(parent, child, parent.getDependencies(child))

    override fun onDependenciesViewChanged(coordinatorLayout: CoordinatorLayout, child: View, dependencies: List<View>): Boolean {
        var appBarLayout: AppBarLayout? = null
        var resultTranslation = 0f
        for (dependency in dependencies) {
            if (dependency is AppBarLayout) {
                appBarLayout = dependency
            }
        }

        if (null != appBarLayout) {
            resultTranslation += computeAppBarDependency(appBarLayout)
        }

        child.translationY = resultTranslation
        return true
    }

    private fun computeAppBarDependency(dependency: AppBarLayout): Float {
        val dependencyView = findDependencyView(dependency)
        return -(dependencyView.measuredHeight + dependency.y)
    }

    private fun findDependencyView(appBarLayout: AppBarLayout): View {
        var toolbar: View? = null
        if (justToolbar) {
            toolbar = appBarLayout.findViewById(R.id.tb)
        }
        return if (null == toolbar) appBarLayout else toolbar
    }

}