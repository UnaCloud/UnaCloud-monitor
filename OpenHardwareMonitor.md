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
* Load CPU Core #1. Porcentaje de uso de CPU.
* Load CPU Core #2. Porcentaje de uso de CPU.
* Load CPU Total. Porcentaje de uso de CPU.
* Temperatura CPU Core #1. En Celcius.
* Temperatura CPU Core #2. En Celcius.
* Clock CPU Core #1. En MHz.
* Clock CPU Core #2. En MHz.
* Bus Speed. En MHz.
* Load Memoria RAM. En Porcentaje.
* Memoria RAM utilizada. En GB.
* Memoria RAM Disponible. En GB.
* Temperatura GPU Core. En Celcius.
* GPU Fan. En Porcentaje.
* Clock GPU Core. En MHz.
* Clock GPU Memory. En MHz.
* Voltage GPU. En Voltios.
* Load GPU Core. Porcentaje de uso de GPU.
* HDD Temperature. En Celcius.
* HDD Used Space. En Porcentaje.



