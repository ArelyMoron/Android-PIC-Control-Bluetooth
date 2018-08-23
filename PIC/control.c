#include <16f887.h>
#fuses XT, NOWDT
#use delay(clock = 4M)
#use fast_io(D)
#use RS232(BAUD=9600,BITS=8,PARITY=N,XMIT=PIN_C6,RCV=PIN_C7) // habilito el puerto serie del PIC
// Elaborado por Arely M.

/*
Nota
El led esta conectado al pin D-1 y el servomotor al pin D-0,
la señal PWM que ocupo para mover el servomotor la produzco con ayuda del timer0
*/
int8 dato, i;
int dat;

void main()
{
   set_tris_D(0x00); // pongo todos los pines del puerto D como salidas
   output_D(0x00);
   enable_interrupts(GLOBAL);
   enable_interrupts(INT_TIMER0); // habilito la interrupcion por timer0
   setup_timer_0(RTCC_INTERNAL | RTCC_DIV_4);
   set_TIMER0(235); // pongo la carga del timer
   dato = 5; // con esta variable controlo el ancho del pulso de la señal que se le envia al servomotor
   
   while(1)
   {
      dat = getc(); // obtengo el dato que se envio por bluetooth
      if(dat == 1)
      {
         output_low(PIN_D1); // apago el led
      }
      
      else if(dat == 2)
      {
         output_high(PIN_D1); // enciendo el led
      }
      
      else
      {
         dato = dat; // cambio el ancho del pulso de la señal del servomotor
      }
   }
}

#int_timer0
void timer0 () // esto se ejecuta cuando se produce la interrupcion por timer0
{
   i++;
   if(i == 0)
   {
      output_high(PIN_D0);
   }
   
   if(i == dato)
   {
      output_low(PIN_D0);
   }
   set_TIMER0(235);
}
