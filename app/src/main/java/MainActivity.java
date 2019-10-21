import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;

import static android.app.ProgressDialog.show;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abc);
    }
    public void btn1(View v){
        show(1);
    }
    public void btn2(View v){
        show(2);
    }
    public void btn3(View v){
        show(3);
    }
    private void show(int i) {
        TextView k =(TextView)findViewById(R.id.scorea);
        String oldScore=(String)k.getText();
        String newScore=String.valueOf(Integer.parseInt(oldScore)+i);
        k.setText(newScore);
    }
    public void btnReset(View v){
        TextView kk =(TextView)findViewById(R.id.scorea);
        kk.setText("0");
    }
    public void onClicko(View v){
    }
}
