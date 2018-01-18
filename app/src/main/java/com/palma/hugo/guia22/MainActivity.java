package com.palma.hugo.guia22;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private static final String [] A = {"n/d", "preciso", "impreciso"};
    private static final String [] B = {"n/d", "bajo", "medio", "alto"};
    private static final String [] E = {"fuera de servicio", "temporalmente no disponible", "disponible"};

    private LocationManager manejador;
    private TextView salida;
    private String proveedor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        salida = (TextView) findViewById(R.id.salida);
        manejador = (LocationManager) getSystemService(LOCATION_SERVICE);
        log("Proveedores de Localizacion: \n");
        muestraProveedores();
        Criteria criteria = new Criteria();
        proveedor = manejador.getBestProvider(criteria, true);
        log("Mejor proveedor: " + proveedor + "\n");
        log("Ultima localizacion conocida:");
        Location location = manejador.getLastKnownLocation(proveedor);
        showLocation(location);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ( ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  Manifest.permission.ACCESS_FINE_LOCATION  },
                     );
        }
        manejador.requestLocationUpdates(proveedor, 10000, 1, this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        manejador.removeUpdates(this);
    }
    public void onLocationChanged(Location location){
        log("Nueva Localizacion");
        showLocation(location);
    }
    public void onProviderDisabled(String provider){
        log("Proveedor deshabilitado: " + provider + "\n");
    }
    public void onProviderEnabled(String provider){
        log("Proveedor habilitado: " + provider + "\n");
    }
    public void onStatusChanged(String provider, int state, Bundle extras){
        log("Proveedor con estado cambiado: " + provider + ", estado="+
        E[Math.max(0, state)] + ", extras=" + extras+ "\n");
    }
    private void log(String string){
        salida.append(string + "\n");
    }
    private void showLocation(Location location){
        if(location == null){
            log("Localizacion desconocida\n");
        }
        else {
            log("Localizacion obtenida: " + location.toString() + "\n" + "Latitud: " + location.getLatitude()+
            "\n" + "Longitud: " + location.getLongitude() + "\n" + "Altitud: " + location.getAltitude() + "\n");
        }
    }
    private void muestraProveedores(){
        List<String> providers = manejador.getAllProviders();
        for(String provider : providers){
            muestraProveedor(provider);
        }
    }
    private void muestraProveedor(String provider){
        LocationProvider info = manejador.getProvider(provider);
        log("locationProvider[ " + "getName=" + info.getName() + ", isProviderEnabled=" +
        manejador.isProviderEnabled(provider) +
                "getAccuracy=" + A[Math.max(0, info.getAccuracy())] + ", hasMonetaryCost=" + info.hasMonetaryCost()+
                ", requiresCell=" + info.requiresCell()+
                ", requiresNetwork=" + info.requiresNetwork()+
                ",requiresSatellite" + info.requiresSatellite()+
                ", supportsAltitude=" + info.supportsAltitude()+
                ", supportsBearing=" + info.supportsBearing()+
                ", supportsSpeed=" + info.supportsSpeed()+" ]\n"
        );
    }
}
