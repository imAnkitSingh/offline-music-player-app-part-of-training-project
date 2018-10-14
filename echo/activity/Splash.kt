package com.internshala.echo.activity

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.widget.Toast
import com.internshala.echo.R

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
class Splash : AppCompatActivity() {


    var permissionString =
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.MODIFY_AUDIO_SETTINGS,
                    Manifest.permission.PROCESS_OUTGOING_CALLS,
                    Manifest.permission.RECORD_AUDIO)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (!haspermission(this@Splash, *permissionString)) {
            ActivityCompat.requestPermissions(this@Splash, permissionString, 131)
        } else {
            Handler().postDelayed({
                var startAct = Intent(this@Splash, MainActivity::class.java)
                startActivity(startAct)
                this.finish()
            }, 1000)
        }
    }

    fun haspermission(context: Context, vararg permissions: String): Boolean {
        var haspermission = true
        for (permission in permissions) {
            var res = context.checkCallingOrSelfPermission(permission)
            if (res != PackageManager.PERMISSION_GRANTED) {

                haspermission = false
            }
        }
        return haspermission
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            131 -> {
                if (grantResults.isNotEmpty() &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[3] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[4] == PackageManager.PERMISSION_GRANTED) {
                    Handler().postDelayed({
                        var startAct = Intent(this@Splash, MainActivity::class.java)
                        startActivity(startAct)
                        this.finish()
                    }, 1000)

                } else {
                    Toast.makeText(this@Splash, "please grant all permission", Toast.LENGTH_SHORT).show()

                    this.finish()
                }
                return
            }

            else -> {
                Toast.makeText(this@Splash, "something went wrong", Toast.LENGTH_SHORT).show()
                this.finish()
                return
            }
        }
    }
}
