
import java.math.BigDecimal;
public enum FranjaTarifaria {

    VALLE("0.85"),
    NORMAL("1.40"),
    PUNTA("2.10");

   
    private final BigDecimal tarifa;

    
    FranjaTarifaria(String tarifa) {
        this.tarifa = new BigDecimal(tarifa);
    }
    public BigDecimal getTarifa() {
        return tarifa;
    }


    public static FranjaTarifaria deHora(int horaDelDia) {

        if (horaDelDia < 0) {
            throw new IllegalArgumentException(
                    "La hora no puede ser negativa."
            );
        }

        if (horaDelDia > 23) {
            throw new IllegalArgumentException(
                    "La hora no puede ser mayor que 23."
            );
        }

        
        if (horaDelDia < 6) {
            return VALLE;
        }
        if (horaDelDia < 18) {
            return NORMAL;
        }

        if (horaDelDia < 22) {
            return PUNTA;
        }

        return VALLE;
    }
}