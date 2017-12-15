package io.scal.ambi.ui.global.view.behavior

import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import io.scal.ambi.R
import timber.log.Timber

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
            resultTranslation += computeAppBarDependency(child, appBarLayout)
        }

        child.translationY = resultTranslation
        return true
    }

    private fun computeAppBarDependency(child: View, dependency: AppBarLayout): Float {
        val dependencyView = findDependencyView(dependency)
        val distanceToScroll = child.measuredHeight
        val ratio = (dependencyView.measuredHeight + dependency.y) / dependencyView.measuredHeight
        val result = -distanceToScroll * ratio
        Timber.w("ratio = $ratio; distanceTOScroll = $distanceToScroll; result = $result")
        Timber.w("dependency.y = ${dependency.y}")
        return result
    }

    private fun findDependencyView(appBarLayout: AppBarLayout): View {
        var toolbar: View? = null
        if (justToolbar) {
            toolbar = appBarLayout.findViewById(R.id.tb)
        }
        return if (null == toolbar) appBarLayout else toolbar
    }

}