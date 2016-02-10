package etg.com.mapfragmentssamp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by goutham on 08-02-2016.
 */
public class Login extends Activity {

    public static String globalVar = "";
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pin_layout);

        editText = (EditText)findViewById(R.id.editEmail);

        Button nxtBtn = (Button)findViewById(R.id.nxt_btn);

        nxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                globalVar = editText.getText().toString();
                if(globalVar.length()!=0){
                    startActivity(new Intent(Login.this, MainActivity.class));
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "Please enter the field", Toast.LENGTH_LONG).show();
                }

            }
        });

    }
}
