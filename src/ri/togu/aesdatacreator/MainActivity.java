package ri.togu.aesdatacreator;

import static ri.togu.aesdatacreator.Constants.POST_URL;
import ri.togu.aesdatacreator.ServerClient.AesMenu;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class MainActivity extends Activity {

    RadioGroup mAesMenu;
    Button mAesExecute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAesMenu = (RadioGroup) findViewById(R.id.activity_main_aes_menu);
        mAesMenu.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.activity_main_aes_menu_encrypt) {
                    System.out.println("AES 暗号化を選択しました");
                } else if (checkedId == R.id.activity_main_aes_menu_decrypt) {
                    System.out.println("AES 復号を選択しました");
                }
            }
        });

        mAesExecute = (Button) findViewById(R.id.activity_main_aes_execute);
        mAesExecute.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                int checkedId = mAesMenu.getCheckedRadioButtonId();
                if (checkedId == R.id.activity_main_aes_menu_encrypt) {
                    ServerClient.getInstance().sendPostRequestAsync(
                            getApplicationContext(), POST_URL, null,
                            AesMenu.ENCRYPTED);
                } else if (checkedId == R.id.activity_main_aes_menu_decrypt) {
                    ServerClient.getInstance().sendPostRequestAsync(
                            getApplicationContext(), POST_URL, null,
                            AesMenu.DECRYPTED);
                }
                Toast.makeText(getApplicationContext(), "しゅうりょう",
                        Toast.LENGTH_LONG).show();;
            }
        });

        TextView url = (TextView) findViewById(R.id.activity_main_load_url_text);
        url.setText(POST_URL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
