# UnaCloudMonitoringComponent

UnaCloud consiste en 4 proyectos.

* Agente Monitoring Services
* Agent Monitoring
* Monitoring Communication
* Monitoring Shares

## Herramientas de Monitoreo

El proyecto actualmente funciona utilizando cuatro herramientas las cuales realizan las mediciones.

* [Sigar v1.6.3](https://github.com/UnaCloud/UnaCloudMonitor/blob/manre_develop/Sigar.md)
* [Intel PowerGadget v3.0](https://github.com/UnaCloud/UnaCloudMonitor/blob/manre_develop/Intel%20Power%20Gadget.md)
* [Open Hardware Monitor v0.7.1 Beta](https://github.com/UnaCloud/UnaCloudMonitor/blob/manre_develop/OpenHardwareMonitor.md)
* [Perfmon](https://github.com/UnaCloud/UnaCloudMonitor/blob/manre_develop/Perfmon.md)

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
