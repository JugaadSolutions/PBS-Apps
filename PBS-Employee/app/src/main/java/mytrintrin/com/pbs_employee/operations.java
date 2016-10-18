package mytrintrin.com.pbs_employee;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class operations extends AppCompatActivity {

    Button Checkout,Lock,Transfer;
    LinearLayout redistributioninsidestation,redistributionoustidestation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operations);
        Checkout = (Button) findViewById(R.id.bcheckout);
        Lock = (Button) findViewById(R.id.block);
        Transfer =(Button) findViewById(R.id.btransfer);
        redistributioninsidestation = (LinearLayout) findViewById(R.id.redistributionstationinrangelayout);
        redistributionoustidestation = (LinearLayout) findViewById(R.id.redistributionstationoutofrangelayout);

    }

    public  void Showdockingstations(View view)
    {
        Intent dockingstation = new Intent(this,Dockingstations.class);
        startActivity(dockingstation);
    }

    public  void Showdockingstationslock(View view)
    {
        Intent dockingstationlock = new Intent(this,Dockingstations_lock.class);
        startActivity(dockingstationlock);
    }

    public  void logoutfromall(View view)
    {
        startActivity(new Intent(this,LoginusingNFC.class));
    }

    public void checkincycle(View view)
    {
        startActivity(new Intent(this,Checkincycle.class));
    }

    public void checkoutcycle(View view)
    {
        startActivity(new Intent(this,Checkoutcycle.class));
    }

    public void gotocheckoutmanual(View view)
    {
        startActivity(new Intent(this,checkoutmanually.class));
    }

}
