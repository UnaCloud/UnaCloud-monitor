# Nagios

A continuación se muestra una guia de configuración de Nagios.

## Instalación Nagios Core

A continuación se presenta un resumen para realizar la instalación de Nagios Core. Se deberá realizar la instalación de Nagios y sus plugins, para mayor información seguir [guía](https://www.digitalocean.com/community/tutorials/how-to-install-nagios-4-and-monitor-your-servers-on-ubuntu-14-04).

**Importante**: Se debe realizar la instalación de Nagios Core, plugins, NRPE y NSCA para el correcto funcionamiento.

### Creación de usuarios y grupo

Se deberá crear el usuario y el grupo al cual pertenecerá.

```bash
$ sudo useradd nagios
$ sudo groupadd nagcmd
$ sudo usermod -a -G nagcmd nagios
```

## Configuración Nagios

A continuación se presenta un resumen de las configuraciones a realizar.

### Servidores

Se deberá eliminar comentario y crear carpeta en donde se guardarán los servidores que se van a monitorizar.

```bash
# Ingresar al archivo
sudo nano /usr/local/nagios/etc/nagios.cfg
# Buscar y eliminar comentario de linea
cfg_dir=/usr/local/nagios/etc/servers
# Crear directorio
sudo mkdir /usr/local/nagios/etc/servers
```

### Configuración Windows

* Se debe habilitar que el host permita el envío de passive checks.

```bash
define host
{
    use             windows-server  ; #Inherit default values from a template
    host_name       winserver       ; #The name we're giving to this host
    alias           My Windows Server2      ; #A longer name associated with the host
    address         192.168.56.110  ; #IP address of the host
    active_checks_enabled   1   # Habilita checks activos para el host
    passive_checks_enabled  1   # Habilita checks pasivos para el host
}
```

* Configurar servicio pasivo

```bash
define service
{
    use                                     generic-service
    name                                    passive_service
    active_checks_enabled                   0
    passive_checks_enabled                  1 # We want only passive checking
    flap_detection_enabled                  0
    register                                0 # This is a template, not a real service
    is_volatile                             0
    check_period                            24x7
    max_check_attempts                      1
    normal_check_interval                   5
    retry_check_interval                    1
    check_freshness                         0
    contact_groups                          admins
    check_command                           check_dummy!0
    notification_interval                   120
    notification_period                     24x7
    notification_options                    w,u,c,r
    stalking_options                        w,c,u
}

define service
{
    use                                     passive_service
    service_description                     TestMessage
    host_name                               winserver
}
```

### Configuración NSCA

* Se deberá configurar cómo el servidor recibirá las peticiones pasivas.

```bash
# Abrir el archivo
sudo nano /etc/nsca.cfg
# Configurar el puerto por el que escuchará el servicio NSCA
server_port=5667
# Configurar la IP del servidor Nagios
server_address=192.168.56.101
# Configurar el usuario con el que se ejcutará
nsca_user=nagios
# Configurar log donde se guardarán los checks
command_file=/var/log/nsca
# Configurar contraseña para el envío de información hacia el servidor NSCA
password=N4gi0s.T3st.100%
# Configurar el método de cifrado
decryption_method=1
```









