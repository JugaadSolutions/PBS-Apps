package mytrintrin.com.pbs_employee;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Dockingstations_lock extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dockingstations_lock);
    }

    public  void lock(View view)
    {

        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.lockreason, null);


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Dockingstations_lock.this);
        alertDialog.setTitle("Lock");
        final EditText userInput = (EditText) promptsView.findViewById(R.id.etlockreason);
        alertDialog.setView(promptsView);
        alertDialog.setMessage("Do you want to lock?");
        alertDialog.setIcon(R.drawable.ic_lock);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Toast.makeText(getApplicationContext(), "You clicked on yes", Toast.LENGTH_SHORT).show();

            }
        });


        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
             Toast.makeText(getApplicationContext(), "You clicked on no", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        alertDialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "You clicked on Cancel",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
}
