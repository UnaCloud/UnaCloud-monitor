# Perfmon

El archivo config.properties permite habilitar el monitoreo utilizando Sigar. En este archivo se deben configurar las siguientes lineas.

```
PM_RECORD_PATH=C:\\Monitoreo\\Monitoreo_Logs\\
PM_MAX_SIZE=512
COUNTERS=\\Processor(0)\\% Processor Time\t\\Processor(1)\\% Processor Time\t\\Processor(2)\\% Processor Time\t\\Processor(3)\\% Processor Time\t\\Processor(4)\\% Processor Time\t\\Processor(5)\\% Processor Time\t\\Processor(6)\\% Processor Time\t\\Processor(7)\\% Processor Time
COUNTER_NAME=CPU_counter
```

A continuación se define el significado de cada elemento:

* PM_RECORD_PATH: Define la ruta donde se guardará el log de monitoreo.s
* PM_MAX_SIZE: Define el tamaño máximo del archivo. Este será pasado por parámetro cuando se crea el log.
* COUNTERS: Define las métricas a medir.
* COUNTER_NAME: Define el nombre del contador que será guardado en Windows.

# Métricas

A continuación se definen las métricas que se guardarán.

* Hora en la que se tomó la medición.
* Processor Time: Determina el porcentaje de uso del core. [Fuente Microsoft](https://technet.microsoft.com/en-us/library/cc938603.aspx)