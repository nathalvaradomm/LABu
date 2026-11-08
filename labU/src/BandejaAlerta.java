
import java.util.ArrayList;
import java.util.Collections; // Collections nos permite trabajar con colecciones, lo veremos implementado en unmodifiableList() para impedir que se borren datos duplicados 
import java.util.Iterator; // Iterator recorre los elementos uno por uno, además nos permite usar for each
import java.util.TreeSet; // evita duplicados y ordena el alacenaje de los dstos


public class BandejaAlerta implements Iterable<Alerta> { // deja que se pueda recorrer la bandeja 

  
    private TreeSet<Alerta> pendientes; // Coleccion que ordena las alertas pendientes por su urgencia 

    
    public BandejaAlerta() {// costructor para crear bandeja vacia
        pendientes = new TreeSet<>();
    }

    void agregar(Alerta alerta) {

        if (alerta == null) {
            throw new IllegalArgumentException(
                    "La alerta no puede ser nula"
            );
        }

        if (!alerta.estaPendiente()) {
            throw new IllegalArgumentException(
                    "La alerta ya fue atendida"
            );
        }

        
        if (!pendientes.add(alerta)) {// add devuelve false si ya existe una alerta que fue comparada y considerada equivalente a otra
            throw new IllegalArgumentException(
                    "La alerta ya esta en la bandeja"
            );
        }
    }

    
    Alerta primera() {// Devuelve la más urgente SIN ELIMINARLA.

        if (estaVacia()) {
            throw new IllegalStateException(
                    "No hay alertas pendientes"
            );
        }

        return pendientes.first(); // .first para devolver la alerta más urgente
    }

    
    Alerta retirarPrimera() {// Retira y devuelve la alerta más urgente
        Alerta alerta = primera();
        pendientes.remove(alerta);
        return alerta;
    }

    
    public boolean estaVacia() {// Informa si la bandeja está vacía
        return pendientes.isEmpty();
    }

    // Muestra el numero de alertas hay pendientes
    public int size() {
        return pendientes.size();
    }


    public Iterator<Alerta> iterator() { // proteccion de las alertas para asi respetar el orden y no eliminarlas 
       ArrayList<Alerta> copia = new ArrayList<Alerta>(pendientes);
        return Collections.unmodifiableList(copia).iterator();
    }
}