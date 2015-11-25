package com.example.josenavarro.restauranteapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupMenu;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;


/**
 * Created by Jose Navarro on 28/04/2015.
 */
public class MenuActivity extends ActionBarActivity {
    clsGlobal glo = new clsGlobal();
    int request_code = 1;
    double subT = 0;
    double Total = 0;
    double impIV = 0;
    double impIS = 0;
    String sub;
    String tot;
    String imp;
    String impS;
    String IdMesa;

    ArrayList<String> ListaIdEnTabla = new ArrayList<>();
    ArrayList<String> ListaCantidadEnTabla = new ArrayList<>();
    ArrayList<String> ListaProductoEnTabla = new ArrayList<>();
    ArrayList<String> ListaPrecioEnTabla = new ArrayList<>();
    ArrayList<String> ListaIVEnTabla = new ArrayList<>();
    ArrayList<String> ListaISEnTabla = new ArrayList<>();
    ArrayList<String> ListaPrecioUnidad = new ArrayList<>();
    ArrayList<String> ListaImpresoraEnTabla = new ArrayList<>();
    TextView txPrecio;
    TextView txCantidad;
    TextView txDescripcion;
    TableRow currenRow;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        glo.ListaComanda = new Comandas();
        clsGlobal.currentComanda = "0";
        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setCustomView(R.layout.action_bar);
        final TextView txMesa = (TextView) actionBar.getCustomView().findViewById(R.id.titulo_mesa);
        Bundle datos = this.getIntent().getExtras();
        String mesa = datos.getString("Mesa");
        txMesa.setText(mesa);
        IdMesa = datos.getString("IdMesa");
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        CargarCategorias();

        if (clsGlobal.currentMesa.Estado.equals("1")) {
            CargarComanda(IdMesa);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        int lineas = ListaProductoEnTabla.size();
        if (lineas == 0) {
            spActualizarMesa(clsGlobal.currentMesa.Id, "0");
        } else {
            spActualizarMesa(clsGlobal.currentMesa.Id, "1");
        }
    }

    public void CargarCategorias() {
        TabHost tabs = (TabHost) findViewById(R.id.tabHost);
        tabs.setup();
        if (glo.lCategoria != null) {
            for (int i = 0; i < glo.lCategoria.length; i++) {
                String color = "#" + glo.lCategoria[i].color.toString();
                TabHost.TabSpec tSpecSalon = tabs.newTabSpec(glo.lCategoria[i].codigo.toString());
                tSpecSalon.setIndicator(glo.lCategoria[i].descripcion.toString());
                tSpecSalon.setContent(new TabContent(getBaseContext()));
                tabs.addTab(tSpecSalon);
                tabs.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor(color));
                tabs.getTabWidget().setStripEnabled(true);
            }
            CargarMenu(glo.lCategoria[0].descripcion.toString());
            TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {
                @Override
                public void onTabChanged(String tabId) {
                    CargarMenu(tabId);
                }
            };
            tabs.setOnTabChangedListener(tabChangeListener);
            tabs.setCurrentTab(1);
            tabs.setCurrentTab(0);//Activar el TAB LISTENER

        } else {
            Toast.makeText(MenuActivity.this, "Error al cargar las categorias", Toast.LENGTH_LONG).show();
        }

    }

    public void CargarMenu(String _IdCategoria) {

        ListView listMenu = (ListView) findViewById(R.id.listMenu);
        int c = 0;
        for (int i = 0; i < glo.lMenu.length; i++) {
            if (_IdCategoria.equals(glo.lMenu[i].codigoCategoria.toString())) {
                c = c + 1;
            }
        }


        final String lNombreMenu[] = new String[c];
        if (glo.lMenu != null) {

            try {
                c = 0;
                for (int i = 0; i < glo.lMenu.length; i++) {
                    if (_IdCategoria.equals(glo.lMenu[i].codigoCategoria.toString())) {
                        lNombreMenu[c] = glo.lMenu[i].descripcion;
                        c = c + 1;
                    }
                }
                glo.lMenuSeleccionado = new Menu[c];
                int pos = 0;
                for (int i = 0; i < glo.lMenu.length; i++) {
                    if (_IdCategoria.equals(glo.lMenu[i].codigoCategoria.toString())) {
                        glo.lMenuSeleccionado[pos] = glo.lMenu[i];
                        pos++;
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(listMenu.getContext(), android.R.layout.simple_list_item_1, lNombreMenu) {
                    @Override
                    public View getView(int position, View convertView,
                                        ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        String color = "#" + glo.lMenuSeleccionado[position].colores.toString();
                        TextView textView = (TextView) view.findViewById(android.R.id.text1);
                        textView.setTextColor(Color.parseColor(color));

                        return view;
                    }
                };
                listMenu.setAdapter(adapter);

                ListView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        int cantidad = 1;
                        ListAdapter adap = (ListAdapter) parent.getAdapter();
                        parent.getContext();

                        double precio = Double.parseDouble(glo.lMenuSeleccionado[position].precio.toString());
                        NumberFormat fmt = NumberFormat.getNumberInstance(Locale.US);
                        boolean agruparAuto = false;
                        String precioStr = glo.fnFormat(precio);
                        String tienemod = glo.lMenuSeleccionado[position].modificadores.toString();
                        int tieneaco = Integer.parseInt(glo.lMenuSeleccionado[position].acompañamientos.toString());
                        boolean Existe = false;

                        if (ListaProductoEnTabla.size() != 0) {

                            if (agruparAuto == true) {

                                for (int i = 0; i < ListaProductoEnTabla.size(); i++) {
                                    if (adap.getItem(position).equals(ListaProductoEnTabla.get(i))) {
                                        try {
                                            int Cant = Integer.valueOf(ListaCantidadEnTabla.get(i));
                                            int nCant = Cant + 1;
                                            double Pre = precio;
                                            double nPre = Pre * nCant;
                                            ListaCantidadEnTabla.set(i, String.valueOf(nCant));
                                            ListaProductoEnTabla.set(i, adap.getItem(position).toString());
                                            ListaPrecioEnTabla.set(i, glo.fnFormat(nPre));
                                            Existe = true;

                                        } catch (Exception ex) {
                                        }
                                    }
                                }
                            }
                            if (Existe == false) {
                                //TODO: CargarMenu-> Añadir en arreglo
                                Comanda com = new Comanda(glo.lMenuSeleccionado[position].codigo,
                                        "0",
                                        "1",
                                        glo.lMenuSeleccionado[position].descripcion,
                                        precioStr,
                                        glo.currentMesa.Id,
                                        glo.lMenuSeleccionado[position].impuestoVenta,
                                        glo.lMenuSeleccionado[position].impuestoServicio,
                                        glo.lMenuSeleccionado[position].impresora,
                                        "0", "Principal");
                                ListaIdEnTabla.add(glo.lMenuSeleccionado[position].descripcion.toString() + position);
                                ListaProductoEnTabla.add(adap.getItem(position).toString());
                                ListaCantidadEnTabla.add(String.valueOf(cantidad));
                                ListaPrecioEnTabla.add(precioStr);
                                ListaPrecioUnidad.add(precioStr);
                                ListaIVEnTabla.add(glo.lMenuSeleccionado[position].impuestoVenta.toString());
                                ListaISEnTabla.add(glo.lMenuSeleccionado[position].impuestoServicio.toString());
                                ListaImpresoraEnTabla.add(glo.lMenuSeleccionado[position].impresora.toString());
                                spAddComanda(com);
                                Existe = false;
                            }
                            InsertarProducto();
                        } else {
                            Comanda com = new Comanda(glo.lMenuSeleccionado[position].codigo,
                                    "0",
                                    "1",
                                    glo.lMenuSeleccionado[position].descripcion,
                                    precioStr,
                                    glo.currentMesa.Id,
                                    glo.lMenuSeleccionado[position].impuestoVenta,
                                    glo.lMenuSeleccionado[position].impuestoServicio,
                                    glo.lMenuSeleccionado[position].impresora,
                                    "0", "Principal");
                            ListaIdEnTabla.add(glo.lMenuSeleccionado[position].descripcion.toString() + position);
                            ListaProductoEnTabla.add(adap.getItem(position).toString());
                            ListaCantidadEnTabla.add(String.valueOf(cantidad));
                            ListaPrecioEnTabla.add(precioStr);
                            ListaIVEnTabla.add(glo.lMenuSeleccionado[position].impuestoVenta.toString());
                            ListaISEnTabla.add(glo.lMenuSeleccionado[position].impuestoServicio.toString());
                            ListaPrecioUnidad.add(precioStr);
                            ListaImpresoraEnTabla.add(glo.lMenuSeleccionado[position].impresora.toString());
                            spAddComanda(com);
                            InsertarProducto();
                        }

                        if (tienemod.equals("true")) {//TODO: Modificadores
                            Intent mod = new Intent(MenuActivity.this, ModificadoresActivity.class);
                            mod.putExtra("Categoria", glo.lMenuSeleccionado[position].codigoCategoria.toString());
                            startActivityForResult(mod, request_code);
                        }
                        if (tieneaco > 0) {
                            Intent acom = new Intent(MenuActivity.this, AcompanamientoActivity.class);
                            acom.putExtra("Categoria", glo.lMenuSeleccionado[position].codigoCategoria.toString());
                            startActivityForResult(acom, request_code);
                        }
                    }
                };
                listMenu.setOnItemClickListener(onItemClickListener);
            } catch (Exception ex) {
                String error = ex.getMessage();
            }
        } else {
            Toast.makeText(MenuActivity.this, "Error al cargar el menú", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == request_code) && (resultCode == RESULT_OK)) {
            ListaIdEnTabla.add("M:" + data.getDataString());
            ListaCantidadEnTabla.add("M");
            ListaProductoEnTabla.add(data.getDataString());
            ListaPrecioEnTabla.add("0");
            ListaIVEnTabla.add("false");
            ListaISEnTabla.add("false");
            ListaPrecioUnidad.add("0");
            ListaImpresoraEnTabla.add("COCINA");
            Comanda com = new Comanda("0", clsGlobal.currentComanda, "-1", data.getDataString(), "0", clsGlobal.currentMesa.Id, "0", "0", clsGlobal.currentImpresora, "0", "Modificador");
            clsGlobal.ListaComanda.spAddComanda(com);
            InsertarProducto();
        }
    }

    public void InsertarProducto() {

        final TableLayout tb = (TableLayout) findViewById(R.id.tbComanda);

        while (tb.getChildCount() > 2) {
            TableRow row = (TableRow) tb.getChildAt(2);
            tb.removeView(row);
        }

        for (int i = 0; i < ListaProductoEnTabla.size(); i++) {

            currenRow = new TableRow(getBaseContext());
            TableLayout.LayoutParams params = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
            txCantidad = new TextView(getBaseContext());
            txPrecio = new TextView(getBaseContext());
            txDescripcion = new TextView(getBaseContext());

            txCantidad.setText(ListaCantidadEnTabla.get(i));
            txCantidad.setGravity(Gravity.CENTER);
            txCantidad.setTextColor(Color.BLACK);
            txCantidad.setPadding(5, 0, 0, 0);
            txCantidad.setTextSize(18);

            txDescripcion.setText(ListaProductoEnTabla.get(i));
            txDescripcion.setTextColor(Color.BLACK);
            txDescripcion.setGravity(Gravity.CENTER);
            txDescripcion.setTextSize(18);

            txPrecio.setText(ListaPrecioEnTabla.get(i).toString());
            txPrecio.setTextColor(Color.BLACK);
            txPrecio.setGravity(Gravity.CENTER);
            txPrecio.setTextSize(18);

            currenRow.setLayoutParams(params);
            currenRow.addView(txCantidad);
            currenRow.addView(txDescripcion);
            currenRow.addView(txPrecio);
            currenRow.setTag(ListaIdEnTabla.get(i));

            currenRow.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            PopupMenu pop = new PopupMenu(getApplicationContext(), v);

                            pop.getMenuInflater().inflate(R.menu.menu_opciones, pop.getMenu());
                            pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                                               @Override
                                                               public boolean onMenuItemClick(MenuItem item) {
                                                                   String id = v.getTag().toString();
                                                                   final int posicion = getPosicion(id);

                                                                   switch (item.getItemId()) {

                                                                       case R.id.mNota:
                                                                           AlertDialog.Builder notaDialog = new AlertDialog.Builder(MenuActivity.this);
                                                                           notaDialog.setTitle("Notas");
                                                                           notaDialog.setMessage("Nota:");
                                                                           final EditText note = new EditText(MenuActivity.this);
                                                                           LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                                                                   LinearLayout.LayoutParams.MATCH_PARENT,
                                                                                   LinearLayout.LayoutParams.MATCH_PARENT);
                                                                           note.setLayoutParams(lp);
                                                                           notaDialog.setView(note);
                                                                           notaDialog.setIcon(android.R.drawable.ic_menu_agenda);
                                                                           //TODO: Aqui guardar nota

                                                                           notaDialog.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                                                                               @Override
                                                                               public void onClick(DialogInterface dialog, int which) {
                                                                                   Comanda com = new Comanda("0",
                                                                                           "0",
                                                                                           "-1",
                                                                                           note.getText().toString(),
                                                                                           "0",
                                                                                           glo.currentMesa.Id,
                                                                                           "0",
                                                                                           "0",
                                                                                           "COCINA",
                                                                                           "0", "Modificador");
                                                                                   ListaIdEnTabla.add(posicion + 1, note.getText().toString() + (posicion + 1));
                                                                                   ListaProductoEnTabla.add(posicion + 1, note.getText().toString());
                                                                                   ListaCantidadEnTabla.add(posicion + 1, "N");
                                                                                   ListaPrecioEnTabla.add(posicion + 1, "0");
                                                                                   ListaIVEnTabla.add(posicion + 1, "false");
                                                                                   ListaISEnTabla.add(posicion + 1, "false");
                                                                                   ListaPrecioUnidad.add(posicion + 1, "0");
                                                                                   ListaImpresoraEnTabla.add(posicion + 1, "COCINA");
                                                                                   spAddComanda(com);
                                                                                   InsertarProducto();

                                                                               }
                                                                           });
                                                                           notaDialog.show();
                                                                           return true;

                                                                       case R.id.mQuitar:

                                                                           spQuitarElementos(posicion, tb);
                                                                           return true;


// TODO: AQUI TODO LO RELACIONADO A CAMBIAR LA CANTIDAD  DESCOMENTAR EN MENU_OPCIONES.xml
// case R.id.mModificar:
//                                                                                                if (ListaCantidadEnTabla.get(posicion).equals("N") || ListaCantidadEnTabla.get(posicion).equals("M")) {
//                                                                                                    Toast.makeText(MenuActivity.this, "No se puede modificar la cantidad", Toast.LENGTH_SHORT);
//                                                                                                    return true;
//                                                                                                } else {
//                                                                                                    AlertDialog.Builder modificarDialog = new AlertDialog.Builder(MenuActivity.this);
//                                                                                                    modificarDialog.setTitle("Modificar");
//                                                                                                    modificarDialog.setMessage("Cantidad:");
//                                                                                                    final NumberPicker nuCantidad = new NumberPicker(MenuActivity.this);
//                                                                                                    LinearLayout.LayoutParams layoutParamsp = new LinearLayout.LayoutParams(
//                                                                                                            LinearLayout.LayoutParams.MATCH_PARENT,
//                                                                                                            LinearLayout.LayoutParams.MATCH_PARENT);
//                                                                                                    nuCantidad.setLayoutParams(layoutParamsp);
//                                                                                                    modificarDialog.setView(nuCantidad);
//                                                                                                    //  modificarDialog.setIcon(R.drawable.refresh_blue);
//                                                                                                    nuCantidad.setMinValue(2);
//                                                                                                    nuCantidad.setMaxValue(100);
//                                                                                                    modificarDialog.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
//                                                                                                        @Override
//                                                                                                        public void onClick(DialogInterface dialog, int which) {
//                                                                                                            //TODO: Aqui guarda modificación de cantidad
//                                                                                                            try {
//                                                                                                                int nuevaCant = nuCantidad.getValue();
//                                                                                                                String precioCambio = ListaPrecioEnTabla.get(posicion).toString();
//                                                                                                                NumberFormat fmt = NumberFormat.getNumberInstance(Locale.US);
//                                                                                                                double pr = fmt.parse(precioCambio).doubleValue();
//                                                                                                                double preciofinal = nuevaCant * pr;
//                                                                                                                ListaCantidadEnTabla.set(posicion, String.valueOf(nuevaCant));
//                                                                                                                String resultado = clsGlobal.fnFormat(preciofinal);
//                                                                                                                ListaPrecioEnTabla.set(posicion, resultado);
//                                                                                                                clsGlobal.ListaComanda.getLinea(posicion).cantidad = String.valueOf(nuevaCant);
//                                                                                                                InsertarProducto();
//                                                                                                            } catch (Exception ex) {
//                                                                                                                Toast.makeText(MenuActivity.this, "No se puede cambiar la cantidad", Toast.LENGTH_LONG);
//                                                                                                            }
//
//
//                                                                                                        }
//                                                                                                    });
//
//                                                                                                    modificarDialog.show();
//
//                                                                                                    return true;
//                                                                                                }

                                                                   }
                                                                   return false;
                                                               }
                                                           }

                            );
                            pop.show();
                        }
                    }

            );
            tb.addView(currenRow);

        }
        CalcularTotales();

    }

    public int getPosicion(String id) {
        int lineas = ListaIdEnTabla.size();
        for (int i = 0; i < lineas; i++) {
            if (ListaIdEnTabla.get(i).equals(id)) {
                return i;
            }
        }
        return 0;
    }

    public void spQuitarElementos(int posicion, TableLayout tb) {
        Log.d("MenuActivity", "spQuitarElemento:" + posicion);
        int posicionVisual = posicion + 2;
        ListaIdEnTabla.remove(posicion);
        ListaProductoEnTabla.remove(posicion);
        ListaPrecioEnTabla.remove(posicion);
        ListaCantidadEnTabla.remove(posicion);
        ListaIVEnTabla.remove(posicion);
        ListaISEnTabla.remove(posicion);
        ListaPrecioUnidad.remove(posicion);
        ListaImpresoraEnTabla.remove(posicion);
        spRemoverComanda(posicion);
        tb.removeViewAt(posicionVisual);
        CalcularTotales();

    }

    public void CalcularTotales() {
        try {

              final TextView total = (TextView) findViewById(R.id.txTotal);
               double precio;
            int i = 0;
            int cant = ListaPrecioEnTabla.size();
            for (i = 0; i < cant; i++) {
                String precioCambio = ListaPrecioEnTabla.get(i).toString();
                NumberFormat fmt = NumberFormat.getNumberInstance(Locale.US);
                precio = fmt.parse(precioCambio).doubleValue();
                if (ListaIVEnTabla.get(i).equals("true") && ListaISEnTabla.get(i).equals("false")) {
                    subT = subT + precio;
                    impIV = subT * (0.13);
                    impIS = impIS + 0.0;
                    Total = subT + impIS + impIV;
                    sub = clsGlobal.fnFormat(subT);
                    impS = clsGlobal.fnFormat(impIS);
                    imp = clsGlobal.fnFormat(impIV);
                    tot = clsGlobal.fnFormat(Total);

                } else if (ListaIVEnTabla.get(i).equals("false") && ListaISEnTabla.get(i).equals("true")) {
                    subT = subT + precio;
                    impIV = impIV + 0.0;
                    impIS = subT * (0.10);
                    Total = subT + impIS + impIV;
                    sub = clsGlobal.fnFormat(subT);
                    impS = clsGlobal.fnFormat(impIS);
                    imp = clsGlobal.fnFormat(impIV);
                    tot = clsGlobal.fnFormat(Total);
                } else if (ListaIVEnTabla.get(i).equals("true") && ListaISEnTabla.get(i).equals("true")) {
                    subT = subT + precio;
                    impIV = subT * (0.13);
                    impIS = subT * (0.10);
                    Total = subT + impIS + impIV;
                    sub = clsGlobal.fnFormat(subT);
                    impS = clsGlobal.fnFormat(impIS);
                    imp = clsGlobal.fnFormat(impIV);
                    tot = clsGlobal.fnFormat(Total);
                } else {
                    subT = subT + precio;
                    impIV = impIV + 0.0;
                    impIS = impIS + 0.0;
                    Total = subT + impIS + impIV;
                    sub = clsGlobal.fnFormat(subT);
                    impS = clsGlobal.fnFormat(impIS);
                    imp = clsGlobal.fnFormat(impIV);
                    tot = clsGlobal.fnFormat(Total);
                }
            }

            if (ListaPrecioEnTabla.size() == 0) {
                subT = 0.0;
                impIV = 0.0;
                impIS = 0.0;
                Total = 0.0;
                sub = clsGlobal.fnFormat(subT);
                impS = clsGlobal.fnFormat(impIS);
                imp = clsGlobal.fnFormat(impIV);
                tot = clsGlobal.fnFormat(Total);
            }

            total.setText(tot);
            total.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            subT = 0;
            impIV = 0;
            impIS = 0;
            Total = 0;
            precio = 0;
        } catch (Exception ex) {

        }
    }

    public void CargarComanda(final String _Mesa) {
        glo.currentComanda = "0"; //En caso de no que la mesa este vacia no conserve el valor de la mesa consultada antes

        final ProgressDialog dialogo = ProgressDialog.show(MenuActivity.this, "Espere", "Cargando");
        Thread hilo = new Thread() {

            @Override
            public void run() {
                String NAMESPACE = new clsGlobal().NAMESPACE;
                String URL = new clsGlobal().URL;
                String METHOD_NAME = "fnCargarComandas";
                String SOAP_ACTION = new clsGlobal().SOAP_ACTION + "/fnCargarComandas";

                SoapObject sp = new SoapObject(NAMESPACE, METHOD_NAME);
                sp.addProperty("_IdMesa", _Mesa);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(sp);

                HttpTransportSE transporte = new HttpTransportSE(URL, glo.Time_out);
                try {
                    transporte.call(SOAP_ACTION, envelope);
                    SoapObject resul = (SoapObject) envelope.getResponse();
                    SoapObject resul2 = (SoapObject) resul.getProperty(1);
                    if (resul2.getPropertyCount() > 0) {
                        SoapObject filas = (SoapObject) resul2.getProperty(0);
                        int cant = filas.getPropertyCount();
                        int x = 0;
                        while (x < cant) {
                            //TODO: Añadir datos de la BD en Arreglo
                            SoapObject columnas = (SoapObject) filas.getProperty(x);
                            double precio = Double.parseDouble(columnas.getProperty(4).toString());
                            glo.currentComanda = columnas.getProperty(1).toString();
                            glo.ListaComanda.spAddComanda(
                                    new Comanda(columnas.getProperty(0).toString(),//Producto
                                            columnas.getProperty(1).toString(),
                                            columnas.getProperty(2).toString(),
                                            columnas.getProperty(3).toString(),
                                            clsGlobal.fnFormat(precio),
                                            columnas.getProperty(6).toString(),
                                            columnas.getProperty(7).toString(),
                                            columnas.getProperty(8).toString(),
                                            columnas.getProperty(9).toString(),
                                            columnas.getProperty(10).toString(), "Principal"
                                    ));
                            ListaIdEnTabla.add(columnas.getProperty(1).toString() + x);
                            if (columnas.getProperty(2).toString().equals("0")) {
                                ListaCantidadEnTabla.add("M");//Es un modificador

                            } else {
                                if (columnas.getProperty(2).toString().equals("-1")) {
                                    ListaCantidadEnTabla.add("N");
                                } else {
                                    ListaCantidadEnTabla.add(columnas.getProperty(2).toString());
                                }
                            }
                            ListaProductoEnTabla.add(columnas.getProperty(3).toString());
                            ListaPrecioEnTabla.add(clsGlobal.fnFormat(precio));
                            ListaPrecioUnidad.add(clsGlobal.fnFormat(precio));
                            ListaIVEnTabla.add(columnas.getProperty(7).toString());
                            ListaISEnTabla.add(columnas.getProperty(8).toString());
                            ListaImpresoraEnTabla.add(columnas.getProperty(9).toString());
                            x++;
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    dialogo.dismiss();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                    dialogo.dismiss();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (glo.ListaComanda != null) {

                            InsertarProducto();
                            dialogo.dismiss();

                        } else {
                            Toast.makeText(MenuActivity.this, "Error al cargar la comanda", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        };

        hilo.start();

    }

    public void btGuardarClick(View v) {
        spGuardar();
        finish();
    }

    private void spGuardar() {

        clsGlobal.ListaComanda.spGuardar();

        int lineas = ListaProductoEnTabla.size();
        if (lineas == 0) {
            clsGlobal.currentMesa.Estado = "0";
            spActualizarMesa(clsGlobal.currentMesa.Id, "0");

        } else {
            clsGlobal.currentMesa.Estado = "1";
        }
    }

    private void spRemoverComanda(int pos) {
        try {
            int nL = 0;
            if (clsGlobal.ListaComanda.getFilas() > 0) {
                if (clsGlobal.ListaComanda.getLinea(pos).estado == "Activo") {
                    clsGlobal.ListaComanda.getLinea(pos).estado = "Inactivo";
                } else {
                    spRemoverComanda(pos + 1);
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void spActualizarMesa(final String _id, final String _estado) {

        Thread hilo = new Thread() {

            String res = "0";

            @Override
            public void run() {
                String NAMESPACE = new clsGlobal().NAMESPACE;
                String URL = new clsGlobal().URL;
                String METHOD_NAME = "fnActualizaMesa";
                String SOAP_ACTION = new clsGlobal().SOAP_ACTION + "/fnActualizaMesa";

                SoapObject sp = new SoapObject(NAMESPACE, METHOD_NAME);
                sp.addProperty("_id", _id);
                sp.addProperty("_activa", _estado);
                SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                sobre.dotNet = true;
                sobre.setOutputSoapObject(sp);
                HttpTransportSE transporte = new HttpTransportSE(URL, clsGlobal.Time_out);

                try {
                    //TODO: Procesa linea Revisar si se envia la petici�n
                    transporte.call(SOAP_ACTION, sobre);
                    SoapPrimitive resulxml = (SoapPrimitive) sobre.getResponse();
                    res = resulxml.toString();

                } catch (IOException e) {
                    e.printStackTrace();

                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
        };
        hilo.start();

    }

    public void spAddComanda(Comanda cmd) {
        cmd.solicitud = glo.currentSolicitud;
        if (cmd.codigoComanda.equals("0")) {
            cmd.codigoComanda = glo.currentComanda;

        }

        clsGlobal.ListaComanda.spAddComanda(cmd);
    }

    @Override
    protected void onSaveInstanceState(Bundle guardaEstado) {
        super.onSaveInstanceState(guardaEstado);
        if (ListaProductoEnTabla != null) {
            guardaEstado.putStringArrayList("id", ListaIdEnTabla);
            guardaEstado.putStringArrayList("producto", ListaProductoEnTabla);
            guardaEstado.putStringArrayList("cantidad", ListaCantidadEnTabla);
            guardaEstado.putStringArrayList("precio", ListaPrecioEnTabla);
            guardaEstado.putStringArrayList("impuestoVenta", ListaIVEnTabla);
            guardaEstado.putStringArrayList("impuestoServicio", ListaISEnTabla);
            guardaEstado.putStringArrayList("impresora", ListaImpresoraEnTabla);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle recuperaEstado) {
        super.onRestoreInstanceState(recuperaEstado);

        if (recuperaEstado != null) {
            ListaIdEnTabla = recuperaEstado.getStringArrayList("id");
            ListaProductoEnTabla = recuperaEstado.getStringArrayList("producto");
            ListaCantidadEnTabla = recuperaEstado.getStringArrayList("cantidad");
            ListaPrecioEnTabla = recuperaEstado.getStringArrayList("precio");
            ListaIVEnTabla = recuperaEstado.getStringArrayList("impuestoVenta");
            ListaISEnTabla = recuperaEstado.getStringArrayList("impuestoServicio");
            ListaImpresoraEnTabla = recuperaEstado.getStringArrayList("impresora");
            InsertarProducto();
        }
    }


}
