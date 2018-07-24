package com.brian.speechrecognizer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView mTextViewListening;
    private EditText mEditTextResult;
    private Button mButtonListen;
    private boolean mIsListening;
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // check permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED)) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
            }
        }

        mTextViewListening = findViewById(R.id.text_view_listening);
        mEditTextResult = findViewById(R.id.edit_text_result);
        mEditTextResult.setKeyListener(null); // dont allow typing into the edit text
        mButtonListen = findViewById(R.id.button_listen);
        mIsListening = false;

        // init the speech recognizers
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        mButtonListen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsListening) {
                    mButtonListen.setText("listen");
                    mTextViewListening.setVisibility(View.INVISIBLE);
                    mEditTextResult.setVisibility(View.VISIBLE);
                    mSpeechRecognizer.stopListening();
                    mIsListening = false;
                }
                else {
                    mButtonListen.setText("done");
                    mTextViewListening.setVisibility(View.VISIBLE);
                    mEditTextResult.setVisibility(View.INVISIBLE);
                    mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                    mIsListening = true;
                }
            }
        });

        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> result = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (result != null)
                    mEditTextResult.setText(result.get(0)); // first result is probably the best one
                else
                    mEditTextResult.setText("...");
            }
            @Override
            public void onReadyForSpeech(Bundle bundle) {} // don't need
            @Override
            public void onBeginningOfSpeech() {} // don't need
            @Override
            public void onRmsChanged(float v) {} // don't need
            @Override
            public void onBufferReceived(byte[] bytes) {} // don't need
            @Override
            public void onEndOfSpeech() {} // don't need
            @Override
            public void onError(int i) {} // don't need
            @Override
            public void onPartialResults(Bundle bundle) {} // don't need
            @Override
            public void onEvent(int i, Bundle bundle) {} // don't need
        });
    }
}
