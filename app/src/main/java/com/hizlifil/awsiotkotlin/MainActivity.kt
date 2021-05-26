package com.hizlifil.awsiotkotlin

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.amazonaws.mobileconnectors.iot.*
import com.amplifyframework.AmplifyException
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.AWSDataStorePlugin
import com.google.gson.Gson
import java.lang.Thread.sleep
import kotlinx.android.synthetic.main.activity_main.*




class MainActivity : AppCompatActivity() {
    var data:ByteArray? = null
    var baseValue = 0F
    var weight = ""
    var isKeystoreExists : Boolean =false

    fun saveValueOfWeightInstance(view : View){

        textView.text=this.weight
        try {
            this.baseValue = this.weight.toFloat()
        }
        catch (e : Exception){

            e.printStackTrace()

        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            //Amplify.addPlugin(AWSApiPlugin()) // UNCOMMENT this line once backend is deployed
            Amplify.addPlugin(AWSDataStorePlugin())
            Amplify.configure(applicationContext)
            Log.i("Amplify", "Initialized Amplify")
        } catch (e: AmplifyException) {
            Log.e("Amplify", "Could not initialize Amplify", e)
        }

        var subscribe = findViewById<TextView>(R.id.subscribe)
        var alarm = findViewById<TextView>(R.id.alarm)



        val clientEndpoint = "a2coo54mlp1170-ats.iot.eu-west-1.amazonaws.com"
        val clientId = "sdk-java"


        // Initialize the AWSIotMqttManager with the configuration
        val mqttManager = AWSIotMqttManager(
                clientId,
                clientEndpoint
        )
        val path = this.filesDir
        for (i in path.listFiles()){

            isKeystoreExists = i.endsWith("AwsIotKotlinKeystore")
        }


        if (isKeystoreExists==false) {
            val keyStore = AWSIotKeystoreHelper.saveCertificateAndPrivateKey(
                    "6",
                    "-----BEGIN CERTIFICATE-----\n" +
                            "MIIDWjCCAkKgAwIBAgIVALW7XWj74PguTYE7TOyvnSx6SGUbMA0GCSqGSIb3DQEB\n" +
                            "CwUAME0xSzBJBgNVBAsMQkFtYXpvbiBXZWIgU2VydmljZXMgTz1BbWF6b24uY29t\n" +
                            "IEluYy4gTD1TZWF0dGxlIFNUPVdhc2hpbmd0b24gQz1VUzAeFw0yMTA1MDcxMzMy\n" +
                            "NDFaFw00OTEyMzEyMzU5NTlaMB4xHDAaBgNVBAMME0FXUyBJb1QgQ2VydGlmaWNh\n" +
                            "dGUwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCw8KS2KUBx+CtnprmJ\n" +
                            "lbffmrGL7Q8KBT2frnR9qnM8u/U2dIvgta6pEzkonQs8Rm6CVfHx4E5dQxOebFN7\n" +
                            "YM3GDMH/2AwzEj/g8sD5E8MvA4tthnO0a+w8Ch18GBwbqKlEGb/bhMRkHLAQUkUs\n" +
                            "y2rZPBW/TeB4L5bHxma9U4jORK88ZO9fUAAXeEj38foHEQU+mRZ0WP3viYJDjpKM\n" +
                            "E8XqzxmP2uVw7sblmTnK1vIS4+FYgFUltlAvO+1hYF1Rjwq8q4SCp+lP8rSsA+It\n" +
                            "CxiOm0NiuTVUickVS4ZIXbBjng7Vn3geeFbfogiI+0cVltT+1GoNlxA1f59u/8Hc\n" +
                            "nvgXAgMBAAGjYDBeMB8GA1UdIwQYMBaAFAgYhaugJ9fpiEbwgTWxsj0Zrky7MB0G\n" +
                            "A1UdDgQWBBQWDawVB7A8gcSDLdZXqACS07ULajAMBgNVHRMBAf8EAjAAMA4GA1Ud\n" +
                            "DwEB/wQEAwIHgDANBgkqhkiG9w0BAQsFAAOCAQEAnK5JVvqq1c0SaNxGub6jkFyN\n" +
                            "AN1EwJLPezKgv9qmKqnVQin66PjbLzrGPGk7pyV7nXjELt8aklGu4HS/RUsaku1Z\n" +
                            "mgcVtGQo9+360R5eWQ7SU0U4BT4xWvn9RCP4Orr2W6zD9afQwvrpwoWfT3G31YOW\n" +
                            "TdIRu0f8FA/fJzWROPKels31soyoTAPXTMYNo4mvhD8XhJyWtb5pECydhyKI/o4u\n" +
                            "/f5W1lLaay2tU0DHcKcyG9dJaQZQ82tbOLoRUwHLyEZFmbNpljuNbBfphKrQTVuX\n" +
                            "IKnCNbeZBWIlIGLWI/ZjAY5ci+IOvHnhfCQ9FXCqsqqYtpRFvI+JJkqZVQza9A==\n" +
                            "-----END CERTIFICATE-----", "-----BEGIN RSA PRIVATE KEY-----\n" +
                    "MIIEpAIBAAKCAQEAsPCktilAcfgrZ6a5iZW335qxi+0PCgU9n650fapzPLv1NnSL\n" +
                    "4LWuqRM5KJ0LPEZuglXx8eBOXUMTnmxTe2DNxgzB/9gMMxI/4PLA+RPDLwOLbYZz\n" +
                    "tGvsPAodfBgcG6ipRBm/24TEZBywEFJFLMtq2TwVv03geC+Wx8ZmvVOIzkSvPGTv\n" +
                    "X1AAF3hI9/H6BxEFPpkWdFj974mCQ46SjBPF6s8Zj9rlcO7G5Zk5ytbyEuPhWIBV\n" +
                    "JbZQLzvtYWBdUY8KvKuEgqfpT/K0rAPiLQsYjptDYrk1VInJFUuGSF2wY54O1Z94\n" +
                    "HnhW36IIiPtHFZbU/tRqDZcQNX+fbv/B3J74FwIDAQABAoIBAQCcd5EQTRZiMlUf\n" +
                    "yb3UvwLkx0UuOanM8Kl35au53Nse4A+N4i43vjH/7bHkCkv/N0FOHHZhsDtcGY7l\n" +
                    "z2Fbf1U4R9veRM009HRslf6zxBgcIQRRDE6RQ8aZqVA9hoyEoPdd829HzfZlmUDQ\n" +
                    "jhAB9rg79fg9Pb68SUkKzJXZ4l7eF0GqeVv8+EBYU59+3UkDxgbHdrMrGHQsWUwR\n" +
                    "knvD3E6ezMW6gKpptfuSqE+KCPNdBE7Q2yq4kkkv/84loPRZ0uzfBCxijpZKKNTe\n" +
                    "gD8eBV5WR1g9B2TKoHO9bsipN1uD9f0FiJ4OMOZT8bYz14ZSh5RXvr9t4OJ9i8s7\n" +
                    "gIhaYx4BAoGBAONfBNmmaDEEEBEBe5OFJNW6sHJWvyAhj7Kb+tfJcVAk1Da57A2i\n" +
                    "EuBMZ+D/01+zjfxRxj285xlWFcM0AmBB78NBalYN0isI+CAJtlTleEXBgLf7ZaeI\n" +
                    "kcllnmSkLFAO2mtN0yjFgtyrMl13AQgexYgsSxMONz2gRWaR2UKt+K6BAoGBAMc4\n" +
                    "DI+BdWLihEBlyudhwpTAzI/b2w0gwh/3Tox7sUhJuWJSciyMPE6Yrk1uIjr/mLPb\n" +
                    "S8+jaeZzwke7Pu9dLOmPqGzX0n9KylkXf2p/IdPRl9NcFyTMKpxMLcWBYm56cilt\n" +
                    "/mvsgE+J8jCbaN87dDcHy6Eie0mnVkuGkEYmnwqXAoGBAI4yz9q7GvCExPONxNZ2\n" +
                    "75mHn3By1idnNfKTYKbyAh8IWXw8fOft0/ZZcqIh/PUeW49RKTVVXgbXctbZQR8o\n" +
                    "PaYU1Ecrb6SggGWxDUo9FSIzKahm3qWPYPXeytfQYTJUh7+SNZyLIhOWMfKISanl\n" +
                    "bM6EPROW0W+GO8ExN/peyWeBAoGALyNY/X/BWdOFPYOYfiVImFACVyvLahDNPikT\n" +
                    "QfRmn1cqcVRWxams/1/rJlEf8lvWMwB5sLjW7vuHBdgNbS/b16vxS0fJ++qjdG81\n" +
                    "6+oHBAq85PsOUtuoXXE9B1W1B7UbXCg1oi3Eso1ObCfpqaB0wCytBwSUrXnV1msR\n" +
                    "qjerPCMCgYAzJLw7HrY0e+9mX+3h+4h70szFWEd5HUd42DPbV0rdsLSQzodR3zbm\n" +
                    "30wynjsspb8+IB1tIvKFrcyM6z7Q9ElaB7ZzATMi4C3MtW/W+K6ZsZsM/y+h/qce\n" +
                    "u7sSzUK62rbde0rJCVHhL0SR/sdhcKtw19nPgqB+pZQEl1qOVKdY6Q==\n" +
                    "-----END RSA PRIVATE KEY-----",


                    path.absolutePath,
                    "AwsIotKotlinKeystore",
                    "123456"
            )
        }

        println(AWSIotKeystoreHelper.isKeystorePresent(path.absolutePath, "AwsIotKotlinKeystore"))



        try {
            Log.d(ContentValues.TAG, "clientId = $clientId")
            mqttManager.connect(AWSIotKeystoreHelper.getIotKeystore("6", path.absolutePath, "AwsIotKotlinKeystore", "123456"), AWSIotMqttClientStatusCallback { status, throwable ->
                Log.d(ContentValues.TAG, "Status = " + status.toString())

            })
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Connection error.", e)
        }

        try {
            sleep(5000)
            mqttManager.subscribeToTopic("sdk/test/java", AWSIotMqttQos.QOS0, AWSIotMqttNewMessageCallback { topic, data ->
               val message = String(data)
                this.weight = message.substring(12).dropLast(4)


                ThreadUtils.runOnUiThread {


                    subscribe.setText(message)
                    //this.subscribe.setText(message)
            try {
                if ( this.weight.toFloat() < this.baseValue/5){
                    alarm.setText("BOŞ")
                }else{
                    alarm.setText("DOLU")
                }
            }
            catch (e : Exception){
                e.printStackTrace()

            }



                    Log.d(ContentValues.TAG, message) }
            })

        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Subscribing error.", e)


        }

        try {
            sleep(5000)

            mqttManager.publishString("Hello to all subscribers!", "sdk/test/java", AWSIotMqttQos.QOS0, AWSIotMqttMessageDeliveryCallback { status, userData ->
                ThreadUtils.runOnUiThread { Log.d(ContentValues.TAG, "Publish Status = " + status.toString()) }


            }, Any())
        } catch (e: java.lang.Exception) {
            Log.e(ContentValues.TAG, "Publish error: ", e)
        }

    }



}

