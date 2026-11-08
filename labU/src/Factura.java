
import java.math.BigDecimal; 
import java.math.RoundingMode;
import java.util.Collections;
import java.util.EnumMap; //  Map relaciona valores con claves de tipo enum
import java.util.Map;


public class Factura {

    
    private  Oficina oficina;

   
    private  long dia;
    private  Map<FranjaTarifaria, Double> consumos;
    private  Map<FranjaTarifaria, BigDecimal> montos;
    private  BigDecimal creditoGenerado;
    private  BigDecimal creditoAplicado;
    private  BigDecimal total;

    public Factura(
            Oficina oficina,
            long dia,
            Map<FranjaTarifaria, Double> consumos,
            BigDecimal credito) {

        if (oficina == null) {
            throw new IllegalArgumentException(
                    "La oficina es obligatoria."
            );
        }

        if (dia < 0) {
            throw new IllegalArgumentException(
                    "El dia no puede ser negativo."
            );
        }

        if (consumos == null) {
            throw new IllegalArgumentException(
                    "Los consumos son obligatorios."
            );
        }

        if (credito == null) {
            throw new IllegalArgumentException(
                    "El credito es obligatorio."
            );
        }


        if (credito.signum() < 0) {
            throw new IllegalArgumentException(
                    "El credito no puede ser negativo."
            );
        }

        this.oficina = oficina;
        this.dia = dia;

       
        EnumMap<FranjaTarifaria, Double> copiaConsumos =
                new EnumMap<>(FranjaTarifaria.class);

        EnumMap<FranjaTarifaria, BigDecimal> copiaMontos =
                new EnumMap<>(FranjaTarifaria.class);

        BigDecimal subtotal = BigDecimal.ZERO;

        
        for (FranjaTarifaria franja : FranjaTarifaria.values()) {

            Double consumo = consumos.get(franja);

            if (consumo == null) {
                throw new IllegalArgumentException(
                        "Falta el consumo de la franja " + franja + "."
                );
            }

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

            copiaConsumos.put(franja, consumo);

            
            BigDecimal monto = BigDecimal.valueOf(consumo)
                    .multiply(franja.getTarifa());

            copiaMontos.put(franja, monto);

            
            subtotal = subtotal.add(monto);
        }

        // Protege las copias para impedir modificaciones externas 
        this.consumos = Collections.unmodifiableMap(copiaConsumos); 
        this.montos = Collections.unmodifiableMap(copiaMontos);

        this.creditoGenerado = credito;
        this.creditoAplicado = credito.min(subtotal);

        /
        this.total = subtotal.subtract(creditoAplicado)
                .setScale(2, RoundingMode.HALF_UP);
    }



    public Oficina getOficina() {
        return oficina;
    }

    public long getDia() {
        return dia;
    }

    public Map<FranjaTarifaria, Double> getConsumos() {
        return consumos;
    }

    public Map<FranjaTarifaria, BigDecimal> getMontos() {
        return montos;
    }

    public BigDecimal getCreditoGenerado() {
        return creditoGenerado;
    }

    public BigDecimal getCreditoAplicado() {
        return creditoAplicado;
    }

    public BigDecimal getTotal() {
        return total;
    }

    
    private String dinero(BigDecimal monto) {
        return monto.setScale(2, RoundingMode.HALF_UP)
                .toPlainString();
    }


    public String toString() {
        String cadena = "Factura"
                + "\nOficina: " + oficina.getCodigo()
                + "\nDia: " + dia;

        BigDecimal subtotal = BigDecimal.ZERO;

        for (FranjaTarifaria franja : FranjaTarifaria.values()) {

            BigDecimal monto = montos.get(franja);
            subtotal = subtotal.add(monto);

            cadena = cadena
                    + "\n" + franja+ ": " + consumos.get(franja) + " kWh" + " x Q" + dinero(franja.getTarifa())
                    + " = Q" + dinero(monto);
        }

        BigDecimal excedente =
                creditoGenerado.subtract(creditoAplicado);

        cadena = cadena
                + "\nSubtotal: Q" + dinero(subtotal)+ "\nCredito generado: Q" + dinero(creditoGenerado)+ "\nCredito aplicado: Q" + dinero(creditoAplicado)
                
                
                + "\nExcedente descartado: Q" + dinero(excedente)
                + "\nTotal a pagar: Q" + dinero(total);

        return cadena;
    }
}