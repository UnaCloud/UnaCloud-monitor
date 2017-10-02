# Monitoreo Sigar

El archivo config.properties permite habilitar el monitoreo utilizando Sigar. En este archivo se deben configurar las siguientes lineas.

```
SERVICES=sigar:true,openHardware:false,perfmon:false,powerGadget:false
SIGAR_RECORD_PATH=C:\\Monitoreo\\Monitoreo_Logs\\
SIGAR_HEADERS=UpTime,RamFree,RamUsed,...,Processes_Detail,Processes_General
```

A continuación se define el significado de cada elemento:

* **SERVICES**: Permite habilitar o deshabilitar un servicio de medición. Si se quiere habilitar la monitorización para algún elemento en especial se debe definir el valor **true** o **false**. Los elementos se separan utilizando ",". Los elementos posibles son:
    - sigar
    - openHardware
    - perfmon
    - powerGadget
* **SIGAR_RECORD_PATH**: Define la ruta donde se guardarán los archivos de monitoreo de sigar.
* **SIGAR_HEADERS**: Define los elementos a realizar monitoreo. Para más información consultar sección "Variables de Monitorización"

## Variables de Monitorización

A continuación se describen los elementos que pueden ser monitoreados.

* **Timestamp**: Obtiene la fecha y hora en que se realizó la medición. Ej. 01/03/2016  01:23:46 p.m.
* **Username**: Permite conocer el usuario activo de Windows. Utiliza el comando "cmd.exe /c quser"
* **UpTime**: Obtiene el tiempo desde que la máquina se inició en segundos. [[Fuente Sigar]](http://cpansearch.perl.org/src/DOUGM/hyperic-sigar-1.6.3-src/docs/javadoc/org/hyperic/sigar/Uptime.html#getUptime())
* **RamFree**: Obtiene el total de memoria RAM disponible en Bytes. UnaCloud regresa la información en Megabytes. Equivalente administrador de tareas de Windows. [[Fuente Sigar]](http://cpansearch.perl.org/src/DOUGM/hyperic-sigar-1.6.3-src/docs/javadoc/org/hyperic/sigar/Mem.html#getFree())
* **RamUsed**: Obtiene el total de memoria RAM usada en Bytes. UnaCloud regresa la información en Megabytes. Equivalente administrador de tareas de Windows. [[Fuente Sigar]](http://cpansearch.perl.org/src/DOUGM/hyperic-sigar-1.6.3-src/docs/javadoc/org/hyperic/sigar/Mem.html#getUsed())
* **MemFreePercent**: Obtiene el porcentaje total de memoria disponible. [[Fuente Sigar]](http://cpansearch.perl.org/src/DOUGM/hyperic-sigar-1.6.3-src/docs/javadoc/org/hyperic/sigar/Mem.html#getFreePercent())
* **MemUsedPercent**: Obtiene el porcentaje total de memoria utilizada. [[Fuente Sigar]](http://cpansearch.perl.org/src/DOUGM/hyperic-sigar-1.6.3-src/docs/javadoc/org/hyperic/sigar/Mem.html#getFreePercent())
* **SwapMemoryFree**: Obtiene el total de memoria swap disponible. UnaCloud regresa la información en Megabytes. [[Fuente Sigar]](http://cpansearch.perl.org/src/DOUGM/hyperic-sigar-1.6.3-src/docs/javadoc/org/hyperic/sigar/Swap.html#getFree())
* **SwapMemoryPageIn**: Obtiene el total de páginas de entrada. UnaCloud regresa la información en Megabytes. [[Fuente Sigar]](http://cpansearch.perl.org/src/DOUGM/hyperic-sigar-1.6.3-src/docs/javadoc/org/hyperic/sigar/Swap.html#getPageIn())
* **SwapMemoryPageOut**: Obtiene el total de páginas de salida. UnaCloud regresa la información en Megabytes. [[Fuente Sigar]](http://cpansearch.perl.org/src/DOUGM/hyperic-sigar-1.6.3-src/docs/javadoc/org/hyperic/sigar/Swap.html#getPageOut())
* **SwapMemoryUsed**: Obtiene el total de memoria swap utilizada. UnaCloud regresa la información en Megabytes. [[Fuente Sigar]](http://cpansearch.perl.org/src/DOUGM/hyperic-sigar-1.6.3-src/docs/javadoc/org/hyperic/sigar/Swap.html#getUsed())
* **HDFreeSpace**: Obtiene el total de espacio en disco disponible. UnaCloud regresa la información en Gigabytes.[[Fuente Sigar]](http://cpansearch.perl.org/src/DOUGM/hyperic-sigar-1.6.3-src/docs/javadoc/org/hyperic/sigar/FileSystemUsage.html#getFree())
* **HDUsedSpace**: Obtiene el total de espacio en disco usado. UnaCloud regresa la información en Gigabytes.[[Fuente Sigar]](http://cpansearch.perl.org/src/DOUGM/hyperic-sigar-1.6.3-src/docs/javadoc/org/hyperic/sigar/FileSystemUsage.html#getUsed())
* **NetRXBytes**: Obtiene el total de bytes recibidos por la interface de red principal. Se debe tener en cuenta que si la interfaz se reinicia, la métrica comenzará desde cero. [[Fuente Sigar]](http://cpansearch.perl.org/src/DOUGM/hyperic-sigar-1.6.3-src/docs/javadoc/org/hyperic/sigar/NetInterfaceStat.html#getRxBytes())
* **NetTxBytes**: Obtiene el total de bytes recibidos por la interface de red principal. Al igual que con los Bytes recibidos, si la interfaz se reinicia, la métrica comenzará desde cero. [[Fuente Sigar]](http://cpansearch.perl.org/src/DOUGM/hyperic-sigar-1.6.3-src/docs/javadoc/org/hyperic/sigar/NetInterfaceStat.html#getRxBytes())
* **NetSpeed**: Obtiene la velocidad de la interfaz de red. Esta velocidad vienen dada en Bytes. [[Fuente Sigar]](http://cpansearch.perl.org/src/DOUGM/hyperic-sigar-1.6.3-src/docs/javadoc/org/hyperic/sigar/NetInterfaceStat.html#getSpeed())
* **NetRXErrors**: Obtiene el número de paquetes recibidos con errores. Equivalente a utilizar el comando netstat -s en Windows. [[Fuente Sigar]](http://cpansearch.perl.org/src/DOUGM/hyperic-sigar-1.6.3-src/docs/javadoc/org/hyperic/sigar/NetInterfaceStat.html#getRxErrors())
* **NetTxErrors**: Obtiene el número de paquetes transmitidos con errores. Equivalente a utilizar el comando netstat -s en Windows. [[Fuente Sigar]](http://cpansearch.perl.org/src/DOUGM/hyperic-sigar-1.6.3-src/docs/javadoc/org/hyperic/sigar/NetInterfaceStat.html#getTxErrors())
* **NetRxPackets**: Obtiene el número de paquetes recibidos. Equivalente a utilizar el comando netstat -s en Windows. La métrica se mide cuando la máquina se inicia. [[Fuente Sigar]](http://cpansearch.perl.org/src/DOUGM/hyperic-sigar-1.6.3-src/docs/javadoc/org/hyperic/sigar/NetInterfaceStat.html#getRxPackets())
* **NetTxPackets**: Obtiene el número de paquetes transmitidos. Equivalente a utilizar el comando netstat -s en Windows. La métrica se mide cuando la máquina se inicia. [[Fuente Sigar]](http://cpansearch.perl.org/src/DOUGM/hyperic-sigar-1.6.3-src/docs/javadoc/org/hyperic/sigar/NetInterfaceStat.html#getTxPackets()).
* **Processes_Detail**: Se obtiene la siguiente información para cada proceso en ejecución. [[Fuente Sigar]](http://cpansearch.perl.org/src/DOUGM/hyperic-sigar-1.6.3-src/docs/javadoc/org/hyperic/sigar/ProcState.html).
    - Nombre del proceso.
    - Usuario quien lo ejecuta
    - Memoria RAM asignada por SO. En Bytes.
    - Prioridad del proceso. En Windows un número entero de 1 a 31 según [[documentación]](https://msdn.microsoft.com/en-us/library/windows/desktop/ms685100(v=vs.85).aspx).
    - Procesador en el que se se ejecutó. No se encuentra implementado en Windows.
    - Estado del proceso. Running, Zombie, etc. En Windows solo se obtienen datos de procesos en ejecución.
    - Número de hilos/subprocesos que ejecuta.
    - Porcentaje CPU del proceso. **Actualmente no funcional**
    - Fecha en la que inició el proceso. **Actualmente no funcional**
* **Processes_General**: Se obtiene la siguiente información de todos los procesos del sistema en el que se toma la medición. Se debe tener en cuenta que en Windows solo se obtendrán datos para procesos en ejecución.[[Fuente Sigar]](http://cpansearch.perl.org/src/DOUGM/hyperic-sigar-1.6.3-src/docs/javadoc/org/hyperic/sigar/ProcStat.html). [[Procesos Windows]](https://technet.microsoft.com/en-us/library/cc730909.aspx):
    - Número de procesos inactivos.
    - Número de procesos en ejecución.
    - Número de procesos en estado sleep.
    - Número de procesos en estado detenido.
    - Número de procesos en estado zombie


