# Monitoreo Intel Power Gadget

El archivo config.properties permite habilitar el monitoreo utilizando Intel Power Gadget. En este archivo se deben configurar las siguientes lineas.

```
PG_RECORD_PATH=C:\\Monitoreo\\Monitoreo_Logs\\
PG_POWER_PATH=C:\\Program Files\\Intel\\Power Gadget 3.0\\
PG_EXE_NAME=PowerLog3.0.exe
```

A continuación se define el significado de cada elemento:

* **PG_RECORD_PATH**: Ruta donde guardará el log de monitoreo.
* **PG_POWER_PATH**: Directorio donde se encuentra instalada la aplicación
* **PG_EXE_NAME**: Nombre del ejecutable. Por defecto debe estar PowerLog3.0.exe

## Requerimientos

Se deben cumplir los siguientes requerimientos para el correcto funcionamiento de PowerGadget.

* Windows 7 32-bit y 64-bit
* Windows 8 desktop 32-bit y 64-bit
* Microsoft* .Net Framework 4
* Microsoft Visual C++ 2010 SP1 Redistributable package (x86 o x64 depende del OS)
* 2da Generación Procesador Intel® Core™ o mayor (Sandy Bridge). Si no se cumple este requisito el programa no se ejecutará. [(Listado de procesadores Intel)](https://en.wikipedia.org/wiki/List_of_Intel_microprocessors#64-bit_processors:_Intel_64_.E2.80.93_Sandy_Bridge_.2F_Ivy_Bridge_microarchitecture)

## Variables de Monitorización

Por defecto PowerGadget monitoriza:

* Frecuencia del procesador. En MHz
* Energía promedio. En Wattz
* Temperatura. En Celcius