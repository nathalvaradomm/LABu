import java.util.Optional;

public abstract class DispositivoRegulable
        extends Dispositivo implements Regulable {

    
    private int nivel;
    private  int nivelMinimo;
    private  int nivelMaximo;

   
    protected DispositivoRegulable(String codigo,Oficina oficina,double potencia,int minimo,int nivelInicial) {
        super(codigo, oficina, potencia, false, 0);

        
        if (minimo < 1) {
            throw new IllegalArgumentException(
                    "El nivel minimo debe ser mayor que cero"
            );
        }

        if (minimo > 100) {
            throw new IllegalArgumentException(
                    "El nivel minimo no puede superar 100."
            );
        }

        if (nivelInicial < minimo) {
            throw new IllegalArgumentException(
                    "El nivel inicial no puede ser menor que el minimo."
            );
        }

        if (nivelInicial > 100) {
            throw new IllegalArgumentException(
                    "El nivel inicial no puede superar 100."
            );
        }

        this.nivelMinimo = minimo;
        this.nivelMaximo = 100;
        this.nivel = nivelInicial;
    }

    
    public final void ajustarNivel(int nuevoNivel) {
        exigirEncendido();

        if (nuevoNivel < nivelMinimo) {
            throw new IllegalArgumentException(
                    "El nivel no puede ser menor que " + nivelMinimo + "."
            );
        }

        if (nuevoNivel > nivelMaximo) {
            throw new IllegalArgumentException(
                    "El nivel no puede ser mayor que " + nivelMaximo + "."
            );
        }

        nivel = nuevoNivel;
    }

   
   
    public int getNivel() {
        return nivel;
    }


    public int getNivelMinimo() {
        return nivelMinimo;
    }

   
    public int getNivelMaximo() {
        return nivelMaximo;
    }

    
    public Optional<Regulable> comoRegulable() {
        return Optional.of(this);
    }

   
    public String toString() {String cadena = super.toString()+ "\nNivel: " + nivel + "%";
        return cadena;
    }
}