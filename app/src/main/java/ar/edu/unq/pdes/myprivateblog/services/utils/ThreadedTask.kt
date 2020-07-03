package ar.edu.unq.pdes.myprivateblog.services.utils

import android.os.Handler
import android.os.Looper
import android.os.Message
import java.lang.Exception
import java.util.concurrent.Executor

class ThreadedTask<T> {
    private val onSuccess = mutableListOf<(T) -> Unit>()
    private val onFailure = mutableListOf<(String) -> Unit>()
    private val onComplete = mutableListOf<() -> Unit>()

    fun addOnSuccess(handler: (T) -> Unit)      : ThreadedTask<T> { onSuccess.add(handler); return this; }
    fun addOnFailure(handler: (String) -> Unit) : ThreadedTask<T> { onFailure.add(handler); return this; }
    fun addOnComplete(handler: () -> Unit)      : ThreadedTask<T> { onComplete.add(handler);return this; }

    /**
     * Performs the passed code in a threaded context and executes Success/Failure/Complete handler respectively on the calling thread.
     * If any (uncaught) exception is triggered, the task is considered 'failed'.
     * Call this method last in the chain to avoid race conditions while adding the handlers.
     *
     */
    fun execute(executor: Executor, code: () -> T)
    {
        val handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                publishResult(msg.what, msg.obj)
            }
        }

        executor.execute {
            try {
                handler.obtainMessage(TASK_SUCCESS, code()).sendToTarget()
            } catch (exception: Exception) {
                handler.obtainMessage(TASK_FAILED, exception.toString()).sendToTarget()
            }
        }
    }

    private fun publishResult(returnCode: Int, returnValue: Any)
    {
        if (returnCode == TASK_FAILED)
            onFailure.forEach { it(returnValue as String) }
        else
            onSuccess.forEach { it(returnValue as T) }
        onComplete.forEach { it() }

        // Removes all handlers, cleaning up potential retain cycles.
        onFailure.clear()
        onSuccess.clear()
        onComplete.clear()
    }

    companion object {
        private const val TASK_SUCCESS = 0
        private const val TASK_FAILED  = 1
    }
}