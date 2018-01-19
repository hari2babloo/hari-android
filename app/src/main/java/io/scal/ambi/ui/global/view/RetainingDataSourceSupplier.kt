package io.scal.ambi.ui.global.view

import com.facebook.common.executors.CallerThreadExecutor
import com.facebook.common.internal.Supplier
import com.facebook.datasource.AbstractDataSource
import com.facebook.datasource.DataSource
import com.facebook.datasource.DataSubscriber
import java.util.*
import javax.annotation.concurrent.GuardedBy
import javax.annotation.concurrent.NotThreadSafe
import javax.annotation.concurrent.ThreadSafe

@NotThreadSafe
class RetainingDataSourceSupplier<T> : Supplier<DataSource<T>> {

    private val mDataSources = Collections.newSetFromMap(WeakHashMap<RetainingDataSource, Boolean>())

    private var mCurrentDataSourceSupplier: Supplier<DataSource<T>>? = null

    override fun get(): DataSource<T> {
        val dataSource = RetainingDataSource()
        dataSource.setSupplier(mCurrentDataSourceSupplier)
        mDataSources.add(dataSource)
        return dataSource
    }

    fun setSupplier(supplier: Supplier<DataSource<T>>) {
        mCurrentDataSourceSupplier = supplier
        for (dataSource in mDataSources) {
            dataSource.setSupplier(supplier)
        }
    }

    @ThreadSafe
    private inner class RetainingDataSource : AbstractDataSource<T>() {
        @GuardedBy("RetainingDataSource.this")
        private var mDataSource: DataSource<T>? = null

        fun setSupplier(supplier: Supplier<DataSource<T>>?) {
            // early return without calling {@code supplier.get()} in case we are closed
            if (isClosed) {
                return
            }
            var oldDataSource: DataSource<T>? = null
            var newDataSource: DataSource<T>? = supplier?.get()
            synchronized(this@RetainingDataSource) {
                if (isClosed) {
                    oldDataSource = newDataSource
                    newDataSource = null
                } else {
                    oldDataSource = mDataSource
                    mDataSource = newDataSource
                }
            }
            if (newDataSource != null) {
                newDataSource!!.subscribe(InternalDataSubscriber(), CallerThreadExecutor.getInstance())
            }
            closeSafely(oldDataSource)
        }

        @Synchronized override fun getResult(): T? {
            return if (mDataSource != null) mDataSource!!.result else null
        }

        @Synchronized override fun hasResult(): Boolean {
            return mDataSource != null && mDataSource!!.hasResult()
        }

        override fun close(): Boolean {
            var dataSource: DataSource<T>? = null
            synchronized(this@RetainingDataSource) {
                // it's fine to call {@code super.close()} within a synchronized block because we don't
                // implement {@link #closeResult()}, but perform result closing ourselves.
                if (!super.close()) {
                    return false
                }
                dataSource = mDataSource
                mDataSource = null
            }
            closeSafely(dataSource)
            return true
        }

        private fun onDataSourceNewResult(dataSource: DataSource<T>) {
            if (dataSource === mDataSource) {
                setResult(null, false)
            }
        }

        private fun onDataSourceFailed(dataSource: DataSource<T>) {
            // do not propagate failure
        }

        private fun onDatasourceProgress(dataSource: DataSource<T>) {
            if (dataSource === mDataSource) {
                progress = dataSource.progress
            }
        }

        private fun closeSafely(dataSource: DataSource<T>?) {
            dataSource?.close()
        }

        private inner class InternalDataSubscriber : DataSubscriber<T> {
            override fun onNewResult(dataSource: DataSource<T>) {
                if (dataSource.hasResult()) {
                    this@RetainingDataSource.onDataSourceNewResult(dataSource)
                } else if (dataSource.isFinished) {
                    this@RetainingDataSource.onDataSourceFailed(dataSource)
                }
            }

            override fun onFailure(dataSource: DataSource<T>) {
                this@RetainingDataSource.onDataSourceFailed(dataSource)
            }

            override fun onCancellation(dataSource: DataSource<T>) {}

            override fun onProgressUpdate(dataSource: DataSource<T>) {
                this@RetainingDataSource.onDatasourceProgress(dataSource)
            }
        }
    }
}