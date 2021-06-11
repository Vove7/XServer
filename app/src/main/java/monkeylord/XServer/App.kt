package monkeylord.XServer

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

/**
 * Created by liben on 2020/11/22
 */
class App : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(base)

    }
    init {
        INS=this
    }
    companion object {
        lateinit var INS :Context

    }
}