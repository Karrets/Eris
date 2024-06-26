package space.kodirex.eris

import android.app.Application
import com.google.android.material.color.DynamicColors

class ErisApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}