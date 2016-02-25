# Open Hardware Monitor

El archivo config.properties permite habilitar el monitoreo utilizando [Open Hardware Monitor](http://openhardwaremonitor.org/). En este archivo se deben configurar las siguientes lineas.

```
OH_PATH=C:\\Monitoreo\\OpenHardwareMonitor\\
OH_PROCESS=OpenHardwareMonitor.exe
```

A continuación se define el significado de cada elemento:

* **OH_PATH**: Directorio donde se encuentra la aplicación.
* **OH_PROCESS**: Nombre del ejecutable de OpenHardware.

## Variables de Monitorización

A continuación se describen los elementos que pueden ser monitoreados por Open Hardware.

* Tiempo: Tiempo en el que se tomó la medición. Formato: mm/dd/aaaa hh:mm:ss
* CPU
    - Reloj (Por Core). Medido en MHz.
    - Temperatura (Por Core). En grados Celcius.
    - Carga (Por Core y Total). La carga total es la sumatoria de la carga de cada core / el número de cores.
    - Bus. Medido en MHz.
* RAM
    - Carga
    - Datos (utilizada y disponible)
* HDD 
    - Temperatura. En grados Celcius.
    - Espacio Utilizado
* GPU
    - Voltaje. En Voltios
    - Reloj (Core y Memoria). En MHz.
    - Temperatura
    - Carga. Porcentaje de uso del dispositivo.
    - Ventilador. Porcentaje respecto a las rpm del ventilador.


