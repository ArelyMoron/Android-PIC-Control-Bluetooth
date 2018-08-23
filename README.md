# Android-PIC-Control-Bluetooth

## Ejemplo de como conectar microcontrolador PIC y un celular android por bluetooth


El código que uso en el celular android lo hice en java con android studio y el código del microcontrolador PIC lo hice en C usando  PIC C Compiler. 
y ocupo un modulo bluetooth HC-05 el cual le envía datos al PIC por el puerto serie.

Dicho esto el funcionamiento de la app es el siguiente
Al entrar muestra los dispositivos bluetooth vinculados al celular y se selecciona uno para conectarse a el, si el dispositivo no esta vinculado la app cuenta con opción para buscar, vincularse y conectarse al dispositivo; Una vez conectado se abre otra pantalla en la app donde muestra opciones para controlar un led y un servomotor conectado al PIC.

