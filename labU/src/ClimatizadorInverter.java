
public class ClimatizadorInverter extends Climatizador {
    public ClimatizadorInverter(String codigo,Oficina oficina,double potencia) {
        super(codigo, oficina, potencia, 10);
    }

   
    protected double potenciaEfectivaW() {
        return super.potenciaEfectivaW() * 0.80;
    }

 
    public String toString() {
        String cadena = super.toString() + "\nTecnologia: Inverter";
        return cadena;
    }
}