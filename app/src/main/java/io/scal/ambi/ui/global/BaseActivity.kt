package io.scal.ambi.ui.global

import android.app.Fragment
import android.arch.lifecycle.LifecycleRegistry
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasFragmentInjector
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject


abstract class BaseActivity : AppCompatActivity(), HasFragmentInjector {

    @Inject
    internal lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    private lateinit var mLifecycleRegistry: LifecycleRegistry

    protected val dectroyDisposables: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        mLifecycleRegistry = LifecycleRegistry(this)
    }

    override fun fragmentInjector(): AndroidInjector<Fragment> = fragmentDispatchingAndroidInjector
}