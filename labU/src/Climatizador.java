
import java.util.Optional;


public class Climatizador extends DispositivoRegulable
        implements Medible, GeneradorAlertas {

    
    private double ultimaTemp;

    /
    private boolean tieneLectura = false;

    
    public Climatizador(String codigo,Oficina oficina,double potencia) {
        this(codigo, oficina, potencia, 20);
    }


    protected Climatizador( String codigo,Oficina oficina,double potencia, int minimo) {
        super(codigo, oficina, potencia, minimo, 50);
    }

  

    protected double potenciaEfectivaW() {
        return getPotenciaNominalW()
                * (0.30 + 0.70 * getNivel() / 100.0);
    }

   
    public void registrarLectura(double temperatura) {
        exigirEncendido();

    

        if (temperatura < -10) {
            throw new IllegalArgumentException(
                    "La temperatura no puede ser menor que -10"
            );
        }

        if (temperatura > 60) {
            throw new IllegalArgumentException(
                    "La temperatura no puede ser mayor que 60"
            );
        }

        ultimaTemp = temperatura;
        tieneLectura = true;
    }

    /*
     * Implementa GeneradorAlertas.
     * El sistema proporciona el código y la hora absoluta.
     * Devuelve null cuando no corresponde generar una alerta.
     */
    @Override
    public Alerta generarAlerta(long codigo, long horaSimulacion) {

        if (!tieneLectura) {
            return null;
        }

        if (ultimaTemp < 28) {
            return null;
        }

        Severidad severidad;

        if (ultimaTemp >= 40) {
            severidad = Severidad.CRITICA;
        } else if (ultimaTemp >= 32) {
            severidad = Severidad.ALTA;
        } else {
            severidad = Severidad.BAJA;
        }

      return new Alerta( codigo,dispositivoActual,horaSimulacion,severidad,"Temperatura: " + ultimaTemp + " grados C.");
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


    public double getUltimaTemp() {

        if (!tieneLectura) {
            throw new IllegalStateException(
                    "Lo sentimos, aun no hay una lectura registrada"
            );
        }

        return ultimaTemp;
    }

 
    public String toString() {
        String temperatura;

        if (tieneLectura) {
            temperatura = ultimaTemp + " grados C";
        } else {
            temperatura = "sin lectura";
        }

        String cadena = "Climatizador\n"+ super.toString()+ "\nTemperatura: " + temperatura;     
            return cadena;

    }
}