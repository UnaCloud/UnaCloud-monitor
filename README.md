# UnaCloudMonitoringComponent

UnaCloud consiste en 4 proyectos.

* Agente Monitoring Services
* Agent Monitoring
* Monitoring Communication
* Monitoring Shares

## Herramientas de Monitoreo

El proyecto actualmente funciona utilizando cuatro herramientas las cuales realizan las mediciones.

* Sigar 1.6.3
* PowerGadget
* OpenHardwareMonitor CPU
* Perfmon Log

# Sigar

Teniendo en cuenta la [documentación](http://cpansearch.perl.org/src/DOUGM/hyperic-sigar-1.6.3-src/docs/javadoc/org/hyperic/sigar/Sigar.html):

* **ProcCredName**: Información del usuario y grupo propietarios del proceso.
* **ProcCred**: Información de los UID y GID que son propierarios del proceso.
* **ProcMem**: Información de la memoria (resident, shared, virtual) utilizada por el proceso, ademas de otros valores de error.
* **ProcState**: Información de un proceso en particular; en que estado (Running, Zombie, etc.), cual es el process id, los threads activos, el número del procesador donde corre, la prioridad, entre otros.
* **ProcTime**: Información de tiempos de ejecución del proceso como por ejemplo hace cuanto corre.
* **ProcCpu**: Información del consumo de CPU por el proceso: únicamente porcentaje de uso, y trae los valores del ProcTime.
* **ProcExe**: Nos trae los nombres del directorio de trabajo del proceso y el nombre del ejecutable.
* **ProcStat**: Este nos trae información del status de todos los procesos: cuantos corren, cuantos detenidos, cuantos demonios, cuantos zombies.

# Instalación y Configuración

A continuación se presentan las instrucciones para la configuración de la aplicación cliente.

```ini
SERVICES=sigar:true,openHardware:true,perfmon:true,powerGadget:true
COMMUNICATIONS_PORT=9001
MONITORING_TIME=3600
FRECUENCY=1
PICK_UP_PATH=E:\\Monitoreo\\Monitoreo_PickUp\\
DONE_PATH=E:\\Monitoreo\\Monitoreo_Done\\
OH_PATH=E:\\Monitoreo\\OpenHardwareMonitor\\
OH_PROCESS=OpenHardwareMonitor.exe
SIGAR_RECORD_PATH=E:\\Monitoreo\\Monitoreo_Logs\\
SIGAR_HEADERS=Username,UpTime,RamFree,RamUsed,MemFreePercent,MemUsedPercent,SwapMemoryFree,SwapMemoryPageIn,SwapMemoryPageOut,SwapMemoryUsed,HDFreeSpace,HDUsedSpace,NetRXBytes,NetTxBytes,NetSpeed,NetRXErrors,NetTxErrors,NetRxPackets,NetTxPackets,Processes_Detail,Processes_General
DLL_PATH=E:\\
PM_RECORD_PATH=E:\\Monitoreo\\Monitoreo_Logs\\
PM_MAX_SIZE=512
COUNTERS=\\Processor(0)\\% Processor Time\t\\Processor(1)\\% Processor Time\t\\Processor(2)\\% Processor Time\t\\Processor(3)\\% Processor Time\t\\Processor(4)\\% Processor Time\t\\Processor(5)\\% Processor Time\t\\Processor(6)\\% Processor Time\t\\Processor(7)\\% Processor Time
COUNTER_NAME=CPU_counter
PG_RECORD_PATH=E:\\Monitoreo\\Monitoreo_Logs\\
PG_POWER_PATH=C:\\Program Files\\Intel\\Power Gadget 3.0\\
PG_EXE_NAME=PowerLog3.0.exe
TIME_INIT=7
TIME_END=22
```