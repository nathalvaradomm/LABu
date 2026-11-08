
public class Luminaria extends DispositivoRegulable {

   
    public Luminaria(String codigo,Oficina oficina,double potencia) {  
        super(codigo, oficina, potencia, 10, 100);
    }

    protected double potenciaEfectivaW() {
        return getPotenciaNominalW() * (getNivel() / 100.0);
    }

    public String toString() {
        String cadena = "Luminaria\n" + super.toString();

        return cadena;
    }
}