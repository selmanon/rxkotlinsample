package com.abdellahselassi.rxkotlinsample

import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.EditText
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.functions.BiFunction
import io.reactivex.subscribers.DisposableSubscriber

class MainActivity : AppCompatActivity() {

    @BindView(R.id.btn_demo_form_valid)
    lateinit var btnValidIndicator: TextView

    @BindView(R.id.demo_combl_email)
    lateinit var email: EditText

    @BindView(R.id.demo_combl_password)
    lateinit var password: EditText


    lateinit var disposableObserver: DisposableSubscriber<Boolean>
    lateinit var emailChangeObservable: Flowable<CharSequence>
    lateinit var passwordChangeObservable: Flowable<CharSequence>
    lateinit var unbinder: Unbinder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        unbinder = ButterKnife.bind(this)

        emailChangeObservable = RxTextView.textChanges(email).skip(1).toFlowable(BackpressureStrategy.LATEST)
        passwordChangeObservable = RxTextView.textChanges(password).skip(1).toFlowable(BackpressureStrategy.LATEST)


        combineLatestEvents();
    }

    override fun onDestroy() {
        super.onDestroy()
        unbinder.unbind();
        disposableObserver.dispose();
    }

    private fun combineLatestEvents() {
        disposableObserver = object : DisposableSubscriber<Boolean>() {
            override fun onNext(formValid: Boolean?) {
                if (formValid!!) {
                    btnValidIndicator.setBackgroundColor(Color.BLUE)
                } else {
                    btnValidIndicator.setBackgroundColor(Color.GRAY)
                }
            }

            override fun onError(e: Throwable) {
                //Timber.e(e, "there was an error")
            }

            override fun onComplete() {
                //Timber.d("completed")
            }
        }


        val isSignInEnabled: Flowable<Boolean> = Flowable.combineLatest(
                emailChangeObservable,
                passwordChangeObservable,
                BiFunction { u, p -> u.isNotEmpty() && p.isNotEmpty() })

        isSignInEnabled.subscribe(disposableObserver)
    }
}

