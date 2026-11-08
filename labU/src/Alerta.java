import java.math.BigDecimal; // para utilizar 
import java.math.RoundingMode;

public class Alerta implements Comparable<Alerta> {
    private final long codigo;// se utilizará long para poder ingresar numeros  grandes en codigos
    private final Dispositivo dispositivo;
    private final long horaGeneracion;
    private final Severidad severidad;
    private final String mensaje;
    private long horaAtencion = -1; // -1 para indicar que aun está pendiente

    if (codigo <= 0) {
            throw new IllegalArgumentException(
                    "El codigo debe ser mayor que cero"
            );
        }

        if (dispositivo == null) {
            throw new IllegalArgumentException(
                    "El dispositivo es obligatorio"
            );
        }

        if (horaGeneracion < 0) {
            throw new IllegalArgumentException(
                    "La hora de generacion no puede ser negativa"
            );
        }

        if (severidad == null) {
            throw new IllegalArgumentException(
                    "La severidad es obligatoria"
            );
        }

        if (mensaje == null) {
            throw new IllegalArgumentException(
                    "El mensaje es obligatorio"
            );
        }

        if (mensaje.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El mensaje no puede estar vacio"
            );
        }

        this.codigo = codigo;
        this.dispositivo = dispositivo;
        this.horaGeneracion = horaGeneracion;
        this.severidad = severidad;
        this.mensaje = mensaje.trim();
    
    @Override
    public int compareTo(Alerta otra) {

        
        int resultado = Integer.compare(// mayor prioridad
                otra.severidad.getPrioridad(),
                severidad.getPrioridad()
        );

        
        if (resultado == 0) {// Si empatan, se atiende la más antigua
            resultado = Long.compare(
                    horaGeneracion,
                    otra.horaGeneracion
            );
        }

        
        if (resultado == 0) {// Si coinciden las horas, primero el código menor
            resultado = Long.compare(codigo, otra.codigo);
        }

        return resultado;
    }

    
    public boolean estaPendiente() {// Informa que todavía no tiene hora de atención
        return horaAtencion == -1;
    }

   
    public long retrasoHoras(long hora) { // Calcula horas excedentes del plazo permitido.

        if (hora < horaGeneracion) {
            throw new IllegalArgumentException(
                    "La hora no puede ser anterior a la generacion."
            );
        }

        // Math.max evita devolver un retraso negativo.
        return Math.max(
                0,
                hora - horaGeneracion - severidad.getPlazoHoras()
        );
    }

    
    public BigDecimal creditoPorRetraso(long hora) {// calcular el crédito monetario
        return severidad.calcularCredito(retrasoHoras(hora));
    }

    // Registra la atención
    void atender(long hora) {

        if (!estaPendiente()) {
            throw new IllegalStateException(
                    "La alerta ya fue atendida."
            );
        }

        // Valida la hora antes de modificar el atributo
        retrasoHoras(hora);

        horaAtencion = hora;
    }

    // Getters que permiten consultar datos 

    public long getCodigo() {
        return codigo;
    }

    public Dispositivo getDispositivo() {
        return dispositivo;
    }

    public long getHoraGeneracion() {
        return horaGeneracion;
    }

    public Severidad getSeveridad() {
        return severidad;
    }

    public String getMensaje() {
        return mensaje;
    }

    public long getHoraAtencion() {
        return horaAtencion;
    }

    
    public String toString() {// Describe la alerta sin modificar datos

        String estado;

        if (estaPendiente()) {
            estado = "Pendiente";
        } else {
            estado = "Atendida en h=" + horaAtencion
                    + ", retraso=" + retrasoHoras(horaAtencion)
                    + " h, credito=Q"
                    + creditoPorRetraso(horaAtencion)
                        .setScale(2, RoundingMode.HALF_UP); // RoundingMode.HALF_UP indica como redondear el valor
        }
            String cadena = "Alerta " + codigo+ "\nSeveridad: " + severidad+ "\nDispositivo: " + dispositivo.getCodigo()
            + "\nOficina: " + dispositivo.getOficina().getCodigo()+ "\nHora de generacion: " + horaGeneracion+ "\nEstado: " + estado
            + "\nMensaje: " + mensaje;
            return cadena;
            
            
            
    }
}