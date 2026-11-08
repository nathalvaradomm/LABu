
import java.util.Optional;

public class SensorHumo extends Dispositivo
        implements Medible, GeneradorAlertas {

   
    private double ultimaConcentracion;

   
    private boolean tieneLectura = false;

    public SensorHumo(String codigo,Oficina oficina,double potencia) {
        super(codigo, oficina, potencia, true, 0);
    }

  
    public void off() {
        throw new IllegalStateException(
                "El sensor de humo no puede apagarse."
        );
    }

 
    protected double potenciaEfectivaW() {
        return getPotenciaNominalW();
    }


    public void registrarLectura(double ppm) {
        exigirEncendido();

        if (!Double.isFinite(ppm)) {
            throw new IllegalArgumentException(
                    "La concentracion debe ser un numero finito."
            );
        }

        if (ppm < 0) {
            throw new IllegalArgumentException(
                    "La concentracion no puede ser negativa."
            );
        }

        if (ppm > 1000) {
            throw new IllegalArgumentException(
                    "La concentracion no puede superar 1000 ppm."
            );
        }

        ultimaConcentracion = ppm;
        tieneLectura = true;
    }

    
    public Alerta generarAlerta(long codigo, long horaSimulacion) {

        if (!tieneLectura) {
            return null;
        }

        if (ultimaConcentracion < 150) {
            return null;
        }

        Severidad severidad;

        if (ultimaConcentracion >= 300) {
            severidad = Severidad.CRITICA;
        } else {
            severidad = Severidad.ALTA;
        }


        return new Alerta(
                codigo,
                this,
                horaSimulacion,
                severidad,
                "Concentracion de humo: "
                        + ultimaConcentracion + " ppm."
        );
    }


    public Optional<Medible> comoMedible() {
        return Optional.of(this);
    }

    
    public Optional<GeneradorAlertas> comoGeneradorAlertas() {
        return Optional.of(this);
    }

 
    public boolean tieneLectura() {
        return tieneLectura;
    }


    public double getUltimaConcentracion() {

        if (!tieneLectura) {
            throw new IllegalStateException(
                    "Aun no hay una lectura registrada."
            );
        }

        return ultimaConcentracion;
    }


    public String toString() {
        String concentracion;

        if (tieneLectura) {
            concentracion = ultimaConcentracion + " ppm";
        } else {
            concentracion = "Sin lectura";
        }

        String cadena = "Sensor de humo\n"+ super.toString() + "\nConcentracion: " + concentracion;
        return cadena;
    }
}