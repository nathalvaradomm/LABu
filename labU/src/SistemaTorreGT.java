
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class SistemaTorreGT {

    private  Map<String, Oficina> oficinas;
    private  Map<String, Dispositivo> dispositivos;
    private  BandejaAlerta bandeja;
    private  ArrayList<Alerta> atendidas;
    private long horaSimulacion;
    private long siguienteCodigoAlerta;


    public SistemaTorreGT() {
        oficinas = new LinkedHashMap<String, Oficina>();
        dispositivos = new LinkedHashMap<String, Dispositivo>();

        bandeja = new BandejaAlerta();
        atendidas = new ArrayList<Alerta>();

        horaSimulacion = 0;
        siguienteCodigoAlerta = 1;
    }

    
    private String validarCodigo(String codigo) {

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

        return codigo.trim();
    }

   
    public void registrarOficina(
            String codigo,
            String inquilino) {

        String clave = validarCodigo(codigo);

        if (oficinas.containsKey(clave)) {
            throw new IllegalArgumentException(
                    "La oficina ya existe."
            );
        }

        Oficina oficina = new Oficina(clave, inquilino);

        oficinas.put(clave, oficina);
    }

  
    public Oficina buscarOficina(String codigo) {
        String clave = validarCodigo(codigo);

        Oficina oficina = oficinas.get(clave);

        if (oficina == null) {
            throw new IllegalArgumentException(
                    "No existe esa oficina."
            );
        }

        return oficina;
    }


    public Dispositivo buscarDispositivo(String codigo) {
        String clave = validarCodigo(codigo);

        Dispositivo dispositivo = dispositivos.get(clave);

        if (dispositivo == null) {
            throw new IllegalArgumentException(
                    "No existe ese dispositivo."
            );
        }

        return dispositivo;
    }

    public void registrarDispositivo(Dispositivo dispositivo) {

        if (dispositivo == null) {
            throw new IllegalArgumentException(
                    "El dispositivo es obligatorio."
            );
        }

        if (dispositivos.containsKey(dispositivo.getCodigo())) {
            throw new IllegalArgumentException(
                    "El codigo de dispositivo ya existe."
            );
        }

        Oficina oficinaRegistrada = oficinas.get(
                dispositivo.getOficina().getCodigo()
        );

        if (oficinaRegistrada != dispositivo.getOficina()) {
            throw new IllegalArgumentException(
                    "Debe utilizar una oficina registrada en este sistema."
            );
        }

        if (dispositivo.getConsumoDiaKW() != 0) {
            throw new IllegalArgumentException(
                    "El dispositivo debe registrarse sin consumo previo."
            );
        }

        oficinaRegistrada.agregarDispositivo(dispositivo);

        dispositivos.put(
                dispositivo.getCodigo(),
                dispositivo
        );
    }


    public void encender(String codigo) {
        Dispositivo dispositivo = buscarDispositivo(codigo);
        dispositivo.on();
    }

    public void apagar(String codigo) {
        Dispositivo dispositivo = buscarDispositivo(codigo);
        dispositivo.off();
    }

    public void regular(String codigo, int nivel) {
        Dispositivo dispositivo = buscarDispositivo(codigo);

        Optional<Regulable> capacidad =
                dispositivo.comoRegulable();

        if (!capacidad.isPresent()) {
            throw new IllegalArgumentException(
                    "El dispositivo no es regulable."
            );
        }

        Regulable regulable = capacidad.get();

        regulable.ajustarNivel(nivel);
    }

    private void validarSecuencia() {

        if (siguienteCodigoAlerta == Long.MAX_VALUE) {
            throw new IllegalStateException(
                    "Se agoto la secuencia de alertas."
            );
        }
    }

   
    public void registrarLectura(String codigo, double valor) {
        Dispositivo dispositivo = buscarDispositivo(codigo);

        Optional<Medible> capacidad =
                dispositivo.comoMedible();

        if (!capacidad.isPresent()) {
            throw new IllegalArgumentException(
                    "El dispositivo no admite lecturas."
            );
        }

        Optional<GeneradorAlertas> generador =
                dispositivo.comoGeneradorAlertas();

        validarSecuencia();

        Medible medible = capacidad.get();
        medible.registrarLectura(valor);

       
        if (generador.isPresent()) {
            procesarAlerta(generador.get());
        }
    }

    
    public void registrarMovimiento(String codigo) {
        Dispositivo dispositivo = buscarDispositivo(codigo);

        Optional<DetectorMovimiento> capacidad =
                dispositivo.comoDetector();

        if (!capacidad.isPresent()) {
            throw new IllegalArgumentException(
                    "El dispositivo no admite movimientos."
            );
        }

        Optional<GeneradorAlertas> generador =
                dispositivo.comoGeneradorAlertas();

        validarSecuencia();

        
        int horaDelDia = (int) (horaSimulacion % 24);

        DetectorMovimiento detector = capacidad.get();
        detector.registrarMovimiento(horaDelDia);

        if (generador.isPresent()) {
            procesarAlerta(generador.get());
        }
    }


    private void procesarAlerta(GeneradorAlertas generador) {
        Alerta alerta = generador.generarAlerta(
                siguienteCodigoAlerta,
                horaSimulacion
        );

        if (alerta != null) {
            bandeja.agregar(alerta);
            siguienteCodigoAlerta++;
        }
    }

    public void avanzarHora() {

        if (horaSimulacion == Long.MAX_VALUE) {
            throw new IllegalStateException(
                    "El reloj no puede avanzar mas."
            );
        }

        int horaDelDia = (int) (horaSimulacion % 24);

        FranjaTarifaria franja =
                FranjaTarifaria.deHora(horaDelDia);

       
        Map<Dispositivo, Double> calculos =
                new LinkedHashMap<Dispositivo, Double>();

        
        Map<Oficina, Double> sumas =
                new LinkedHashMap<Oficina, Double>();

        for (Dispositivo dispositivo : dispositivos.values()) {

           
            double consumo = dispositivo.consumoHoraKW();

            dispositivo.validarAcumulacion(consumo);

            calculos.put(dispositivo, consumo);

            Oficina oficina = dispositivo.getOficina();

            double sumaActual = 0;

            if (sumas.containsKey(oficina)) {
                sumaActual = sumas.get(oficina);
            }

            double nuevaSuma = sumaActual + consumo;

            if (!Double.isFinite(nuevaSuma)) {
                throw new IllegalArgumentException(
                        "El consumo total excede el rango permitido."
                );
            }

            sumas.put(oficina, nuevaSuma);
        }

        
        for (Map.Entry<Oficina, Double> dato : sumas.entrySet()) {
            Oficina oficina = dato.getKey();
            double consumo = dato.getValue();

            oficina.validarAcumulacion(franja, consumo);
        }

       
        for (Map.Entry<Dispositivo, Double> dato
                : calculos.entrySet()) {

            Dispositivo dispositivo = dato.getKey();
            double consumo = dato.getValue();

            dispositivo.acumularConsumo(consumo);
        }

    
        for (Map.Entry<Oficina, Double> dato : sumas.entrySet()) {
            Oficina oficina = dato.getKey();
            double consumo = dato.getValue();

            oficina.acumular(franja, consumo);
        }

        long diaTerminado = horaSimulacion / 24;

        horaSimulacion++;

        
        if (horaSimulacion % 24 == 0) {
            for (Oficina oficina : oficinas.values()) {
                oficina.cerrarDia(diaTerminado);
            }
        }
    }


    public Alerta atenderMasUrgente() {
        Alerta alerta = bandeja.primera();

        BigDecimal credito =
                alerta.creditoPorRetraso(horaSimulacion);

        bandeja.retirarPrimera();

        alerta.atender(horaSimulacion);

        Oficina oficina = alerta.getDispositivo().getOficina();
        oficina.acreditar(credito);

        atendidas.add(alerta);

        return alerta;
    }

    // Consulta la hora absoluta.
    public long getHoraSimulacion() {
        return horaSimulacion;
    }

    public List<Oficina> consultarOficinas() {
        ArrayList<Oficina> lista =
                new ArrayList<Oficina>(oficinas.values());

        lista.sort(Comparator.comparing(Oficina::getCodigo));

        return Collections.unmodifiableList(lista);
    }


    public List<Dispositivo> consultarDispositivos(
            String codigoOficina) {

        Oficina oficina = buscarOficina(codigoOficina);

        ArrayList<Dispositivo> lista =
                oficina.dispositivosOrdenados();

        return Collections.unmodifiableList(lista);
    }


    public Iterable<Alerta> consultarPendientes() {
        ArrayList<Alerta> lista = new ArrayList<Alerta>();

        for (Alerta alerta : bandeja) {
            lista.add(alerta);
        }

        return Collections.unmodifiableList(lista);
    }

    public List<Alerta> consultarAtendidas() {
        ArrayList<Alerta> copia =
                new ArrayList<Alerta>(atendidas);

        return Collections.unmodifiableList(copia);
    }

    
    public List<Alerta> consultarAtendidasConRetraso() {
        ArrayList<Alerta> lista = new ArrayList<Alerta>();

        for (Alerta alerta : atendidas) {
            long retraso = alerta.retrasoHoras(
                    alerta.getHoraAtencion()
            );

            if (retraso > 0) {
                lista.add(alerta);
            }
        }

     
        Collections.sort(lista);

        return Collections.unmodifiableList(lista);
    }

    // Devuelve las facturas de una oficina.
    public List<Factura> consultarFacturas(String codigoOficina) {
        Oficina oficina = buscarOficina(codigoOficina);

        return oficina.getFacturas();
    }

    /*
     * Suma los montos de las facturas por franja.
     * Estos montos se reportan antes de descontar créditos.
     */
    public Map<FranjaTarifaria, BigDecimal> totalesPorFranja() {
        EnumMap<FranjaTarifaria, BigDecimal> totales =
                new EnumMap<FranjaTarifaria, BigDecimal>(
                        FranjaTarifaria.class
                );

        for (FranjaTarifaria franja : FranjaTarifaria.values()) {
            totales.put(franja, BigDecimal.ZERO);
        }

        for (Oficina oficina : oficinas.values()) {

            for (Factura factura : oficina.getFacturas()) {

                for (FranjaTarifaria franja
                        : FranjaTarifaria.values()) {

                    BigDecimal acumulado = totales.get(franja);
                    BigDecimal monto = factura.getMontos().get(franja);

                    totales.put(
                            franja,
                            acumulado.add(monto)
                    );
                }
            }
        }

        return Collections.unmodifiableMap(totales);
    }

    // Suma los créditos realmente aplicados en las facturas.
    public BigDecimal totalCreditosAplicados() {
        BigDecimal total = BigDecimal.ZERO;

        for (Oficina oficina : oficinas.values()) {

            for (Factura factura : oficina.getFacturas()) {
                total = total.add(
                        factura.getCreditoAplicado()
                );
            }
        }

        return total;
    }

    // Suma los totales finales de todas las facturas.
    public BigDecimal totalFacturado() {
        BigDecimal total = BigDecimal.ZERO;

        for (Oficina oficina : oficinas.values()) {

            for (Factura factura : oficina.getFacturas()) {
                total = total.add(factura.getTotal());
            }
        }

        return total;
    }
}