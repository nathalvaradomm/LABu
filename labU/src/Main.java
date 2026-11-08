
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Scanner;
import java.util.NoSuchElementException;// Detecta cuando ya no hay entrada disponible.

public class Main {

    private final SistemaTorreGT sistema;
    private final Scanner entrada;

    public Main() {
        sistema = new SistemaTorreGT();
        entrada = new Scanner(System.in);
        cargarDatosIniciales();
    }

    public static void main(String[] args) {
        Main programa = new Main();
        programa.ejecutarMenu();
    }

    
    private void cargarDatosIniciales() {
        sistema.registrarOficina("A101", "Estudio Norte");
        sistema.registrarOficina("A102", "Tecnologia Central");
        sistema.registrarOficina("B201", "Consultoria Sur");

        Oficina oficina1 = sistema.buscarOficina("A101");
        Oficina oficina2 = sistema.buscarOficina("A102");
        Oficina oficina3 = sistema.buscarOficina("B201");

        sistema.registrarDispositivo(
                new Luminaria("L01", oficina1, 60)
        );

        sistema.registrarDispositivo(
                new Luminaria("L02", oficina2, 100)
        );

        sistema.registrarDispositivo(
                new Climatizador("C01", oficina1, 1500)
        );

        sistema.registrarDispositivo(
                new Climatizador("C02", oficina3, 1200)
        );

        sistema.registrarDispositivo(
                new ClimatizadorInverter("I01", oficina2, 1500)
        );

        sistema.registrarDispositivo(
                new ClimatizadorInverter("I02", oficina3, 1000)
        );

        sistema.registrarDispositivo(
                new SensorHumo("H01", oficina1, 5)
        );

        sistema.registrarDispositivo(
                new SensorHumo("H02", oficina2, 4)
        );

        sistema.registrarDispositivo(
                new CamaraSeguridad("K01", oficina2, 20)
        );

        sistema.registrarDispositivo(
                new CamaraSeguridad("K02", oficina3, 15)
        );
    }

    
    private void ejecutarMenu() {
        boolean continuar = true;

        System.out.println("TORREGT");
        System.out.println("Administracion de oficinas inteligentes");

        while (continuar) {
            long hora = sistema.getHoraSimulacion();

            System.out.println();
            System.out.println("Dia: " + hora / 24);
            System.out.println("Hora del dia: " + hora % 24 + ":00");
            System.out.println("Hora absoluta: " + hora);

            System.out.println("\n1. Registrar oficina");
            System.out.println("2. Registrar dispositivo");
            System.out.println("3. Encender dispositivo");
            System.out.println("4. Apagar dispositivo");
            System.out.println("5. Ajustar nivel");
            System.out.println("6. Registrar lectura");
            System.out.println("7. Registrar movimiento");
            System.out.println("8. Avanzar horas");
            System.out.println("9. Atender alerta mas urgente");
            System.out.println("10. Consultar oficinas");
            System.out.println("11. Consultar dispositivos de una oficina");
            System.out.println("12. Consultar alertas pendientes");
            System.out.println("13. Consultar alertas atendidas con retraso");
            System.out.println("14. Consultar facturas de una oficina");
            System.out.println("15. Consultar totales facturados");
            System.out.println("16. Consultar consumo actual por oficina");
            System.out.println("17. Consultar todas las alertas atendidas");
            System.out.println("0. Salir");

            try {
                int opcion = leerEntero("Opcion: ");

                if (opcion == 0) {
                    continuar = false;

                } else if (opcion == 1) {
                    String codigo = leerTexto("Codigo de oficina: ");
                    String inquilino = leerTexto("Inquilino: ");

                    sistema.registrarOficina(codigo, inquilino);

                    System.out.println("Oficina registrada.");

                } else if (opcion == 2) {
                    registrarDispositivo();

                } else if (opcion == 3) {
                    String codigo = leerTexto("Codigo del dispositivo: ");

                    sistema.encender(codigo);

                    System.out.println("Dispositivo encendido.");

                } else if (opcion == 4) {
                    String codigo = leerTexto("Codigo del dispositivo: ");

                    sistema.apagar(codigo);

                    System.out.println("Dispositivo apagado.");

                } else if (opcion == 5) {
                    String codigo = leerTexto("Codigo del dispositivo: ");
                    int nivel = leerEntero("Nuevo nivel: ");

                    sistema.regular(codigo, nivel);

                    System.out.println("Nivel actualizado.");

                } else if (opcion == 6) {
                    String codigo = leerTexto("Codigo del dispositivo: ");
                    double lectura = leerDecimal(
                            "Lectura en grados C o ppm: "
                    );

                    sistema.registrarLectura(codigo, lectura);

                    System.out.println(
                            "Lectura registrada y alerta evaluada."
                    );

                } else if (opcion == 7) {
                    String codigo = leerTexto("Codigo de camara: ");

                    sistema.registrarMovimiento(codigo);

                    System.out.println(
                            "Movimiento registrado y alerta evaluada."
                    );

                } else if (opcion == 8) {
                    avanzarHoras();

                } else if (opcion == 9) {
                    Alerta alerta = sistema.atenderMasUrgente();

                    System.out.println(alerta);

                } else if (opcion == 10) {
                    mostrar(sistema.consultarOficinas());

                } else if (opcion == 11) {
                    String codigo = leerTexto("Codigo de oficina: ");

                    mostrar(sistema.consultarDispositivos(codigo));

                } else if (opcion == 12) {
                    mostrar(sistema.consultarPendientes());

                } else if (opcion == 13) {
                    mostrar(sistema.consultarAtendidasConRetraso());

                } else if (opcion == 14) {
                    String codigo = leerTexto("Codigo de oficina: ");

                    mostrar(sistema.consultarFacturas(codigo));

                } else if (opcion == 15) {
                    mostrarTotales();

                } else if (opcion == 16) {
                    mostrarAcumulados();

                } else if (opcion == 17) {
                    mostrar(sistema.consultarAtendidas());

                } else {
                    System.out.println("Opcion inexistente.");
                }

            } catch (NoSuchElementException e) {
                System.out.println("La entrada ha terminado.");
                continuar = false;

            } catch (IllegalArgumentException e) {
                System.out.println(
                        "Operacion rechazada: " + e.getMessage()
                );

            } catch (IllegalStateException e) {
                System.out.println(
                        "Operacion rechazada: " + e.getMessage()
                );
            }
        }

        System.out.println("Programa finalizado.");
    }

   
    private void registrarDispositivo() {
        System.out.println("1. Luminaria");
        System.out.println("2. Climatizador");
        System.out.println("3. Climatizador inverter");
        System.out.println("4. Sensor de humo");
        System.out.println("5. Camara de seguridad");

        int tipo = leerEntero("Tipo: ");

        if (tipo < 1) {
            throw new IllegalArgumentException("Tipo inexistente.");
        }

        if (tipo > 5) {
            throw new IllegalArgumentException("Tipo inexistente.");
        }

        String codigo = leerTexto("Codigo nuevo: ");
        String codigoOficina = leerTexto("Codigo de oficina: ");

        Oficina oficina = sistema.buscarOficina(codigoOficina);

        double potencia = leerDecimal("Potencia nominal en watts: ");

        Dispositivo dispositivo;

        if (tipo == 1) {
            dispositivo = new Luminaria(codigo, oficina, potencia);

        } else if (tipo == 2) {
            dispositivo = new Climatizador(codigo, oficina, potencia);

        } else if (tipo == 3) {
            dispositivo = new ClimatizadorInverter(
                    codigo, oficina, potencia
            );

        } else if (tipo == 4) {
            dispositivo = new SensorHumo(codigo, oficina, potencia);

        } else {
            // El tipo ya fue validado: aquí necesariamente es 5.
            dispositivo = new CamaraSeguridad(
                    codigo, oficina, potencia
            );
        }

        sistema.registrarDispositivo(dispositivo);

        System.out.println("Dispositivo registrado:");
        System.out.println(dispositivo);
    }

    
    private void avanzarHoras() {
        int cantidad = leerEntero(
                "Horas a avanzar, entre 1 y 8760: "
        );

        if (cantidad < 1) {
            throw new IllegalArgumentException(
                    "Debe avanzar al menos una hora."
            );
        }

        if (cantidad > 8760) {
            throw new IllegalArgumentException(
                    "El maximo por operacion es 8760 horas."
            );
        }

        if (sistema.getHoraSimulacion() > Long.MAX_VALUE - cantidad) {
            throw new IllegalArgumentException(
                    "El reloj excederia su rango."
            );
        }

        long diaAnterior = sistema.getHoraSimulacion() / 24;

        for (int i = 0; i < cantidad; i++) {
            sistema.avanzarHora();
        }

        long diaActual = sistema.getHoraSimulacion() / 24;

        System.out.println("Horas avanzadas: " + cantidad);
        System.out.println(
                "Cierres diarios realizados: " + (diaActual - diaAnterior)
        );
    }

    
    private void mostrarAcumulados() {

        for (Oficina oficina : sistema.consultarOficinas()) {
            System.out.println();
            System.out.println(oficina);

            // Cada Entry contiene una franja y su consumo
            for (Map.Entry<FranjaTarifaria, Double> dato
                    : oficina.getConsumoPorFranja().entrySet()) {

                System.out.println(
                        dato.getKey() + ": "
                                + dato.getValue() + " kWh"
                );
            }

            System.out.println(
                    "Credito generado hoy: Q"
                            + dinero(oficina.getCreditosDia())
            );
        }
    }

    // Muestra los resultados de las facturas generadas
    private void mostrarTotales() {
        System.out.println(
                "Montos historicos por franja, antes de creditos:"
        );

        for (Map.Entry<FranjaTarifaria, BigDecimal> dato
                : sistema.totalesPorFranja().entrySet()) {

            System.out.println(
                    dato.getKey() + ": Q" + dinero(dato.getValue())
            );
        }

        System.out.println(
                "Creditos aplicados: Q"
                        + dinero(sistema.totalCreditosAplicados())
        );

        System.out.println(
                "Total de las facturas: Q"
                        + dinero(sistema.totalFacturado())
        );
    }

    /*
     * Permite mostrar colecciones de distintos tipos.
     * Al imprimir cada objeto se utiliza su toString().
     */
    private void mostrar(Iterable<?> elementos) {
        boolean vacio = true;

        for (Object elemento : elementos) {
            System.out.println();
            System.out.println(elemento);
            vacio = false;
        }

        if (vacio) {
            System.out.println("No hay datos para mostrar.");
        }
    }

    
    private String dinero(BigDecimal monto) {
        return monto.setScale(2, RoundingMode.HALF_UP)
                .toPlainString();
    }

   
    private int leerEntero(String mensaje) {

        while (true) {
            String texto = leerTexto(mensaje);

            try {
                return Integer.parseInt(texto);

            } catch (NumberFormatException e) {
                System.out.println(
                        "Ingrese un numero entero valido."
                );
            }
        }
    }

   
    private double leerDecimal(String mensaje) {

        while (true) {
            String texto = leerTexto(mensaje);

            try {
                double valor = Double.parseDouble(texto);

                if (Double.isFinite(valor)) {
                    return valor;
                }

            } catch (NumberFormatException e) {
                // El mensaje se muestra debajo.
            }

            System.out.println(
                    "Ingrese un numero finito con punto decimal."
            );
        }
    }

   
    private String leerTexto(String mensaje) {
        System.out.print(mensaje);

        if (!entrada.hasNextLine()) {
            throw new NoSuchElementException("Fin de entrada.");
        }

        return entrada.nextLine().trim();
    }
}