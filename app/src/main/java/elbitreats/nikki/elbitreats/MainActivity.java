package elbitreats.nikki.elbitreats;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bundle = getIntent().getExtras();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        TextView name = (TextView) header.findViewById(R.id.navigationName);
        TextView username = (TextView) header.findViewById(R.id.navigationUsername);
        name.setText(bundle.getString("name"));

        if (!(bundle.getString("username").equals(""))) {
            username.setText("@" + bundle.getString("username"));
        } else {
            username.setVisibility(View.GONE);
        }

        if (bundle.getInt("userid") == 0) {
            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.nav_profile_layout).setVisible(false);
            menu.findItem(R.id.nav_favorite_layout).setVisible(false);
            menu.findItem(R.id.nav_logout_layout).setTitle("Back to Login Page").setIcon(R.drawable.ic_about_password);
        }

        navigationView.setNavigationItemSelectedListener(this);

        getSupportActionBar().setTitle("Home");
        navHome_fragment nhf = new navHome_fragment();
        nhf.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.content_frame, nhf).commit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile_layout) {
            navProfile_fragment npf = new navProfile_fragment();
            npf.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.content_frame, npf).commit();
            getSupportActionBar().setTitle("Profile");
        } else if (id == R.id.nav_favorite_layout) {
            navFavorite_fragment nff = new navFavorite_fragment();
            nff.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.content_frame, nff).commit();
            getSupportActionBar().setTitle("Favorites");
        } else if (id == R.id.nav_establishment_layout) {
            navEstablishment_fragment nef = new navEstablishment_fragment();
            nef.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.content_frame, nef).commit();
            getSupportActionBar().setTitle("Food Establishments");
        } else if (id == R.id.nav_submitEstab_layout) {
            getFragmentManager().beginTransaction().replace(R.id.content_frame, new navSubmit_fragment()).commit();
            getSupportActionBar().setTitle("Submit New");
        } else if (id == R.id.nav_about_layout) {
            getFragmentManager().beginTransaction().replace(R.id.content_frame, new navAbout_fragment()).commit();
            getSupportActionBar().setTitle("About");
        } else if (id == R.id.nav_logout_layout) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            MainActivity.this.startActivity(intent);
            finish();
        } else {
            navHome_fragment nhf = new navHome_fragment();
            nhf.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.content_frame, nhf).commit();
            getSupportActionBar().setTitle("Home");
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
