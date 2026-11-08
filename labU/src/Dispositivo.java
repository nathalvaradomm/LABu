
import java.util.Optional;
public abstract class Dispositivo {

    private final String codigo;
    private final Oficina oficina;
    private final double potenciaNominalW;
    private boolean on;
    private double consumoDiaKW;


    protected Dispositivo(String codigo,Oficina oficina,double potenciaNominalW,boolean encendido, double consumoDiaKW) {
        
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

        if (oficina == null) {
            throw new IllegalArgumentException(
                    "La oficina es obligatoria."
            );
        }

        if (!Double.isFinite(potenciaNominalW)) {
            throw new IllegalArgumentException(
                    "La potencia debe ser un numero finito."
            );
        }

        if (potenciaNominalW <= 0) {
            throw new IllegalArgumentException(
                    "La potencia debe ser mayor que cero."
            );
        }

        if (consumoDiaKW != 0) {
            throw new IllegalArgumentException(
                    "El dispositivo debe iniciar sin consumo."
            );
        }

        this.codigo = codigo.trim();
        this.oficina = oficina;
        this.potenciaNominalW = potenciaNominalW;
        this.on = encendido;
        this.consumoDiaKW = 0;
    }

   
    public void on() {
        on = true;
    }

    
    public void off() {
        on = false;
    }

    
    public final double consumoHoraKW() {

        if (!on) {
            return 0;
        }

        return potenciaEfectivaW() / 1000.0;
    }

    
    protected abstract double potenciaEfectivaW();

    
    protected  void exigirEncendido() {

        if (!on) {
            throw new IllegalStateException(
                    "El dispositivo esta apagado."
            );
        }
    }

    
    void validarAcumulacion(double consumo) {

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

        if (!Double.isFinite(consumoDiaKW + consumo)) {
            throw new IllegalArgumentException(
                    "El consumo acumulado excede el rango permitido."
            );
        }
    }

    
    void acumularConsumo(double consumo) {
        validarAcumulacion(consumo);
        consumoDiaKW = consumoDiaKW + consumo;
    }

    .
    void reiniciarDia() {
        consumoDiaKW = 0;
    }


    public Optional<Regulable> comoRegulable() {
        return Optional.empty();
    }

    public Optional<Medible> comoMedible() {
        return Optional.empty();
    }

    public Optional<DetectorMovimiento> comoDetector() {
        return Optional.empty();
    }

    public Optional<GeneradorAlertas> comoGeneradorAlertas() {
        return Optional.empty();
    }

   

    public String getCodigo() {
        return codigo;
    }

    public Oficina getOficina() {
        return oficina;
    }

    public double getPotenciaNominalW() {
        return potenciaNominalW;
    }

    public boolean isOn() {
        return on;
    }

    public double getConsumoDiaKW() {
        return consumoDiaKW;
    }

    
    public String toString() {
        String estado;

        if (on) {
            estado = "Encendido";
        } else {
            estado = "Apagado";
        }

        String cadena = "Codigo: " + codigo+ "\nOficina: " + oficina.getCodigo()+ "\nPotencia nominal: " + potenciaNominalW + " W"
                + "\nEstado: " + estado+ "\nConsumo del dia: " + consumoDiaKW + " kWh";
        return cadena;
    }
}