
import java.util.Optional; // Optional para devolver las capacidades de la camara

public class CamaraSeguridad extends Dispositivo 
        implements DetectorMovimiento, GeneradorAlertas {

    private int ultimaHoraMovimiento = -1;

    public CamaraSeguridad(String codigo,Oficina oficina, double potencia) { // constructor 
        super(codigo, oficina, potencia, false, 0); // recordar que la camara inica apagada
    }



    protected double potenciaEfectivaW() {
        return getPotenciaNominalW();
    }


    public void registrarMovimiento(int horaDelDia) {

        exigirEncendido(); 
        if (horaDelDia < 0) {
            throw new IllegalArgumentException(
                    "La hora no puede ser negativa"
            );
        }

        if (horaDelDia > 23) {
            throw new IllegalArgumentException(
                    "La hora no puede ser mayor que 23"
            );
        }

        ultimaHoraMovimiento = horaDelDia;
    }


    public Alerta generarAlerta(long codigo, long horaSimulacion) {
        
        if (ultimaHoraMovimiento == -1) {
            return null;
        }

        
        if (ultimaHoraMovimiento >= 7) {
            if (ultimaHoraMovimiento < 19) {
                return null;
            }
        }

        
        return new Alerta(
                codigo, this,horaSimulacion,Severidad.ALTA,
                "Movimiento fuera de horario laboral, a las "
                        + ultimaHoraMovimiento + ":00."
        );
    }


    public Optional<DetectorMovimiento> comoDetector() { // como nos permite consultar capacidades 
        return Optional.of(this);
    }

  
    public Optional<GeneradorAlertas> comoGeneradorAlertas() {
        return Optional.of(this);
    }

    
    public int getUltimaHoraMovimiento() {
        return ultimaHoraMovimiento;
    }


    public String toString() {
        String movimiento;

        if (ultimaHoraMovimiento == -1) {
            movimiento = "ninguno";
        } else {
            movimiento = ultimaHoraMovimiento + ":00";
        }

        String cadena = "Camara de seguridad\n"+ super.toString()+ "\nUltimo movimiento: " + movimiento;
        return cadena;
        
    
    }
}