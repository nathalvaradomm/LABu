
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;



import java.util.LinkedHashMap;//LinkedHashMap permite buscar dispositivos por código


import java.util.List;
import java.util.Map;


public class Oficina {

    
    private  String codigo;
    private  String inquilino;
    private  Map<String, Dispositivo> dispositivos;
    private  EnumMap<FranjaTarifaria, Double> consumoPorFranja;

    
    private BigDecimal creditosDia;
    private  ArrayList<Factura> facturas;

    
    public Oficina(String codigo, String inquilino) {

        if (codigo == null) {
            throw new IllegalArgumentException(
                    "El codigo es obligatorio."
            );
        }

        if (codigo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El codigo no puede estar vacio."
            );
        }

        if (inquilino == null) {
            throw new IllegalArgumentException(
                    "El inquilino es obligatorio."
            );
        }

        if (inquilino.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El inquilino no puede estar vacio."
            );
        }

        this.codigo = codigo.trim();
        this.inquilino = inquilino.trim();

        dispositivos = new LinkedHashMap<String, Dispositivo>();

        consumoPorFranja =
                new EnumMap<FranjaTarifaria, Double>(
                        FranjaTarifaria.class
                );

        creditosDia = BigDecimal.ZERO;
        facturas = new ArrayList<Factura>();

        
        for (FranjaTarifaria franja : FranjaTarifaria.values()) {
            consumoPorFranja.put(franja, 0.0);
        }
    }

    
    void agregarDispositivo(Dispositivo dispositivo) {

        if (dispositivo == null) {
            throw new IllegalArgumentException(
                    "El dispositivo es obligatorio."
            );
        }

        // Compara que sea el mismo objeto Oficina.
        if (dispositivo.getOficina() != this) {
            throw new IllegalArgumentException(
                    "El dispositivo pertenece a otra oficina."
            );
        }

        if (dispositivos.containsKey(dispositivo.getCodigo())) {
            throw new IllegalArgumentException(
                    "El codigo de dispositivo ya existe."
            );
        }

        dispositivos.put(dispositivo.getCodigo(), dispositivo);
    }

    
    void validarAcumulacion(FranjaTarifaria franja, double consumo) {

        if (franja == null) {
            throw new IllegalArgumentException(
                    "La franja es obligatoria."
            );
        }

        if (!Double.isFinite(consumo)) {
            throw new IllegalArgumentException(
                    "El consumo debe ser un numero finito."
            );
        }

        if (consumo < 0) {
            throw new IllegalArgumentException(
                    "El consumo no puede ser negativo."
            );
        }

        double nuevoConsumo = consumoPorFranja.get(franja) + consumo;

        if (!Double.isFinite(nuevoConsumo)) {
            throw new IllegalArgumentException(
                    "El consumo acumulado excede el rango permitido."
            );
        }
    }

    
    void acumular(FranjaTarifaria franja, double consumo) {
        validarAcumulacion(franja, consumo);

        double consumoActual = consumoPorFranja.get(franja);

        consumoPorFranja.put(franja,consumoActual + consumo       
        );
    }

    
    void acreditar(BigDecimal monto) {

        if (monto == null) {
            throw new IllegalArgumentException(
                    "El monto es obligatorio."
            );
        }

        if (monto.signum() < 0) {
            throw new IllegalArgumentException(
                    "El credito no puede ser negativo."
            );
        }

       
        creditosDia = creditosDia.add(monto);
    }

  
    Factura cerrarDia(long dia) {

        if (dia < 0) {
            throw new IllegalArgumentException(
                    "El dia no puede ser negativo."
            );
        }

        if (!facturas.isEmpty()) {
            Factura ultimaFactura = facturas.get(facturas.size() - 1);

            if (ultimaFactura.getDia() >= dia) {
                throw new IllegalArgumentException(
                        "Ese dia ya fue cerrado."
                );
            }
        }

        
        Factura factura = new Factura(
                this,
                dia,
                consumoPorFranja,
                creditosDia
        );

        facturas.add(factura);

        
        for (FranjaTarifaria franja : FranjaTarifaria.values()) {
            consumoPorFranja.put(franja, 0.0);
        }

       
        creditosDia = BigDecimal.ZERO;

        
        for (Dispositivo dispositivo : dispositivos.values()) {
            dispositivo.reiniciarDia();
        }

        return factura;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getInquilino() {
        return inquilino;
    }

    
    public Map<String, Dispositivo> getDispositivos() {
        Map<String, Dispositivo> copia =
                new LinkedHashMap<String, Dispositivo>(dispositivos);

        return Collections.unmodifiableMap(copia);
    }

    
    public Map<FranjaTarifaria, Double> getConsumoPorFranja() {
        EnumMap<FranjaTarifaria, Double> copia =
                new EnumMap<FranjaTarifaria, Double>(
                        consumoPorFranja
                );

        return Collections.unmodifiableMap(copia);
    }

    public BigDecimal getCreditosDia() {
        return creditosDia;
    }

   
    public List<Factura> getFacturas() {
        ArrayList<Factura> copia =
                new ArrayList<Factura>(facturas);

        return Collections.unmodifiableList(copia);
    }

    
    public ArrayList<Dispositivo> dispositivosOrdenados() {
        ArrayList<Dispositivo> lista =
                new ArrayList<Dispositivo>(dispositivos.values());

        
        lista.sort(Comparator.comparing(Dispositivo::getCodigo)); // sort() sirve para ordenar los elementos de una lista.

        return lista;
    }

    
    public String toString() {String cadena = "Oficina: " + codigo+ "\nInquilino: " + inquilino;
        return cadena;
    }
}