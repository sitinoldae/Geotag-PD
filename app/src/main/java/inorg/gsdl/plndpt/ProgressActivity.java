package inorg.gsdl.plndpt;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.plndpt.R;

public class ProgressActivity extends AppCompatActivity {
    Button preconst, duringexe, finaex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        preconst = findViewById(R.id.construction);
        duringexe = findViewById(R.id.Execution);
        finaex = findViewById(R.id.Completion);
        preconst.setOnClickListener(view -> {
            Intent intent = new Intent(ProgressActivity.this, PreconstructionActivity.class);
            startActivity(intent);
        });

        duringexe.setOnClickListener(view -> {
            Intent intent = new Intent(ProgressActivity.this, DuringExcutionActivity.class);
            startActivity(intent);
        });


        finaex.setOnClickListener(view -> {
            Intent intent = new Intent(ProgressActivity.this, FinalCompletionActivity.class);
            startActivity(intent);
        });


    }
}