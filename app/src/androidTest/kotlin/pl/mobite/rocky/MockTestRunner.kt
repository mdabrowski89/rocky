package pl.mobite.rocky

import android.app.Application
import android.content.Context
import android.support.test.runner.AndroidJUnitRunner


class MockTestRunner: AndroidJUnitRunner() {

    @Throws(InstantiationException::class, IllegalAccessException::class, ClassNotFoundException::class)
    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
        return super.newApplication(cl, MockRockyApp::class.java.name, context)
    }
}